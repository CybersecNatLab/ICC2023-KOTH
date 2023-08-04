import React, { createContext, FunctionComponent, ReactNode, useEffect, useState } from 'react';
import Api, { Me } from './api';

export const UserContext = createContext<{
  user: Me | null;
  login: (username: string, password: string) => Promise<boolean>;
  logout: () => Promise<void>;
}>({
  user: null,
  login: async () => {
    return false;
  },
  logout: async () => {}
});

const UserProvider: FunctionComponent<{ children: ReactNode }> = (props) => {
  const [user, setUser] = useState<Me | null>(null);

  const login = async (username: string, password: string) => {
    const ok = await Api.login(username, password);
    if (!ok) {
      setUser(null);
      return false;
    }

    await refreshUser();
    return true;
  };

  const logout = async () => {
    await Api.logout();
    setUser(null);
  };

  const refreshUser = async () => {
    try {
      const user = await Api.me();
      setUser(user);
    } catch (err) {
      setUser(null);
      throw err;
    }
  };

  useEffect(() => {
    void refreshUser();
  }, []);

  return (
    <UserContext.Provider value={{ user, login, logout }}>{props.children}</UserContext.Provider>
  );
};

export default UserProvider;
