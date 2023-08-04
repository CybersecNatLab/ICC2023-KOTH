package it.cybersecnatlab.koth23.vm.instructions;

import it.cybersecnatlab.koth23.vm.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class UseAbilityInstruction implements VMInstruction {
    VMInstructionParam op1;

    public UseAbilityInstruction(JSONArray params, List<VMRegistry> registries) throws JSONException {
        if (params.length() != 1) {
            throw new JSONException("Wrong params number");
        }
        JSONObject param1 = params.getJSONObject(0);
        this.op1 = VMInstructionUtils.parseVMInstructionParam(param1, registries);
    }

    @Override
    public void execute() throws VMException {

    }

    public int[] abilityInfo() throws VMException {
        return new int[]{op1.getIntegerValue()};
    }

    @Override
    public int getGasCost() {
        return 0;
    }

}


