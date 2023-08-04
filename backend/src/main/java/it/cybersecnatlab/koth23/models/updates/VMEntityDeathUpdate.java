package it.cybersecnatlab.koth23.models.updates;

import it.cybersecnatlab.koth23.models.Update;
import org.json.JSONObject;

public final class VMEntityDeathUpdate implements Update {
    private final String entityID;

    public VMEntityDeathUpdate(String entityID) {
        this.entityID = entityID;
    }


    @Override
    public JSONObject getUpdateData() {
        JSONObject updateData = new JSONObject();
        updateData.put("entityID", entityID);
        return updateData;
    }

    @Override
    public Type getUpdateType() {
        return Type.VMEntityDeath;
    }
}
