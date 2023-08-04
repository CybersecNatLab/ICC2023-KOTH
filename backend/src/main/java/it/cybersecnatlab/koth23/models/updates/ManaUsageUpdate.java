package it.cybersecnatlab.koth23.models.updates;

import it.cybersecnatlab.koth23.models.Update;
import org.json.JSONObject;

public final class ManaUsageUpdate implements Update {
    private final String entityID;
    private final int usage;
    private final int newMana;

    public ManaUsageUpdate(String entityID, int usage, int newMana) {
        this.entityID = entityID;
        this.usage = usage;
        this.newMana = newMana;
    }

    @Override
    public JSONObject getUpdateData() {
        JSONObject updateData = new JSONObject();
        updateData.put("entityID", entityID);
        updateData.put("usage", usage);
        updateData.put("newMana", newMana);
        return updateData;
    }

    @Override
    public Type getUpdateType() {
        return Type.ManaUsage;
    }
}
