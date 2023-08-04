
from config import *
import logging
import verboselogs
import coloredlogs
import json

logger = verboselogs.VerboseLogger('VMCompiler')
logger.addHandler(logging.StreamHandler())
logger.setLevel(logging.DEBUG)
coloredlogs.install(level='DEBUG', logger=logger)


class Compiler:
    code = None
    registries = []
    cmpRegistry = None
    compiled_code = None

    def __init__(self, file):
        data = open(file, "r").readlines()
        self.code = [d.strip() for d in data if d.strip() != ""]
        for i in range(n_registries):
            self.registries.append(Registry(f"r{i}", i))
        self.cmpRegistry = Registry("cmpRegistry", -1)

    def compile(self):
        labels = {}
        code_without_labels = []
        compiled_code = []
        jmp_row_number = 0
        for row_number in range(len(self.code)):
            data = self.code[row_number].split(";")  # remove comment
            line = data[0]
            if (line.strip() != ""):
                if (line.strip()[0] == ":"):  # label
                    if (line.strip()[1:] in labels):
                        logger.critical(
                            f"SyntaxError, duplicate label {line.strip()[1:]} at line {row_number}, previous was at line {labels[line.strip()[1:]]}, near {line.strip()}")
                        exit(-1)
                    labels[line.strip()[1:]] = jmp_row_number
                else:
                    jmp_row_number += 1
                    code_without_labels.append(line)
        for row_number in range(len(code_without_labels)):
            data = code_without_labels[row_number].split(";")  # remove comment
            line = data[0]
            instruction_name = line.split(" ")[0]
            parameters = " ".join(line.split(" ")[1:])
            instruction_name = instruction_name.strip().lower()
            parameters = parameters.strip()
            instruction = None
            if instruction_name == "AddBooleanToArray".lower():
                instruction = AddBooleanToArray()
            elif instruction_name == "AddFloatToArray".lower():
                instruction = AddFloatToArray()
            elif instruction_name == "AddIntegerToArray".lower():
                instruction = AddIntegerToArray()
            elif instruction_name == "AddStringToArray".lower():
                instruction = AddStringToArray()
            elif instruction_name == "GetFromArray".lower():
                instruction = GetFromArray()
            elif instruction_name == "RemoveFromArray".lower():
                instruction = RemoveFromArray()
            elif instruction_name == "SetBooleanToArray".lower():
                instruction = SetBooleanToArray()
            elif instruction_name == "SetFloatToArray".lower():
                instruction = SetFloatToArray()
            elif instruction_name == "SetIntegerToArray".lower():
                instruction = SetIntegerToArray()
            elif instruction_name == "SetStringToArray".lower():
                instruction = SetStringToArray()
            elif instruction_name == "InitBoolean".lower():
                instruction = InitBoolean()
            elif instruction_name == "InitBooleanArray".lower():
                instruction = InitBooleanArray()
            elif instruction_name == "InitFloat".lower():
                instruction = InitFloat()
            elif instruction_name == "InitFloatArray".lower():
                instruction = InitFloatArray()
            elif instruction_name == "InitInteger".lower():
                instruction = InitInteger()
            elif instruction_name == "InitIntegerArray".lower():
                instruction = InitIntegerArray()
            elif instruction_name == "InitString".lower():
                instruction = InitString()
            elif instruction_name == "InitStringArray".lower():
                instruction = InitStringArray()
            elif instruction_name == "Add".lower():
                instruction = Add()
            elif instruction_name == "Sub".lower():
                instruction = Sub()
            elif instruction_name == "Mul".lower():
                instruction = Mul()
            elif instruction_name == "Div".lower():
                instruction = Div()
            elif instruction_name == "Mod".lower():
                instruction = Mod()
            elif instruction_name == "Pow".lower():
                instruction = Pow()
            elif instruction_name == "Sqrt".lower():
                instruction = Sqrt()
            elif instruction_name == "And".lower():
                instruction = And()
            elif instruction_name == "Or".lower():
                instruction = Or()
            elif instruction_name == "Not".lower():
                instruction = Not()
            elif instruction_name == "Xor".lower():
                instruction = Xor()
            elif instruction_name == "Neg".lower():
                instruction = Neg()
            elif instruction_name == "Shr".lower():
                instruction = Shr()
            elif instruction_name == "Shl".lower():
                instruction = Shl()
            elif instruction_name == "Sar".lower():
                instruction = Sar()
            elif instruction_name == "Ceil".lower():
                instruction = Ceil()
            elif instruction_name == "Floor".lower():
                instruction = Floor()
            elif instruction_name == "Round".lower():
                instruction = Round()
            elif instruction_name == "Mov".lower():
                instruction = Mov()
            elif instruction_name == "Rnd".lower():
                instruction = Rnd()
            elif instruction_name == "IsNull".lower():
                instruction = IsNull()
            elif instruction_name == "GetTileInformation".lower():
                instruction = GetTileInformation()
            elif instruction_name == "GetOwnInformation".lower():
                instruction = GetOwnInformation()
            elif instruction_name == "JMP".lower():
                instruction = JMP()
            elif instruction_name == "JMPEQ0".lower():
                instruction = JMPEQ0()
            elif instruction_name == "JMPNEQ0".lower():
                instruction = JMPNEQ0()
            elif instruction_name == "JMPL0".lower():
                instruction = JMPL0()
            elif instruction_name == "JMPG0".lower():
                instruction = JMPG0()
            elif instruction_name == "CMP".lower():
                instruction = CMP()
            elif instruction_name == "MoveTo".lower():
                instruction = MoveTo()
            elif instruction_name == "UseAbility".lower():
                instruction = UseAbility()
            elif instruction_name == "DoNothing".lower():
                instruction = DoNothing()
            elif instruction_name == "Log".lower():
                instruction = Log()
            else:
                logger.critical(
                    f"SyntaxError, instruction {instruction_name} at line {row_number} is not valid, near {line.strip()}")
                exit(-1)
            if (instruction.instruction in [Instructions.JMP, Instructions.JMPEQ0, Instructions.JMPNEQ0, Instructions.JMPG0, Instructions.JMPL0]):
                jumpto = parameters.strip()
                if (jumpto not in labels):
                    logger.critical(
                        f"SyntaxError, label {jumpto} not defined, at line {row_number}, near {line.strip()}")
                    exit(-1)
                jumpto = labels[jumpto]
                compiled_code.append({"instruction": instruction.instruction.value[0], "params": [
                                     {"type": typeMap[Types.INTEGER], "value":jumpto}]})
            else:
                # Amount of expected parameters
                param_number = len(instruction.params)
                params = [x.strip() for x in parameters.split(",")]
                if (param_number == 0 and len(params) > 1):
                    logger.critical(
                        f"SyntaxError, expected {param_number} parameters for instruction {instruction_name} at line {row_number} but found {len(params)} instead, near {line.strip()}")
                    exit(-1)
                if (param_number == 0 and len(params) <= 1):
                    compiled_code.append(
                        {"instruction": instruction.instruction.value[0], "params": []})
                else:
                    if (len(params) != param_number):
                        logger.critical(
                            f"SyntaxError, expected {param_number} parameters for instruction {instruction_name} at line {row_number} but found {len(params)} instead, near {line.strip()}")
                        exit(-1)
                    param_values = []  # values of parameters
                    param_types = []  # types of parameters
                    for param in params:
                        if (param == ""):
                            logger.critical(
                                f"SyntaxError, empty parameter for instruction {instruction_name} at line {row_number}, near {line.strip()}")
                            exit(-1)
                        else:
                            if (param[0] == "r"):  # if a registry
                                try:
                                    registry_number = int(param[1:])
                                    if (not (registry_number >= 0 and registry_number < n_registries)):
                                        logger.critical(
                                            f"SyntaxError, registry {param} for instruction {instruction_name} at line {row_number} is not a valid registry, near {line.strip()}")
                                        exit(-1)
                                    param_values.append(
                                        self.registries[registry_number])
                                    param_types.append(Types.REGISTRY)
                                except ValueError:
                                    logger.critical(
                                        f"SyntaxError, registry {param} for instruction {instruction_name} at line {row_number} is not a valid registry, near {line.strip()}")
                                    exit(-1)
                            elif (param[0] == "#"):  # if a number or float
                                try:
                                    value = int(param[1:])
                                    param_values.append(value)
                                    param_types.append(Types.INTEGER)
                                except ValueError:
                                    try:
                                        value = float(param[1:])
                                        param_values.append(value)
                                        param_types.append(Types.FLOAT)
                                    except ValueError:
                                        logger.critical(
                                            f"SyntaxError, value {param} for instruction {instruction_name} at line {row_number} is not a valid integer or float, near {line.strip()}")
                                        exit(-1)
                            elif (param[0] == "\""):  # if a string
                                if (param[-1] != "\""):
                                    logger.critical(
                                        f"SyntaxError, value {param} for instruction {instruction_name} at line {row_number} is not a valid string, near {line.strip()}")
                                    exit(-1)
                                value = param[1:-1]
                                param_values.append(value)
                                param_types.append(Types.STRING)
                            elif (param.lower() == "true" or param.lower() == "false"):  # if a boolean
                                param_values.append(param.lower() == "true")
                                param_types.append(Types.BOOLEAN)
                            else:
                                logger.critical(
                                    f"SyntaxError, value {param} for instruction {instruction_name} at line {row_number} is not a valid registry, integer, float, string or boolean, near {line.strip()}")
                                exit(-1)
                    if (len(param_values) != param_number):  # check length
                        logger.critical(
                            f"SyntaxError, expected {param_number} parameters for instruction {instruction_name} at line {row_number} but found {len(param_values)} instead, near {line.strip()}")
                        exit(-1)
                    expected_registry_or_not = instruction.paramsTypes
                    if (len(expected_registry_or_not) != param_number):  # must be same
                        logger.critical(
                            f"Compiler error, instruction {instruction_name} is badly formatted, near {line.strip()}")
                        exit(-1)
                    # for each parameter, check if it must be a registry and if it is
                    for i in range(len(expected_registry_or_not)):
                        if (expected_registry_or_not[i] == ParamTypes.REGISTRY and param_types[i] != Types.REGISTRY):
                            logger.critical(
                                f"SyntaxError, parameter number {i} for instruction {instruction_name} at line {row_number} is expected to be a registry, found {param_types[i]} instead, near {line.strip()}")
                            exit(-1)
                    expected_types = instruction.params
                    for i in range(len(expected_types)):
                        true_type = param_types[i]  # get type of parameter
                        if (param_types[i] != Types.REGISTRY):
                            # wrong immediate, critical
                            if (true_type not in expected_types[i]):
                                logger.critical(
                                    f"SyntaxError, parameter number {i} for instruction {instruction_name} at line {row_number} is expected to be of type {expected_types[i]}, found {true_type} instead. Near {line.strip()}")
                                exit(-1)
                    instruction.update_registries_types(
                        param_values, self.cmpRegistry)
                    final_params = []
                    for i in range(param_number):
                        value = param_values[i]
                        if (param_types[i] == Types.REGISTRY):
                            value = param_values[i].number
                        final_params.append(
                            {"type": typeMap[param_types[i]], "value": value})
                    compiled_code.append(
                        {"instruction": instruction.instruction.value[0], "params": final_params})
        self.compiled_code = compiled_code

    def write_to_file(self, file):
        with open(file, "w") as f:
            f.write(json.dumps(self.compiled_code))

    def get_code(self):
        return self.compiled_code


if __name__ == '__main__':
    import sys
    c = Compiler(sys.argv[1])
    c.compile()
    print(c.get_code())
    c.write_to_file(sys.argv[2])
