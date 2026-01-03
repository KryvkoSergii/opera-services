# AWS Lambda base image (Python 3.11)
FROM public.ecr.aws/lambda/python:3.11
LABEL authors="Sergii_Kryvko"

# System deps for librosa/soundfile/scipy
# (Lambda image is Amazon Linux; use yum)
RUN yum -y update && yum -y install \
    gcc gcc-c++ make \
    libsndfile \
    ffmpeg \
  && yum clean all

# Install Python deps into the Lambda task root (/var/task)
# (Better caching: copy only requirements first)
COPY requirements.txt .
RUN pip3 install --no-cache-dir -r requirements.txt -t ${LAMBDA_TASK_ROOT}

# Copy your app code into the Lambda task root
COPY . ${LAMBDA_TASK_ROOT}

# Lambda entrypoint: <module>.<function>
# handler.py must expose function `handler(event, context)`
CMD ["feature_extraction.handler"]