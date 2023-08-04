package it.cybersecnatlab.koth23.models;

public class BackgroundTile implements Cell {
    public static final BackgroundTile EMPTY = new BackgroundTile(Type.Empty);
    public static final BackgroundTile MOUNTAIN = new BackgroundTile(Type.Mountain);
    public static final BackgroundTile WATER = new BackgroundTile(Type.Water);
    public static final BackgroundTile BUSH = new BackgroundTile(Type.Bush);
    public static final BackgroundTile LAVA = new BackgroundTile(Type.Lava);
    public static final BackgroundTile UNKNOWN = new BackgroundTile(Type.Unknown);
    public static final BackgroundTile UNKNOWN_LIQUID = new BackgroundTile(Type.Unknown_Water);
    public static final BackgroundTile UNUSED = new BackgroundTile(Type.Unused);
    public final Type type;

    private BackgroundTile(Type type) {
        this.type = type;
    }

    public enum Type {
        Empty,
        Mountain,
        Water,
        Bush,
        Lava,
        Unknown,
        Unknown_Water,

        Unused // Used during the map generation process
    }
}
