import asyncio
import websockets

import json
import sys
import gzip
import base64
from bots.human_player_bot import Bot, Tileset
import threading

random_code=[{"instruction": 55, "params": [{"type": 4, "value": "[random] random test initialized"}]}, {"instruction": 45, "params": [{"type": 0, "value": 1}, {"type": 0, "value": 2}, {"type": 0, "value": 3}, {"type": 0, "value": 4}]}, {"instruction": 55, "params": [{"type": 4, "value": "[random] current position:"}]}, {"instruction": 55, "params": [{"type": 0, "value": 1}]}, {"instruction": 55, "params": [{"type": 0, "value": 2}]}, {"instruction": 37, "params": [{"type": 1, "value": 1}, {"type": 1, "value": 4}, {"type": 0, "value": 0}]}, {"instruction": 55, "params": [{"type": 4, "value": "[random] random returned:"}]}, {"instruction": 55, "params": [{"type": 0, "value": 0}]}, {"instruction": 51, "params": [{"type": 0, "value": 0}, {"type": 1, "value": 1}]}, {"instruction": 47, "params": [{"type": 1, "value": 16}]}, {"instruction": 51, "params": [{"type": 0, "value": 0}, {"type": 1, "value": 2}]}, {"instruction": 47, "params": [{"type": 1, "value": 22}]}, {"instruction": 51, "params": [{"type": 0, "value": 0}, {"type": 1, "value": 3}]}, {"instruction": 47, "params": [{"type": 1, "value": 28}]}, {"instruction": 51, "params": [{"type": 0, "value": 0}, {"type": 1, "value": 4}]}, {"instruction": 47, "params": [{"type": 1, "value": 34}]}, {"instruction": 55, "params": [{"type": 4, "value": "[random] case one"}]}, {"instruction": 18, "params": [{"type": 0, "value": 1}, {"type": 1, "value": 1}, {"type": 0, "value": 1}]}, {"instruction": 55, "params": [{"type": 4, "value": "[random] moving to:"}]}, {"instruction": 55, "params": [{"type": 0, "value": 1}]}, {"instruction": 55, "params": [{"type": 0, "value": 2}]}, {"instruction": 52, "params": [{"type": 0, "value": 1}, {"type": 0, "value": 2}]}, {"instruction": 55, "params": [{"type": 4, "value": "[random] case two"}]}, {"instruction": 18, "params": [{"type": 0, "value": 2}, {"type": 1, "value": 1}, {"type": 0, "value": 2}]}, {"instruction": 55, "params": [{"type": 4, "value": "[random] moving to:"}]}, {"instruction": 55, "params": [{"type": 0, "value": 1}]}, {"instruction": 55, "params": [{"type": 0, "value": 2}]}, {"instruction": 52, "params": [{"type": 0, "value": 1}, {"type": 0, "value": 2}]}, {"instruction": 55, "params": [{"type": 4, "value": "[random] case three"}]}, {"instruction": 18, "params": [{"type": 0, "value": 1}, {"type": 1, "value": -1}, {"type": 0, "value": 1}]}, {"instruction": 55, "params": [{"type": 4, "value": "[random] moving to:"}]}, {"instruction": 55, "params": [{"type": 0, "value": 1}]}, {"instruction": 55, "params": [{"type": 0, "value": 2}]}, {"instruction": 52, "params": [{"type": 0, "value": 1}, {"type": 0, "value": 2}]}, {"instruction": 55, "params": [{"type": 4, "value": "[random] case four"}]}, {"instruction": 18, "params": [{"type": 0, "value": 2}, {"type": 1, "value": -1}, {"type": 0, "value": 2}]}, {"instruction": 55, "params": [{"type": 4, "value": "[random] moving to:"}]}, {"instruction": 55, "params": [{"type": 0, "value": 1}]}, {"instruction": 55, "params": [{"type": 0, "value": 2}]}, {"instruction": 52, "params": [{"type": 0, "value": 1}, {"type": 0, "value": 2}]}]
cat_code=[{"instruction": 50, "params": [{"type": 4, "value": "[cat] cat test initialized"}]}, {"instruction": 14, "params": [{"type": 0, "value": 1}, {"type": 1, "value": 0}]}, {"instruction": 14, "params": [{"type": 0, "value": 3}, {"type": 1, "value": 0}]}, {"instruction": 14, "params": [{"type": 0, "value": 2}, {"type": 1, "value": 0}]}, {"instruction": 39, "params": [{"type": 0, "value": 1}, {"type": 0, "value": 2}, {"type": 0, "value": 10}, {"type": 0, "value": 11}, {"type": 0, "value": 12}, {"type": 0, "value": 13}, {"type": 0, "value": 14}, {"type": 0, "value": 15}]}, {"instruction": 50, "params": [{"type": 4, "value": "[cat] got entity:"}]}, {"instruction": 50, "params": [{"type": 0, "value": 12}]}, {"instruction": 38, "params": [{"type": 0, "value": 12}]}, {"instruction": 42, "params": [{"type": 1, "value": 16}]}, {"instruction": 46, "params": [{"type": 0, "value": 12}, {"type": 1, "value": 0}]}, {"instruction": 43, "params": [{"type": 1, "value": 16}]}, {"instruction": 50, "params": [{"type": 4, "value": "[cat] got entityID:"}]}, {"instruction": 50, "params": [{"type": 0, "value": 13}]}, {"instruction": 46, "params": [{"type": 0, "value": 13}, {"type": 4, "value": "changeme"}]}, {"instruction": 42, "params": [{"type": 1, "value": 16}]}, {"instruction": 47, "params": [{"type": 0, "value": 1}, {"type": 0, "value": 2}]}, {"instruction": 18, "params": [{"type": 0, "value": 2}, {"type": 1, "value": 1}, {"type": 0, "value": 2}]}, {"instruction": 46, "params": [{"type": 0, "value": 2}, {"type": 1, "value": 50}]}, {"instruction": 43, "params": [{"type": 1, "value": 23}]}, {"instruction": 18, "params": [{"type": 0, "value": 1}, {"type": 1, "value": 1}, {"type": 0, "value": 1}]}, {"instruction": 46, "params": [{"type": 0, "value": 1}, {"type": 1, "value": 50}]}, {"instruction": 42, "params": [{"type": 1, "value": 24}]}, {"instruction": 41, "params": [{"type": 1, "value": 3}]}, {"instruction": 41, "params": [{"type": 1, "value": 4}]}, {"instruction": 49, "params": []}]
move_to_origin_code=[{"instruction": 47, "params": [{"type": 1, "value": 0}, {"type": 1, "value": 0}]}]
mole_ability_code=[{"instruction": 37, "params": [{"type": 1, "value": 1}, {"type": 1, "value": 2}, {"type": 0, "value": 0}]}, {"instruction": 46, "params": [{"type": 0, "value": 0}, {"type": 1, "value": 1}]}, {"instruction": 42, "params": [{"type": 1, "value": 4}]}, {"instruction": 47, "params": [{"type": 1, "value": 0}, {"type": 1, "value": 0}]}, {"instruction": 48, "params": [{"type": 1, "value": 0}]}]
exploit_code=[{'instruction': 47, 'params': [{'type': 0, 'value': 1}, {'type': 0, 'value': 2}]}]
# Adapter for the remote interface. Allows for your bot to run remotely without any modifications to your code.
# You can change this, but if I were you I wouldn't :)
class RemotePlayerAdapter():
    port=0
    bot=None
    def __init__(self, port):
        self.port=port
        print(f"Booting player on port {port}")
        t = threading.Thread(target=self.run_asyncio_code, args=())
        t.start()
        self.bot=Bot(Tileset("./bots/tmw_desert_spacing.png"), size=(50, 50))
        self.bot.run()
        
    
    def run_asyncio_code(self):
        loop = asyncio.new_event_loop()
        asyncio.set_event_loop(loop)
        start_server = websockets.serve(self.handle_messages, '0.0.0.0', self.port)
        loop.run_until_complete(start_server)
        loop.run_forever()

    async def handle_messages(self, websocket, path):
        global cat_code
        prev_move=None
        try:
            print(f"Connection instantiated")
            conn_msg = await websocket.recv()
            conn_msg = gzip.decompress(base64.b64decode(conn_msg.encode()))
            print(f"Connection received, message: {conn_msg}")
            id=json.loads(conn_msg.decode())["id"]
            cat_code=json.loads(json.dumps(cat_code).replace("changeme", id))
            
            while(True):
                data = await websocket.recv()
                data = gzip.decompress(base64.b64decode(data.encode()))
                map = json.loads(data)
                my_location=(0,0)
                for row in range(len(map["map"])):
                    for column in range(len(map["map"][row])):
                        if("entity" in map["map"][row][column]):
                            if(map["map"][row][column]["entity"]["type"]=="summoner"):
                                if(map["map"][row][column]["entity"]["id"]==id):
                                    my_location=(row, column)
                print(my_location)
                self.bot.render(map)
                move = self.bot.move_queue.get(block=True)
                print(f"Got move {move}")
                if(move=="e"):
                    other_location=(-1,-1)
                    for row in range(len(map["map"])):
                        for column in range(len(map["map"][row])):
                            if("entity" in map["map"][row][column]):
                                if(map["map"][row][column]["entity"]["type"]=="summoner"):
                                    if(map["map"][row][column]["entity"]["id"]!=id):
                                        other_location=(row, column)
                    await websocket.send(json.dumps({"type":"punch", "to":[other_location[0], other_location[1]]}))
                if(move=="w"):
                    await websocket.send(json.dumps({"type":"move", "to":[my_location[0], my_location[1]-1]}))
                if(move=="s"):
                    await websocket.send(json.dumps({"type":"move", "to":[my_location[0], my_location[1]+1]}))
                if(move=="a"):
                    await websocket.send(json.dumps({"type":"move", "to":[my_location[0]-1, my_location[1]]}))
                if(move=="d"):
                    await websocket.send(json.dumps({"type":"move", "to":[my_location[0]+1, my_location[1]]}))
                if(move=="p" and prev_move!=None):
                    direction_dict={"w":0, "s":1, "a":2, "d":3}
                    await websocket.send(json.dumps({"type":"push", "direction":direction_dict[prev_move]}))
                if(move=="h" and prev_move!=None):
                    direction_dict={"w":0, "s":1, "a":2, "d":3}
                    await websocket.send(json.dumps({"type":"punch", "direction":direction_dict[prev_move]}))
                if(move in ["1","2", "3","4","5","7","8"] and prev_move!=None):
                    direction_dict={"w":0, "s":1, "a":2, "d":3}
                    await websocket.send(json.dumps({"type":"summon", "direction":direction_dict[prev_move], "entityType":int(move), "code":move_to_origin_code}))
                # if(move=="2" and prev_move!=None):
                #     direction_dict={"w":0, "s":1, "a":2, "d":3}
                #     await websocket.send(json.dumps({"type":"summon", "direction":direction_dict[prev_move], "entityType":int(move), "code":mole_ability_code}))
                if(move=="6" and prev_move!=None):
                    direction_dict={"w":0, "s":1, "a":2, "d":3}
                    await websocket.send(json.dumps({"type":"summon", "direction":direction_dict[prev_move], "entityType":int(move), "code":exploit_code}))
                
                if(move in ["w", "s", "a", "d"]):
                    prev_move=move
        except websockets.exceptions.ConnectionClosedError:
            return

RemotePlayerAdapter(sys.argv[1])