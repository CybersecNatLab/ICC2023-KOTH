import React, { useCallback, useContext, useRef, useState } from 'react';
import './Login.css';
import { UserContext } from '../user';
import { useNavigate } from 'react-router-dom';

const Login = () => {
  const { login } = useContext(UserContext);
  const navigate = useRef(useNavigate());
  const [username, setUsername] = useState<string>('');
  const [password, setPassword] = useState<string>('');
  const [error, setError] = useState<string>('');

  const handleLogin = useCallback(() => {
    if (!username || !password) {
      return;
    }

    login(username, password)
      .then((ok) => {
        if (!ok) {
          setError('Invalid username or password');
          return;
        }

        setUsername('');
        setPassword('');
        setError('');

        navigate.current('/');
      })
      .catch((err) => {
        console.error('Failed logging in');
        console.error(err);
        setError('Failed logging in');
      });
  }, [login, username, password]);

  return (
    <div className="login">
      <h1>LOGIN</h1>

      <div>
        <h3>Team</h3>
        <input type="text" value={username} onChange={(ev) => setUsername(ev.target.value)} />

        <h3>Password</h3>
        <input type="password" value={password} onChange={(ev) => setPassword(ev.target.value)} />

        <button onClick={handleLogin}>Login</button>

        {error && <h2>{error}</h2>}
      </div>
    </div>
  );
};

export default Login;
