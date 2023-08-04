package it.cybersecnatlab.koth23.models.updates;

import it.cybersecnatlab.koth23.models.Update;
import org.json.JSONObject;

public final class AbilityUpdate implements Update {
    private final String VMEntityID;
    private final int remainingUses;
    private final String abilityName;
    private Direction direction = null;

    public AbilityUpdate(String VMEntityID, int remainingUses, String abilityName) {
        this.VMEntityID = VMEntityID;
        this.remainingUses = remainingUses;
        this.abilityName = abilityName;
    }

    public AbilityUpdate(String VMEntityID, int remainingUses, String abilityName, Direction direction) {
        this.VMEntityID = VMEntityID;
        this.remainingUses = remainingUses;
        this.abilityName = abilityName;
        this.direction = direction;
    }

    @Override
    public JSONObject getUpdateData() {
        JSONObject updateData = new JSONObject();
        updateData.put("VMEntityID", VMEntityID);
        updateData.put("remainingUses", remainingUses);
        updateData.put("abilityName", abilityName);
        if (this.direction != null) {
            updateData.put("direction", direction.ordinal());
        }
        return updateData;
    }

    @Override
    public Type getUpdateType() {
        return Type.UseAbility;
    }

    public enum Direction {
        Up, Down, Left, Right
    }
}
