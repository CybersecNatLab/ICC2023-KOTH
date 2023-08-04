package it.cybersecnatlab.koth23.vm;

import it.cybersecnatlab.koth23.Config;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public final class VMInstructionUtils {

    private VMInstructionUtils() {
    }

    public static VMInstructionParam parseVMInstructionParam(JSONObject param, List<VMRegistry> registryList) {
        if (!(param.has("type") && param.has("value") && param.getInt("type") >= 0 && param.getInt("type") < ParameterType.values().length)) {
            throw new JSONException("Wrong param");
        }

        var type = VMInstructionUtils.ParameterType.values()[param.getInt("type")];
        switch (type) {
            case REGISTRY -> {
                int reg = param.getInt("value");
                if (reg >= 0 && reg < Config.VM_REGISTRIES_NUMBER) {
                    return new VMInstructionParam<>(registryList.get(reg));
                }
            }
            case INTEGER -> {
                Integer ival = param.getInt("value");
                return new VMInstructionParam<>(ival);
            }
            case FLOAT -> {
                Float fval = param.getFloat("value");
                return new VMInstructionParam<>(fval);
            }
            case BOOLEAN -> {
                Boolean bval = param.getBoolean("value");
                return new VMInstructionParam<>(bval);
            }
            case STRING -> {
                String sval = param.getString("value");
                return new VMInstructionParam<>(sval);
            }
        }

        throw new JSONException("Could not parse param");
    }

    public static VMInstructionParam parseVMInstructionParamNoRegistry(JSONObject param, List<VMRegistry> registryList) {
        if (!(param.has("type") && param.has("value") && param.getInt("type") >= 0 && param.getInt("type") < ParameterType.values().length) && ParameterType.values()[param.getInt("type")] != ParameterType.REGISTRY) {
            throw new JSONException("Wrong param");
        }

        return parseVMInstructionParam(param, registryList);
    }

    public static VMRegistry parseVMRegistry(JSONObject param, List<VMRegistry> registryList) throws JSONException {
        if (!(param.has("type") && param.has("value") && param.getInt("type") >= 0 && param.getInt("type") < ParameterType.values().length && VMInstructionUtils.ParameterType.values()[param.getInt("type")] == ParameterType.REGISTRY)) {
            throw new JSONException("Wrong param");
        }

        int reg = param.getInt("value");
        if (reg >= 0 && reg < Config.VM_REGISTRIES_NUMBER) {
            return registryList.get(reg);
        }

        throw new JSONException("Could not parse param");
    }

    public enum ParameterType {
        REGISTRY,
        INTEGER,
        FLOAT,
        BOOLEAN,
        STRING
    }
}
