from enum import Enum


class Directions(Enum):
    UP = 0,
    DOWN = 1,
    LEFT = 2,
    RIGHT = 3


class Entities(Enum):
    DRAGON = 1,
    MOLE = 2,
    CAT = 3,
    SPECTRE = 4,
    MAGICARROW = 5,
    WOLF = 6,
    MOUSE = 7,
    ARMADILLO = 8


class Backgrounds(Enum):
    EMPTY = 0,
    MOUNTAIN = 1,
    WATER = 2,
    BUSH = 3,
    LAVA = 4,
    UNKNOWN = 5,
    UNKNOWN_WATER = 6
