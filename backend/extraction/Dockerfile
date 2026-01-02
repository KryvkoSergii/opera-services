FROM public.ecr.aws/lambda/python:3.10
LABEL authors="Sergii_Kryvko"

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

ENTRYPOINT ["top", "-b"]