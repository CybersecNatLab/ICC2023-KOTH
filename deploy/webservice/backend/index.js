"use strict";

const express = require("express");
const passport = require("passport");
const LocalStrategy = require("passport-local");
const session = require("express-session");
const fs = require("fs");
const { matches } = require("ip-matching");

const passwords = [
  "C77sFW1J50gS",
  "89tYso40AJ2s",
  "C0e2AfqTD21S",
  "Oop0GHL427fq",
  "YazdAyb14f4x",
  "lqQVZA5689nm",
  "J0dRBW49sUKQ",
  "Kk7eW0g948zy",
];

const users = {
  africa: [passwords[0], 2],
  asia: [passwords[1], 3],
  canada: [passwords[2], 4],
  europe: [passwords[3], 5],
  latina: [passwords[4], 6],
  oceania: [passwords[5], 8],
  usa: [passwords[6], 9],
  backend: [passwords[7], 10],
};
console.log(users);

passport.use(
  new LocalStrategy(function verify(username, password, callback) {
    if (username in users && users[username][0] == password) {
      return callback(null, { ID: users[username][1], shortname: username });
    }
    return callback(null, false, { message: "Incorrect username or password" });
  })
);

passport.serializeUser((user, cb) => {
  cb(null, { ID: user.ID, shortname: user.shortname });
});

passport.deserializeUser((user, cb) => {
  return cb(null, user);
});

const app = new express();

app.use(express.json());
app.set("trust proxy", "172.16.0.0/12,10.10.0.0/24");
app.use(
  session({
    proxy: true, // Crucial
    resave: false,
    saveUninitialized: true,
    secret: "mdbfmDFMl2DxwRuC3JZql7DF8Dv6jGyrkO4MKTsuKVmPkURZx7SwqTGpJoWH57GL",
  })
);
app.use(passport.authenticate("session"));

const isLoggedIn = (req, res, next) => {
  if (req.isAuthenticated()) {
    return next();
  }
  return res.status(401).json({ message: "Not authenticated" });
};

app.post("/api/login", passport.authenticate("local"), (req, res) => {
  try {
    res.status(200).json({ ID: req.user.ID, shortname: req.user.shortname });
  } catch (err) {
    res.status(500).end();
  }
});

app.get("/api/logout", (req, res) => {
  try {
    req.logout(() => {
      res.status(200).end();
    });
  } catch (err) {
    res.status(500).end();
  }
});

app.get("/api/me", isLoggedIn, async (req, res) => {
  try {
    res.status(200).json({ ID: req.user.ID, shortname: req.user.shortname });
  } catch (err) {
    res.status(500).end();
  }
});

app.get("/api/games", async (req, res) => {
  try {
    let rawdata = fs.readFileSync("/results/complete_rounds.json");
    res.status(200).json(JSON.parse(rawdata));
  } catch (err) {
    res.status(200).json([]);
  }
});

app.get("/api/scoreboard", async (req, res) => {
  try {
    let rawdata;
    if (matches(req.ip, "10.11.42.0/24") || matches(req.ip, "10.10.42.0/24"))
      rawdata = fs.readFileSync("/results/current_scoreboard.json");
    else rawdata = fs.readFileSync("/results/user_scoreboard.json");
    res.status(200).json(JSON.parse(rawdata));
  } catch (err) {
    res.status(200).json([]);
  }
});

app.get("/api/games/:game/scoreboard", async (req, res) => {
  try {
    const game = req.params.game;
    if (!/^[0-9]+$/.test(game)) {
      return res.status(404).json({ error: "game does not exists" });
    }
    let data;
    try {
      data = fs.readFileSync("/results/outputs/" + game + "/scoreboard.json");
      res.status(200).json(JSON.parse(data));
    } catch (err) {
      res.status(404).json({ error: "game does not exists" });
    }
  } catch (err) {
    res.status(500).end();
  }
});

app.get("/api/games/:game/history", async (req, res) => {
  try {
    const game = req.params.game;
    if (!/^[0-9]+$/.test(game)) {
      return res.status(404).json({ error: "game does not exists" });
    }
    let data;
    try {
      data = fs.readFileSync("/results/outputs/" + game + "/map.json");
      res.status(200).json(JSON.parse(data));
    } catch (err) {
      res.status(404).json({ error: "game does not exists" });
    }
  } catch (err) {
    res.status(500).end();
  }
});

app.get("/api/games/scoreboards", async (req, res) => {
  try {
    let games = [];
    try {
      games = JSON.parse(fs.readFileSync("/results/complete_rounds.json"));
    } catch (err) {}
    let result = {};
    for (const game of games) {
      if (/^[0-9]+$/.test(game)) {
        result[game] = {};
        try {
          const data = fs.readFileSync(
            "/results/outputs/" + game + "/scoreboard.json"
          );
          result[game]["scoreboard"] = JSON.parse(data);
        } catch (err) {}
      }
    }
    res.status(200).json(result);
  } catch (err) {
    res.status(500).end();
  }
});

app.get("/api/games/:game/outputs", isLoggedIn, async (req, res) => {
  try {
    const game = req.params.game;
    if (!/^[0-9]+$/.test(game)) {
      return res.status(404).json({ error: "game does not exists" });
    }
    const shortname = req.user.shortname;
    const basepath = "/results/outputs/" + game + "/" + shortname + "/";
    let data = {};
    let files = fs.readdirSync(basepath);
    for (const file of files) {
      try {
        const fileContent = fs.readFileSync(basepath + file);
        data[file] = fileContent.toString("base64");
      } catch (err) {}
    }
    res.status(200).json(data);
  } catch (err) {
    console.error(err);
    res.status(500).end();
  }
});

app.use("/", express.static("/static"));

app.listen(3000, "0.0.0.0", () =>
  console.log(`Server running on http://0.0.0.0:3000/`)
);
