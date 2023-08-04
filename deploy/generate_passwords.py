
import os
import random
import string
import json
passwords = []
if (not os.path.exists("passwords.json")):
    for x in range(1, 13):
        password = ''.join(random.choice(
            string.ascii_letters + string.digits) for _ in range(32))
        passwords.append(password)
    json.dump(passwords, open("passwords.json", 'w'))
else:
    passwords = json.load(open("./passwords.json", "r"))


teams = ["africa", "asia", "canada", "europe", "latina",  "oceania", "usa"]

if (not os.path.exists("./auth")):
    os.mkdir("./auth")

for team in range(len(teams)):
    if (not os.path.exists(f"./auth/{teams[team]}")):
        os.mkdir(f"./auth/{teams[team]}")
    os.system(
        f"htpasswd -Bcb ./auth/{teams[team]}/registry.password {teams[team]} {passwords[team]}")
