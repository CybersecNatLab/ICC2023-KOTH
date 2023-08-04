FROM python:3.8-alpine

RUN apk add gcc python3-dev linux-headers musl-dev g++

RUN python3 -m pip install websockets asyncio

COPY bot.py /bot.py
COPY constants.py /constants.py
COPY RPC_adapter.py /RPC_adapter.py

ENTRYPOINT ["python3", "-u", "/RPC_adapter.py"]