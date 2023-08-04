import React, { useContext, useEffect, useState } from 'react';
import { Map, Update } from '../game/map';
import { useNavigate, useParams } from 'react-router-dom';
import Api, { Scoreboard } from '../api';
import GameInterface from '../game/GameInterface';
import { UserContext } from '../user';

const ViewGame = () => {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const { user } = useContext(UserContext);

  const [gameData, setGameData] = useState<[Map, Update[]] | null>(null);
  const [gameScoreboard, setGameScoreboard] = useState<Scoreboard | null>(null);
  const [gameOutputs, setGameOutputs] = useState<{ [key: string]: string }>({});

  useEffect(() => {
    if (id === undefined || !isFinite(parseInt(id))) {
      return;
    }

    Api.gameHistory(parseInt(id))
      .then(setGameData)
      .catch((err) => {
        console.error('Failed loading game');
        console.error(err);
        setGameData(null);
        navigate('/');
      });

    Api.gameScoreboard(parseInt(id))
      .then(setGameScoreboard)
      .catch((err) => {
        console.error('Failed loading game scoreboard');
        console.error(err);
        setGameScoreboard(null);
        navigate('/');
      });
  }, [id, navigate, user]);

  useEffect(() => {
    if (!user || id === undefined || !isFinite(parseInt(id))) {
      return;
    }

    Api.gameOutputs(parseInt(id))
      .then(setGameOutputs)
      .catch((err) => {
        console.error('Failed loading game outputs');
        console.error(err);
        setGameOutputs({});
        navigate('/');
      });
  }, [id, navigate, user]);

  return (
    <>
      {gameData && gameScoreboard && (
        <GameInterface
          title={id ? `Game ${id.toString().padStart(3, '0')}` : ''}
          map={gameData[0]}
          updates={gameData[1]}
          scoreboard={gameScoreboard}
          logs={gameOutputs}
          autoplay={false}
        />
      )}
    </>
  );
};

export default ViewGame;
