const mulberry32 = (a: number) => {
  return () => {
    let t = (a += 0x6d2b79f5);
    t = Math.imul(t ^ (t >>> 15), t | 1);
    t ^= t + Math.imul(t ^ (t >>> 7), t | 61);
    return (t ^ (t >>> 14)) >>> 0;
  };
};

export class Random {
  readonly #gen: () => number;

  constructor(seed: number) {
    this.#gen = mulberry32(seed);
  }

  int(max: number): number;
  int(min: number, max?: number): number {
    if (max === undefined) {
      max = min;
      min = 0;
    }

    return min + (this.#gen() % (max - min));
  }
}
