package it.cybersecnatlab.koth23.models.updates;

import it.cybersecnatlab.koth23.models.Update;
import org.json.JSONObject;

public final class SummonerEntityDeadUpdate implements Update {
    private final String summonerID;

    public SummonerEntityDeadUpdate(String summonerID) {
        this.summonerID = summonerID;
    }


    @Override
    public JSONObject getUpdateData() {
        JSONObject updateData = new JSONObject();
        updateData.put("summonerID", summonerID);
        return updateData;
    }

    @Override
    public Type getUpdateType() {
        return Type.SummonerDeath;
    }
}
