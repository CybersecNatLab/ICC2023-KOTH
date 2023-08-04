package it.cybersecnatlab.koth23.vm.instructions;

import it.cybersecnatlab.koth23.vm.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class AddIntegerToArrayInstruction implements VMInstruction {
    VMRegistry op1;
    VMInstructionParam op2;

    public AddIntegerToArrayInstruction(JSONArray params, List<VMRegistry> registries) throws JSONException {
        if (params.length() != 2) {
            throw new JSONException("Wrong params number");
        }
        JSONObject param1 = params.getJSONObject(0);
        this.op1 = VMInstructionUtils.parseVMRegistry(param1, registries);
        JSONObject param2 = params.getJSONObject(1);
        this.op2 = VMInstructionUtils.parseVMInstructionParam(param2, registries);
    }

    @Override
    public void execute() throws VMException {
        Object firstData = op1.getObject();
        if (firstData instanceof java.util.Map<?, ?>) {
            ((java.util.Map<Integer, Integer>) firstData).put(((Map<Integer, Integer>) firstData).size(), op2.getIntegerValue());
            return;
        }
        throw new VMException("Cannot add Integer to array!");
    }

    @Override
    public int getGasCost() {
        return 2;
    }
}


