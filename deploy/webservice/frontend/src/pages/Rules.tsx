import React from 'react';
import './Rules.css';

const Rules = () => {
  return (
    <div className="rules">
      <h1>GAME RULES</h1>
      <h2>The game</h2>

      <div>
        Within these pages lies an ancient tome transcribing a venerable rite cherished by our
        people throughout the eons. Aspiring summoners, yearning for enlightenment, engage in a
        grand contest, vying against each other to transcend mortal bounds and attain heightened
        consciousness.
      </div>

      <div>Behold, the sacred tenets of this mystical contest are revealed as follows.</div>

      <div>
        Summoners who dare to partake must attune their senses, listening for the beckoning of
        ethereal connections, through which their minds shall journey to the hallowed ritual plane.
        Between each iteration of the ritual, new minds can be forged, molded into existence, and
        imbued into the ethereal storage, known as the "docker registry", aligned exclusively with
        the summoner's essence.
      </div>

      <div>
        The ritual plane itself manifests as a desolate, levitating platform, spanning fourty
        celestial units, fraught with perilous pitfalls and impenetrable terrain. Upon this plane,
        each awakened mind shall traverse, striving to prolong its survival in this realm of mystery
        and wonder. In the pursuit of longevity, one may harness their magical energies to
        outmaneuver their fellow seekers. With each cosmic alignment, a malevolent fog encroaches
        upon the players, prompting them to combat for the dwindling space.
      </div>

      <h3>Terrains</h3>
      <div>
        Across this mystical expanse, the platform exhibits diverse terrain, with each tile holding
        its own arcane attributes:
      </div>
      <table border={1} className="terrains-table">
        <thead>
          <tr>
            <th>Tile</th>
            <th>Description</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>
              <img src="/sprites/desert.png" alt="desert" />
            </td>
            <td>The desert allows all to traverse freely without hindrance.</td>
          </tr>
          <tr>
            <td>
              <img src="/sprites/bush.png" alt="bush" />
            </td>
            <td>
              The bush conceals entities from distant adversaries, shrouding them in obscurity.
            </td>
          </tr>
          <tr>
            <td>
              <img src="/sprites/mountain.png" alt="mountain" />
            </td>
            <td>
              The mountain stands as an unyielding fortress, barring passage to all who dare to
              traverse its majestic peaks.
            </td>
          </tr>
          <tr>
            <td>
              <img src="/sprites/water.png" alt="water" />
            </td>
            <td>
              The water lake acts as an aqueous sanctuary, entrapping summoners who fall within its
              depths, compelling them to forfeit a turn to teleport out.
            </td>
          </tr>
          <tr>
            <td>
              <img src="/sprites/lava.png" alt="lava" />
            </td>
            <td>
              The lava lake serves as an infernal abyss, consuming the essence of any summoner
              unfortunate enough to plunge into its molten embrace.
            </td>
          </tr>
        </tbody>
      </table>
      <h3>Powers</h3>
      <div>
        In the mystical cadence of each cosmic tick, summoners wield their powers, granted four
        choices to shape their fate:
      </div>
      <ul>
        <li>
          To <code>move</code>, swiftly traversing to an adjacent location, seeking advantage or
          retreat.
        </li>
        <li>
          To <code>push</code>, coercing another entity away, casting them into the treacherous
          realms of lava or water.
        </li>
        <li>
          To <code>punch</code>, afflicting a fellow adversary with a measured degree of anguish.
        </li>
        <li>
          To <code>summon</code>, conjuring forth a loyal minion, sworn to heed their call.
        </li>
      </ul>
      <div>
        With each action taken, summoners must beware, for within this plane, their health and mana
        are finite. The latter can be automatically rejuvenated through communion with the void,
        while the former can only be replenished through the act of stealing vitality from an enemy.
        However, this process proves arduous, allowing only meager portions of vitality to be
        restored at a time. A fateful demise befalls any summoner, their life force becoming an
        ephemeral current, flowing towards the last summoner who wrought any form of harm.
      </div>
      <h2>Game key features</h2>
      <ul>
        <li>
          Once awakened, the fog commences its enigmatic dance, beginning to dwindle after the
          passage of 20 in-game ticks. Thereafter, its ethereal form undergoes a metamorphosis every
          10 ticks, shrinking evermore.
        </li>
        <li>
          The summoner's vision extends across 10 tiles, granting them glimpses of the arcane
          tapestry that weaves around them.
        </li>
        <li>
          The fog's malevolent touch inflicts 20 wounds upon any entity it embraces during each
          inexorable tick.
        </li>
        <li>
          The summoner, an aspirant of great power, holds dominion over their corporeal form, with
          life and mana bound by finite limits. A celestial equilibrium blesses them with 100 life
          and 100 mana, both granted at their fullest as the ritual commences.
        </li>
        <li>
          The summoner's mana regenerates 1 mana point for each tick, a humble gift from the void
          itself.
        </li>
        <li>
          The summoner may summon as many minions as they are willing, provided the mana in their
          possession is enough.
        </li>
        <li>
          When the ritual turns lethal, and a summoner's life succumbs to the void, a glimmer of
          hope arises amidst the darkness. 50 life essence returns to the last summoner causing
          harm, alongside 5 additional points at the ritual's end, a testament to their prowess and
          might. But heed this, for the adjudication of death lies not solely upon the player who
          deals the fatal blow. The player who last pushes a foe, driving them into the grasp of the
          gas or the embrace of lava, earns the mantle of executioner and the credit for the
          vanquished foe's demise.
        </li>
      </ul>

      <h2>Scoring</h2>

      <div>
        As the mystic ritual draws to its conclusion, each summoner shall be bestowed with points in
        accordance with the order of their demise. The fortunate soul to stand as the last survivor
        shall be graced with a grand total of 10 points, a testament to their mastery and
        resilience. The penultimate participant, who valiantly clung to existence until the end,
        shall be rewarded with 7 points, a commendable achievement indeed.
      </div>
      <div>
        Following closely, the summoner who met their fate just before the runner-up shall claim 5
        points, a respectable showing in the face of the mystical trials. The one who departed two
        steps before the third shall grasp 4 points, an acknowledgment of their tenacity and
        prowess.
      </div>
      <div>
        Next in line, the summoner who journeyed to the ethereal realm's departure shall be granted
        3 points, an acknowledgment of their courage and spirit. Likewise, the participant who
        succumbed two steps before this stage shall be granted 2 points, a testament to their
        resilience and skill.
      </div>
      <div>
        Lastly, the summoner who embraced their fate just before the two-point achiever shall be
        gifted 1 point, a token of recognition for their valiant efforts.
      </div>
      <div>
        Finally, 5 additional points are provided to the last summoner causing harm as a summoner's
        life succumbs to the void.
      </div>
      <div>
        This is the cosmic ballet of life and death, where mana waxes and wanes, and the fog
        breathes its toxic fury. In the pursuit of victory, may the summoners navigate these arcane
        rules and harness their power to emerge triumphant, transcending mortal bounds and ascending
        to the pinnacle of mystical glory.
      </div>

      <h2>The minions</h2>
      {/************************************/}
      <h3>Armadillo</h3>
      <div>
        <table className="minion-table float-left" border={1}>
          <thead>
            <tr>
              <th colSpan={2}>Armadillo</th>
            </tr>
            <tr>
              <th colSpan={2}>
                <img src="/sprites/armadillo.png" alt="armadillo" width={64} />
              </th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>Health</td>
              <td>200</td>
            </tr>
            <tr>
              <td>Mana cost</td>
              <td>20</td>
            </tr>
            <tr>
              <td>Speed</td>
              <td>1</td>
            </tr>
            <tr>
              <td>Vision radius</td>
              <td>10</td>
            </tr>
          </tbody>
        </table>
        <div>
          The mighty armadillo is a guardian spirit bound by ancient oaths to its venerable owner.
          This mystical creature, with its formidable shell, stands resolute against the onslaught
          of great adversities, shielding its master from harm's way.{' '}
        </div>
      </div>
      {/************************************/}
      <h3>Magic Arrow</h3>
      <div>
        <table className="minion-table float-right" border={1}>
          <thead>
            <tr>
              <th colSpan={2}>Magic Arrow</th>
            </tr>
            <tr>
              <th colSpan={2}>
                <img src="/sprites/arrow.png" alt="arrow" width={64} />
              </th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>Health</td>
              <td>1</td>
            </tr>
            <tr>
              <td>Mana cost</td>
              <td>10</td>
            </tr>
            <tr>
              <td>Speed</td>
              <td>3</td>
            </tr>
            <tr>
              <td>Damage</td>
              <td>25</td>
            </tr>
            <tr>
              <td>Vision radius</td>
              <td>10</td>
            </tr>
          </tbody>
        </table>
        <div>
          Within the enchanted arsenal of mystic weaponry lies the wondrous creation known as the
          magic arrow. Crafted by adept sorcerers, this arcane projectile possesses an extraordinary
          trait: the ability to be guided through the control of its ethereal connections. When
          unleashed by a summoner, it streaks across the firmament with remarkable speed, as if
          blessed by the very winds themselves. However, this swift journey is ephemeral, for the
          arrow quickly loses its velocity, slowing down as it traverses the celestial expanse.{' '}
        </div>{' '}
        <div>
          Yet, a marvel unfurls as the arrow defies the constraints of time and space. Endowed with
          everlasting flight, it can soar tirelessly through the skies, traversing the heavens with
          boundless determination.
        </div>
        <div>
          In its ethereal existence, the magic arrow wields unique potency against airborne
          adversaries. Unfazed by the limitations of terrestrial foes, it possesses the ability to
          strike with unerring precision those who share the realm of flight. When its path
          intersects with that of a fellow entity, it fulfills its purpose and vanishes, leaving
          behind no testament to its mystical might.
        </div>
      </div>
      {/************************************/}
      <h3>Cat</h3>
      <div>
        <table className="minion-table float-left" border={1}>
          <thead>
            <tr>
              <th colSpan={2}>Cat</th>
            </tr>
            <tr>
              <th colSpan={2}>
                <img src="/sprites/cat.png" alt="cat" width={64} />
              </th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>Health</td>
              <td>20</td>
            </tr>
            <tr>
              <td>Mana cost</td>
              <td>20</td>
            </tr>
            <tr>
              <td>Speed</td>
              <td>1</td>
            </tr>
            <tr>
              <td>Vision radius</td>
              <td>10</td>
            </tr>
          </tbody>
        </table>
        <div>
          In the realm of mystical beings, there resides a creature known as the cat, a marvel of
          grace and keen perception. Within the depths of its mesmerizing gaze lies a gift bestowed
          by ancient enchantments: the ability to perceive the presence of the nearest enemy
          summoner, regardless of the expanse that separates them.
        </div>
      </div>
      {/************************************/}
      <h3>Dragon</h3>
      <div>
        <table className="minion-table float-right" border={1}>
          <thead>
            <tr>
              <th colSpan={2}>Dragon</th>
            </tr>
            <tr>
              <th colSpan={2}>
                <img src="/sprites/dragon.png" alt="dragon" width={64} />
              </th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>Health</td>
              <td>1</td>
            </tr>
            <tr>
              <td>Mana cost</td>
              <td>20</td>
            </tr>
            <tr>
              <td>Speed</td>
              <td>1</td>
            </tr>
            <tr>
              <td>Vision radius</td>
              <td>10</td>
            </tr>
          </tbody>
        </table>

        <div>
          The powerful dragon, a majestic force of nature, is endowed with wings that grant it
          mastery over the skies. It soars with grace and grandeur, freely navigating over the most
          treacherous of liquids, unbound by the constraints of land, water, or lava.{' '}
        </div>
        <div>
          From the heights of the skies, the dragon's watchful eyes peer down upon the realm below,
          relaying vital information to its master.{' '}
        </div>
        <div>
          Yet, despite its grandeur and formidable presence, the dragon's fate is inextricably tied
          to an ancient adversary - the magic arrow. A creation of unparalleled mystic might, the
          magic arrow possesses the unique ability to seek out its target with unerring accuracy.
          Swift as a bolt of lightning, the arrow strikes true, and upon meeting the powerful
          dragon, it claims victory with but a single touch. In an instant, the dragon's magnificent
          form is stilled, and it succumbs to the potency of the enchanted projectile.{' '}
        </div>
      </div>
      {/************************************/}
      <h3>Mole</h3>
      <div>
        <table className="minion-table float-left" border={1}>
          <thead>
            <tr>
              <th colSpan={2}>Mole</th>
            </tr>
            <tr>
              <th colSpan={2}>
                <img src="/sprites/mole.png" alt="mole" width={64} />
              </th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>Health</td>
              <td>40</td>
            </tr>
            <tr>
              <td>Mana cost</td>
              <td>20</td>
            </tr>
            <tr>
              <td>Speed</td>
              <td>1</td>
            </tr>
            <tr>
              <td>Vision radius</td>
              <td>10</td>
            </tr>
            <tr>
              <td>Ability</td>
              <td>3x orogeny</td>
            </tr>
          </tbody>
        </table>

        <div>
          As the borrowing mole traverses the land, its movements leave behind a trail of awe and
          wonder. Mountains that once stood as imposing barriers are rendered asunder, their mighty
          forms yielding to the relentless force of the mole's passage. With every stride, it
          reshapes the very landscape, paving the way for its summoner to forge ahead, unhindered by
          the mightiest of obstacles.
        </div>
        <div>
          Yet, the borrowing mole's mastery over the earth does not end with destruction, for it
          wields a sublime art known as orogeny. In the blink of an eye, it can harness the essence
          of the earth, employing its mystic prowess to create new mountains, thrusting them forth
          from the very ground it traverses. These newfound peaks rise with incredible speed,
          forming barriers that may serve as protective fortresses or strategic vantage points for
          its summoner.
        </div>
      </div>
      {/************************************/}
      <h3>Mouse</h3>
      <div>
        <table className="minion-table float-right" border={1}>
          <thead>
            <tr>
              <th colSpan={2}>Mouse</th>
            </tr>
            <tr>
              <th colSpan={2}>
                <img src="/sprites/rat.png" alt="mouse" width={64} />
              </th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>Health</td>
              <td>10</td>
            </tr>
            <tr>
              <td>Mana cost</td>
              <td>20</td>
            </tr>
            <tr>
              <td>Speed</td>
              <td>1</td>
            </tr>
            <tr>
              <td>Vision radius</td>
              <td>10</td>
            </tr>
            <tr>
              <td>Ticks alive</td>
              <td>20</td>
            </tr>
          </tbody>
        </table>

        <div>
          The mouse, a creature of cunning and illusion, possesses the ability to shield its master
          from the watchful eyes of the formidable cat, shrouding them in a veil of deception. In a
          mesmerizing display of mastery over disguise, the mouse takes on the appearance of the
          seen target, diverting the cat's attention from its true quarry.{' '}
        </div>
        <div>
          Yet, the mouse's arcane talent comes with a caveat, for its presence upon the plane is but
          ephemeral. Like a flickering candle in the night, its corporeal form wanes with each
          passing moment. Swiftly, it vanishes from sight, returning to the mystical realms from
          whence it came, leaving the cat to realize the ruse it unwittingly followed.{' '}
        </div>
      </div>
      {/************************************/}
      <h3>Spectre</h3>
      <div>
        <table className="minion-table float-left" border={1}>
          <thead>
            <tr>
              <th colSpan={2}>Spectre</th>
            </tr>
            <tr>
              <th colSpan={2}>
                <img src="/sprites/spectre.png" alt="spectre" width={64} />
              </th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>Health</td>
              <td>10</td>
            </tr>
            <tr>
              <td>Mana cost</td>
              <td>10</td>
            </tr>
            <tr>
              <td>Speed</td>
              <td>1</td>
            </tr>
            <tr>
              <td>Vision radius</td>
              <td>2</td>
            </tr>
          </tbody>
        </table>

        <div>
          Mindless in its existence, the spectre's purpose is confined to ceaseless drifting in one
          direction, an eternal journey without a destination. Yet, within this seemingly simple
          nature lies a profound gift for its summoner.
        </div>
        <div>
          Endowed with the ability to traverse any terrain with effortless grace, the spectre glides
          through mountains and obstacles as if they were mere illusions. Its form passes unhindered
          through all barriers, bestowing its summoner with a small but invaluable area of vision.
        </div>
        <div>
          Though the spectre's movement knows no bounds, its summoner wields limited control over
          its course. The only command the summoner may give is the power to temporarily halt the
          spectre's eternal drift. Upon receiving this directive, the spectre comes to a standstill,
          anchoring itself in its current location.
        </div>
        <div>
          But alas, like a spectral phantom, the spectre's existence is tethered to the confines of
          the map. Once it reaches the edge of this mystical plane, it fades into the enigmatic
          darkness from whence it came, departing the world of the living.
        </div>
      </div>
      {/************************************/}
      <h3>Wolf</h3>
      <div>
        <table className="minion-table float-right" border={1}>
          <thead>
            <tr>
              <th colSpan={2}>Werewolf</th>
            </tr>
            <tr>
              <th colSpan={2}>
                <img src="/sprites/werewolf.png" alt="werewolf" width={64} />
              </th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>Health</td>
              <td>30</td>
            </tr>
            <tr>
              <td>Mana cost</td>
              <td>30</td>
            </tr>
            <tr>
              <td>Speed</td>
              <td>1</td>
            </tr>
            <tr>
              <td>Damage</td>
              <td>25</td>
            </tr>
            <tr>
              <td>Vision radius</td>
              <td>10</td>
            </tr>
            <tr>
              <td>Ability</td>
              <td>3x area damage</td>
            </tr>
            <tr>
              <td>Ability radius</td>
              <td>3</td>
            </tr>
            <tr>
              <td>Ability damage</td>
              <td>40</td>
            </tr>
          </tbody>
        </table>

        <div>
          The wolf stands as a symbol of power and ferocity, a marvel of strength and cunning.
        </div>
        <div>
          This majestic being possesses a striking ability, one that instills fear in the hearts of
          adversaries and leaves a trail of awe in its wake. With a single mighty swipe of its sharp
          claws, the wolf can deal a devastating blow to any foe within its reach.
        </div>
        <div>
          Yet, the wolf's true mastery lies in its ability to unleash a whirlwind of destruction,
          using its fearsome claws to lash out at multiple enemies within its close vicinity. Like a
          tempest of fury, it pummels its foes with relentless assaults, leaving no refuge for those
          who dare stand in its path.
        </div>
      </div>
      <h2>About mind control</h2>
      <div>
        In the mystical tomes of old, the inner workings of summoned minions are veiled in arcane
        complexity. The aspiring summoner must tread cautiously, lest they strain the delicate
        threads that bind these beings to corporeal existence, risking their return to the realm of
        pure energy.
      </div>
      <div>
        Within this grimoire, we impart wisdom on common operations and mystical structures that
        shall empower you to successfully program these ethereal entities.
      </div>
      <div>
        Minions are but pawns, governed by an enchanted compendium of spells, which we shall now
        refer to as "instructions." These incantations are usually woven together in a linear
        fashion, enabling the execution of elaborate enchantments.
      </div>
      <div>
        Every instruction wields power over a limited number of magical repositories, known as
        "registries." The lesser minions, most suitable for fledgling summoners, are bound to 32
        ordinary registries, along with a special one dedicated to tracking differences.
      </div>
      <div>
        The nature of these wondrous registries transcends comprehension, for they can harbor a
        myriad of knowledge - be it whole numbers, known as "integers", fractions, known as
        "floats", true and false values, known as "booleans," and ephemeral vocal messages known as
        "strings". Through delving into the abyssal depths, the summoner can expand these storages
        infinitely, metamorphosing them into enigmatic "random access arrays". Yet, each array may
        contain only a singular type of information.
      </div>
      <div>
        But beware, dear summoner, lest you attempt to extract data from incompatible registries.
        Such folly would undoubtedly unleash a cataclysmic surge of energy, causing the summoned
        minion to vanish into the ether.
      </div>
      <div>
        Behold, there exist esoteric instructions that wield dominion over time itself, enabling the
        execution of complex rituals, including enchanting loops and enigmatic jumps.
      </div>
      <div>
        Lastly, know this - a moment shall arise when the minion is liberated from the bindings of
        its spell and roams unshackled. This liberation comes to pass when three extraordinary
        instructions are cast:
      </div>
      <ul>
        <li>"MoveTo" - compelling it to traverse the mystical planes.</li>
        <li>"UseAbility" - commanding it to wield its magical prowess.</li>
        <li>"DoNothing" - directing it to bide its time in patient repose.</li>
      </ul>
      <h2>Spell list</h2>
      <div>
        In this section, the notation r followed by a capital letter is used to indicate any
        register. For example one may write "r0" to refer to register 0. Moreover, imm stands for
        immediate, referring to a constant value. A notation of "rB/imm" indicates any of a register
        or immediate.
      </div>
      {/******************************************************************/}
      {/******************************************************************/}
      <h3>AddBooleanToArray</h3>
      <div>
        <code>AddBooleanToArray rA, rB/imm </code>: Adds the boolean contained in rB or imm to the
        boolean array contained in rA.
      </div>
      <h3>AddFloatToArray</h3>
      <div>
        <code>AddFloatToArray rA, rB/imm </code>: Adds the float contained in rB or imm to the float
        array contained in rA.
      </div>
      <h3>AddIntegerToArray</h3>
      <div>
        <code>AddIntegerToArray rA, rB/imm </code>: Adds the integer contained in rB or imm to the
        integer array contained in rA.
      </div>
      <h3>AddStringToArray</h3>
      <div>
        <code>AddStringToArray rA, rB/imm </code>: Adds the string contained in rB or imm to the
        string array contained in rA.
      </div>
      <h3>GetFromArray</h3>
      <div>
        <code>GetFromArray rA, rB/imm, rC</code>: Gets the item contained in rA at the index of the
        value contained in rB or imm and places it into rC.
      </div>
      <h3>RemoveFromArray</h3>
      <div>
        <code>RemoveFromArray rA, rB/imm</code>: Removes the item contained in rA at the index of
        the value contained in rB or imm.
      </div>
      <h3>SetBooleanToArray</h3>
      <div>
        <code>SetBooleanToArray rA, rB/imm, rC/imm2</code>: Sets into the boolean array contained in
        rA the value contained in rC or imm2 at the index of the value contained in rB or imm.
      </div>
      <h3>SetFloatToArray</h3>
      <div>
        <code>SetFloatToArray rA, rB/imm, rC/imm</code>: Sets into the float array contained in rA
        the value contained in rC or imm2 at the index of the value contained in rB or imm.
      </div>
      <h3>SetIntegerToArray</h3>
      <div>
        <code>SetIntegerToArray rA, rB/imm, rC/imm</code>: Sets into the integer array contained in
        rA the value contained in rC or imm2 at the index of the value contained in rB or imm.
      </div>
      <h3>SetStringToArray</h3>
      <div>
        <code>SetStringToArray rA, rB/imm, rC/imm</code>: Sets into the string array contained in rA
        the value contained in rC or imm2 at the index of the value contained in rB or imm.
      </div>
      <h3>InitBoolean</h3>
      <div>
        <code>InitBoolean rA, rB/imm</code>: Sets rA to the boolean value contained in rB or imm.
      </div>
      <h3>InitBooleanArray</h3>
      <div>
        <code>InitBooleanArray rA</code>: Sets rA to a new empty boolean array.
      </div>
      <h3>InitFloat</h3>
      <div>
        <code>InitFloat rA, rB/imm</code>: Sets rA to the float value contained in rB or imm.
      </div>
      <h3>InitFloatArray</h3>
      <div>
        <code>InitFloatArray rA</code>: Sets rA to a new empty float array.
      </div>
      <h3>InitInteger</h3>
      <div>
        <code>InitInteger rA, rB/imm</code>: Sets rA to the integer value contained in rB or imm.
      </div>
      <h3>InitIntegerArray</h3>
      <div>
        <code>InitIntegerArray rA</code>: Sets rA to a new empty integer array.
      </div>
      <h3>InitString</h3>
      <div>
        <code>InitString rA, rB/imm</code>: Sets rA to the string value contained in rB or imm.
      </div>
      <h3>InitStringArray</h3>
      <div>
        <code>InitStringArray rA</code>: Sets rA to a new empty string array.
      </div>
      <h3>Add</h3>
      <div>
        <code>Add rA, rB/imm, rC</code>: Sets rC to the sum of the values contained in rA and rB or
        imm.
      </div>
      <h3>Sub</h3>
      <div>
        <code>Sub rA, rB/imm, rC</code>: Sets rC to the subtraction of the values contained in rA
        and rB or imm.
      </div>
      <h3>Mul</h3>
      <div>
        <code>Mul rA, rB/imm, rC</code>: Sets rC to the multiplication of the values contained in rA
        and rB or imm.
      </div>
      <h3>Div</h3>
      <div>
        <code>Div rA, rB/imm, rC</code>: Sets rC to the division of the values contained in rA and
        rB or imm.
      </div>
      <h3>Mod</h3>
      <div>
        <code>Mod rA, rB/imm, rC</code>: Sets rC to the value contained in rA modulus the value
        contained in rB or imm.
      </div>
      <h3>Pow</h3>
      <div>
        <code>Pow rA, rB/imm, rC</code>: Sets rC to the value contained in rA to the power of the
        value contained in rB or imm.
      </div>
      <h3>Sqrt</h3>
      <div>
        <code>Sqrt rA, rB</code>: Sets rB to the square root of the value contained in rA.
      </div>
      <h3>And</h3>
      <div>
        <code>And rA, rB/imm, rC</code>: Sets rC to the value contained in rA ANDed with the value
        contained in rB or imm.
      </div>
      <h3>Or</h3>
      <div>
        <code>Or rA, rB/imm, rC</code>: Sets rC to the value contained in rA ORed with the value
        contained in rB or imm.
      </div>
      <h3>Not</h3>
      <div>
        <code>Not rA, rB</code>: Sets rB to the value contained in rA bitwise negated.
      </div>
      <h3>Xor</h3>
      <div>
        <code>Xor rA, rB/imm, rC</code>: Sets rC to the value contained in rA XORed with the value
        contained in rB or imm.
      </div>
      <h3>Neg</h3>
      <div>
        <code>Neg rA, rB</code>: Sets rB to the value contained in rA negated.
      </div>
      <h3>Shr</h3>
      <div>
        <code>Shr rA, rB/imm, rC</code>: Sets rC to the value contained in rA bitwise shifted right
        of the value contained in rB or imm.
      </div>
      <h3>Shl</h3>
      <div>
        <code>Shl rA, rB/imm, rC</code>: Sets rC to the value contained in rA bitwise shifted left
        of the value contained in rB or imm.
      </div>
      <h3>Sar</h3>
      <div>
        <code>Sar rA, rB/imm, rC</code>: Sets rC to the value contained in rA arithmetically shifted
        right of the value contained in rB or imm.
      </div>
      <h3>Ceil</h3>
      <div>
        <code>Ceil rA, rB</code>: Sets rB to the value contained in rA approximated by excess.
      </div>
      <h3>Floor</h3>
      <div>
        <code>Floor rA, rB</code>: Sets rB to the value contained in rA approximated by defect.
      </div>
      <h3>Round</h3>
      <div>
        <code>Round rA, rB</code>: Sets rB to the value contained in rA approximated to the nearest
        integer.
      </div>
      <h3>Mov</h3>
      <div>
        <code>Mov rA, rB</code>: Sets rB to the value contained in rA.
      </div>
      <h3>Rnd</h3>
      <div>
        <code>Rnd rA/imm, rB/imm2, rC</code>: Sets rC to a random value included between the value
        contained in rA or imm and the value contained in rB or imm2.
      </div>
      <h3>IsNull</h3>
      <div>
        <code>IsNull rA</code>: Sets the compare register to 1 if the registry rA contains a null
        value.
      </div>
      <h3>GetTileInformation</h3>
      <div>
        <code>GetTileInformation rA/imm, rB/imm2, rC, rD, rE, rF, rG, rH</code>: Loads the map
        information at the tile with X coordinate the value contained in rA or imm and with Y
        coordinate the value contained in rB or imm2. Subsequently, it sets the following values:
        <ul>
          <li>rC to the type of background at that location</li>
          <li>rD to a boolean flag indicating the isDamaging property of that location</li>
          <li>rE to the type of entity at that location or null if no entity is present</li>
          <li>rF to the UUID of the entity at that location or null if no entity is present</li>
          <li>rG to the life of the entity at that location or null if no entity is present</li>
          <li>
            rH to the UUID of the owner of the entity at that location or null if the entity has no
            owner of if no entity is present
          </li>
        </ul>
      </div>
      <h3>GetOwnInformation</h3>
      <div>
        <code>GetOwnInformation rA, rB, rC, rD</code>: It sets the following values:
        <ul>
          <li>rA to the current X location of the entity</li>
          <li>rB to the current Y location of the entity</li>
          <li>rC to the current life of the entity</li>
          <li>rD to the amount of ability uses left of the entity</li>
        </ul>
      </div>
      <h3>JMP</h3>
      <div>
        <code>JMP rA/imm</code>: Sets the instruction pointer to the value contained in rA or imm.
      </div>
      <h3>JMPEQ0</h3>
      <div>
        <code>JMPEQ0 rA/imm</code>: If the compare registry contains 0 it sets the instruction
        pointer to the value contained in rA or imm.
      </div>
      <h3>JMPNEQ0</h3>
      <div>
        <code>JMPNEQ0 rA/imm</code>: If the compare registry does not contain 0 it sets the
        instruction pointer to the value contained in rA or imm.
      </div>
      <h3>JMPL0</h3>
      <div>
        <code>JMPL0 rA/imm</code>: If the compare registry contains a number lower than 0 it sets
        the instruction pointer to the value contained in rA or imm.
      </div>
      <h3>JMPG0</h3>
      <div>
        <code>JMPG0 rA/imm</code>: If the compare registry contains a number greater than 0 it sets
        the instruction pointer to the value contained in rA or imm.
      </div>
      <h3>CMP</h3>
      <div>
        <code>CMP rA, rB/imm</code>: Sets the compare registry to the difference between the value
        contained in rA and the value contained in rB or imm.
      </div>
      <h3>MoveTo</h3>
      <div>
        <code>MoveTo rA/imm, rB/imm2</code>: Instructs the entity to move towards the location with
        X coordinate the value contained in rA or imm and with Y coordinate the value contained in
        rB or imm2. Note that you can provide as destination any tile and the path, if existing,
        will be automatically calculated.
        Once this instruction is executed the program returns.
      </div>
      <h3>UseAbility</h3>
      <div>
        <code>UseAbility rA/imm</code>: Instructs the entity to use its ability with parameter the
        value contained in rA or imm. Specifically, the wolf can use this instruction to use its
        area damage ability, for which no parameter is necessary. The mole can use this instruction
        to use orogeny, with as a parameter an integer value indicating a direction.
        Once this instruction is executed the program returns.
      </div>
      <h3>DoNothing</h3>
      <div>
        <code>DoNothing</code>: Instructs the entity to do nothing.
        Once this instruction is executed the program returns.
      </div>
      <h3>Log</h3>
      <div>
        <code>Log rA/imm</code>: Logs the value contained in rA or imm to the web interface.
      </div>
      {/******************************************************************/}
      {/******************************************************************/}
      <h2>Appendix A</h2>
      <div className="rune">
        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin in tellus in diam convallis
        viverra. Integer nec risus est. Sed tristique vestibulum varius. Aliquam et risus sagittis,
        semper dolor id, rutrum augue. Phasellus ultricies risus neque, ac suscipit velit commodo
        id. Nam non nisl leo. In volutpat diam sit amet laoreet sodales. Phasellus id magna eget
        magna convallis blandit. Cras malesuada diam diam, vel tempus ipsum blandit vel. Sed
        efficitur, augue non congue mattis, arcu felis feugiat enim, sed tincidunt sem risus ac
        eros. Ut sem ex, convallis id dolor et, efficitur congue neque.
      </div>
      <div className="rune opacity-60">
        Morbi est arcu, cursus id gravida ac, aliquet ac magna. Proin blandit hendrerit placerat.
        Nam id sodales justo. Integer tincidunt lectus tortor, a porta neque volutpat ac. Donec sed
        elementum sem, vel pulvinar velit. Integer ac lacus vel quam rutrum eleifend. Suspendisse
        volutpat lorem sit amet posuere ultricies. Curabitur id facilisis ligula. Aenean dignissim
        tellus nisl. Fusce at elementum sapien.
      </div>
      <div className="rune opacity-40">
        Etiam id venenatis risus. Suspendisse in turpis neque. Donec sapien nibh, tempor venenatis
        massa semper, ultricies vestibulum risus. Praesent molestie, lorem non pulvinar dapibus,
        lacus metus ornare mi, non auctor turpis eros mattis leo. Integer nec dolor mollis,
        malesuada nisi eu, rutrum quam. Cras imperdiet placerat elit, quis vestibulum lorem ornare
        vel. Aliquam erat volutpat. Curabitur in nisl viverra, consectetur risus at, varius leo.
        Praesent enim neque, rhoncus sed suscipit sit amet, mollis sed lacus. Fusce blandit leo
        facilisis, mollis dolor ut, sodales nunc. Vestibulum at efficitur magna. Praesent vitae
        ullamcorper magna, at maximus leo. Quisque placerat massa vulputate, tempor augue vel,
        cursus orci. Phasellus sed fringilla lacus.
      </div>
      <div className="rune opacity-20">
        Proin vulputate tempor lacus, ac tempus tortor faucibus sed. Donec fringilla imperdiet
        metus, nec maximus arcu dictum id. Curabitur volutpat orci lectus, sit amet euismod tellus
        dignissim a. Etiam pellentesque metus ac orci rutrum, non sodales risus consectetur. Proin a
        libero sit amet tortor rutrum pulvinar sed sit amet massa. Sed a est eu lectus consectetur
        malesuada vel et sapien. Fusce egestas fringilla libero et dignissim. Mauris luctus quam et
        pellentesque dignissim. Interdum et malesuada fames ac ante ipsum primis in faucibus.
      </div>
      <div className="rune opacity-10">
        Cras varius metus at massa sodales, id finibus erat tincidunt. Nam ullamcorper nulla vel
        justo bibendum, in gravida diam placerat. Integer facilisis, leo vel facilisis posuere, orci
        nulla eleifend ex, ac pulvinar tortor enim id libero. Phasellus pellentesque velit vitae
        ultrices semper. Morbi mattis porta ante et rutrum. Duis commodo rutrum aliquam. Cras eu
        imperdiet sapien.
      </div>
    </div>
  );
};

export default Rules;
