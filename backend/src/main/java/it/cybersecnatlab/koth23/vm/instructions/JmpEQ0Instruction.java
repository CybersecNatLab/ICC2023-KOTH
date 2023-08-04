package it.cybersecnatlab.koth23.vm.instructions;

import it.cybersecnatlab.koth23.vm.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class JmpEQ0Instruction implements VMInstruction {
    VMInstructionParam op1;
    VMRegistry cmpRegistry;

    public JmpEQ0Instruction(JSONArray params, List<VMRegistry> registries, VMRegistry cmpRegistry) throws JSONException {
        if (params.length() != 1) {
            throw new JSONException("Wrong params number");
        }
        JSONObject param1 = params.getJSONObject(0);
        this.op1 = VMInstructionUtils.parseVMInstructionParamNoRegistry(param1, registries);
        this.cmpRegistry = cmpRegistry;
    }

    @Override
    public void execute() throws VMException {
    }

    public Integer jumpLocation(Integer instruction_pointer) throws VMException {
        Object firstValue = this.cmpRegistry.getObject();
        if (firstValue == null) {
            return instruction_pointer + 1;
        }
        if (firstValue instanceof Integer && ((Integer) firstValue) == 0) {
            return op1.getIntegerValue();
        }
        if (firstValue instanceof Float && ((Float) firstValue) == 0.0) {
            return op1.getIntegerValue();
        }
        if (firstValue instanceof Boolean && ((Boolean) firstValue)) {
            return op1.getIntegerValue();
        }
        if (firstValue instanceof String && firstValue.equals("")) {
            return op1.getIntegerValue();
        }
        return instruction_pointer + 1;
    }

    @Override
    public int getGasCost() {
        return 3;
    }

}


