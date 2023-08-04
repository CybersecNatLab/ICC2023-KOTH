package it.cybersecnatlab.koth23.models.updates;

import it.cybersecnatlab.koth23.models.Update;
import org.json.JSONObject;

public final class TickUpdate implements Update {
    private final int newTick;

    public TickUpdate(int newTick) {
        this.newTick = newTick;
    }

    @Override
    public JSONObject getUpdateData() {
        JSONObject updateData = new JSONObject();
        updateData.put("newTick", this.newTick);
        return updateData;
    }

    @Override
    public Type getUpdateType() {
        return Type.Tick;
    }
}
