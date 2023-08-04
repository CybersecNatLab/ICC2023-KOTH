import React, { useEffect, useState } from 'react';
import { deserializeMapWithUpdates, Map, Update } from '../game/map';
import { useNavigate, useParams } from 'react-router-dom';
import GameInterface from '../game/GameInterface';
import { Scoreboard } from '../api';

const ViewUploadedGame = () => {
  const navigate = useNavigate();
  const { uploadId } = useParams<{ uploadId: string }>();

  const [gameData, setGameData] = useState<[Map, Update[], Scoreboard]>();

  useEffect(() => {
    if (!uploadId) {
      return;
    }

    fetch(atob(uploadId))
      .then((x) => x.text())
      .then((x) => {
        const [map, updates] = deserializeMapWithUpdates(x);

        const findSummoner = (id: string): string | null => {
          for (let y = 0; y < map.height; y++) {
            for (let x = 0; x < map.width; x++) {
              const entity = map.map[y][x].entity;
              if (entity && entity.type === 'summoner' && entity.id === id) {
                return entity.name;
              }
            }
          }

          return null;
        };

        const scoreboard: Scoreboard = [];
        for (const upd of updates) {
          if (upd.type !== 'scoreboard') {
            continue;
          }

          const summoner = findSummoner(upd.data.summonerID);
          if (!summoner) {
            console.warn(`No summoner for ${upd.data.summonerID}`);
            continue;
          }

          scoreboard.push({
            name: summoner,
            score: upd.data.score
          });
        }

        scoreboard.reverse();
        setGameData([map, updates, scoreboard]);
      })
      .catch((err) => {
        console.error('Failed loading uploaded map');
        console.error(err);
        setGameData(undefined);
        navigate('/');
      });
  }, [uploadId, navigate]);

  return (
    <>
      {gameData && (
        <GameInterface
          title="Uploaded game"
          map={gameData[0]}
          updates={gameData[1]}
          scoreboard={gameData[2]}
          logs={{}}
          autoplay={false}
        />
      )}
    </>
  );
};

export default ViewUploadedGame;
