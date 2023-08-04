import React, { useContext, useEffect, useState } from 'react';
import { UserContext } from '../user';
import { Map, Update } from '../game/map';
import Api, { Scoreboard } from '../api';
import GameInterface from '../game/GameInterface';

const LiveGame = () => {
  const { user } = useContext(UserContext);

  const [gameData, setGameData] = useState<[Map, Update[]] | null>(null);
  const [gameScoreboard, setGameScoreboard] = useState<Scoreboard | null>(null);
  const [gameOutputs, setGameOutputs] = useState<{ [key: string]: string }>({});
  const [gameId, setGameId] = useState(-1);
  const [autoplay, setAutoplay] = useState(false);

  useEffect(() => {
    if (gameId === -1) {
      return;
    }

    Api.gameHistory(gameId)
      .then(setGameData)
      .catch((err) => {
        console.error('Failed loading game');
        console.error(err);
        setGameData(null);
        window.location.reload();
      });

    Api.gameScoreboard(gameId)
      .then(setGameScoreboard)
      .catch((err) => {
        console.error('Failed loading game scoreboard');
        console.error(err);
        setGameScoreboard(null);
        window.location.reload();
      });
  }, [gameId, user]);

  useEffect(() => {
    if (!user || gameId === -1) {
      return;
    }

    Api.gameOutputs(gameId)
      .then(setGameOutputs)
      .catch((err) => {
        console.error('Failed loading game outputs');
        console.error(err);
        setGameOutputs({});
        window.location.reload();
      });
  }, [gameId, user]);

  useEffect(() => {
    Api.games()
      .then((games) => {
        setGameId(games[games.length - 1] || -1);
        setTimeout(() => setAutoplay(true), 2000);
      })
      .catch((err) => {
        console.error('Failed loading games');
        console.error(err);
        setGameId(-1);
      });
  }, []);

  return (
    <>
      {gameData && gameScoreboard && (
        <GameInterface
          title={`Game ${gameId.toString().padStart(3, '0')}`}
          map={gameData[0]}
          updates={gameData[1]}
          scoreboard={gameScoreboard}
          logs={gameOutputs}
          autoplay={autoplay}
          initialSpeed={5}
          onGameEnded={() => setTimeout(() => window.location.reload(), 5000)}
        />
      )}
    </>
  );
};

export default LiveGame;
