# Vulnerabilities writeups

In total the game contains 5 vulnerabilities and 2 quirks which can be used to obtain advantages.

## Game quirks

The issues reported here are not real vulnerabilities but intended undocumented game designs which could be abused to improve your bot.

### Registry r0 not resetted between rounds

The registry r0 is not resetted between rounds, allowing you to build more complex and stateful shellcodes for your minions.

### A\* algorithm ignoring of vision radius

The A\* implementation in the game doesn't take care of the vision radius and can be abused to find the shortest path in unseen areas of the map. For example, one could summon a minion moving towards a location and simply follow it.

## Vulnerabilities

The issues reported here are real vulnerabilities, unintended behaviours in the game logic.

### Entity push allows for deleting other entities

When an entity is pushed a check is missing, allowing for it to be pushed on top of another entity.
When the now deleted entity is processed a sanity check is performed to verify its existence in the map. Since it is not found it gets killed. During the killing process the entity reference in the map is set to null. This causes the pushed entity to be deleted and killed too.

This can be abused to instantly kill any entity, including any summoner. It has to be noted that if a summoner is killed by having another entity pushed on top the kill may be awarded to the wrong player as the reference is never updated.

### Entity push into lava allows for killing summoner

Pushing a VEentity into lava allows for killing its summoner instead due to a logic error where the summoner entity is targeted instead of the VMEntity.

### Entity summon out of bounds

An entity may be summoned one tile outside the map and will be summoned on the other side. This can only be abused at the start of the game before the fog closes in.
For example, it may be used to gain vision on the other side of the map or deal damage to a distant player.

### Orogeny can summon mountains over entities

The mole's ability orogeny is used to summon mountain tiles. It misses the check for an entity present on that tile, allowing for mountains to be summoned behind existing entities.
This has the following effects:

- If a mountain is summoned behind a VMEntity such entity won't be able to move as the pathfinding algorithm breaks. This does not apply to spectres and moles as they can freely walk through mountains.
- If a mountain is summoned behind a SummonerEntity/VMEntity it becomes intargettable by the MagicArrow and the wolf's basic attack, as the pathfinding is needed for those to work. It stays targettable by the wolf's special attack, the summoner's punch and push.

This can be used to block other player's entities or defend your summoner.

Yes, you can [pwn the mole](https://twitter.com/pwnthem0le).

### Remote punch allows for kill-stealing in certain circumstances

When a punch action is triggered using the "to" parameter the summoner can specify any target tile. However, the game incorrectly updates the "lastHitBy" property before checking if the punch is valid.
This allows for "stealing" a kill if a player is about to die from either the fog or lava. Note that it can't be used to steal a kill from entity damage as when damage from any entity is applied the "lastHitBy" property is correctly updated and immediately checked.
Finally, this vulnerability can be used to avoid giving points to other players when dying from the fog as one can "punch" themselves and update the property. This does not allow for giving points to yourself as a sanity check is made.
