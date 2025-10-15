package com.game2048;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// Helper classes for move results
class MoveResult {
    private final GameBoard board;
    private final boolean changed;

    public MoveResult(GameBoard board, boolean changed) {
        this.board = board;
        this.changed = changed;
    }

    public GameBoard getBoard() { return board; }
    public boolean hasChanged() { return changed; }
}

class LineMoveResult {
    private final List<Tile> tiles;
    private final int score;
    private final boolean changed;

    public LineMoveResult(List<Tile> tiles, int score, boolean changed) {
        this.tiles = tiles;
        this.score = score;
        this.changed = changed;
    }

    public List<Tile> getTiles() { return tiles; }
    public int getScore() { return score; }
    public boolean hasChanged() { return changed; }
}

public final class GameBoard {
    private final int size;
    private final List<List<Tile>> grid;
    private final int score;
    private final boolean gameOver;
    private final boolean won;

    // Private constructor
    private GameBoard(int size, List<List<Tile>> grid, int score, boolean gameOver, boolean won) {
        this.size = size;
        this.grid = grid;
        this.score = score;
        this.gameOver = gameOver;
        this.won = won;
    }

    // Static factory method to create initial board
    public static GameBoard createInitialBoard(int size) {
        List<List<Tile>> emptyGrid = initializeEmptyGrid(size);
        GameBoard emptyBoard = new GameBoard(size, emptyGrid, 0, false, false);
        return emptyBoard.addRandomTile().addRandomTile();
    }

    private static List<List<Tile>> initializeEmptyGrid(int size) {
        return IntStream.range(0, size)
                .mapToObj(row -> IntStream.range(0, size)
                        .mapToObj(col -> new Tile(0, row, col))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    public List<Tile> getAllTiles() {
        return grid.stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public Tile getTile(int row, int col) {
        if (row < 0 || row >= size || col < 0 || col >= size) {
            throw new IllegalArgumentException("Invalid position: (" + row + ", " + col + ")");
        }
        return grid.get(row).get(col);
    }

    public GameBoard placeTile(Tile tile) {
        List<List<Tile>> newGrid = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            List<Tile> newRow = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                if (i == tile.getRow() && j == tile.getCol()) {
                    newRow.add(tile);
                } else {
                    newRow.add(grid.get(i).get(j));
                }
            }
            newGrid.add(newRow);
        }
        return new GameBoard(size, newGrid, score, gameOver, won);
    }

    public GameBoard addRandomTile() {
        List<Tile> emptyTiles = getAllTiles().stream()
                .filter(Tile::isEmpty)
                .collect(Collectors.toList());

        if (emptyTiles.isEmpty()) {
            return this;
        }

        Random random = new Random();
        Tile randomTile = emptyTiles.get(random.nextInt(emptyTiles.size()));
        int newValue = random.nextDouble() < 0.9 ? 2 : 4;

        return placeTile(new Tile(newValue, randomTile.getRow(), randomTile.getCol()));
    }

    public GameBoard move(Direction direction) {
        MoveResult result = performMove(direction);
        GameBoard newBoard = result.getBoard();
        
        if (!result.hasChanged()) {
            return newBoard;
        }

        newBoard = newBoard.addRandomTile();
        boolean newGameOver = newBoard.isTerminalState();
        boolean newWon = newBoard.hasWon() || this.won;

        return new GameBoard(size, newBoard.grid, newBoard.score, newGameOver, newWon);
    }

    private MoveResult performMove(Direction direction) {
        List<List<Tile>> newGrid = new ArrayList<>();
        int newScore = this.score;
        boolean changed = false;

        for (int i = 0; i < size; i++) {
            List<Tile> line = getLine(i, direction);
            LineMoveResult lineResult = moveLine(line);
            
            newScore += lineResult.getScore();
            changed = changed || lineResult.hasChanged();
            
            List<Tile> newLine = lineResult.getTiles();
            setLine(newGrid, i, direction, newLine);
        }

        // Reset merged flags for next move
        List<List<Tile>> resetGrid = newGrid.stream()
                .map(row -> row.stream()
                        .map(tile -> tile.withMerged(false))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        return new MoveResult(new GameBoard(size, resetGrid, newScore, gameOver, won), changed);
    }

    private List<Tile> getLine(int index, Direction direction) {
        return IntStream.range(0, size)
                .mapToObj(i -> {
                    switch (direction) {
                        case LEFT: return getTile(index, i);
                        case RIGHT: return getTile(index, size - 1 - i);
                        case UP: return getTile(i, index);
                        case DOWN: return getTile(size - 1 - i, index);
                        default: throw new IllegalArgumentException("Invalid direction");
                    }
                })
                .collect(Collectors.toList());
    }

    private void setLine(List<List<Tile>> grid, int index, Direction direction, List<Tile> line) {
        for (int i = 0; i < size; i++) {
            int row, col;
            switch (direction) {
                case LEFT: row = index; col = i; break;
                case RIGHT: row = index; col = size - 1 - i; break;
                case UP: row = i; col = index; break;
                case DOWN: row = size - 1 - i; col = index; break;
                default: throw new IllegalArgumentException("Invalid direction");
            }
            
            while (grid.size() <= row) {
                grid.add(new ArrayList<>());
            }
            List<Tile> currentRow = grid.get(row);
            while (currentRow.size() <= col) {
                currentRow.add(new Tile(0, row, currentRow.size()));
            }
            currentRow.set(col, line.get(i).withPosition(row, col));
        }
    }

    private LineMoveResult moveLine(List<Tile> line) {
        List<Tile> nonEmptyTiles = line.stream()
                .filter(tile -> !tile.isEmpty())
                .collect(Collectors.toList());

        List<Tile> result = new ArrayList<>();
        int score = 0;
        boolean changed = nonEmptyTiles.size() != line.size();
        int i = 0;

        while (i < nonEmptyTiles.size()) {
            Tile current = nonEmptyTiles.get(i);
            
            if (i < nonEmptyTiles.size() - 1 && 
                current.getValue() == nonEmptyTiles.get(i + 1).getValue()) {
                // Merge tiles
                Tile merged = current.mergeWith(nonEmptyTiles.get(i + 1));
                result.add(merged);
                score += merged.getValue();
                i += 2;
                changed = true;
            } else {
                result.add(current);
                i += 1;
            }
        }

        // Pad with empty tiles
        while (result.size() < size) {
            result.add(new Tile(0, -1, -1));
        }

        return new LineMoveResult(result, score, changed);
    }

    public boolean hasWon() {
        return getAllTiles().stream()
                .anyMatch(tile -> tile.getValue() == 2048);
    }

    public boolean isTerminalState() {
        if (getAllTiles().stream().anyMatch(Tile::isEmpty)) {
            return false;
        }

        // Check for possible merges
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int current = getTile(i, j).getValue();
                if ((j < size - 1 && current == getTile(i, j + 1).getValue()) ||
                    (i < size - 1 && current == getTile(i + 1, j).getValue())) {
                    return false;
                }
            }
        }
        return true;
    }

    // Getters
    public int getSize() { return size; }
    public int getScore() { return score; }
    public boolean isGameOver() { return gameOver; }
    public boolean isWon() { return won; }

    @Override
    public String toString() {
        return grid.stream()
                .map(row -> row.stream()
                        .map(tile -> String.valueOf(tile.getValue()))
                        .collect(Collectors.joining(" ")))
                .collect(Collectors.joining("\n"));
    }
}