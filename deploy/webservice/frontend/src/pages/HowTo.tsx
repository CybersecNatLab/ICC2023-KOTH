import React from 'react';
import './HowTo.css';

const HowTo = () => {
  return (
    <div className="howto">
      <h1>HOW TO PLAY</h1>

      <h2>What is this game?</h2>

      <div>
        The ICC2023 KOTH is a king of the hill game where you control a summoner, trying to be the
        last player standing while the fog shrinks the map. It is played on a square map of side 40.
        The game contains many features, described below. Your objective is to write a bot able to
        play against other bots and survive the longest!
      </div>

      <h2>TL;DR</h2>

      <ul>
        <li>
          Goal of the KOTH is to write a bot able to play a free-for-all game, you can use the
          provided base bot with all the communication protocol already correctly implemented and an
          example of bot game logic.
        </li>
        <li>
          The KOTH is played in round of 6 minutes, independent from each other. In each round you
          will gain points accordingly to your death order and the amount of kills. The final KOTH
          score is the sum of the score of each round.
        </li>
        <li>
          The bot must be submitted to the game manager as a docker container to your personal
          docker registry. Please use the provided upload template script in <code>push.sh</code>{' '}
          with your team shortname and your team password.
        </li>
        <li>
          You can update and submit the bot to the game manager a (reasonably) unlimited number of
          times but it will only be executed at the start of the new round.
        </li>
        <li>
          Each round of the KOTH will be played in ticks and, in each tick, all team bots will be
          contacted, in random order, exactly once, requesting the actions to be performed.
        </li>
        <li>
          In each round you will be assigned a wizard in a random position to control. In each tick
          you will be able to perform an action between moving, throwing a punch or pushing an
          entity and summon a minion from several available types.
        </li>
        <li>
          While you can control your wizard directly at each tick, when you summon a minion you will
          have to define its behavior through a shellcode written according to the specifications of
          an instruction set defined in the game rules. The minion's shellcode can no longer be
          updated after it's summoned and will continue to run on every tick as long as it's on the
          map.
        </li>
        <li>
          Inside the <code>VMCodeCompiler</code> folder you will have a compiler that, given the
          assembly, will return the json with the shellcode in the correct format to be sent to the
          game manager
        </li>
        <li>
          The vulnerabilities are present exclusively in the <code>ICC2023-KOTH.jar</code> file, an
          exact copy of the game engine running on the server. All other components (website,
          registry, ...) do not contain (expected) vulnerabilities and are explicitly out of target.
        </li>
        <li>
          The vulnerabilities should be exploited only through the bot actions. If you are in doubt
          about the vulnerabilities ask the staff first.
        </li>
        <li>
          Have fun and hack the <del>map</del> planet!
        </li>
      </ul>

      <h2>Scoring and game rules</h2>

      <div>
        The game is played in ticks, with no delay between them. At every tick your bot should
        submit a move in less than 100ms, failure to submit a move will result in no move being
        done. The valid moves are "move", "punch", "push", and "summon".
      </div>

      <ul>
        <li>"move" allows you to move to a nearby tile</li>
        <li>"punch" allows you to punch a nearby entity</li>
        <li>"push" allows you to push a nearby entity away</li>
        <li>"summon" allows you to summon minions</li>
      </ul>

      <div>
        Each summoner has 100 life and 100 mana. Mana is recovered at a rate of 1 each tick. Life is
        recovered only by killing another player, earning you 50 life. Both life and mana are capped
        at 100. The game awards life to either the player that dealt damage or pushed to the now
        dead player last.
      </div>

      <div>
        Minions cost mana to be summoned and can be programmed to behave autonomously. A full list
        of summonable minions and instructions to program them is available on the web interface.
        Note that each minion's VM has only a certain amount of "gas", resetting each tick, to avoid
        "accidental" infinite loops. Each minion has 32 general purpose registers, reset at each
        tick before execution.
      </div>

      <div>
        The scoring of each game is based on the death order, awarding 1,2,3,4,5,7, and 10 points
        respectively. Moreover, 5 extra points are awarded for each kill.
      </div>

      <h2>Game features</h2>

      <div>
        The game is played on a square map. Each player is assigned a summoner on the map. You can
        only see a portion of the map nearby your summoner. Some minions provide extra vision.
        Everything else is hidden under a fog of war. You can only see where a lake or lava pond is.
        The map contains impassable terrain, represented as mountains, lava and water lakes. Some
        minions can tear down and build mountains, while other can traverse multiple terrains.
        Moreover, the map contains bushes, hiding you from distant enemies. All other tiles are
        sand.
      </div>

      <h2>Updating a bot</h2>

      <div>
        The bot provided is not very smart, it just moves, pushes and punches randomly, occasionally
        summoning a random minion doing random movements. You can upgrade it by editing bot.py:
      </div>

      <ul>
        <li>At every tick the function move is called, with as data the map information. </li>
        <li>
          Your code should answer with one of the four already provided JSONs, properly filled.
        </li>
      </ul>

      <div>
        You can test that your code works by running the provided docker-compose. The configuration
        fi le can be optionally changed to override some settings to ease development or test
        features.
      </div>

      <div>Feel free to decompile and read the code, it may contain useful vulnerabilities :)</div>

      <div>
        The results stored in result are a map.json and a scoreboard.json file, alongside the
        outputs of your minions. You can upload map.json to the web app to see the game.
      </div>

      <div>
        When your bot is ready you can now build, tag and push your image to the provided
        repository, you can see an example on how to do that in push.sh.template Be sure to first
        log into the remote docker registry. Keep in mind that remotely you are limited to 2 cpus
        and 2gb of ram!
      </div>

      <div>
        By logging in on the web interface you can see the output of your bot and of the "Log"
        instructions for debugging remotely.
      </div>

      <div>
        You are free to write your bot in any language you want, as long as it accepts websocket
        connections correctly and answers in the right way. Specifically it should:
      </div>

      <ul>
        <li>Listen and accept incoming websocket connections from the backend on port 5000.</li>
        <li>
          Receive gzip compressed messages from the backend of two kinds:
          <ul>
            <li>
              A "game-start" JSON message containing your player ID, the new game status being
              start, and your player name
            </li>
            <li>
              A "map" JSON message, containing the encoded map as an array of columns where each
              entry is an array of tiles. You can access to the tile at coordinate x,y by accessing
              map[x][y]. Each tile is a JSON object containing a "background" key with an integer
              value, referring to the background type, an "entity" key which may be null or may
              contain a JSON object with a "type" key, which may have value "summoner" or
              "VMEntity". In case of value "summoner" the additional keys "id", "life", and "mana"
              are present. In case of value "VMEntity" the additional keys "VMType", "id",
              "summonerID", and "life" are present.
            </li>
          </ul>
        </li>
        <li>
          Send JSON encoded messages as a response after at most 100ms from receiving a "map"
          message. Responses should be one of the following:
          <ul>
            <li>
              A JSON object with key "type" and value "move", key "to" and value a JSON array with
              two integer values, representing the nearby destination tile [x][y] or key "direction" 
              and value an integer, representing the direction to move to.
            </li>
            <li>
              A JSON object with key "type" and value "punch", key "to" and value a JSON array with
              two integer values, representing the nearby destination tile [x][y] or key "direction" 
              and value an integer, representing the direction to punch towards.
            </li>
            <li>
              A JSON object with key "type" and value "push", key "to" and value a JSON array with
              two integer values, representing the nearby destination tile [x][y] or key "direction" 
              and value an integer, representing the direction to push towards.
            </li>
            <li>
              A JSON object with key "type" and value "summon", key "to" and value a JSON array with
              two integer values, representing the nearby destination tile [x][y] or key "direction" 
              and value an integer, representing the direction to summon towards., key "entityType" and value an
              integer, representing the entity to summon, key "code" and value a JSON object
              containing the code as returned by the compiler.
            </li>
          </ul>
        </li>
      </ul>

      <div>
        You can find, in the compiler directory, a simple utility to create shellcodes for your
        minions, alongside an example for random movements.
      </div>
      {/* <div>
        I suggest reading the rules on the web page as those contains every detail of the game,
        altought in a more "cryptic" fashion.
      </div> */}
    </div>
  );
};

export default HowTo;
