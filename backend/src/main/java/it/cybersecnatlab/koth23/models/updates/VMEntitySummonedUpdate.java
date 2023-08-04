package it.cybersecnatlab.koth23.models.updates;

import it.cybersecnatlab.koth23.models.Update;
import it.cybersecnatlab.koth23.models.VMEntity;
import org.json.JSONObject;

public final class VMEntitySummonedUpdate implements Update {
    private final String summonerID;
    private final String entityId;
    private final int entityLife;
    private final int summonX;
    private final int summonY;
    private final VMEntity.Type type;

    public VMEntitySummonedUpdate(String summonerID, String entityId, int entityLife, int summonX, int summonY, VMEntity.Type type) {
        this.summonerID = summonerID;
        this.entityId = entityId;
        this.entityLife = entityLife;
        this.summonX = summonX;
        this.summonY = summonY;
        this.type = type;
    }

    @Override
    public JSONObject getUpdateData() {
        JSONObject updateData = new JSONObject();
        updateData.put("summonerID", summonerID);
        updateData.put("entityId", entityId);
        updateData.put("summonX", summonX);
        updateData.put("summonY", summonY);
        updateData.put("type", type.ordinal());
        updateData.put("life", entityLife);
        return updateData;
    }

    @Override
    public Type getUpdateType() {
        return Type.SummonerSummon;
    }
}
