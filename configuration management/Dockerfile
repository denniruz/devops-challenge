FROM python:2.7

ENV FABRIC_VERSION=1.14

RUN apt-get update \
    && apt-get install -y rsync

RUN pip install fabric==$FABRIC_VERSION

WORKDIR /app

#ENTRYPOINT ["fab"]

# Add script that handles entrypoint commands
#ADD startup.py /root/startup.py
#RUN chmod +x /root/startup.py

# Register our entry point script handler as the Docker Entrypoint
#ENTRYPOINT ["python","/root/startup.py"]