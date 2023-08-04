import { Map, Update } from './game/map';

export type Me = {
  shortname: string;
};

export type Scoreboard = {
  name: string;
  score: number;
}[];

class Api {
  async login(username: string, password: string) {
    const resp = await fetch('/api/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    });
    return resp.status === 200;
  }

  async logout() {
    await fetch('/api/logout');
  }

  async me(): Promise<Me | null> {
    const resp = await fetch('/api/me');
    if (resp.status === 401) {
      return null;
    }

    return await resp.json();
  }

  async scoreboard(): Promise<{ [id: number]: { [team: string]: number } }> {
    const resp = await fetch('/api/scoreboard');
    return await resp.json();
  }

  async games(): Promise<number[]> {
    const resp = await fetch('/api/games');
    return await resp.json();
  }

  async scoreboards(): Promise<{ [id: number]: { scoreboard: Scoreboard } }> {
    const resp = await fetch('/api/games/scoreboards');
    return await resp.json();
  }

  async gameHistory(id: number): Promise<[Map, Update[]]> {
    const resp = await fetch(`/api/games/${id}/history`);
    const json = await resp.json();
    return [json[0], json.slice(1)];
  }

  async gameScoreboard(id: number): Promise<Scoreboard> {
    const resp = await fetch(`/api/games/${id}/scoreboard`);
    return await resp.json();
  }

  async gameOutputs(id: number): Promise<{ [key: string]: string }> {
    const resp = await fetch(`/api/games/${id}/outputs`);
    return await resp.json();
  }
}

const api = new Api();
export default api;
