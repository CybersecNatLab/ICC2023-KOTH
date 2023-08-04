import React, { FC, ReactNode, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { Ticker } from 'pixi.js';
import Modal from 'react-modal';
import { Map, SummonerEntity, Update, VMEntity, VMEntityIcons, VMEntityNames } from './map';
import ResizableGameCanvas from './ResizableGameCanvas';
import { UserContext } from '../user';
import { Scoreboard } from '../api';
import './GameInterface.css';
import { associateNamesWithColors, Color } from './sprites';

const BaseInterval = 200;
const UuidLogRegex =
  /^[0-9a-fA-F]{8}\b-[0-9a-fA-F]{4}\b-[0-9a-fA-F]{4}\b-[0-9a-fA-F]{4}\b-[0-9a-fA-F]{12}\.log$/;

const hash = (s: string) => {
  let h = 0;
  for (let i = 0; i < s.length; i++) {
    h = ((h << 5) - h + s.charCodeAt(i++)) | 0;
  }
  return h;
};

const colorToWizardImageMap: { [C in Color]: string } = {
  [Color.Pink]: '/sprites/wizard-pink.png',
  [Color.Blue]: '/sprites/wizard-blue.png',
  [Color.Red]: '/sprites/wizard-red.png',
  [Color.Purple]: '/sprites/wizard-purple.png',
  [Color.Orange]: '/sprites/wizard-orange.png',
  [Color.White]: '/sprites/wizard-white.png',
  [Color.Green]: '/sprites/wizard-green.png'
};

const LogsModal: FC<{ data: { title: ReactNode; content: string } | null; onHide: () => void }> = ({
  data,
  onHide
}) => {
  return (
    <Modal isOpen={!!data} onRequestClose={onHide}>
      <div>
        <h2>{data && data.title}</h2>
        <button onClick={onHide}>Close</button>
      </div>
      <div>
        <pre>{data && data.content}</pre>
      </div>
    </Modal>
  );
};

const GameInterface: FC<{
  title: string;
  map: Map;
  updates: Update[];
  scoreboard: Scoreboard;
  logs: { [id: string]: string };
  autoplay: boolean;
  initialSpeed?: number;
  onGameEnded?: () => void;
}> = ({
  title,
  map,
  updates,
  scoreboard: finalScoreboard,
  logs,
  autoplay,
  initialSpeed,
  onGameEnded
}) => {
  const { user } = useContext(UserContext);

  const [updateIndex, setUpdateIndex] = useState(-1);
  const [scoreboard, setScoreboard] = useState<({ name: string; score: number } | null)[]>([]);
  const [history, setHistory] = useState<ReactNode[]>([]);
  const [colors, setColors] = useState<{ [name: string]: Color }>({});

  const [speed, setSpeed] = useState(10);
  const [play, setPlay] = useState(false);

  // little hack to reset everything when the game changes
  useEffect(() => {
    setPlay(false);
    setUpdateIndex(-1);
    requestAnimationFrame(() => Ticker.shared.update());
  }, [title]);

  useEffect(() => {
    if (initialSpeed) setSpeed(initialSpeed);
  }, [initialSpeed]);

  useEffect(() => {
    if (autoplay) setPlay(true);
  }, [autoplay]);

  const findSummoner = useCallback(
    (id: string): SummonerEntity | undefined => {
      for (let y = 0; y < map.height; y++) {
        for (let x = 0; x < map.width; x++) {
          const entity = map.map[y][x].entity;
          if (entity && entity.type === 'summoner' && entity.id === id) {
            return entity;
          }
        }
      }

      return undefined;
    },
    [map]
  );

  const findEntity = useCallback(
    (id: string): (VMEntity & { tick: number }) | undefined => {
      let tick = 0;
      for (const upd of updates) {
        if (upd.type === 'tick') {
          tick += 1;
        }

        if (upd.type !== 'summon' || upd.data.entityId !== id) {
          continue;
        }

        return {
          type: 'VMEntity',
          VMType: upd.data.type,
          id: upd.data.entityId,
          summonerID: upd.data.summonerID,
          life: upd.data.life,
          tick
        };
      }

      return undefined;
    },
    [updates]
  );

  const getLogEntryDisplayName = useCallback(
    (filename: string) => {
      if (filename === 'container.logs') {
        return 'container';
      } else if (UuidLogRegex.test(filename)) {
        const entity = findEntity(filename.slice(0, -4));
        if (!entity) {
          return filename.slice(0, -4);
        }

        return (
          <>
            <img
              alt="entity"
              src={VMEntityIcons[entity.VMType]}
              style={{ height: '20px', marginBottom: '-4px' }}
            />{' '}
            <b>{VMEntityNames[entity.VMType]}</b> at {entity.tick}
          </>
        );
      } else {
        return filename;
      }
    },
    [findEntity]
  );

  useEffect(() => {
    if (scoreboard.length === finalScoreboard.length && updateIndex !== -1) {
      return;
    }

    setColors(associateNamesWithColors(finalScoreboard.map((x) => x.name)));
    setScoreboard(Array.from({ length: finalScoreboard.length }, () => null));
    setHistory([]);
  }, [finalScoreboard, updateIndex, scoreboard.length]);

  const lastTick = useMemo(() => {
    for (let i = updates.length - 1; i >= 0; i--) {
      const upd = updates[i];
      if (upd.type === 'tick') {
        return upd.data.newTick;
      }
    }

    return -1;
  }, [updates]);

  const currentTick = useMemo(() => {
    for (let i = Math.min(updateIndex, updates.length - 1); i >= 0; i--) {
      const upd = updates[i];
      if (upd.type === 'tick') {
        return upd.data.newTick;
      }
    }

    return 0;
  }, [updates, updateIndex]);

  const handleUpdates = useCallback(
    (updates: Update[]) => {
      const newScoreboard = [...scoreboard],
        newHistory = [...history];
      for (const update of updates) {
        if (update.type === 'scoreboard') {
          for (let i = newScoreboard.length - 1; i >= 0; i--) {
            if (newScoreboard[i] !== null) {
              continue;
            }

            const summoner = findSummoner(update.data.summonerID);
            if (!summoner) {
              console.warn(`Missing summoner ${update.data.summonerID} from scoreboard update`);
              break;
            }

            newScoreboard[i] = { score: update.data.score, name: summoner.name };
            break;
          }
        } else if (update.type === 'summonerDeath') {
          const summoner = findSummoner(update.data.summonerID);
          if (!summoner) {
            console.warn(`Missing summoner ${update.data.summonerID} from death event`);
            return;
          }

          newHistory.unshift(
            <>
              <img
                alt="wizard"
                src={colorToWizardImageMap[colors[summoner.name]]}
                style={{ height: '20px', marginBottom: '-4px' }}
              />{' '}
              <b>{summoner.name}</b> died!
            </>
          );
        } else if (update.type === 'summon') {
          const summoner = findSummoner(update.data.summonerID);
          if (!summoner) {
            console.warn(`Missing summoner ${update.data.summonerID} from summon event`);
            return;
          }

          newHistory.unshift(
            <>
              <img
                alt="wizard"
                src={colorToWizardImageMap[colors[summoner.name]]}
                style={{ height: '20px', marginBottom: '-4px' }}
              />{' '}
              <b>{summoner.name}</b> summoned a{' '}
              <img
                alt="entity"
                src={VMEntityIcons[update.data.type]}
                style={{ height: '20px', marginBottom: '-4px' }}
              />{' '}
              <b>{VMEntityNames[update.data.type]}</b>!
            </>
          );
        } else if (update.type === 'zoneReduce') {
          newHistory.unshift(
            <>
              <img
                alt="fog"
                src="/sprites/poison.png"
                style={{ height: '20px', marginBottom: '-4px', opacity: 0.8 }}
              />{' '}
              <b>fog</b> is shrinking!
            </>
          );
        }
      }

      setScoreboard(newScoreboard);
      setHistory(newHistory);
    },
    [scoreboard, findSummoner, history, colors]
  );

  const advanceUpdate = useCallback(() => {
    if (updateIndex + 1 >= updates.length) {
      setPlay(false);
      if (onGameEnded) onGameEnded();
      return;
    }

    const update = updates[updateIndex + 1];
    handleUpdates([update]);

    setUpdateIndex(updateIndex + 1);
    if (!play) {
      requestAnimationFrame(() => Ticker.shared.update());
    }
  }, [updateIndex, updates, handleUpdates, play, onGameEnded]);

  const advanceTick = useCallback(() => {
    let index = updateIndex + 1;
    const toPrecess: Update[] = [];
    while (true) {
      if (index >= updates.length) {
        setPlay(false);
        if (onGameEnded) onGameEnded();
        break;
      }

      const update = updates[index];
      toPrecess.push(update);
      if (update.type === 'tick') {
        break;
      }

      index += 1;
    }

    handleUpdates(toPrecess);

    setUpdateIndex(index);
    if (!play) {
      requestAnimationFrame(() => Ticker.shared.update());
    }
  }, [updateIndex, updates, handleUpdates, play, onGameEnded]);

  useEffect(() => {
    if (!play) {
      Ticker.shared.stop();
      return;
    } else if (updateIndex >= updates.length) {
      setUpdateIndex(-1);
    }

    Ticker.shared.start();

    const id = setInterval(() => advanceTick(), BaseInterval / (speed / 10));
    return () => clearInterval(id);
  }, [play, speed, advanceTick, updateIndex, updates.length]);

  const [logsModalData, setLogsModalData] = useState<{ title: ReactNode; content: string } | null>(
    null
  );

  return (
    <div className="game-interface">
      <div>
        <h1>{title}</h1>
        <h5>
          Tick: {currentTick}/{lastTick}
        </h5>
        <h5>
          Event: {Math.max(0, Math.min(updates.length - 1, updateIndex))}/{updates.length - 1}
        </h5>

        <hr />

        <div className="buttons">
          <button onClick={() => setPlay(!play)}>{play ? 'Pause' : 'Play'}</button>
          <button
            onClick={() => {
              setPlay(false);
              setUpdateIndex(-1);
              requestAnimationFrame(() => Ticker.shared.update());
            }}
          >
            Reset
          </button>
        </div>

        <hr />

        <div className="buttons">
          <button
            onClick={() => {
              setPlay(false);
              advanceUpdate();
            }}
          >
            Next event
          </button>
          <button
            onClick={() => {
              setPlay(false);
              advanceTick();
            }}
          >
            Next tick
          </button>
        </div>

        <hr />

        <div className="speed">
          <label>Speed {`x${(speed / 10).toFixed(1)}`}</label>
          <input
            type="range"
            min={1}
            max={30}
            onChange={(ev) => setSpeed(parseInt(ev.target.value))}
            value={speed}
          />
        </div>

        <hr />

        {user && Object.keys(logs).length > 0 && (
          <>
            <h1>Logs</h1>
            <ul className="logs">
              {Object.keys(logs).map((x) => {
                const title = getLogEntryDisplayName(x);
                return (
                  <li
                    key={x}
                    onClick={() =>
                      setLogsModalData({
                        title: (
                          <>
                            {title} ({x})
                          </>
                        ),
                        content: atob(logs[x])
                      })
                    }
                  >
                    {title}
                  </li>
                );
              })}
            </ul>
          </>
        )}
      </div>
      <div>
        <ResizableGameCanvas
          map={map}
          updates={updates}
          updateIndex={updateIndex}
          spritesSeed={hash(title)}
        />
      </div>
      <div>
        <h1>Scoreboard</h1>
        <ul className="scoreboard">
          {scoreboard.map((x, i) => (
            <li key={i}>
              <div>
                {i + 1}.{' '}
                {x && (
                  <>
                    <img
                      alt="wizard"
                      src={colorToWizardImageMap[colors[x.name]]}
                      style={{ height: '20px', marginBottom: '-4px' }}
                    />{' '}
                    {x && x.name}
                  </>
                )}
              </div>

              {x && x.score}
            </li>
          ))}
        </ul>

        <h1>History</h1>
        <ul className="history">
          {history.map((x, i) => (
            <li key={history.length - i}>{x}</li>
          ))}
        </ul>
      </div>

      <LogsModal data={logsModalData} onHide={() => setLogsModalData(null)} />
    </div>
  );
};

export default GameInterface;
