package it.cybersecnatlab.koth23.vm;

import it.cybersecnatlab.koth23.Config;
import it.cybersecnatlab.koth23.models.GameMap;
import it.cybersecnatlab.koth23.models.VMEntity;
import it.cybersecnatlab.koth23.vm.instructions.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class VMExecutionEngine {
    private static final Logger LOGGER = Logger.getLogger(VMExecutionEngine.class.getSimpleName());
    private final List<VMInstruction> code = new ArrayList<>();
    private final List<VMRegistry> registries = new ArrayList<>();
    private final VMRegistry cmpRegistry = new VMRegistry("cmpRegistry");

    public VMExecutionEngine(JSONArray instructions, GameMap map, VMEntity entity) throws JSONException {
        for (int i = 0; i < Config.VM_REGISTRIES_NUMBER; i++) {
            registries.add(new VMRegistry("r" + i));
        }

        for (Object _item : instructions) {
            if (_item instanceof JSONObject item) {
                if (!item.has("instruction")) {
                    continue;
                }

                int _instruction = item.getInt("instruction");
                if (_instruction < 0 || _instruction >= Instructions.values().length) {
                    continue;
                }

                Instructions instruction = Instructions.values()[_instruction];
                JSONArray params = item.getJSONArray("params");

                LOGGER.info("Parsing instruction %s".formatted(instruction.name()));

                VMInstruction vmInstruction = switch (instruction) {
                    case AddBooleanToArray -> new AddBooleanToArrayInstruction(params, registries);
                    case AddFloatToArray -> new AddFloatToArrayInstruction(params, registries);
                    case AddIntegerToArray -> new AddIntegerToArrayInstruction(params, registries);
                    case AddStringToArray -> new AddStringToArrayInstruction(params, registries);
                    case GetFromArray -> new GetFromArrayInstruction(params, registries);
                    case RemoveFromArray -> new RemoveFromArrayInstruction(params, registries);
                    case SetBooleanToArray -> new SetBooleanToArrayInstruction(params, registries);
                    case SetFloatToArray -> new SetFloatToArrayInstruction(params, registries);
                    case SetIntegerToArray -> new SetIntegerToArrayInstruction(params, registries);
                    case SetStringToArray -> new SetStringToArrayInstruction(params, registries);
                    case InitBoolean -> new InitBooleanInstruction(params, registries);
                    case InitBooleanArray -> new InitBooleanArrayInstruction(params, registries);
                    case InitFloat -> new InitFloatInstruction(params, registries);
                    case InitFloatArray -> new InitFloatArrayInstruction(params, registries);
                    case InitInteger -> new InitIntegerInstruction(params, registries);
                    case InitIntegerArray -> new InitIntegerArrayInstruction(params, registries);
                    case InitString -> new InitStringInstruction(params, registries);
                    case InitStringArray -> new InitStringArrayInstruction(params, registries);
                    case Add -> new AddInstruction(params, registries);
                    case Sub -> new SubInstruction(params, registries);
                    case Mul -> new MulInstruction(params, registries);
                    case Div -> new DivInstruction(params, registries);
                    case Mod -> new ModInstruction(params, registries);
                    case Pow -> new PowInstruction(params, registries);
                    case Sqrt -> new SqrtInstruction(params, registries);
                    case And -> new AndInstruction(params, registries);
                    case Or -> new OrInstruction(params, registries);
                    case Not -> new NotInstruction(params, registries);
                    case Xor -> new XorInstruction(params, registries);
                    case Neg -> new NegInstruction(params, registries);
                    case Shr -> new ShrInstruction(params, registries);
                    case Shl -> new ShlInstruction(params, registries);
                    case Sar -> new SarInstruction(params, registries);
                    case Ceil -> new CeilInstruction(params, registries);
                    case Floor -> new FloorInstruction(params, registries);
                    case Round -> new RoundInstruction(params, registries);
                    case Mov -> new MovInstruction(params, registries);
                    case Rnd -> new RndInstruction(params, registries);
                    case IsNull -> new IsNullInstruction(params, registries, cmpRegistry);
                    case GetTileInformation -> new GetTileInformationInstruction(params, registries, map);
                    case GetOwnInformation -> new GetOwnInformationInstruction(params, registries, entity);
                    case JMP -> new JmpInstruction(params, registries, cmpRegistry);
                    case JMPEQ0 -> new JmpEQ0Instruction(params, registries, cmpRegistry);
                    case JMPNEQ0 -> new JmpNEQ0Instruction(params, registries, cmpRegistry);
                    case JMPL0 -> new JmpL0Instruction(params, registries, cmpRegistry);
                    case JMPG0 -> new JmpG0Instruction(params, registries, cmpRegistry);
                    case CMP -> new CmpInstruction(params, registries, cmpRegistry);
                    case MoveTo -> new MoveToInstruction(params, registries);
                    case UseAbility -> new UseAbilityInstruction(params, registries);
                    case DoNothing -> new DoNothingInstruction(params, registries);
                    case Log -> new LogInstruction(params, registries, entity);
                };
                code.add(vmInstruction);
            }
        }
    }

    public int[] executeCode() throws VMException {
        cmpRegistry.setData(null);

        for (int i = 1; i < registries.size(); i++) {
            registries.get(i).setData(null);
        }

        int instructionPointer = 0;
        int gas = Config.VM_INITIAL_GAS;

        while (true) {
            if (instructionPointer < 0 || instructionPointer >= code.size()) {
                throw new VMException("Instruction pointer out of code");
            }

            VMInstruction instruction = code.get(instructionPointer);
            if (instruction.getGasCost() <= gas) {
                LOGGER.info("Executing instruction %s".formatted(instruction.getClass().getSimpleName()));

                gas -= instruction.getGasCost();
                instruction.execute();
                if (instruction instanceof MoveToInstruction moveToInstruction) {
                    return moveToInstruction.moveTowardsLocation();
                } else if (instruction instanceof UseAbilityInstruction useAbilityInstruction) {
                    return useAbilityInstruction.abilityInfo();
                } else if (instruction instanceof DoNothingInstruction) {
                    return new int[]{};
                }

                if (instruction instanceof JmpInstruction jmpInstruction) {
                    instructionPointer = jmpInstruction.jumpLocation();
                } else if (instruction instanceof JmpEQ0Instruction jmpEQ0Instruction) {
                    instructionPointer = jmpEQ0Instruction.jumpLocation(instructionPointer);
                } else if (instruction instanceof JmpNEQ0Instruction jmpNEQ0Instruction) {
                    instructionPointer = jmpNEQ0Instruction.jumpLocation(instructionPointer);
                } else if (instruction instanceof JmpL0Instruction jmpL0Instruction) {
                    instructionPointer = jmpL0Instruction.jumpLocation(instructionPointer);
                } else if (instruction instanceof JmpG0Instruction jmpG0Instruction) {
                    instructionPointer = jmpG0Instruction.jumpLocation(instructionPointer);
                } else {
                    instructionPointer += 1;
                }
            } else {
                throw new VMException("Gas limit reached");
            }
        }
    }

    public enum Instructions {
        AddBooleanToArray,
        AddFloatToArray,
        AddIntegerToArray,
        AddStringToArray,
        GetFromArray,
        RemoveFromArray,
        SetBooleanToArray,
        SetFloatToArray,
        SetIntegerToArray,
        SetStringToArray,
        InitBoolean,
        InitBooleanArray,
        InitFloat,
        InitFloatArray,
        InitInteger,
        InitIntegerArray,
        InitString,
        InitStringArray,
        Add,
        Sub,
        Mul,
        Div,
        Mod,
        Pow,
        Sqrt,
        And,
        Or,
        Not,
        Xor,
        Neg,
        Shr,
        Shl,
        Sar,
        Ceil,
        Floor,
        Round,
        Mov,
        Rnd,
        IsNull,
        GetTileInformation,
        GetOwnInformation,
        JMP,
        JMPEQ0,
        JMPNEQ0,
        JMPL0,
        JMPG0,
        CMP,
        MoveTo,
        UseAbility,
        DoNothing,
        Log
    }
}
