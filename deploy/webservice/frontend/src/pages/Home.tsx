import React, { ChangeEvent, useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Api, { Scoreboard } from '../api';
import './Home.css';
import trophyGold from './TrophyGold.png';
import trophySilver from './TrophySilver.png';
import trophyBronze from './TrophyBronze.png';

const GameStart = new Date('2023-08-03T10:10:00-07:00').getTime();
const GameRoundDuration = 360 * 1000;

const Home = () => {
  const navigate = useNavigate();
  const [games, setGames] = useState<number[]>([]);
  const [scoreboards, setScoreboards] = useState<{ [id: number]: { scoreboard: Scoreboard } }>({});
  const [uploadedGameId, setUploadedGameId] = useState<string>('');
  const [uploadedGameFilename, setUploadedGameName] = useState<string>('');

  const uploadedGameChange = (ev: ChangeEvent<HTMLInputElement>) => {
    const file = ev.target.files?.[0];
    if (!file) {
      return;
    }

    // Stash uploaded game file as object URL
    setUploadedGameId(btoa(URL.createObjectURL(file)));
    setUploadedGameName(file.name);
  };

  const viewUploadedGame = useCallback(() => {
    if (!uploadedGameId) {
      return;
    }

    navigate('/game/uploaded/' + uploadedGameId);
  }, [uploadedGameId, navigate]);

  useEffect(() => {
    Api.games()
      .then((games) => {
        setGames(games.reverse());
      })
      .catch((err) => {
        console.log('Failed loading games');
        console.error(err);
        setGames([]);
      });

    Api.scoreboards()
      .then(setScoreboards)
      .catch((err) => {
        console.log('Failed loading scoreboards');
        console.error(err);
        setScoreboards({});
      });
  }, []);

  return (
    <div className="home">
      <div>
        <div className="sprites-header">
          <img alt="wizard" width={96} src="/sprites/wizard-5.png" />
          <img alt="wizard" width={96} src="/sprites/wizard-4.png" />
          <img alt="wizard" width={96} src="/sprites/wizard-2.png" />
          <img alt="wizard" width={96} src="/sprites/wizard-1.png" />
          <img alt="wizard" width={96} src="/sprites/wizard-6.png" />
          <img alt="wizard" width={96} src="/sprites/wizard-3.png" />
          <img alt="wizard" width={96} src="/sprites/wizard-8.png" />
        </div>

        <h1>
          <span>King</span> <span>of the</span> <span>Hill</span>
        </h1>

        <div className="sprites-header">
          <img alt="armadillo" width={96} src="/sprites/armadillo.png" />
          <img alt="cat" width={96} src="/sprites/cat.png" />
          <img alt="dragon" width={96} src="/sprites/dragon.png" />
          <img alt="mole" width={96} src="/sprites/mole.png" />
          <img alt="rat" width={96} src="/sprites/rat.png" />
          <img alt="spectre" width={96} src="/sprites/spectre.png" />
          <img alt="werewolf" width={96} src="/sprites/werewolf.png" />
        </div>
      </div>

      <h1>
        <span>UPLOAD</span> <span>A</span> <span>GAME</span>
      </h1>

      <div className="upload">
        <label>
          {uploadedGameFilename ? uploadedGameFilename : 'Select a file to upload'}
          <input type="file" onChange={uploadedGameChange} />
        </label>
        <button type="submit" onClick={viewUploadedGame}>
          View
        </button>
      </div>

      <h1>
        <span>OR</span> <span>SELECT A</span> <span>ROUND</span>
      </h1>

      <ul>
        {games.map((x) => (
          <li key={x}>
            <a href={`/#/game/${x}`}>
              <div>Game {x.toString().padStart(3, '0')}</div>

              <div>
                @{' '}
                {new Date(GameStart + x * GameRoundDuration).toLocaleTimeString(undefined, {
                  timeStyle: 'short'
                })}
              </div>

              {scoreboards[x] && (
                <ul className="scoreboard">
                  {scoreboards[x].scoreboard?.slice(0, 3).map((y, i) => (
                    <li key={y.name}>
                      {i + 1}
                      {'.'}
                      <img
                        alt="score type"
                        src={[trophyGold, trophySilver, trophyBronze][i]}
                      ></img>{' '}
                      {y.name}
                    </li>
                  ))}
                </ul>
              )}
            </a>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Home;
