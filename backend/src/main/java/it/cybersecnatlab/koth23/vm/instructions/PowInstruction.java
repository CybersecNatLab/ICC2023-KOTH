package it.cybersecnatlab.koth23.vm.instructions;

import it.cybersecnatlab.koth23.vm.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PowInstruction implements VMInstruction {
    VMRegistry op1;
    VMInstructionParam op2;
    VMRegistry op3;

    public PowInstruction(JSONArray params, List<VMRegistry> registries) throws JSONException {
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
            op3.setData(Math.round(Math.pow(op1.getInteger(), op2.getIntegerValue())));
            return;
        }
        if (firstData instanceof Float) {
            op3.setData((float) Math.pow(op1.getFloat(), op2.getFloatValue()));
            return;
        }
        throw new VMException("Cannot pow types!");
    }

    @Override
    public int getGasCost() throws VMException {
        Object firstData = op1.getObject();
        Integer exponent = null;
        if (firstData instanceof Integer) {
            exponent = op2.getIntegerValue();
        } else if (firstData instanceof Float) {
            exponent = Math.round(op2.getFloatValue());
        }
        if (exponent == null) {
            throw new VMException("Cannot get GAS for pow");
        }
        if (exponent == 0) {
            return 10;
        } else {
            return (int) Math.floor(10 + 10 * (1 + Math.log(exponent) / Math.log(256)));
        }
    }
}


