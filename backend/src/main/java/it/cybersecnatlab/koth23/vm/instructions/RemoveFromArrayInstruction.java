package it.cybersecnatlab.koth23.vm.instructions;

import it.cybersecnatlab.koth23.vm.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class RemoveFromArrayInstruction implements VMInstruction {
    VMRegistry op1;
    VMInstructionParam op2;

    public RemoveFromArrayInstruction(JSONArray params, List<VMRegistry> registries) throws JSONException {
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
        if (firstData instanceof Map<?, ?>) {
            ((Map<?, ?>) firstData).remove(op2.getIntegerValue());
            return;
        }
        throw new VMException("Cannot remove from array!");
    }

    @Override
    public int getGasCost() {
        return 1;
    }
}


