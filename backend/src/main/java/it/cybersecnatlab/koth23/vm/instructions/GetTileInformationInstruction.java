package it.cybersecnatlab.koth23.vm.instructions;

import it.cybersecnatlab.koth23.vm.*;
import it.cybersecnatlab.koth23.models.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class GetTileInformationInstruction implements VMInstruction {
    VMInstructionParam x;
    VMInstructionParam y;
    VMRegistry bgRegistry;
    VMRegistry damagingRegistry;
    VMRegistry entityRegistry;
    VMRegistry entityIDRegistry;
    VMRegistry entityLifeRegistry;
    VMRegistry entityOwnerRegistry;
    GameMap m;

    public GetTileInformationInstruction(JSONArray params, List<VMRegistry> registries, GameMap m) throws JSONException {
        if (params.length() != 8) {
            throw new JSONException("Wrong params number");
        }
        JSONObject param1 = params.getJSONObject(0);
        this.x = VMInstructionUtils.parseVMInstructionParam(param1, registries);
        JSONObject param2 = params.getJSONObject(1);
        this.y = VMInstructionUtils.parseVMInstructionParam(param2, registries);
        JSONObject param3 = params.getJSONObject(2);
        this.bgRegistry = VMInstructionUtils.parseVMRegistry(param3, registries);
        JSONObject param4 = params.getJSONObject(3);
        this.damagingRegistry = VMInstructionUtils.parseVMRegistry(param4, registries);
        JSONObject param5 = params.getJSONObject(4);
        this.entityRegistry = VMInstructionUtils.parseVMRegistry(param5, registries);
        JSONObject param6 = params.getJSONObject(5);
        this.entityIDRegistry = VMInstructionUtils.parseVMRegistry(param6, registries);
        JSONObject param7 = params.getJSONObject(6);
        this.entityLifeRegistry = VMInstructionUtils.parseVMRegistry(param7, registries);
        JSONObject param8 = params.getJSONObject(7);
        this.entityOwnerRegistry = VMInstructionUtils.parseVMRegistry(param8, registries);

        this.m = m;
    }

    @Override
    public void execute() throws VMException {
        if (x.getIntegerValue() < 0 || x.getIntegerValue() >= m.width || y.getIntegerValue() < 0 || y.getIntegerValue() >= m.height) {
            throw new VMException("Entity cannot access map at " + x + " " + y);
        }
        bgRegistry.setData(m.tiles[x.getIntegerValue()][y.getIntegerValue()].bgTile.type.ordinal());
        damagingRegistry.setData(m.tiles[x.getIntegerValue()][y.getIntegerValue()].isDamaging);
        if (m.tiles[x.getIntegerValue()][y.getIntegerValue()].entity == null) {
            entityRegistry.setData(null);
            entityIDRegistry.setData(null);
            entityLifeRegistry.setData(null);
            entityOwnerRegistry.setData(null);
        } else {
            entityRegistry.setData(m.tiles[x.getIntegerValue()][y.getIntegerValue()].entity.type.ordinal());
            entityIDRegistry.setData(m.tiles[x.getIntegerValue()][y.getIntegerValue()].entity.entityId);
            entityLifeRegistry.setData(m.tiles[x.getIntegerValue()][y.getIntegerValue()].entity.life);
            if (m.tiles[x.getIntegerValue()][y.getIntegerValue()].entity.owner == null) {
                entityOwnerRegistry.setData(null);
            } else {
                entityOwnerRegistry.setData(m.tiles[x.getIntegerValue()][y.getIntegerValue()].entity.owner.entityId);
            }
        }
    }

    @Override
    public int getGasCost() {
        return 1;
    }
}
