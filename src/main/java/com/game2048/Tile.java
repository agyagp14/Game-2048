package com.game2048;
import java.util.Objects;

public final class Tile {
    private final int value;
    private final int row;
    private final int col;
    private final boolean merged;

    public Tile(int value, int row, int col) {
        this(value, row, col, false);
    }

    public Tile(int value, int row, int col, boolean merged) {
        this.value = value;
        this.row = row;
        this.col = col;
        this.merged = merged;
    }

    public int getValue() { return value; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public boolean isMerged() { return merged; }

    public Tile withPosition(int newRow, int newCol) {
        return new Tile(this.value, newRow, newCol, this.merged);
    }

    public Tile withMerged(boolean merged) {
        return new Tile(this.value, this.row, this.col, merged);
    }

    public Tile mergeWith(Tile other) {
        if (this.value != other.value) {
            throw new IllegalArgumentException("Can only merge tiles with same value");
        }
        return new Tile(this.value * 2, this.row, this.col, true);
    }

    public boolean isEmpty() {
        return value == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return value == tile.value && row == tile.row && col == tile.col && merged == tile.merged;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, row, col, merged);
    }

    @Override
    public String toString() {
        return String.format("Tile[value=%d, pos=(%d,%d), merged=%s]", value, row, col, merged);
    }
}