package it.cybersecnatlab.koth23.vm.instructions;

import it.cybersecnatlab.koth23.vm.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CmpInstruction implements VMInstruction {
    VMRegistry op1;
    VMInstructionParam op2;
    VMRegistry op3;

    public CmpInstruction(JSONArray params, List<VMRegistry> registries, VMRegistry cmpRegistry) throws JSONException {
        if (params.length() != 2) {
            throw new JSONException("Wrong params number");
        }
        JSONObject param1 = params.getJSONObject(0);
        this.op1 = VMInstructionUtils.parseVMRegistry(param1, registries);
        JSONObject param2 = params.getJSONObject(1);
        this.op2 = VMInstructionUtils.parseVMInstructionParam(param2, registries);
        this.op3 = cmpRegistry;
    }

    @Override
    public void execute() throws VMException {
        Object firstData = op1.getObject();
        if (firstData instanceof Integer) {
            op3.setData(op1.getInteger() - op2.getIntegerValue());
            return;
        }
        if (firstData instanceof Float) {
            op3.setData(op1.getFloat() - op2.getFloatValue());
            return;
        }
        if (firstData instanceof Boolean) {
            op3.setData(op1.getBoolean() == op2.getBooleanValue());
            return;
        }
        if (firstData instanceof String) {
            op3.setData(op1.getString().equals(op2.getStringValue()));
            return;
        }
        throw new VMException("Cannot cmp types!");
    }

    @Override
    public int getGasCost() {
        return 1;
    }
}


