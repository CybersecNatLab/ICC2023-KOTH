package it.cybersecnatlab.koth23.models.updates;

import it.cybersecnatlab.koth23.models.Update;
import org.json.JSONObject;

public final class ZoneReduceUpdate implements Update {
    private final int leftMistWidth;
    private final int rightMistWidth;
    private final int topMistWidth;
    private final int bottomMistWidth;

    public ZoneReduceUpdate(int leftMistWidth, int rightMistWidth, int topMistWidth, int bottomMistWidth) {
        this.leftMistWidth = leftMistWidth;
        this.rightMistWidth = rightMistWidth;
        this.topMistWidth = topMistWidth;
        this.bottomMistWidth = bottomMistWidth;
    }

    @Override
    public JSONObject getUpdateData() {
        JSONObject updateData = new JSONObject();
        updateData.put("leftMistWidth", leftMistWidth);
        updateData.put("rightMistWidth", rightMistWidth);
        updateData.put("topMistWidth", topMistWidth);
        updateData.put("bottomMistWidth", bottomMistWidth);
        return updateData;
    }

    @Override
    public Type getUpdateType() {
        return Type.ZoneReduce;
    }
}
