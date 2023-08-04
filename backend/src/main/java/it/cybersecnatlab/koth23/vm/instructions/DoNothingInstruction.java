package it.cybersecnatlab.koth23.vm.instructions;

import it.cybersecnatlab.koth23.vm.VMException;
import it.cybersecnatlab.koth23.vm.VMInstruction;
import it.cybersecnatlab.koth23.vm.VMRegistry;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class DoNothingInstruction implements VMInstruction {
    public DoNothingInstruction(JSONArray params, List<VMRegistry> registries) throws JSONException {
        if (params.length() != 0) {
            throw new JSONException("Wrong params number");
        }
    }

    @Override
    public void execute() throws VMException {
    }

    @Override
    public int getGasCost() {
        return 0;
    }
}


