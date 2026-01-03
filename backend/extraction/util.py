# source https://github.com/evelyn0414/OPERA.git

from models.models_cola import Cola
from scipy.signal import butter, lfilter
import librosa
import boto3

ENCODER_PATH_OPERA_CE_EFFICIENTNET = "libs/encoder-operaCE.ckpt"
ENCODER_PATH_OPERA_CT_HT_SAT = "models/encoder-operaCT.ckpt"

s3 = boto3.client("s3")

"""
extract features using OPERA models
"""
def extract_opera_feature(file_ref: FileInS3, pretrain: str = "operaCE", input_sec: int = 8, from_spec: bool = False,
                          dim: int = 1280, pad0: bool = False):

    device: str = "gpu" if torch.cuda.is_available() else "cpu"

    encoder_path: str = get_encoder_path(pretrain)

    ckpt = torch.load(encoder_path, map_location=torch.device(device))
    model = initialize_pretrained_model(pretrain)
    model.eval()
    model.load_state_dict(ckpt["state_dict"], strict=False)

    opera_features = []

    if from_spec:
        data = audio_file
    else:
        # input is filename of an audio
        if pad0:
            data = get_entire_signal_librosa(file_ref, spectrogram=True, input_sec=input_sec, pad=True, types='zero')
        else:
            data = get_entire_signal_librosa(file_ref, spectrogram=True, input_sec=input_sec, pad=True)

    data = np.array(data)

    # for entire audio, batchsize = 1
    data = np.expand_dims(data, axis=0)

    x = torch.tensor(data, dtype=torch.float)
    features = model.extract_feature(x, dim).detach().numpy()

    # for entire audio, batchsize = 1
    opera_features.append(features.tolist()[0])

    x_data = np.array(opera_features)
    print(x_data.shape)
    return x_data

"""
load file content from AWS S3 with specified sample rate (also converts to mono)
"""
def load_wav_from_s3_in_memory(file_ref: FileInS3, sample_rate: int):
    obj = s3.get_object(Bucket=file_ref.bucket, Key=file_ref.key)
    wav_bytes = obj["Body"].read()

    bio = io.BytesIO(wav_bytes)
    data, rate = librosa.load(bio, sr=sample_rate)
    return data, rate


def get_encoder_path(pretrain) -> str:
    encoder_paths = {
        "operaCT": ENCODER_PATH_OPERA_CT_HT_SAT,
        "operaCE": ENCODER_PATH_OPERA_CE_EFFICIENTNET
    }
    if not os.path.exists(encoder_paths[pretrain]):
        raise FileNotFoundError(
            f"Encoder path not found: {encoder_paths[pretrain]}, please download the pretrained model.")
    return encoder_paths[pretrain]


def initialize_pretrained_model(pretrain) -> Cola:
    if pretrain == "operaCT":
        model = Cola(encoder="htsat")
    elif pretrain == "operaCE":
        model = Cola(encoder="efficientnet")
    else:
        raise NotImplementedError(f"Model not exist: {pretrain}, please check the parameter.")
    return model


def get_entire_signal_librosa(file_ref: FileInS3, input_sec: int = 8, sample_rate: int = 16000, butterworth_filter=None,
                              spectrogram: bool = False, pad: bool = False, from_cycle: bool = False, yt=None,
                              types='repeat'):
    if not from_cycle:

        # load file with specified sample rate (also converts to mono)
        data, rate = load_wav_from_s3_in_memory(file_ref, sample_rate = sample_rate)

        if butterworth_filter:
            # butter bandpass filter
            data = _butter_bandpass_filter(lowcut=200, highcut=1800, fs=sample_rate, order=butterworth_filter)

        # Trim leading and trailing silence from an audio signal.
        FRAME_LEN = int(sample_rate / 10)  #
        HOP = int(FRAME_LEN / 2)  # 50% overlap, meaning 5ms hop length

        TRIM = True
        if TRIM:
            yt, index = librosa.effects.trim(
                data, frame_length=FRAME_LEN, hop_length=HOP
            )
        else:
            yt = data

    # check audio not too short
    duration = librosa.get_duration(y=yt, sr=sample_rate)
    if duration < input_sec:
        if not pad:
            print("Warning: audio too short, skipped")
            return None
        else:
            yt = split_pad_sample([yt, 0, 0], input_sec, sample_rate, types)[0][0]

    # directly process to spectrogram
    if spectrogram:
        # # visualization for testing the spectrogram parameters
        # plot_melspectrogram(yt.squeeze(), title=filename.replace("/", "-"))
        return pre_process_audio_mel_t(yt.squeeze(), f_max=8000)

    return yt

def _butter_bandpass(lowcut, highcut, fs, order=5):
    nyq = 0.5 * fs
    low = lowcut / nyq
    high = highcut / nyq
    b, a = butter(order, [low, high], btype='band')

    return b, a

def _butter_bandpass_filter(data, lowcut, highcut, fs, order=5):
    b, a = _butter_bandpass(lowcut, highcut, fs, order=order)
    y = lfilter(b, a, data)

    return y

def split_pad_sample(sample, desired_length, sample_rate, types='repeat'):
    """
    if the audio sample length > desired_length, then split and pad samples
    else simply pad samples according to pad_types
    * types 'zero'   : simply pad by zeros (zero-padding)
    * types 'repeat' : pad with duplicate on both sides (half-n-half)
    * types 'aug'    : pad with augmented sample on both sides (half-n-half)
    """
    if types == 'zero':
        return _equally_slice_pad_sample(sample, desired_length, sample_rate)

    output_length = int(desired_length * sample_rate)
    soundclip = sample[0].copy()
    n_samples = len(soundclip)

    output = []
    if n_samples > output_length:
        """
        if sample length > desired_length, slice samples with desired_length then just use them,
        and the last sample is padded according to the padding types
        """
        # frames[j] = x[j * hop_length : j * hop_length + frame_length]
        frames = librosa.util.frame(
            soundclip, frame_length=output_length, hop_length=output_length//2, axis=0)
        for i in range(frames.shape[0]):
            output.append((frames[i], sample[1], sample[2]))

        # get the last sample
        last_id = frames.shape[0] * (output_length//2)
        last_sample = soundclip[last_id:]

        padded = _duplicate_padding(
            soundclip, last_sample, output_length, sample_rate, types)
        output.append((padded, sample[1], sample[2]))
    else:  # only pad
        padded = _duplicate_padding(
            soundclip, soundclip, output_length, sample_rate, types)
        output.append((padded, sample[1], sample[2]))

    return output

def _equally_slice_pad_sample(sample, desired_length, sample_rate):
    """
    pad_type == 0: zero-padding
    if sample length > desired_length,
    all equally sliced samples with samples_per_slice number are zero-padded or recursively duplicated
    """
    output_length = int(
        desired_length * sample_rate)  # desired_length is second
    soundclip = sample[0].copy()
    n_samples = len(soundclip)

    total_length = n_samples / sample_rate  # length of cycle in seconds
    # get the minimum number of slices needed
    n_slices = int(math.ceil(total_length / desired_length))
    samples_per_slice = n_samples // n_slices

    output = []  # holds the resultant slices
    src_start = 0  # staring index of the samples to copy from the sample buffer
    for i in range(n_slices):
        src_end = min(src_start + samples_per_slice, n_samples)
        length = src_end - src_start

        copy = _zero_padding(soundclip[src_start:src_end], output_length)
        output.append((copy, sample[1], sample[2]))
        src_start += length

    return output

def _duplicate_padding(sample, source, output_length, sample_rate, types):
    # pad_type == 1 or 2
    copy = np.zeros(output_length, dtype=np.float32)
    src_length = len(source)
    left = output_length - src_length  # amount to be padded

    if types == 'repeat':
        aug = sample

    while len(aug) < left:
        aug = np.concatenate([aug, aug])

    random.seed(7456)
    prob = random.random()
    if prob < 0.5:
        # pad the back part of original sample
        copy[left:] = source
        copy[:left] = aug[len(aug)-left:]
    else:
        # pad the front part of original sample
        copy[:src_length] = source[:]
        copy[src_length:] = aug[:left]

    return copy

def _zero_padding(source, output_length):
    copy = np.zeros(output_length, dtype=np.float32)
    src_length = len(source)

    frac = src_length / output_length
    if frac < 0.5:
        # tile forward sounds to fill empty space
        cursor = 0
        while (cursor + src_length) < output_length:
            copy[cursor:(cursor + src_length)] = source[:]
            cursor += src_length
    else:
        # [src_length:] part will be zeros
        copy[:src_length] = source[:]

    return copy

def pre_process_audio_mel_t(audio, sample_rate=16000, n_mels=64, f_min=50, f_max=2000, nfft=1024, hop=512):
    S = librosa.feature.melspectrogram(
        y=audio, sr=sample_rate, n_mels=n_mels, fmin=f_min, fmax=f_max, n_fft=nfft, hop_length=hop)
    # convert scale to dB from magnitude
    S = librosa.power_to_db(S, ref=np.max)
    if S.max() != S.min():
        mel_db = (S - S.min()) / (S.max() - S.min())
    else:
        mel_db = S
        print("warning in producing spectrogram!")

    return mel_db.T

class FileInS3:
    def __init__(self, bucket: str, key: str):
        self.bucket = bucket
        self.key = key
