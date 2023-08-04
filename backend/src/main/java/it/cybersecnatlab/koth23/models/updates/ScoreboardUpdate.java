package it.cybersecnatlab.koth23.models.updates;

import it.cybersecnatlab.koth23.models.Update;
import org.json.JSONObject;


public final class ScoreboardUpdate implements Update {
    private final String summonerId;
    private final int score;

    public ScoreboardUpdate(String summonerId, int score) {
        this.summonerId = summonerId;
        this.score = score;
    }

    @Override
    public JSONObject getUpdateData() {
        JSONObject obj = new JSONObject();
        obj.put("summonerID", summonerId);
        obj.put("score", score);
        return obj;
    }

    @Override
    public Type getUpdateType() {
        return Type.Scoreboard;
    }
}
