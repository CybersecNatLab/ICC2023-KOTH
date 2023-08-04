package it.cybersecnatlab.koth23.vm.instructions;

import it.cybersecnatlab.koth23.vm.*;
import it.cybersecnatlab.koth23.models.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class LogInstruction implements VMInstruction {
    VMInstructionParam op1;
    VMEntity entity;

    public LogInstruction(JSONArray params, List<VMRegistry> registries, VMEntity entity) throws JSONException {
        if (params.length() != 1) {
            throw new JSONException("Wrong params number");
        }
        JSONObject param1 = params.getJSONObject(0);
        this.op1 = VMInstructionUtils.parseVMInstructionParam(param1, registries);
        this.entity = entity;
    }

    @Override
    public void execute() throws VMException {
        entity.logs.add(op1.getStringValue());
    }

    @Override
    public int getGasCost() {
        return 1;
    }
}


