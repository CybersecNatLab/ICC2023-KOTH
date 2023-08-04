package it.cybersecnatlab.koth23.models.updates;

import it.cybersecnatlab.koth23.models.BackgroundTile;
import it.cybersecnatlab.koth23.models.Update;
import org.json.JSONObject;

public final class BackgroundTileChangeUpdate implements Update {
    private final int x;
    private final int y;
    private final BackgroundTile.Type newType;

    public BackgroundTileChangeUpdate(int x, int y, BackgroundTile.Type newType) {
        this.x = x;
        this.y = y;
        this.newType = newType;
    }

    @Override
    public JSONObject getUpdateData() {
        JSONObject updateData = new JSONObject();
        updateData.put("x", x);
        updateData.put("y", y);
        updateData.put("newBgTile", newType.ordinal());
        return updateData;
    }

    @Override
    public Type getUpdateType() {
        return Type.BackgroundTileChange;
    }
}
