import asyncio
import websockets

import json
import sys
import gzip
import base64
from bot import Bot

# Adapter for the remote interface. Allows for your bot to run remotely without any modifications to your code.
# You can change this, but if I were you I wouldn't :)


class RemotePlayerAdapter():
    def __init__(self, port):
        start_server = websockets.serve(self.handle_messages, '0.0.0.0', port)
        print(f"Booting player on port {port}")
        asyncio.get_event_loop().run_until_complete(start_server)
        asyncio.get_event_loop().run_forever()

    async def handle_messages(self, websocket, path):
        try:
            conn_msg = await websocket.recv()
            conn_msg = gzip.decompress(base64.b64decode(conn_msg.encode()))
            print(f"Connection received, message: {conn_msg}")
            id = json.loads(conn_msg.decode())["id"]
            bot = Bot(id)
            while (True):
                data = await websocket.recv()
                data = gzip.decompress(base64.b64decode(data.encode()))
                map = json.loads(data)
                await websocket.send(bot.move(map))
        except websockets.exceptions.ConnectionClosedError:
            return


RemotePlayerAdapter(5000)
