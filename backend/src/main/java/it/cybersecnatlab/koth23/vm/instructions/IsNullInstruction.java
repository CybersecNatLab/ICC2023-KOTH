package it.cybersecnatlab.koth23.vm.instructions;

import it.cybersecnatlab.koth23.vm.VMException;
import it.cybersecnatlab.koth23.vm.VMInstruction;
import it.cybersecnatlab.koth23.vm.VMInstructionUtils;
import it.cybersecnatlab.koth23.vm.VMRegistry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class IsNullInstruction implements VMInstruction {
    VMRegistry op1;
    VMRegistry cmpRegistry;

    public IsNullInstruction(JSONArray params, List<VMRegistry> registries, VMRegistry cmpRegistry) throws JSONException {
        if (params.length() != 1) {
            throw new JSONException("Wrong params number");
        }
        JSONObject param1 = params.getJSONObject(0);
        this.op1 = VMInstructionUtils.parseVMRegistry(param1, registries);
        this.cmpRegistry = cmpRegistry;
    }

    @Override
    public void execute() throws VMException {
        cmpRegistry.setData(op1.getObject() == null);
    }

    @Override
    public int getGasCost() {
        return 1;
    }
}


