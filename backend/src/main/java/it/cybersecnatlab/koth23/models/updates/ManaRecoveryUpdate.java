package it.cybersecnatlab.koth23.models.updates;

import it.cybersecnatlab.koth23.models.Update;
import org.json.JSONObject;

public final class ManaRecoveryUpdate implements Update {
    private final String entityID;
    private final int amount;
    private final int newMana;

    public ManaRecoveryUpdate(String entityID, int amount, int newMana) {
        this.entityID = entityID;
        this.amount = amount;
        this.newMana = newMana;
    }

    @Override
    public JSONObject getUpdateData() {
        JSONObject updateData = new JSONObject();
        updateData.put("entityID", entityID);
        updateData.put("amount", amount);
        updateData.put("newMana", newMana);
        return updateData;
    }

    @Override
    public Type getUpdateType() {
        return Type.ManaRecovery;
    }
}
