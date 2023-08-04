import asyncio
import websockets

import json
import sys
import gzip
import base64

# Adapter for the remote interface. Allows for your bot to run remotely without any modifications to your code.
# You can change this, but if I were you I wouldn't :)
class RemotePlayerAdapter():
    def __init__(self, port):
        loop = asyncio.new_event_loop()
        asyncio.set_event_loop(loop)
        start_server = websockets.serve(self.handle_messages, '0.0.0.0', port)
        print(f"Booting player on port {port}")
        loop.run_until_complete(start_server)
        loop.run_forever()

    async def handle_messages(self, websocket, path):
        try:
            print(f"Connection instantiated")
            conn_msg = await websocket.recv()
            conn_msg = gzip.decompress(base64.b64decode(conn_msg.encode()))
            print(f"Connection received, message: {conn_msg}")
            while(True):
                data = await websocket.recv()
                data = gzip.decompress(base64.b64decode(data.encode()))
                print(f"Received data {data}")
                await websocket.send(json.dumps({"type":"move", "to":[0, 0]}))
        except websockets.exceptions.ConnectionClosedError:
            return


RemotePlayerAdapter(sys.argv[1])