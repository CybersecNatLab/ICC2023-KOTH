import { TileBackground, VMEntityType } from './map';
import { Spritesheet, Texture } from 'pixi.js';
import { Random } from './rand';

export enum Direction {
  Left,
  Right,
  Up,
  Down
}

export enum Color {
  Pink,
  Blue,
  Red,
  Purple,
  Orange,
  White,
  Green
}

export const colorToInt = (color: Color): number => {
  switch (color) {
    case Color.Pink:
      return 0xe6718e; // oceania
    case Color.Blue:
      return 0x306e96; // usa
    case Color.Red:
      return 0xe14e30; // latina
    case Color.Purple:
      return 0xa24688; // europe
    case Color.Orange:
      return 0xdb6d21; // canada
    case Color.White:
      return 0x878e8d; // asia
    case Color.Green:
      return 0x3f946f; // africa
    default:
      throw new Error('Unknown color');
  }
};

export const associateNamesWithColors = (names: string[]) => {
  names.sort();

  const availableColors = [
    Color.Blue, // usa
    Color.Pink, // oceania
    Color.Red, // latina
    Color.Purple, // europe
    Color.Orange, // canada
    Color.White, // asia
    Color.Green // africa
  ];

  const map: { [name: string]: Color } = {};
  for (const name of names) {
    const color = availableColors.pop();
    if (color === undefined) {
      throw new Error('Not enough colors');
    }

    map[name] = color;
  }
  return map;
};

export type Sprites = {
  background: (
    x: number,
    y: number,
    tile: TileBackground,
    background: TileBackground[][]
  ) => Texture[];
  poison: (x: number, y: number) => Texture;
  summoner: (tick: number, dir: Direction, color: Color) => Texture;
  entity: (tick: number, dir: Direction, color: Color, type: VMEntityType) => Texture;
  bar: (type: 'life' | 'mana') => [Texture, Texture];
};

export const loadSprites = async (seed: number): Promise<Sprites> => {
  const tilesSheet = new Spritesheet(
    await Texture.fromURL('/sprites.png'),
    await fetch('/sprites.json').then((x) => x.json())
  );

  const parsedTileSheet = await tilesSheet.parse();

  return {
    bar: (type) => {
      return [
        parsedTileSheet['Empty_Bar.png'],
        type === 'life'
          ? parsedTileSheet['Life_Bar_Content.png']
          : parsedTileSheet['Mana_Bar_Content.png']
      ];
    },
    summoner: (tick, dir, color) => {
      const dirStr = {
          [Direction.Left]: 'Left',
          [Direction.Right]: 'Right',
          [Direction.Up]: 'Up',
          [Direction.Down]: 'Down'
        }[dir],
        colorStr = {
          [Color.Pink]: 'Pink',
          [Color.Blue]: 'Blue',
          [Color.Red]: 'Red',
          [Color.Purple]: 'Purple',
          [Color.Orange]: 'Orange',
          [Color.White]: 'White',
          [Color.Green]: 'Green'
        }[color];

      return parsedTileSheet[`Summoner/Summoner_${colorStr}_${dirStr}_${tick % 3}.png`];
    },
    entity: (tick, dir, color, type) => {
      if (type === VMEntityType.Spectre && dir === Direction.Down) {
        dir = Direction.Left;
      }

      const typeStr = {
          [VMEntityType.Dragon]: 'Dragon',
          [VMEntityType.Mole]: 'Mole',
          [VMEntityType.Cat]: 'Cat',
          [VMEntityType.Spectre]: 'Spectre',
          [VMEntityType.MagicArrow]: 'MagicArrow',
          [VMEntityType.Wolf]: 'Wolf',
          [VMEntityType.Mouse]: 'Mouse',
          [VMEntityType.Armadillo]: 'Armadillo'
        }[type],
        dirStr = {
          [Direction.Left]: 'Left',
          [Direction.Right]: 'Right',
          [Direction.Up]: 'Up',
          [Direction.Down]: 'Down'
        }[dir];

      if (type === VMEntityType.MagicArrow) {
        return parsedTileSheet[`${typeStr}/${typeStr}_${dirStr}.png`];
      }

      return parsedTileSheet[`${typeStr}/${typeStr}_${dirStr}_${tick % 3}.png`];
    },
    poison: (x, y) => {
      const rand = new Random(seed + y * 65536 + x);
      return parsedTileSheet[`Poison/Poison_Cloud_${rand.int(4) + 1}.png`];
    },
    background: (x, y, tile, bg) => {
      const rand = new Random(seed + y * 65536 + x);
      const top = <T>(map: T[][]): T | undefined => map[y - 1]?.[x],
        topLeft = <T>(map: T[][]): T | undefined => map[y - 1]?.[x - 1],
        left = <T>(map: T[][]): T | undefined => map[y]?.[x - 1],
        bottomLeft = <T>(map: T[][]): T | undefined => map[y + 1]?.[x - 1],
        bottom = <T>(map: T[][]): T | undefined => map[y + 1]?.[x],
        bottomRight = <T>(map: T[][]): T | undefined => map[y + 1]?.[x + 1],
        right = <T>(map: T[][]): T | undefined => map[y]?.[x + 1],
        topRight = <T>(map: T[][]): T | undefined => map[y - 1]?.[x + 1];

      if (tile === TileBackground.Empty) {
        const getRndTile = () => {
          const specialSandRnd = rand.int(100);
          if (specialSandRnd === 0) return parsedTileSheet['Sand/Sand_Grass.png'];
          else if (specialSandRnd === 1) return parsedTileSheet['Sand/Sand_Leaf.png'];
          else if (specialSandRnd === 2) return parsedTileSheet['Sand/Sand_Rock.png'];
          else return parsedTileSheet[`Sand/Sand_${rand.int(4)}.png`];
        };

        return [
          getRndTile(),
          getRndTile(),
          getRndTile(),
          getRndTile(),
          getRndTile(),
          getRndTile(),
          getRndTile(),
          getRndTile(),
          getRndTile()
        ];
      } else if (tile === TileBackground.Mountain) {
        if (
          top(bg) === TileBackground.Mountain &&
          bottom(bg) === TileBackground.Mountain &&
          left(bg) === TileBackground.Mountain &&
          right(bg) === TileBackground.Mountain
        )
          return [
            topRight(bg) !== TileBackground.Mountain &&
            topLeft(bg) !== TileBackground.Mountain &&
            bottomRight(bg) !== TileBackground.Mountain &&
            bottomLeft(bg) !== TileBackground.Mountain
              ? parsedTileSheet['Mountain/Mountain_TopBottomRightLeft_0.png']
              : topRight(bg) !== TileBackground.Mountain &&
                bottomRight(bg) !== TileBackground.Mountain &&
                bottomLeft(bg) !== TileBackground.Mountain
              ? parsedTileSheet['Mountain/Mountain_TopBottomRightLeft_1.png']
              : topLeft(bg) !== TileBackground.Mountain &&
                bottomRight(bg) !== TileBackground.Mountain &&
                bottomLeft(bg) !== TileBackground.Mountain
              ? parsedTileSheet['Mountain/Mountain_TopBottomRightLeft_2.png']
              : topRight(bg) !== TileBackground.Mountain &&
                topLeft(bg) !== TileBackground.Mountain &&
                bottomRight(bg) !== TileBackground.Mountain
              ? parsedTileSheet['Mountain/Mountain_TopBottomRightLeft_3.png']
              : topRight(bg) !== TileBackground.Mountain &&
                topLeft(bg) !== TileBackground.Mountain &&
                bottomLeft(bg) !== TileBackground.Mountain
              ? parsedTileSheet['Mountain/Mountain_TopBottomRightLeft_4.png']
              : topRight(bg) !== TileBackground.Mountain &&
                bottomLeft(bg) !== TileBackground.Mountain
              ? parsedTileSheet['Mountain/Mountain_TopBottomRightLeft_5.png']
              : topLeft(bg) !== TileBackground.Mountain &&
                bottomRight(bg) !== TileBackground.Mountain
              ? parsedTileSheet['Mountain/Mountain_TopBottomRightLeft_6.png']
              : bottomRight(bg) !== TileBackground.Mountain &&
                bottomLeft(bg) !== TileBackground.Mountain
              ? parsedTileSheet['Mountain/Mountain_TopBottomRightLeft_7.png']
              : topRight(bg) !== TileBackground.Mountain && topLeft(bg) !== TileBackground.Mountain
              ? parsedTileSheet['Mountain/Mountain_TopBottomRightLeft_8.png']
              : topRight(bg) !== TileBackground.Mountain &&
                bottomRight(bg) !== TileBackground.Mountain
              ? parsedTileSheet['Mountain/Mountain_TopBottomRightLeft_9.png']
              : topLeft(bg) !== TileBackground.Mountain &&
                bottomLeft(bg) !== TileBackground.Mountain
              ? parsedTileSheet['Mountain/Mountain_TopBottomRightLeft_10.png']
              : parsedTileSheet['Mountain/Mountain_Full.png']
          ];
        else if (
          left(bg) === TileBackground.Mountain &&
          bottom(bg) === TileBackground.Mountain &&
          right(bg) === TileBackground.Mountain
        )
          return [
            bottomLeft(bg) !== TileBackground.Mountain &&
            bottomRight(bg) !== TileBackground.Mountain
              ? parsedTileSheet['Mountain/Mountain_LeftBottomRight_0.png']
              : bottomLeft(bg) !== TileBackground.Mountain
              ? parsedTileSheet['Mountain/Mountain_LeftBottomRight_1.png']
              : bottomRight(bg) !== TileBackground.Mountain
              ? parsedTileSheet['Mountain/Mountain_LeftBottomRight_2.png']
              : parsedTileSheet['Mountain/Mountain_Bottom_0.png']
          ];
        else if (
          left(bg) === TileBackground.Mountain &&
          top(bg) === TileBackground.Mountain &&
          right(bg) === TileBackground.Mountain
        )
          return [
            topLeft(bg) !== TileBackground.Mountain && topRight(bg) !== TileBackground.Mountain
              ? parsedTileSheet['Mountain/Mountain_LeftTopRight_0.png']
              : topLeft(bg) !== TileBackground.Mountain
              ? parsedTileSheet['Mountain/Mountain_LeftTopRight_1.png']
              : topRight(bg) !== TileBackground.Mountain
              ? parsedTileSheet['Mountain/Mountain_LeftTopRight_2.png']
              : parsedTileSheet['Mountain/Mountain_Top_0.png']
          ];
        else if (
          top(bg) === TileBackground.Mountain &&
          right(bg) === TileBackground.Mountain &&
          bottom(bg) === TileBackground.Mountain
        )
          return [
            topRight(bg) !== TileBackground.Mountain && bottomRight(bg) !== TileBackground.Mountain
              ? parsedTileSheet['Mountain/Mountain_TopRightBottom_0.png']
              : topRight(bg) !== TileBackground.Mountain
              ? parsedTileSheet['Mountain/Mountain_TopRightBottom_2.png']
              : bottomRight(bg) !== TileBackground.Mountain
              ? parsedTileSheet['Mountain/Mountain_TopRightBottom_1.png']
              : parsedTileSheet['Mountain/Mountain_Right_0.png']
          ];
        else if (
          top(bg) === TileBackground.Mountain &&
          left(bg) === TileBackground.Mountain &&
          bottom(bg) === TileBackground.Mountain
        )
          return [
            topLeft(bg) !== TileBackground.Mountain && bottomLeft(bg) !== TileBackground.Mountain
              ? parsedTileSheet['Mountain/Mountain_TopLeftBottom_0.png']
              : topLeft(bg) !== TileBackground.Mountain
              ? parsedTileSheet['Mountain/Mountain_TopLeftBottom_2.png']
              : bottomLeft(bg) !== TileBackground.Mountain
              ? parsedTileSheet['Mountain/Mountain_TopLeftBottom_1.png']
              : parsedTileSheet['Mountain/Mountain_Left_0.png']
          ];
        else if (top(bg) === TileBackground.Mountain && bottom(bg) === TileBackground.Mountain)
          return [parsedTileSheet['Mountain/Mountain_TopBottom.png']];
        else if (left(bg) === TileBackground.Mountain && right(bg) === TileBackground.Mountain)
          return [parsedTileSheet['Mountain/Mountain_LeftRight.png']];
        else if (bottom(bg) === TileBackground.Mountain && left(bg) === TileBackground.Mountain)
          return [
            bottomLeft(bg) === TileBackground.Mountain
              ? parsedTileSheet['Mountain/Mountain_BottomLeft_0.png']
              : parsedTileSheet['Mountain/Mountain_BottomLeft_1.png']
          ];
        else if (bottom(bg) === TileBackground.Mountain && right(bg) === TileBackground.Mountain)
          return [
            bottomRight(bg) === TileBackground.Mountain
              ? parsedTileSheet['Mountain/Mountain_BottomRight_0.png']
              : parsedTileSheet['Mountain/Mountain_BottomRight_1.png']
          ];
        else if (top(bg) === TileBackground.Mountain && left(bg) === TileBackground.Mountain)
          return [
            topLeft(bg) === TileBackground.Mountain
              ? parsedTileSheet['Mountain/Mountain_TopLeft_0.png']
              : parsedTileSheet['Mountain/Mountain_TopLeft_1.png']
          ];
        else if (top(bg) === TileBackground.Mountain && right(bg) === TileBackground.Mountain)
          return [
            topRight(bg) === TileBackground.Mountain
              ? parsedTileSheet['Mountain/Mountain_TopRight_0.png']
              : parsedTileSheet['Mountain/Mountain_TopRight_1.png']
          ];
        else if (right(bg) === TileBackground.Mountain)
          return [parsedTileSheet['Mountain/Mountain_Right_1.png']];
        else if (left(bg) === TileBackground.Mountain)
          return [parsedTileSheet['Mountain/Mountain_Left_1.png']];
        else if (top(bg) === TileBackground.Mountain)
          return [parsedTileSheet['Mountain/Mountain_Top_1.png']];
        else if (bottom(bg) === TileBackground.Mountain)
          return [parsedTileSheet['Mountain/Mountain_Bottom_1.png']];

        return [parsedTileSheet['Mountain/Mountain_None.png']];
      } else if (tile === TileBackground.Water) {
        return [
          // Row 0
          top(bg) !== TileBackground.Water && left(bg) !== TileBackground.Water
            ? parsedTileSheet['Water/Water_BottomRight_0.png']
            : top(bg) !== TileBackground.Water
            ? parsedTileSheet[`Water/Water_Bottom_${rand.int(2)}.png`]
            : left(bg) !== TileBackground.Water
            ? parsedTileSheet[`Water/Water_Right_${rand.int(2)}.png`]
            : topLeft(bg) !== TileBackground.Water
            ? parsedTileSheet['Water/Water_BottomRight_1.png']
            : parsedTileSheet[`Water/Water_${rand.int(4)}.png`],
          top(bg) !== TileBackground.Water
            ? parsedTileSheet[`Water/Water_Bottom_${rand.int(2)}.png`]
            : parsedTileSheet[`Water/Water_${rand.int(4)}.png`],
          top(bg) !== TileBackground.Water && right(bg) !== TileBackground.Water
            ? parsedTileSheet['Water/Water_BottomLeft_0.png']
            : top(bg) !== TileBackground.Water
            ? parsedTileSheet[`Water/Water_Bottom_${rand.int(2)}.png`]
            : right(bg) !== TileBackground.Water
            ? parsedTileSheet[`Water/Water_Left_${rand.int(2)}.png`]
            : topRight(bg) !== TileBackground.Water
            ? parsedTileSheet['Water/Water_BottomLeft_1.png']
            : parsedTileSheet[`Water/Water_${rand.int(4)}.png`],
          // Row 1
          left(bg) !== TileBackground.Water
            ? parsedTileSheet[`Water/Water_Right_${rand.int(2)}.png`]
            : parsedTileSheet[`Water/Water_${rand.int(4)}.png`],
          parsedTileSheet[`Water/Water_${rand.int(4)}.png`],
          right(bg) !== TileBackground.Water
            ? parsedTileSheet[`Water/Water_Left_${rand.int(2)}.png`]
            : parsedTileSheet[`Water/Water_${rand.int(4)}.png`],
          // Row 2
          bottom(bg) !== TileBackground.Water && left(bg) !== TileBackground.Water
            ? parsedTileSheet['Water/Water_TopRight_0.png']
            : bottom(bg) !== TileBackground.Water
            ? parsedTileSheet[`Water/Water_Top_${rand.int(2)}.png`]
            : left(bg) !== TileBackground.Water
            ? parsedTileSheet[`Water/Water_Right_${rand.int(2)}.png`]
            : bottomLeft(bg) !== TileBackground.Water
            ? parsedTileSheet['Water/Water_TopRight_1.png']
            : parsedTileSheet[`Water/Water_${rand.int(4)}.png`],
          bottom(bg) !== TileBackground.Water
            ? parsedTileSheet[`Water/Water_Top_${rand.int(2)}.png`]
            : parsedTileSheet[`Water/Water_${rand.int(4)}.png`],
          bottom(bg) !== TileBackground.Water && right(bg) !== TileBackground.Water
            ? parsedTileSheet['Water/Water_TopLeft_0.png']
            : bottom(bg) !== TileBackground.Water
            ? parsedTileSheet[`Water/Water_Top_${rand.int(2)}.png`]
            : right(bg) !== TileBackground.Water
            ? parsedTileSheet[`Water/Water_Left_${rand.int(2)}.png`]
            : bottomRight(bg) !== TileBackground.Water
            ? parsedTileSheet['Water/Water_TopLeft_1.png']
            : parsedTileSheet[`Water/Water_${rand.int(4)}.png`]
        ];
      } else if (tile === TileBackground.Bush) {
        return [parsedTileSheet[`Bush_${rand.int(3)}.png`]];
      } else if (tile === TileBackground.Lava) {
        return [
          // Row 0
          top(bg) !== TileBackground.Lava && left(bg) !== TileBackground.Lava
            ? parsedTileSheet['Lava/Lava_BottomRight_0.png']
            : top(bg) !== TileBackground.Lava
            ? parsedTileSheet[`Lava/Lava_Bottom_${rand.int(2)}.png`]
            : left(bg) !== TileBackground.Lava
            ? parsedTileSheet[`Lava/Lava_Right_${rand.int(2)}.png`]
            : topLeft(bg) !== TileBackground.Lava
            ? parsedTileSheet['Lava/Lava_BottomRight_1.png']
            : parsedTileSheet[`Lava/Lava_${rand.int(4)}.png`],
          top(bg) !== TileBackground.Lava
            ? parsedTileSheet[`Lava/Lava_Bottom_${rand.int(2)}.png`]
            : parsedTileSheet[`Lava/Lava_${rand.int(4)}.png`],
          top(bg) !== TileBackground.Lava && right(bg) !== TileBackground.Lava
            ? parsedTileSheet['Lava/Lava_BottomLeft_0.png']
            : top(bg) !== TileBackground.Lava
            ? parsedTileSheet[`Lava/Lava_Bottom_${rand.int(2)}.png`]
            : right(bg) !== TileBackground.Lava
            ? parsedTileSheet[`Lava/Lava_Left_${rand.int(2)}.png`]
            : topRight(bg) !== TileBackground.Lava
            ? parsedTileSheet['Lava/Lava_BottomLeft_1.png']
            : parsedTileSheet[`Lava/Lava_${rand.int(4)}.png`],
          // Row 1
          left(bg) !== TileBackground.Lava
            ? parsedTileSheet[`Lava/Lava_Right_${rand.int(2)}.png`]
            : parsedTileSheet[`Lava/Lava_${rand.int(4)}.png`],
          parsedTileSheet[`Lava/Lava_${rand.int(4)}.png`],
          right(bg) !== TileBackground.Lava
            ? parsedTileSheet[`Lava/Lava_Left_${rand.int(2)}.png`]
            : parsedTileSheet[`Lava/Lava_${rand.int(4)}.png`],
          // Row 2
          bottom(bg) !== TileBackground.Lava && left(bg) !== TileBackground.Lava
            ? parsedTileSheet['Lava/Lava_TopRight_0.png']
            : bottom(bg) !== TileBackground.Lava
            ? parsedTileSheet[`Lava/Lava_Top_${rand.int(2)}.png`]
            : left(bg) !== TileBackground.Lava
            ? parsedTileSheet[`Lava/Lava_Right_${rand.int(2)}.png`]
            : bottomLeft(bg) !== TileBackground.Lava
            ? parsedTileSheet['Lava/Lava_TopRight_1.png']
            : parsedTileSheet[`Lava/Lava_${rand.int(4)}.png`],
          bottom(bg) !== TileBackground.Lava
            ? parsedTileSheet[`Lava/Lava_Top_${rand.int(2)}.png`]
            : parsedTileSheet[`Lava/Lava_${rand.int(4)}.png`],
          bottom(bg) !== TileBackground.Lava && right(bg) !== TileBackground.Lava
            ? parsedTileSheet['Lava/Lava_TopLeft_0.png']
            : bottom(bg) !== TileBackground.Lava
            ? parsedTileSheet[`Lava/Lava_Top_${rand.int(2)}.png`]
            : right(bg) !== TileBackground.Lava
            ? parsedTileSheet[`Lava/Lava_Left_${rand.int(2)}.png`]
            : bottomRight(bg) !== TileBackground.Lava
            ? parsedTileSheet['Lava/Lava_TopLeft_1.png']
            : parsedTileSheet[`Lava/Lava_${rand.int(4)}.png`]
        ];
      } else {
        throw new Error('Unknown sprite');
      }
    }
  };
};
