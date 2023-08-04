package it.cybersecnatlab.koth23.models;


/**
 * A Tile is a place on the map, defined by a BackgroundTile and an optional entity.
 */
public final class Tile implements Comparable<Tile> {
    public BackgroundTile bgTile = BackgroundTile.EMPTY;
    public Boolean isDamaging = Boolean.FALSE;
    public Entity entity = null; //If null, no entity is present
    public int x;
    public int y;
    public int fscore = 999999999;

    public Tile(BackgroundTile bgTile, Entity entity, int x, int y) {
        this.bgTile = bgTile;
        this.entity = entity;
        this.x = x;
        this.y = y;
    }

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
    }


    public Tile(Tile that) {
        this.bgTile = that.bgTile;
        this.entity = that.entity;
        this.x = that.x;
        this.y = that.y;
        this.isDamaging = that.isDamaging;
    }

    @Override
    public int compareTo(Tile other) {
        if (this.fscore > other.fscore) {
            return 1;
        } else if (this.fscore < other.fscore) {
            return -1;
        } else {
            return 0;
        }
    }
}
