from enum import Enum, Flag, auto

n_registries=32

class Instructions(Enum):
    AddBooleanToArray = 0,
    AddFloatToArray = 1,
    AddIntegerToArray = 2,
    AddStringToArray = 3,
    GetFromArray = 4,
    RemoveFromArray = 5,
    SetBooleanToArray = 6,
    SetFloatToArray = 7,
    SetIntegerToArray = 8,
    SetStringToArray = 9,
    InitBoolean = 10,
    InitBooleanArray = 11,
    InitFloat = 12,
    InitFloatArray = 13,
    InitInteger = 14,
    InitIntegerArray = 15,
    InitString = 16,
    InitStringArray = 17,
    Add = 18,
    Sub = 19,
    Mul = 20,
    Div = 21,
    Mod = 22,
    Pow = 23,
    Sqrt = 24,
    And = 25,
    Or = 26,
    Not = 27,
    Xor = 28,
    Neg = 29,
    Shr = 30,
    Shl = 31,
    Sar = 32,
    Ceil = 33,
    Floor = 34,
    Round = 35,
    Mov = 36,
    Rnd = 37,
    IsNull = 38,
    GetTileInformation = 39,
    GetOwnInformation = 40,
    JMP = 41,
    JMPEQ0 = 42,
    JMPNEQ0 = 43,
    JMPL0 = 44,
    JMPG0 = 45,
    CMP = 46,
    MoveTo = 47,
    UseAbility = 48,
    DoNothing = 49,
    Log = 50,

class Types(Flag):
    REGISTRY=auto()
    INTEGER=auto()
    FLOAT=auto()
    BOOLEAN=auto()
    STRING=auto()
    LIST_OF_INTEGER=auto()
    LIST_OF_FLOAT=auto()
    LIST_OF_BOOLEAN=auto()
    LIST_OF_STRING=auto()
    NULL=auto()
    BOOLEAN_CASTABLE=INTEGER|FLOAT|BOOLEAN
    INTEGER_CASTABLE=INTEGER|FLOAT|BOOLEAN
    FLOAT_CASTABLE=INTEGER|FLOAT|BOOLEAN
    STRING_CASTABLE=INTEGER|FLOAT|BOOLEAN|STRING
    ANY=REGISTRY|INTEGER|FLOAT|BOOLEAN|STRING|LIST_OF_STRING|LIST_OF_BOOLEAN|LIST_OF_INTEGER|LIST_OF_FLOAT|NULL
    ANY_LIST=LIST_OF_STRING|LIST_OF_BOOLEAN|LIST_OF_INTEGER|LIST_OF_FLOAT

typeMap={Types.REGISTRY:0, Types.INTEGER:1, Types.FLOAT:2,Types.BOOLEAN:3,Types.STRING:4}

class ParamTypes(Enum):
    REGISTRY=0,
    REGISTRY_OR_IMMEDIATE=1,
    IMMEDIATE=2

class Registry():
    name=None
    number=-1
    currentType=Types.NULL

    def __init__(self,name, number):
        self.name=name
        self.number=number
    
    def setType(self,type):
        self.currentType=type
    
    def getType(self):
        return self.currentType

class Instruction():
    instruction=None
    params=[]

class AddBooleanToArray(Instruction):
    instruction=Instructions.AddBooleanToArray
    params=[Types.LIST_OF_BOOLEAN, Types.BOOLEAN_CASTABLE]
    paramsTypes=[ParamTypes.REGISTRY, ParamTypes.REGISTRY_OR_IMMEDIATE]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        return

class AddFloatToArray(Instruction):
    instruction=Instructions.AddFloatToArray
    params=[Types.LIST_OF_FLOAT, Types.FLOAT_CASTABLE]
    paramsTypes=[ParamTypes.REGISTRY, ParamTypes.REGISTRY_OR_IMMEDIATE]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        return

class AddIntegerToArray(Instruction):
    instruction=Instructions.AddIntegerToArray
    params=[Types.LIST_OF_INTEGER, Types.INTEGER_CASTABLE]
    paramsTypes=[ParamTypes.REGISTRY, ParamTypes.REGISTRY_OR_IMMEDIATE]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        return

class AddStringToArray(Instruction):
    instruction=Instructions.AddStringToArray
    params=[Types.LIST_OF_STRING, Types.STRING_CASTABLE]
    paramsTypes=[ParamTypes.REGISTRY, ParamTypes.REGISTRY_OR_IMMEDIATE]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        return

class GetFromArray(Instruction):
    instruction=Instructions.GetFromArray
    params=[Types.LIST_OF_BOOLEAN|Types.LIST_OF_FLOAT|Types.LIST_OF_INTEGER|Types.LIST_OF_STRING, Types.INTEGER_CASTABLE, Types.ANY]
    paramsTypes=[ParamTypes.REGISTRY, ParamTypes.REGISTRY_OR_IMMEDIATE, ParamTypes.REGISTRY]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        params[-1].currentType=params[0].currentType

class RemoveFromArray(Instruction):
    instruction=Instructions.RemoveFromArray
    params=[Types.ANY_LIST, Types.INTEGER_CASTABLE]
    paramsTypes=[ParamTypes.REGISTRY, ParamTypes.REGISTRY_OR_IMMEDIATE]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        return

class SetBooleanToArray(Instruction):
    instruction=Instructions.SetBooleanToArray
    params=[Types.LIST_OF_BOOLEAN, Types.INTEGER_CASTABLE, Types.BOOLEAN_CASTABLE]
    paramsTypes=[ParamTypes.REGISTRY, ParamTypes.REGISTRY_OR_IMMEDIATE, ParamTypes.REGISTRY_OR_IMMEDIATE]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        return

class SetFloatToArray(Instruction):
    instruction=Instructions.SetFloatToArray
    params=[Types.LIST_OF_FLOAT, Types.INTEGER_CASTABLE, Types.FLOAT_CASTABLE]
    paramsTypes=[ParamTypes.REGISTRY, ParamTypes.REGISTRY_OR_IMMEDIATE, ParamTypes.REGISTRY_OR_IMMEDIATE]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        return

class SetIntegerToArray(Instruction):
    instruction=Instructions.SetIntegerToArray
    params=[Types.LIST_OF_INTEGER, Types.INTEGER_CASTABLE, Types.INTEGER_CASTABLE]
    paramsTypes=[ParamTypes.REGISTRY, ParamTypes.REGISTRY_OR_IMMEDIATE, ParamTypes.REGISTRY_OR_IMMEDIATE]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        return

class SetStringToArray(Instruction):
    instruction=Instructions.SetStringToArray
    params=[Types.LIST_OF_STRING, Types.INTEGER_CASTABLE, Types.STRING_CASTABLE]
    paramsTypes=[ParamTypes.REGISTRY, ParamTypes.REGISTRY_OR_IMMEDIATE, ParamTypes.REGISTRY_OR_IMMEDIATE]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        return

class InitBoolean(Instruction):
    instruction=Instructions.InitBoolean
    params=[Types.ANY, Types.BOOLEAN_CASTABLE]
    paramsTypes=[ParamTypes.REGISTRY, ParamTypes.REGISTRY_OR_IMMEDIATE]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        params[0].currentType=Types.BOOLEAN_CASTABLE

class InitBooleanArray(Instruction):
    instruction=Instructions.InitBooleanArray
    params=[Types.ANY]
    paramsTypes=[ParamTypes.REGISTRY]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        params[0].currentType=Types.LIST_OF_BOOLEAN

class InitFloat(Instruction):
    instruction=Instructions.InitFloat
    params=[Types.ANY, Types.FLOAT_CASTABLE]
    paramsTypes=[ParamTypes.REGISTRY, ParamTypes.REGISTRY_OR_IMMEDIATE]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        params[0].currentType=Types.FLOAT_CASTABLE

class InitFloatArray(Instruction):
    instruction=Instructions.InitFloatArray
    params=[Types.ANY]
    paramsTypes=[ParamTypes.REGISTRY]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        params[0].currentType=Types.LIST_OF_FLOAT

class InitInteger(Instruction):
    instruction=Instructions.InitInteger
    params=[Types.ANY, Types.INTEGER_CASTABLE]
    paramsTypes=[ParamTypes.REGISTRY, ParamTypes.REGISTRY_OR_IMMEDIATE]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        params[0].currentType=Types.INTEGER_CASTABLE

class InitIntegerArray(Instruction):
    instruction=Instructions.InitIntegerArray
    params=[Types.ANY]
    paramsTypes=[ParamTypes.REGISTRY]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        params[0].currentType=Types.LIST_OF_INTEGER

class InitString(Instruction):
    instruction=Instructions.InitString
    params=[Types.ANY, Types.STRING_CASTABLE]
    paramsTypes=[ParamTypes.REGISTRY, ParamTypes.REGISTRY_OR_IMMEDIATE]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        params[0].currentType=Types.STRING_CASTABLE

class InitStringArray(Instruction):
    instruction=Instructions.InitStringArray
    params=[Types.ANY]
    paramsTypes=[ParamTypes.REGISTRY]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        params[0].currentType=Types.LIST_OF_STRING

class Add(Instruction):
    instruction=Instructions.Add
    params=[Types.INTEGER_CASTABLE|Types.FLOAT_CASTABLE|Types.STRING_CASTABLE,Types.ANY,Types.ANY]
    paramsTypes=[ParamTypes.REGISTRY, ParamTypes.REGISTRY_OR_IMMEDIATE, ParamTypes.REGISTRY]
    def check_cross_types(self, param_types, logger, warningStart):
        if(param_types[0]==Types.STRING_CASTABLE):
            return
        if(param_types[0]!=param_types[1]):
            logger.warning(f"{warningStart} parameters 0 and 1 are supposed to be same type but may be {param_types[0]} and {param_types[1]} instead")
    def update_registries_types(self, params, cmp_registry):
        params[-1].currentType=params[0].currentType

class Sub(Instruction):
    instruction=Instructions.Sub
    params=[Types.INTEGER_CASTABLE|Types.FLOAT_CASTABLE,Types.INTEGER_CASTABLE|Types.FLOAT_CASTABLE,Types.ANY]
    paramsTypes=[ParamTypes.REGISTRY, ParamTypes.REGISTRY_OR_IMMEDIATE, ParamTypes.REGISTRY]
    def check_cross_types(self, param_types, logger, warningStart):
        if(param_types[0]!=param_types[1]):
            logger.warning(f"{warningStart} parameters 0 and 1 are supposed to be same type but may be {param_types[0]} and {param_types[1]} instead")
    def update_registries_types(self, params, cmp_registry):
        params[-1].currentType=params[0].currentType

class Mul(Instruction):
    instruction=Instructions.Mul
    params=[Types.INTEGER_CASTABLE|Types.FLOAT_CASTABLE,Types.INTEGER_CASTABLE|Types.FLOAT_CASTABLE,Types.ANY]
    paramsTypes=[ParamTypes.REGISTRY, ParamTypes.REGISTRY_OR_IMMEDIATE, ParamTypes.REGISTRY]
    def check_cross_types(self, param_types, logger, warningStart):
        if(param_types[0]!=param_types[1]):
            logger.warning(f"{warningStart} parameters 0 and 1 are supposed to be same type but may be {param_types[0]} and {param_types[1]} instead")
    def update_registries_types(self, params, cmp_registry):
        params[-1].currentType=params[0].currentType

class Div(Instruction):
    instruction=Instructions.Div
    params=[Types.INTEGER_CASTABLE|Types.FLOAT_CASTABLE,Types.INTEGER_CASTABLE|Types.FLOAT_CASTABLE,Types.ANY]
    paramsTypes=[ParamTypes.REGISTRY, ParamTypes.REGISTRY_OR_IMMEDIATE, ParamTypes.REGISTRY]
    def check_cross_types(self, param_types, logger, warningStart):
        if(param_types[0]!=param_types[1]):
            logger.warning(f"{warningStart} parameters 0 and 1 are supposed to be same type but may be {param_types[0]} and {param_types[1]} instead")
    def update_registries_types(self, params, cmp_registry):
        params[-1].currentType=Types.FLOAT_CASTABLE

class Mod(Instruction):
    instruction=Instructions.Mod
    params=[Types.INTEGER_CASTABLE,Types.INTEGER_CASTABLE,Types.ANY]
    paramsTypes=[ParamTypes.REGISTRY, ParamTypes.REGISTRY_OR_IMMEDIATE, ParamTypes.REGISTRY]
    def check_cross_types(self, param_types, logger, warningStart):
        if(param_types[0]!=param_types[1]):
            logger.warning(f"{warningStart} parameters 0 and 1 are supposed to be same type but may be {param_types[0]} and {param_types[1]} instead")
    def update_registries_types(self, params, cmp_registry):
        params[-1].currentType=Types.INTEGER_CASTABLE

class Pow(Instruction):
    instruction=Instructions.Pow
    params=[Types.INTEGER_CASTABLE|Types.FLOAT_CASTABLE,Types.INTEGER_CASTABLE|Types.FLOAT_CASTABLE,Types.ANY]
    paramsTypes=[ParamTypes.REGISTRY, ParamTypes.REGISTRY_OR_IMMEDIATE, ParamTypes.REGISTRY]
    def check_cross_types(self, param_types, logger, warningStart):
        if(param_types[0]!=param_types[1]):
            logger.warning(f"{warningStart} parameters 0 and 1 are supposed to be same type but may be {param_types[0]} and {param_types[1]} instead")
    def update_registries_types(self, params, cmp_registry):
        params[-1].currentType=params[0].currentType

class Sqrt(Instruction):
    instruction=Instructions.Sqrt
    params=[Types.INTEGER_CASTABLE|Types.FLOAT_CASTABLE,Types.ANY]
    paramsTypes=[ParamTypes.REGISTRY, ParamTypes.REGISTRY]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        params[-1].currentType=Types.FLOAT_CASTABLE

class And(Instruction):
    instruction=Instructions.And
    params=[Types.INTEGER_CASTABLE|Types.BOOLEAN_CASTABLE,Types.INTEGER_CASTABLE|Types.BOOLEAN_CASTABLE,Types.ANY]
    paramsTypes=[ParamTypes.REGISTRY, ParamTypes.REGISTRY_OR_IMMEDIATE, ParamTypes.REGISTRY]
    def check_cross_types(self, param_types, logger, warningStart):
        if(param_types[0]!=param_types[1]):
            logger.warning(f"{warningStart} parameters 0 and 1 are supposed to be same type but may be {param_types[0]} and {param_types[1]} instead")
    def update_registries_types(self, params, cmp_registry):
        params[-1].currentType=params[0].currentType

class Or(Instruction):
    instruction=Instructions.Or
    params=[Types.INTEGER_CASTABLE|Types.BOOLEAN_CASTABLE,Types.INTEGER_CASTABLE|Types.BOOLEAN_CASTABLE,Types.ANY]
    paramsTypes=[ParamTypes.REGISTRY, ParamTypes.REGISTRY_OR_IMMEDIATE, ParamTypes.REGISTRY]
    def check_cross_types(self, param_types, logger, warningStart):
        if(param_types[0]!=param_types[1]):
            logger.warning(f"{warningStart} parameters 0 and 1 are supposed to be same type but may be {param_types[0]} and {param_types[1]} instead")
    def update_registries_types(self, params, cmp_registry):
        params[-1].currentType=params[0].currentType

class Not(Instruction):
    instruction=Instructions.Not
    params=[Types.INTEGER_CASTABLE|Types.BOOLEAN_CASTABLE,Types.ANY]
    paramsTypes=[ParamTypes.REGISTRY,  ParamTypes.REGISTRY]
    def check_cross_types(self, param_types, logger, warningStart):
        pass
    def update_registries_types(self, params, cmp_registry):
        params[-1].currentType=params[0].currentType

class Xor(Instruction):
    instruction=Instructions.Xor
    params=[Types.INTEGER_CASTABLE|Types.BOOLEAN_CASTABLE,Types.INTEGER_CASTABLE|Types.BOOLEAN_CASTABLE,Types.ANY]
    paramsTypes=[ParamTypes.REGISTRY, ParamTypes.REGISTRY_OR_IMMEDIATE, ParamTypes.REGISTRY]
    def check_cross_types(self, param_types, logger, warningStart):
        if(param_types[0]!=param_types[1]):
            logger.warning(f"{warningStart} parameters 0 and 1 are supposed to be same type but may be {param_types[0]} and {param_types[1]} instead")
    def update_registries_types(self, params, cmp_registry):
        params[-1].currentType=params[0].currentType

class Neg(Instruction):
    instruction=Instructions.Neg
    params=[Types.INTEGER_CASTABLE|Types.BOOLEAN_CASTABLE|Types.FLOAT_CASTABLE,Types.ANY]
    paramsTypes=[ParamTypes.REGISTRY,  ParamTypes.REGISTRY]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        params[-1].currentType=params[0].currentType

class Shr(Instruction):
    instruction=Instructions.Shr
    params=[Types.INTEGER_CASTABLE, Types.INTEGER_CASTABLE, Types.ANY]
    paramsTypes=[ParamTypes.REGISTRY, ParamTypes.REGISTRY_OR_IMMEDIATE, ParamTypes.REGISTRY]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        params[-1].currentType=params[0].currentType

class Shl(Instruction):
    instruction=Instructions.Shl
    params=[Types.INTEGER_CASTABLE, Types.INTEGER_CASTABLE, Types.ANY]
    paramsTypes=[ParamTypes.REGISTRY, ParamTypes.REGISTRY_OR_IMMEDIATE, ParamTypes.REGISTRY]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        params[-1].currentType=params[0].currentType

class Sar(Instruction):
    instruction=Instructions.Sar
    params=[Types.INTEGER_CASTABLE, Types.INTEGER_CASTABLE, Types.ANY]
    paramsTypes=[ParamTypes.REGISTRY, ParamTypes.REGISTRY_OR_IMMEDIATE, ParamTypes.REGISTRY]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        params[-1].currentType=params[0].currentType

class Ceil(Instruction):
    instruction=Instructions.Ceil
    params=[Types.FLOAT_CASTABLE,Types.ANY]
    paramsTypes=[ParamTypes.REGISTRY,  ParamTypes.REGISTRY]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        params[-1].currentType=Types.INTEGER_CASTABLE

class Floor(Instruction):
    instruction=Instructions.Floor
    params=[Types.FLOAT_CASTABLE,Types.ANY]
    paramsTypes=[ParamTypes.REGISTRY,  ParamTypes.REGISTRY]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        params[-1].currentType=Types.INTEGER_CASTABLE

class Round(Instruction):
    instruction=Instructions.Round
    params=[Types.FLOAT_CASTABLE,Types.ANY]
    paramsTypes=[ParamTypes.REGISTRY,  ParamTypes.REGISTRY]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        params[-1].currentType=Types.INTEGER_CASTABLE

class Mov(Instruction):
    instruction=Instructions.Mov
    params=[Types.ANY,Types.ANY]
    paramsTypes=[ParamTypes.REGISTRY,  ParamTypes.REGISTRY]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        params[-1].currentType=params[0].currentType

class Rnd(Instruction):
    instruction=Instructions.Rnd
    params=[Types.INTEGER_CASTABLE,Types.INTEGER_CASTABLE, Types.ANY]
    paramsTypes=[ParamTypes.REGISTRY_OR_IMMEDIATE,  ParamTypes.REGISTRY_OR_IMMEDIATE, ParamTypes.REGISTRY]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        params[-1].currentType=Types.INTEGER_CASTABLE

class IsNull(Instruction):
    instruction=Instructions.IsNull
    params=[Types.ANY]
    paramsTypes=[ParamTypes.REGISTRY]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        cmp_registry.currentType=Types.BOOLEAN_CASTABLE


class GetTileInformation(Instruction):
    instruction=Instructions.GetTileInformation
    params=[Types.INTEGER_CASTABLE, Types.INTEGER_CASTABLE, Types.ANY, Types.ANY, Types.ANY, Types.ANY, Types.ANY, Types.ANY]
    paramsTypes=[ParamTypes.REGISTRY_OR_IMMEDIATE, ParamTypes.REGISTRY_OR_IMMEDIATE, ParamTypes.REGISTRY, ParamTypes.REGISTRY, ParamTypes.REGISTRY, ParamTypes.REGISTRY, ParamTypes.REGISTRY, ParamTypes.REGISTRY]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        params[2].currentType=Types.INTEGER_CASTABLE
        params[3].currentType=Types.BOOLEAN_CASTABLE
        params[4].currentType=Types.INTEGER_CASTABLE
        params[5].currentType=Types.STRING_CASTABLE
        params[6].currentType=Types.INTEGER_CASTABLE
        params[7].currentType=Types.STRING_CASTABLE

class GetOwnInformation(Instruction):
    instruction=Instructions.GetOwnInformation
    params=[Types.ANY, Types.ANY, Types.ANY, Types.ANY]
    paramsTypes=[ParamTypes.REGISTRY, ParamTypes.REGISTRY, ParamTypes.REGISTRY, ParamTypes.REGISTRY]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        params[0].currentType=Types.INTEGER_CASTABLE
        params[1].currentType=Types.INTEGER_CASTABLE
        params[2].currentType=Types.INTEGER_CASTABLE
        params[3].currentType=Types.INTEGER_CASTABLE

class JMP(Instruction):
    instruction=Instructions.JMP
    params=[Types.INTEGER_CASTABLE]
    paramsTypes=[ParamTypes.REGISTRY_OR_IMMEDIATE]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        return

class JMPEQ0(Instruction):
    instruction=Instructions.JMPEQ0
    params=[Types.INTEGER_CASTABLE]
    paramsTypes=[ParamTypes.REGISTRY_OR_IMMEDIATE]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        return

class JMPNEQ0(Instruction):
    instruction=Instructions.JMPNEQ0
    params=[Types.INTEGER_CASTABLE]
    paramsTypes=[ParamTypes.REGISTRY_OR_IMMEDIATE]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        return

class JMPL0(Instruction):
    instruction=Instructions.JMPL0
    params=[Types.INTEGER_CASTABLE]
    paramsTypes=[ParamTypes.REGISTRY_OR_IMMEDIATE]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        return 

class JMPG0(Instruction):
    instruction=Instructions.JMPG0
    params=[Types.INTEGER_CASTABLE]
    paramsTypes=[ParamTypes.REGISTRY_OR_IMMEDIATE]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        return

class CMP(Instruction):
    instruction=Instructions.CMP
    params=[Types.INTEGER_CASTABLE|Types.FLOAT_CASTABLE|Types.BOOLEAN_CASTABLE|Types.STRING_CASTABLE, Types.INTEGER_CASTABLE|Types.FLOAT_CASTABLE|Types.BOOLEAN_CASTABLE|Types.STRING_CASTABLE]
    paramsTypes=[ParamTypes.REGISTRY, ParamTypes.REGISTRY_OR_IMMEDIATE]
    def check_cross_types(self, param_types, logger, warningStart):
        if(param_types[0]!=param_types[1]):
            logger.warning(f"{warningStart} parameters 0 and 1 are supposed to be same type but may be {param_types[0]} and {param_types[1]} instead")
    def update_registries_types(self, params, cmp_registry):
        if(params[0].currentType in Types.INTEGER|Types.FLOAT):
            cmp_registry.currentType=Types.INTEGER
        else:
            cmp_registry.currentType=Types.BOOLEAN

class MoveTo(Instruction):
    instruction=Instructions.MoveTo
    params=[Types.INTEGER_CASTABLE, Types.INTEGER_CASTABLE]
    paramsTypes=[ParamTypes.REGISTRY_OR_IMMEDIATE, ParamTypes.REGISTRY_OR_IMMEDIATE]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        return

class UseAbility(Instruction):
    instruction=Instructions.UseAbility
    params=[Types.INTEGER_CASTABLE]
    paramsTypes=[ParamTypes.REGISTRY_OR_IMMEDIATE]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        return

class DoNothing(Instruction):
    instruction=Instructions.DoNothing
    params=[]
    paramsTypes=[]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        return


class Log(Instruction):
    instruction=Instructions.Log
    params=[Types.ANY]
    paramsTypes=[ParamTypes.REGISTRY_OR_IMMEDIATE]
    def check_cross_types(self, param_types, logger, warningStart):
        return
    def update_registries_types(self, params, cmp_registry):
        return


