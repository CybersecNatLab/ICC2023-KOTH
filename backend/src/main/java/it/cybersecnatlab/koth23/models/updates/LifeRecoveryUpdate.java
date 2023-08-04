package it.cybersecnatlab.koth23.models.updates;

import it.cybersecnatlab.koth23.models.Update;
import org.json.JSONObject;

public final class LifeRecoveryUpdate implements Update {
    private final String entityID;
    private final int amount;
    private final int newLife;

    public LifeRecoveryUpdate(String entityID, int amount, int newLife) {
        this.entityID = entityID;
        this.amount = amount;
        this.newLife = newLife;
    }

    @Override
    public JSONObject getUpdateData() {
        JSONObject updateData = new JSONObject();
        updateData.put("entityID", entityID);
        updateData.put("amount", amount);
        updateData.put("newLife", newLife);
        return updateData;
    }

    @Override
    public Type getUpdateType() {
        return Type.LifeRecovery;
    }
}
