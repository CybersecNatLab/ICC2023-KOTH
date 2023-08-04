package it.cybersecnatlab.koth23.vm.instructions;

import it.cybersecnatlab.koth23.vm.VMException;
import it.cybersecnatlab.koth23.vm.VMInstruction;
import it.cybersecnatlab.koth23.vm.VMInstructionUtils;
import it.cybersecnatlab.koth23.vm.VMRegistry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SqrtInstruction implements VMInstruction {
    VMRegistry op1;
    VMRegistry op2;

    public SqrtInstruction(JSONArray params, List<VMRegistry> registries) throws JSONException {
        if (params.length() != 2) {
            throw new JSONException("Wrong params number");
        }
        JSONObject param1 = params.getJSONObject(0);
        this.op1 = VMInstructionUtils.parseVMRegistry(param1, registries);
        JSONObject param2 = params.getJSONObject(1);
        this.op2 = VMInstructionUtils.parseVMRegistry(param2, registries);
    }

    @Override
    public void execute() throws VMException {
        Object firstData = op1.getObject();
        if (firstData instanceof Integer) {
            op2.setData(Float.valueOf((float) Math.sqrt(op1.getInteger())));
            return;
        }
        if (firstData instanceof Float) {
            op2.setData(Float.valueOf((float) Math.sqrt(op1.getFloat())));
            return;
        }
        throw new VMException("Cannot sqrt types!");
    }

    @Override
    public int getGasCost() {
        return 10;
    }
}


