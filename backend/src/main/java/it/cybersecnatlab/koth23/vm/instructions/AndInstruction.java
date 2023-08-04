package it.cybersecnatlab.koth23.vm.instructions;

import it.cybersecnatlab.koth23.vm.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AndInstruction implements VMInstruction {
    VMRegistry op1;
    VMInstructionParam op2;
    VMRegistry op3;

    public AndInstruction(JSONArray params, List<VMRegistry> registries) throws JSONException {
        if (params.length() != 3) {
            throw new JSONException("Wrong params number");
        }
        JSONObject param1 = params.getJSONObject(0);
        this.op1 = VMInstructionUtils.parseVMRegistry(param1, registries);
        JSONObject param2 = params.getJSONObject(1);
        this.op2 = VMInstructionUtils.parseVMInstructionParam(param2, registries);
        JSONObject param3 = params.getJSONObject(2);
        this.op3 = VMInstructionUtils.parseVMRegistry(param3, registries);
    }

    @Override
    public void execute() throws VMException {
        Object firstData = op1.getObject();
        if (firstData instanceof Integer) {
            op3.setData(op1.getInteger() & op2.getIntegerValue());
            return;
        }
        if (firstData instanceof Boolean) {
            op3.setData(op1.getBoolean() & op2.getBooleanValue());
            return;
        }
        throw new VMException("Cannot and types!");
    }

    @Override
    public int getGasCost() {
        return 1;
    }
}


