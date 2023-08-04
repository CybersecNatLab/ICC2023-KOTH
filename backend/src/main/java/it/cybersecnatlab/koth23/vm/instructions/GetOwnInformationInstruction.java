package it.cybersecnatlab.koth23.vm.instructions;

import it.cybersecnatlab.koth23.vm.VMException;
import it.cybersecnatlab.koth23.vm.VMInstruction;
import it.cybersecnatlab.koth23.vm.VMInstructionUtils;
import it.cybersecnatlab.koth23.models.*;
import it.cybersecnatlab.koth23.vm.VMRegistry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class GetOwnInformationInstruction implements VMInstruction {
    VMRegistry x;
    VMRegistry y;
    VMRegistry life;
    VMRegistry abilityAmountLeft;
    VMEntity entity;

    public GetOwnInformationInstruction(JSONArray params, List<VMRegistry> registries, VMEntity entity) throws JSONException {
        if (params.length() != 4) {
            throw new JSONException("Wrong params number");
        }
        JSONObject param1 = params.getJSONObject(0);
        this.x = VMInstructionUtils.parseVMRegistry(param1, registries);
        JSONObject param2 = params.getJSONObject(1);
        this.y = VMInstructionUtils.parseVMRegistry(param2, registries);
        JSONObject param3 = params.getJSONObject(2);
        this.life = VMInstructionUtils.parseVMRegistry(param3, registries);
        JSONObject param4 = params.getJSONObject(3);
        this.abilityAmountLeft = VMInstructionUtils.parseVMRegistry(param4, registries);
        this.entity = entity;
    }

    @Override
    public void execute() throws VMException {
        this.x.setData(entity.currentX);
        this.y.setData(entity.currentY);
        this.life.setData(entity.life);
        this.abilityAmountLeft.setData(entity.getAbilityUsesRemaining());
    }

    @Override
    public int getGasCost() {
        return 4;
    }
}
