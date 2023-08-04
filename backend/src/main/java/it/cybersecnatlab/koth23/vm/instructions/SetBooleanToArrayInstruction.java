package it.cybersecnatlab.koth23.vm.instructions;

import it.cybersecnatlab.koth23.vm.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class SetBooleanToArrayInstruction implements VMInstruction {
    VMRegistry op1;
    VMInstructionParam op2;
    VMInstructionParam op3;

    public SetBooleanToArrayInstruction(JSONArray params, List<VMRegistry> registries) throws JSONException {
        if (params.length() != 3) {
            throw new JSONException("Wrong params number");
        }
        JSONObject param1 = params.getJSONObject(0);
        this.op1 = VMInstructionUtils.parseVMRegistry(param1, registries);
        JSONObject param2 = params.getJSONObject(1);
        this.op2 = VMInstructionUtils.parseVMInstructionParam(param2, registries);
        JSONObject param3 = params.getJSONObject(2);
        this.op3 = VMInstructionUtils.parseVMInstructionParam(param3, registries);
    }

    @Override
    public void execute() throws VMException {
        Object firstData = op1.getObject();
        if (firstData instanceof Map<?, ?>) {
            ((Map<Integer, Boolean>) firstData).put(op2.getIntegerValue(), op3.getBooleanValue());
            return;
        }
        throw new VMException("Cannot set boolean to array!");
    }

    @Override
    public int getGasCost() {
        return 1;
    }
}


