package it.cybersecnatlab.koth23.vm.instructions;

import it.cybersecnatlab.koth23.vm.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MoveToInstruction implements VMInstruction {
    VMInstructionParam op1;
    VMInstructionParam op2;

    public MoveToInstruction(JSONArray params, List<VMRegistry> registries) throws JSONException {
        if (params.length() != 2) {
            throw new JSONException("Wrong params number");
        }
        JSONObject param1 = params.getJSONObject(0);
        this.op1 = VMInstructionUtils.parseVMInstructionParam(param1, registries);
        JSONObject param2 = params.getJSONObject(1);
        this.op2 = VMInstructionUtils.parseVMInstructionParam(param2, registries);
    }

    @Override
    public void execute() throws VMException {

    }

    public int[] moveTowardsLocation() throws VMException {
        return new int[]{op1.getIntegerValue(), op2.getIntegerValue()};
    }

    @Override
    public int getGasCost() {
        return 0;
    }
}


