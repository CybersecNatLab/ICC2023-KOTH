package it.cybersecnatlab.koth23.vm.instructions;

import it.cybersecnatlab.koth23.vm.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RndInstruction implements VMInstruction {
    VMInstructionParam op1;
    VMInstructionParam op2;
    VMRegistry op3;

    public RndInstruction(JSONArray params, List<VMRegistry> registries) throws JSONException {
        if (params.length() != 3) {
            throw new JSONException("Wrong params number");
        }
        JSONObject param1 = params.getJSONObject(0);
        this.op1 = VMInstructionUtils.parseVMInstructionParam(param1, registries);
        JSONObject param2 = params.getJSONObject(1);
        this.op2 = VMInstructionUtils.parseVMInstructionParam(param2, registries);
        JSONObject param3 = params.getJSONObject(2);
        this.op3 = VMInstructionUtils.parseVMRegistry(param3, registries);
    }

    @Override
    public void execute() throws VMException {
        Integer min = op1.getIntegerValue();
        Integer max = op2.getIntegerValue();
        op3.setData((int) ((Math.random() * (max - min)) + min));
    }

    @Override
    public int getGasCost() {
        return 1;
    }
}


