import React, { useEffect, useState } from 'react';
import Api from '../api';
import './Scoreboard.css';
import trophyGold from './TrophyGold.png';
import trophySilver from './TrophySilver.png';
import trophyBronze from './TrophyBronze.png';

const Scoreboard = () => {
  const [scoreboard, setScoreboard] = useState<{ team: string; score: number }[]>([]);

  useEffect(() => {
    Api.scoreboard()
      .then((scoreboard) => {
        const lastScoreboard =
          scoreboard[Math.max(...Object.keys(scoreboard).map((x) => parseInt(x)))];
        if (!lastScoreboard) {
          setScoreboard([]);
          return;
        }

        const arrayScoreboard = Object.entries(lastScoreboard).map(([team, score]) => ({
          team,
          score
        }));
        arrayScoreboard.sort((a, b) => b.score - a.score);
        setScoreboard(arrayScoreboard);
      })
      .catch((err) => {
        console.log('Failed loading scoreboard');
        console.error(err);
        setScoreboard([]);
      });
  }, []);

  return (
    <div className="scoreboard">
      <ul>
        {scoreboard.map((x, index) => (
          <li key={x.team}>
            <div>
              {index + 1}
              {'.'}
              {index < 3 && (
                <img alt="score type" src={[trophyGold, trophySilver, trophyBronze][index]}></img>
              )}{' '}
              {x.team}
            </div>
            <div>{x.score}</div>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Scoreboard;
