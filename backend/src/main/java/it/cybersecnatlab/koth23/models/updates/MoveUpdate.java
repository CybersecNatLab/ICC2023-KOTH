package it.cybersecnatlab.koth23.models.updates;

import it.cybersecnatlab.koth23.models.Update;
import org.json.JSONObject;

public final class MoveUpdate implements Update {
    private final String entityID;
    private final int x;
    private final int y;

    public MoveUpdate(String entityID, int x, int y) {
        this.entityID = entityID;
        this.x = x;
        this.y = y;
    }

    @Override
    public JSONObject getUpdateData() {
        JSONObject updateData = new JSONObject();
        updateData.put("entityID", entityID);
        updateData.put("newX", x);
        updateData.put("newY", y);
        return updateData;
    }

    @Override
    public Type getUpdateType() {
        return Type.Move;
    }
}
