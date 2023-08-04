package it.cybersecnatlab.koth23.vm.instructions;

import it.cybersecnatlab.koth23.vm.VMException;
import it.cybersecnatlab.koth23.vm.VMInstruction;
import it.cybersecnatlab.koth23.vm.VMInstructionUtils;
import it.cybersecnatlab.koth23.vm.VMRegistry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class InitFloatArrayInstruction implements VMInstruction {
    VMRegistry registry;

    public InitFloatArrayInstruction(JSONArray params, List<VMRegistry> registries) throws JSONException {
        if (params.length() != 1) {
            throw new JSONException("Wrong params number");
        }
        JSONObject param1 = params.getJSONObject(0);
        this.registry = VMInstructionUtils.parseVMRegistry(param1, registries);
    }

    @Override
    public void execute() throws VMException {
        registry.setData(new HashMap<Integer, Float>());
    }

    @Override
    public int getGasCost() {
        return 1;
    }
}
