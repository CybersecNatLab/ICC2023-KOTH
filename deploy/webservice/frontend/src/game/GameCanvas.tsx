import React, { FunctionComponent, useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { Container, Graphics, Sprite, Stage } from '@pixi/react';
import { Map, SummonerEntity, TileBackground, Update, VMEntity, VMEntityType } from './map';
import {
  associateNamesWithColors,
  Color,
  colorToInt,
  Direction,
  loadSprites,
  Sprites
} from './sprites';
import { useNavigate } from 'react-router-dom';
import { Texture } from 'pixi.js';

const EntitiesMaxLife: { [Type in VMEntityType]: number } = {
  [VMEntityType.Dragon]: 1,
  [VMEntityType.Mole]: 40,
  [VMEntityType.Cat]: 20,
  [VMEntityType.Spectre]: 10,
  [VMEntityType.MagicArrow]: 1,
  [VMEntityType.Wolf]: 30,
  [VMEntityType.Mouse]: 10,
  [VMEntityType.Armadillo]: 200
};

type StateEntity = {
  x: number;
  y: number;
  dir: Direction;
  color: Color;
  tick: number;
  entity: VMEntity | SummonerEntity;
};

type State = {
  index: number;
  tick: number;
  zone: {
    left: number;
    right: number;
    top: number;
    bottom: number;
  };
  background: TileBackground[][];
  entities: { [id: string]: StateEntity };
};

const GameContainer: FunctionComponent<{
  initialState: State;
  updates: Update[];
  updateIndex: number;
  tileSize: number;
  mapWidth: number;
  mapHeight: number;
  sprites: Sprites;
}> = ({ initialState, updates, updateIndex, tileSize, mapWidth, mapHeight, sprites }) => {
  const [state, setState] = useState<State>(JSON.parse(JSON.stringify(initialState)));

  useEffect(
    () => {
      if (updateIndex === -1) {
        setState(JSON.parse(JSON.stringify(initialState)));
        return;
      } else if (updateIndex >= updates.length) {
        return;
      }

      const newState = { ...state };
      if (updateIndex < newState.index) {
        console.warn('Update index is lower than current state index, expect inconsistencies');
      }

      console.debug('Update from', newState.index + 1, 'to', updateIndex);
      for (let i = newState.index + 1; i <= updateIndex; i++) {
        const update = updates[i];
        if (update.type === 'move') {
          const entity = newState.entities[update.data.entityID];
          if (!entity) {
            console.warn(`Move update on non-existing entity ${update.data.entityID}`);
            continue;
          }

          if (update.data.newX > entity.x) entity.dir = Direction.Right;
          else if (update.data.newX < entity.x) entity.dir = Direction.Left;
          else if (update.data.newY > entity.y) entity.dir = Direction.Down;
          else entity.dir = Direction.Up;

          entity.tick++;

          entity.x = update.data.newX;
          entity.y = update.data.newY;
        } else if (update.type === 'tick') {
          newState.tick = update.data.newTick;
        } else if (update.type === 'summonerDeath') {
          delete newState.entities[update.data.summonerID];
        } else if (update.type === 'summon') {
          const summoner = newState.entities[update.data.summonerID];
          if (!summoner) {
            console.warn(`Summon by non-existing summoner ${update.data.summonerID}`);
            continue;
          }

          newState.entities[update.data.entityId] = {
            x: update.data.summonX,
            y: update.data.summonY,
            tick: 1,
            color: summoner.color,
            dir: (() => {
              if (update.data.summonX > summoner.x) return Direction.Right;
              else if (update.data.summonX < summoner.x) return Direction.Left;
              else if (update.data.summonY > summoner.y) return Direction.Down;
              else return Direction.Up;
            })(),
            entity: {
              type: 'VMEntity',
              VMType: update.data.type,
              id: update.data.entityId,
              summonerID: update.data.summonerID,
              life: update.data.life
            }
          };
        } else if (update.type === 'vmDeath') {
          delete newState.entities[update.data.entityID];
        } else if (update.type === 'manaUsage') {
          const entity = newState.entities[update.data.entityID];
          if (!entity) {
            console.warn(`Mana usage on non-existing entity ${update.data.entityID}`);
            continue;
          } else if (entity.entity.type !== 'summoner') {
            console.warn(`Mana usage on non-summoner entity ${update.data.entityID}`);
            continue;
          }

          entity.entity.mana = update.data.newMana;
        } else if (update.type === 'manaRecovery') {
          const entity = newState.entities[update.data.entityID];
          if (!entity) {
            console.warn(`Mana recovery on non-existing entity ${update.data.entityID}`);
            continue;
          } else if (entity.entity.type !== 'summoner') {
            console.warn(`Mana recovery on non-summoner entity ${update.data.entityID}`);
            continue;
          }

          entity.entity.mana = update.data.newMana;
        } else if (update.type === 'lifeRecovery') {
          const entity = newState.entities[update.data.entityID];
          if (!entity) {
            console.warn(`Life recovery on non-existing entity ${update.data.entityID}`);
            continue;
          } else if (entity.entity.type !== 'summoner') {
            console.warn(`Life recovery on non-summoner entity ${update.data.entityID}`);
            continue;
          }

          entity.entity.life = update.data.newLife;
        } else if (update.type === 'damage') {
          const entity = newState.entities[update.data.entityID];
          if (!entity) {
            console.warn(`Damage on non-existing entity ${update.data.entityID}`);
            continue;
          }

          entity.entity.life = update.data.newLife;
        } else if (update.type === 'zoneReduce') {
          newState.zone = {
            left: update.data.leftMistWidth,
            right: update.data.rightMistWidth,
            top: update.data.topMistWidth,
            bottom: update.data.bottomMistWidth
          };
        } else if (update.type === 'bgTile') {
          const newBackground = [...newState.background];
          newBackground[update.data.y][update.data.x] = update.data.newBgTile;
          newState.background = newBackground;
        } else if (update.type === 'scoreboard') {
          // ignored
        } else {
          console.warn('Unhandled update', update);
        }

        newState.index = i;
      }

      setState(newState);
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [initialState, updates, updateIndex]
  );

  const makeSprites = useCallback(
    (x: number, y: number, textures: Texture[], key: string, alpha: number) => {
      if (textures.length === 1) {
        if (key === 'bush') {
          return (
            <Sprite
              texture={textures[0]}
              alpha={alpha}
              key={`${x},${y},${key}0`}
              width={tileSize - 2}
              height={tileSize - 2}
              x={x * tileSize + 1}
              y={y * tileSize + 1}
            />
          );
        } else {
          return (
            <Sprite
              texture={textures[0]}
              alpha={alpha}
              key={`${x},${y},${key}0`}
              width={tileSize}
              height={tileSize}
              x={x * tileSize}
              y={y * tileSize}
            />
          );
        }
      } else if (textures.length === 4) {
        return [
          // Row 0
          <Sprite
            texture={textures[0]}
            alpha={alpha}
            key={`${x},${y},${key}0`}
            width={tileSize / 2}
            height={tileSize / 2}
            x={x * tileSize}
            y={y * tileSize}
          />,
          <Sprite
            texture={textures[1]}
            alpha={alpha}
            key={`${x},${y},${key}1`}
            width={tileSize / 2}
            height={tileSize / 2}
            x={x * tileSize + tileSize / 2}
            y={y * tileSize}
          />,
          <Sprite
            texture={textures[2]}
            alpha={alpha}
            key={`${x},${y},${key}2`}
            width={tileSize / 2}
            height={tileSize / 2}
            x={x * tileSize}
            y={y * tileSize + tileSize / 2}
          />,
          <Sprite
            texture={textures[3]}
            alpha={alpha}
            key={`${x},${y},${key}3`}
            width={tileSize / 2}
            height={tileSize / 2}
            x={x * tileSize + tileSize / 2}
            y={y * tileSize + tileSize / 2}
          />
        ];
      } else if (textures.length === 9) {
        return [
          // Row 0
          <Sprite
            texture={textures[0]}
            alpha={alpha}
            key={`${x},${y},${key}0`}
            width={tileSize / 3}
            height={tileSize / 3}
            x={x * tileSize}
            y={y * tileSize}
          />,
          <Sprite
            texture={textures[1]}
            alpha={alpha}
            key={`${x},${y},${key}1`}
            width={tileSize / 3}
            height={tileSize / 3}
            x={x * tileSize + tileSize / 3}
            y={y * tileSize}
          />,
          <Sprite
            texture={textures[2]}
            alpha={alpha}
            key={`${x},${y},${key}2`}
            width={tileSize / 3}
            height={tileSize / 3}
            x={x * tileSize + (tileSize * 2) / 3}
            y={y * tileSize}
          />,
          // Row 1
          <Sprite
            texture={textures[3]}
            alpha={alpha}
            key={`${x},${y},${key}3`}
            width={tileSize / 3}
            height={tileSize / 3}
            x={x * tileSize}
            y={y * tileSize + tileSize / 3}
          />,
          <Sprite
            texture={textures[4]}
            alpha={alpha}
            key={`${x},${y},${key}4`}
            width={tileSize / 3}
            height={tileSize / 3}
            x={x * tileSize + tileSize / 3}
            y={y * tileSize + tileSize / 3}
          />,
          <Sprite
            texture={textures[5]}
            alpha={alpha}
            key={`${x},${y},${key}5`}
            width={tileSize / 3}
            height={tileSize / 3}
            x={x * tileSize + (tileSize * 2) / 3}
            y={y * tileSize + tileSize / 3}
          />,
          // Row 2
          <Sprite
            texture={textures[6]}
            alpha={alpha}
            key={`${x},${y},${key}6`}
            width={tileSize / 3}
            height={tileSize / 3}
            x={x * tileSize}
            y={y * tileSize + (tileSize * 2) / 3}
          />,
          <Sprite
            texture={textures[7]}
            alpha={alpha}
            key={`${x},${y},${key}7`}
            width={tileSize / 3}
            height={tileSize / 3}
            x={x * tileSize + tileSize / 3}
            y={y * tileSize + (tileSize * 2) / 3}
          />,
          <Sprite
            texture={textures[8]}
            alpha={alpha}
            key={`${x},${y},${key}8`}
            width={tileSize / 3}
            height={tileSize / 3}
            x={x * tileSize + (tileSize * 2) / 3}
            y={y * tileSize + (tileSize * 2) / 3}
          />
        ];
      } else {
        throw new Error(`Invalid textures for ${x},${y}: ${textures.length}`);
      }
    },
    [tileSize]
  );

  const poisonTiles = useMemo(() => {
    const poisonTiles: [number, number][] = [];

    for (let x = 0; x < mapWidth; x++) {
      for (let y = 0; y < mapHeight; y++) {
        if (
          x >= state.zone.left &&
          y >= state.zone.top &&
          x <= mapWidth - state.zone.right - 1 &&
          y <= mapHeight - state.zone.bottom - 1
        ) {
          continue;
        }

        poisonTiles.push([x, y]);
      }
    }

    return poisonTiles;
  }, [state.zone, mapWidth, mapHeight]);

  const [backgroundTiles, bushTiles] = useMemo(() => {
    if (!sprites) {
      return [[], []];
    }

    const backgroundTextures: Texture[][][] = [],
      bushTextures: Texture[][][] = [];
    for (let x = 0; x < mapWidth; x++) {
      backgroundTextures[x] = [];
      bushTextures[x] = [];

      for (let y = 0; y < mapHeight; y++) {
        const getTexture = (tile: TileBackground) => {
          const texture = sprites.background(x, y, tile, state.background);
          if (!texture) {
            throw new Error(`Unknown texture for ${x},${y}: ${tile}`);
          }

          return texture;
        };

        const bg = state.background[y][x];
        if (bg === TileBackground.Bush) {
          bushTextures[x][y] = getTexture(TileBackground.Bush);
          backgroundTextures[x][y] = getTexture(TileBackground.Empty);
        } else {
          backgroundTextures[x][y] = getTexture(bg);
        }
      }
    }

    return [backgroundTextures, bushTextures];
  }, [state.background, sprites, mapWidth, mapHeight]);

  const [entityTiles, barTiles] = useMemo<
    [
      { x: number; y: number; id: string; color?: Color; texture: Texture }[],
      { type: 'life' | 'mana'; max: number; value: number; x: number; y: number }[]
    ]
  >(() => {
    if (!sprites) {
      return [[], []];
    }

    return [
      Object.values(state.entities).map((entity) => {
        let texture: Texture, color: Color | undefined;
        if (entity.entity.type === 'summoner') {
          texture = sprites.summoner(entity.tick, entity.dir, entity.color);
          if (!texture) {
            throw new Error(
              `Unknown texture for ${entity.x},${entity.y}: summoner, color ${entity.color}`
            );
          }

          color = undefined;
        } else if (entity.entity.type === 'VMEntity') {
          texture = sprites.entity(entity.tick, entity.dir, entity.color, entity.entity.VMType);
          if (!texture) {
            throw new Error(`Unknown texture for ${entity.x},${entity.y}: ${entity.entity.VMType}`);
          }

          color = entity.color;
        } else {
          throw new Error('Invalid entity type');
        }

        return {
          x: entity.x,
          y: entity.y,
          id: entity.entity.id,
          color,
          texture
        };
      }),
      Object.values(state.entities).flatMap<{
        type: 'life' | 'mana';
        max: number;
        value: number;
        x: number;
        y: number;
      }>((entity) => {
        if (entity.entity.type === 'summoner') {
          return [
            {
              type: 'life',
              x: entity.x,
              y: entity.y,
              value: entity.entity.life,
              max: 100
            },
            {
              type: 'mana',
              x: entity.x,
              y: entity.y,
              value: entity.entity.mana,
              max: 100
            }
          ];
        } else if (entity.entity.type === 'VMEntity') {
          return [
            {
              type: 'life',
              x: entity.x,
              y: entity.y,
              value: entity.entity.life,
              max: EntitiesMaxLife[entity.entity.VMType]
            }
          ];
        } else {
          throw new Error('Unknown entity type');
        }
      })
    ];
  }, [state, sprites]);

  return (
    <Container>
      <Container>
        {useMemo(
          () =>
            backgroundTiles.map((col, x) =>
              col.map((tile, y) => {
                if (!tile) {
                  return undefined;
                }

                return makeSprites(x, y, tile, 'background', 1);
              })
            ),
          [backgroundTiles, makeSprites]
        )}
      </Container>

      {useMemo(
        () =>
          entityTiles.map(({ x, y, id, texture, color }) => {
            const tiles = [
              <Sprite
                texture={texture}
                key={id}
                width={tileSize}
                height={tileSize}
                x={x * tileSize}
                y={y * tileSize}
              />
            ];
            if (color !== undefined) {
              const tileUnit = tileSize / 32;
              tiles.push(
                <Graphics
                  key={`${id},color`}
                  draw={(g) => {
                    g.beginFill(colorToInt(color));
                    g.drawCircle(-8 * tileUnit, -5 * tileUnit, tileUnit * 6);
                    g.endFill();
                  }}
                  x={x * tileSize}
                  y={y * tileSize}
                />
              );
            }

            return tiles;
          }),
        [entityTiles, tileSize]
      )}

      {useMemo(
        () =>
          barTiles.map(({ type, max, value, x, y }) => {
            const [emptyBar, barContent] = sprites.bar(type);

            const tileUnit = tileSize / 32,
              barHeight = tileUnit * 5;
            if (type === 'life') {
              return [
                <Sprite
                  key={`${x},${y},life0`}
                  texture={emptyBar}
                  width={tileSize}
                  height={barHeight}
                  x={x * tileSize}
                  y={y * tileSize - barHeight - 2 * tileUnit}
                />,
                <Sprite
                  key={`${x},${y},life1`}
                  texture={barContent}
                  width={tileSize * (value / max)}
                  height={barHeight}
                  x={x * tileSize}
                  y={y * tileSize - barHeight - 2 * tileUnit}
                />
              ];
            } else if (type === 'mana') {
              return [
                <Sprite
                  key={`${x},${y},mana0`}
                  texture={emptyBar}
                  width={tileSize}
                  height={barHeight}
                  x={x * tileSize}
                  y={y * tileSize - 2 * barHeight - 4 * tileUnit}
                />,
                <Sprite
                  key={`${x},${y},mana1`}
                  texture={barContent}
                  width={tileSize * (value / max)}
                  height={barHeight}
                  x={x * tileSize}
                  y={y * tileSize - 2 * barHeight - 4 * tileUnit}
                />
              ];
            } else {
              throw new Error(`Unknown bar type: ${type}`);
            }
          }),
        [barTiles, tileSize, sprites]
      )}

      {useMemo(
        () =>
          bushTiles.map((col, x) =>
            col.map((tile, y) => {
              if (!tile) {
                return undefined;
              }

              return makeSprites(x, y, tile, 'bush', 0.8);
            })
          ),
        [bushTiles, makeSprites]
      )}

      {useMemo(
        () =>
          poisonTiles.map(([x, y]) => (
            <Sprite
              texture={sprites.poison(x, y)}
              alpha={0.8}
              key={`${x},${y},poison`}
              width={tileSize}
              height={tileSize}
              x={x * tileSize}
              y={y * tileSize}
            />
          )),
        [poisonTiles, tileSize, sprites]
      )}
    </Container>
  );
};

const GameCanvas: FunctionComponent<{
  canvasWidth: number;
  canvasHeight: number;
  map: Map;
  updates: Update[];
  updateIndex: number;
  spritesSeed: number;
}> = ({ map, updates, updateIndex, canvasWidth, canvasHeight, spritesSeed }) => {
  const navigateRef = useRef(useNavigate());

  const tileSize = useMemo(() => {
    const tileWidth =
        (Math.floor((canvasWidth / map.width / 3) * window.devicePixelRatio) * 3) /
        window.devicePixelRatio,
      tileHeight =
        (Math.floor((canvasHeight / map.height / 3) * window.devicePixelRatio) * 3) /
        window.devicePixelRatio;
    const tileSize = Math.min(tileWidth, tileHeight);
    console.debug('Tile size:', tileSize, '\t\tZoom:', window.devicePixelRatio);
    return tileSize;
  }, [canvasWidth, canvasHeight, map.width, map.height]);

  const [sprites, setSprites] = useState<Sprites>();

  useEffect(() => {
    loadSprites(spritesSeed)
      .then((x) => setSprites(x))
      .catch((err) => {
        console.error(err);
        setSprites(undefined);
        navigateRef.current('/');
      });
  }, [spritesSeed]);

  const initialState = useMemo(() => {
    const background: TileBackground[][] = Array.from({ length: map.height }, () =>
        Array.from({ length: map.width })
      ),
      names: string[] = [];
    map.map.forEach((col, x) => {
      col.forEach((tile, y) => {
        background[y][x] = tile.background;

        if (tile.entity?.type === 'summoner') {
          names.push(tile.entity.name);
        }
      });
    });

    const colors = associateNamesWithColors(names);

    return {
      tick: 0,
      index: -1,
      zone: {
        left: 0,
        right: 0,
        top: 0,
        bottom: 0
      },
      background,
      entities: map.map
        .map((col, x) =>
          col.map((tile, y) => {
            return { x, y, tile };
          })
        )
        .flatMap((col) => col)
        .reduce<State['entities']>((obj, { x, y, tile }) => {
          if (!tile.entity) {
            return obj;
          }

          if (obj[tile.entity.id]) {
            console.warn(`Entity #${tile.entity.id} is duplicate`);
          }

          if (tile.entity.type !== 'summoner') {
            throw new Error('Unexpected non-summoner entity');
          }

          obj[tile.entity.id] = {
            x,
            y,
            color: colors[tile.entity.name],
            tick: 1,
            dir: Direction.Down,
            entity: tile.entity
          };
          return obj;
        }, {})
    };
  }, [map]);

  if (!sprites) {
    return <></>;
  }

  return (
    <Stage
      width={tileSize * map.width}
      height={tileSize * map.height}
      options={{
        autoStart: false,
        sharedTicker: true,
        antialias: true
      }}
    >
      <GameContainer
        tileSize={tileSize}
        mapWidth={map.width}
        mapHeight={map.height}
        sprites={sprites}
        updates={updates}
        updateIndex={updateIndex}
        initialState={initialState}
      />
    </Stage>
  );
};

export default GameCanvas;
