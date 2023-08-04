package it.cybersecnatlab.koth23.models.updates;

import it.cybersecnatlab.koth23.models.Update;
import org.json.JSONObject;

public final class DamageUpdate implements Update {
    private final String entityID;
    private final int damage;
    private final int newLife;

    public DamageUpdate(String entityID, int damage, int newLife) {
        this.entityID = entityID;
        this.damage = damage;
        this.newLife = newLife;
    }

    @Override
    public JSONObject getUpdateData() {
        JSONObject updateData = new JSONObject();
        updateData.put("entityID", entityID);
        updateData.put("damage", damage);
        updateData.put("newLife", newLife);
        return updateData;
    }

    @Override
    public Type getUpdateType() {
        return Type.TakeDamage;
    }
}
