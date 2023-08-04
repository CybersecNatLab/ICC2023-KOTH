export enum TileBackground {
  Empty,
  Mountain,
  Water,
  Bush,
  Lava
}

export enum VMEntityType {
  Dragon = 1,
  Mole,
  Cat,
  Spectre,
  MagicArrow,
  Wolf,
  Mouse,
  Armadillo
}

export const VMEntityIcons: { [Entity in VMEntityType]: string } = {
  [VMEntityType.Dragon]: '/sprites/dragon.png',
  [VMEntityType.Mole]: '/sprites/mole.png',
  [VMEntityType.Cat]: '/sprites/cat.png',
  [VMEntityType.Spectre]: '/sprites/spectre.png',
  [VMEntityType.MagicArrow]: '/sprites/arrow.png',
  [VMEntityType.Wolf]: '/sprites/werewolf.png',
  [VMEntityType.Mouse]: '/sprites/rat.png',
  [VMEntityType.Armadillo]: '/sprites/armadillo.png'
};

export const VMEntityNames: { [Entity in VMEntityType]: string } = {
  [VMEntityType.Dragon]: 'dragon',
  [VMEntityType.Mole]: 'mole',
  [VMEntityType.Cat]: 'cat',
  [VMEntityType.Spectre]: 'spectre',
  [VMEntityType.MagicArrow]: 'magic arrow',
  [VMEntityType.Wolf]: 'wolf',
  [VMEntityType.Mouse]: 'mouse',
  [VMEntityType.Armadillo]: 'armadillo'
};

export type Tile = {
  entity?: SummonerEntity | VMEntity;
  background: TileBackground;
};

export type SummonerEntity = {
  type: 'summoner';
  name: string;
  id: string;
  mana: number;
  life: number;
};

export type VMEntity = {
  type: 'VMEntity';
  VMType: VMEntityType;
  id: string;
  summonerID: string;
  life: number;
};

export type Map = {
  type: 'fullMap';
  width: number;
  height: number;
  map: Tile[][];
};

export type TickUpdate = {
  type: 'tick';
  data: {
    newTick: number;
  };
};

export type MoveUpdate = {
  type: 'move';
  data: {
    entityID: string;
    newX: number;
    newY: number;
  };
};

export type DamageUpdate = {
  type: 'damage';
  data: {
    entityID: string;
    damage: number;
    newLife: number;
  };
};

export type AbilityUpdate = {
  type: 'ability';
  data: {
    VMEntityID: string;
    abilityName: 'punch';
    remainingUses: number;
    direction: number;
  };
};

export type SummonerEntityDead = {
  type: 'summonerDeath';
  data: {
    summonerID: string;
  };
};

export type SummonerSummon = {
  type: 'summon';
  data: {
    summonerID: string;
    entityId: string;
    summonX: number;
    summonY: number;
    type: VMEntityType;
    life: number;
  };
};

export type VMEntityDeath = {
  type: 'vmDeath';
  data: {
    entityID: string;
  };
};

export type ManaUsage = {
  type: 'manaUsage';
  data: {
    entityID: string;
    usage: number;
    newMana: number;
  };
};

export type ManaRecovery = {
  type: 'manaRecovery';
  data: {
    entityID: string;
    amount: number;
    newMana: number;
  };
};

export type LifeRecovery = {
  type: 'lifeRecovery';
  data: {
    entityID: string;
    amount: number;
    newLife: number;
  };
};

export type ZoneReduce = {
  type: 'zoneReduce';
  data: {
    leftMistWidth: number;
    rightMistWidth: number;
    topMistWidth: number;
    bottomMistWidth: number;
  };
};

export type ScoreboardChange = {
  type: 'scoreboard';
  data: {
    summonerID: string;
    score: number;
  };
};

export type BackgroundTileChange = {
  type: 'bgTile';
  data: {
    newBgTile: TileBackground;
    x: number;
    y: number;
  };
};

export type Update =
  | MoveUpdate
  | TickUpdate
  | DamageUpdate
  | AbilityUpdate
  | SummonerEntityDead
  | SummonerSummon
  | VMEntityDeath
  | ManaUsage
  | ManaRecovery
  | LifeRecovery
  | ZoneReduce
  | ScoreboardChange
  | BackgroundTileChange;

export const deserializeMapWithUpdates = (encodedData: string): [Map, Update[]] => {
  const mapWithUpdates = JSON.parse(encodedData);
  return [mapWithUpdates[0], mapWithUpdates.slice(1)];
};
