import React, { useContext } from 'react';
import { HashRouter, Route, Routes, useLocation } from 'react-router-dom';
import UserProvider, { UserContext } from './user';
import Home from './pages/Home';
import ViewGame from './pages/ViewGame';
import ViewUploadedGame from './pages/ViewUploadedGame';
import Scoreboard from './pages/Scoreboard';
import Login from './pages/Login';
import Rules from './pages/Rules';
import HowTo from './pages/HowTo';
import LiveGame from './pages/LiveGame';
import './App.css';

const NotFound = () => {
  return (
    <div className="not-found">
      <h1>404 Not found</h1>
    </div>
  );
};

const Content = () => {
  const location = useLocation();
  const { user, logout } = useContext(UserContext);

  return (
    <>
      {location.pathname !== '/game/live' && (
        <header>
          <div className="pixel white" style={{ bottom: 0, left: 0 }}></div>
          <div className="pixel white" style={{ bottom: '36px', left: '36px' }}></div>
          <div className="pixel white" style={{ bottom: 0, left: 'calc(36px*2)' }}></div>
          <div className="pixel accent" style={{ bottom: '-36px', left: '36px' }}></div>
          <div className="pixel white" style={{ bottom: 0, right: 'calc(36px*2)' }}></div>
          <div className="pixel accent" style={{ bottom: '36px', right: '36px' }}></div>
          <div className="pixel white" style={{ bottom: 0, right: 0 }}></div>
          <div className="pixel white" style={{ bottom: 'calc(36px*2)', right: 0 }}></div>

          <ul>
            <li>
              <a href="/#/" className={location.pathname === '/' ? 'active' : ''}>
                Home
              </a>
            </li>
            <li>
              <a href="/#/howto" className={location.pathname === '/howto' ? 'active' : ''}>
                How To
              </a>
            </li>
            <li>
              <a href="/#/rules" className={location.pathname === '/rules' ? 'active' : ''}>
                Rules
              </a>
            </li>
            <li>
              <a
                href="/#/scoreboard"
                className={location.pathname === '/scoreboard' ? 'active' : ''}
              >
                Scoreboard
              </a>
            </li>
          </ul>

          <h1>KOTH GAME</h1>

          <div className="buttons">
            {user ? (
              <>
                <span className="me-3">
                  Logged as <b>{user.shortname}</b>
                </span>
                <button onClick={logout}>Logout</button>
              </>
            ) : (
              <>
                <a href="/#/login">Login</a>
              </>
            )}
            <a href="/#/game/live" className="live">
              ► Live
            </a>
          </div>
        </header>
      )}
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/scoreboard" element={<Scoreboard />} />
        <Route path="/howto" element={<HowTo />} />
        <Route path="/login" element={<Login />} />
        <Route path="/rules" element={<Rules />} />
        <Route path="/game/live" element={<LiveGame />} />
        <Route path="/game/:id" element={<ViewGame />} />
        <Route path="/game/uploaded/:uploadId" element={<ViewUploadedGame />} />
        <Route path="*" element={<NotFound />} />
      </Routes>
      {!location.pathname.startsWith('/game') && (
        <footer>
          <div className="pixel white" style={{ top: 0, left: 0 }}></div>
          <div className="pixel white" style={{ top: '36px', left: '36px' }}></div>
          <div className="pixel white" style={{ top: 0, left: 'calc(36px*2)' }}></div>
          <div className="pixel accent" style={{ top: '-36px', left: '36px' }}></div>
          <div className="pixel white" style={{ top: 0, right: 'calc(36px*2)' }}></div>
          <div className="pixel accent" style={{ top: '36px', right: '36px' }}></div>
          <div className="pixel white" style={{ top: 0, right: 0 }}></div>
          <div className="pixel white" style={{ top: 'calc(36px*2)', right: 0 }}></div>

          <a href="https://cybersecnatlab.it" target="_blank" rel="noreferrer">
            <img src="/cybersecnatlab.svg" alt="CybersecNatLab logo" />
          </a>

          <a href="https://ic3.games" target="_blank" rel="noreferrer">
            <img src="/icc23.png" alt="IC3 logo" />
          </a>
        </footer>
      )}
    </>
  );
};

const App = () => {
  return (
    <HashRouter>
      <UserProvider>
        <Content />
      </UserProvider>
    </HashRouter>
  );
};

export default App;
