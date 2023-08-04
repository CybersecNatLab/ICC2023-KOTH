
import json
import random

summon_random_movement = [{"instruction": 40, "params": [{"type": 0, "value": 1}, {"type": 0, "value": 2}, {"type": 0, "value": 3}, {"type": 0, "value": 4}]}, {"instruction": 37, "params": [{"type": 1, "value": 1}, {"type": 1, "value": 4}, {"type": 0, "value": 0}]}, {"instruction": 46, "params": [{"type": 0, "value": 0}, {"type": 1, "value": 1}]}, {"instruction": 42, "params": [{"type": 1, "value": 10}]}, {"instruction": 46, "params": [{"type": 0, "value": 0}, {"type": 1, "value": 2}]}, {"instruction": 42, "params": [{"type": 1, "value": 12}]}, {"instruction": 46, "params": [{"type": 0, "value": 0}, {"type": 1, "value": 3}]}, {"instruction": 42, "params": [{"type": 1, "value": 14}]}, {"instruction": 46, "params": [{"type": 0, "value": 0}, {"type": 1, "value": 4}]}, {"instruction": 42, "params": [
    {"type": 1, "value": 16}]}, {"instruction": 18, "params": [{"type": 0, "value": 1}, {"type": 1, "value": 1}, {"type": 0, "value": 1}]}, {"instruction": 47, "params": [{"type": 0, "value": 1}, {"type": 0, "value": 2}]}, {"instruction": 18, "params": [{"type": 0, "value": 2}, {"type": 1, "value": 1}, {"type": 0, "value": 2}]}, {"instruction": 47, "params": [{"type": 0, "value": 1}, {"type": 0, "value": 2}]}, {"instruction": 18, "params": [{"type": 0, "value": 1}, {"type": 1, "value": -1}, {"type": 0, "value": 1}]}, {"instruction": 47, "params": [{"type": 0, "value": 1}, {"type": 0, "value": 2}]}, {"instruction": 18, "params": [{"type": 0, "value": 2}, {"type": 1, "value": -1}, {"type": 0, "value": 2}]}, {"instruction": 47, "params": [{"type": 0, "value": 1}, {"type": 0, "value": 2}]}]


class Bot():
    id = None

    def __init__(self, id):
        self.id = id
        print(f"Bot initialized with id {self.id}")

    def move(self, map):
        my_location = (0, 0)
        for column in range(len(map["map"])):
            for row in range(len(map["map"][column])):
                if ("entity" in map["map"][column][row]):
                    if (map["map"][column][row]["entity"]["type"] == "summoner"):
                        if (map["map"][column][row]["entity"]["id"] == self.id):
                            my_location = (column, row)

        if (random.randrange(20) == 10):
            summon_type = random.randint(1, 8)
            summon_direction = random.randint(0, 4)
            return json.dumps({"type": "summon", "direction": summon_direction, "entityType": summon_type, "code": summon_random_movement})
        elif (random.randrange(20) == 10):
            punch_direction = random.randint(0, 4)
            return json.dumps({"type": "punch", "direction": punch_direction})
        elif (random.randrange(20) == 10):
            push_direction = random.randint(0, 4)
            return json.dumps({"type": "push", "direction": push_direction})
        else:
            possible_destinations = [[my_location[0], my_location[1]-1], [my_location[0]-1, my_location[1]], [
                my_location[0], my_location[1]+1], [my_location[0]+1, my_location[1]]]
            possible_destinations = [x for x in possible_destinations if x[0] >=
                                     0 and x[0] < map['width'] and x[1] >= 0 and x[1] < map['height']]
            return json.dumps({"type": "move", "to": random.choice(possible_destinations)})
