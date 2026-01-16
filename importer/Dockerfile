FROM python:3.11-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY import_docker.py .

RUN mkdir -p /data
COPY hcw_courses.json /data/hcw_courses.json

ENV DATA_FILE=/data/hcw_courses.json

CMD ["python", "import_docker.py"]
