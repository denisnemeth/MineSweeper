package sk.itsovy.nemethd.minesweeper;

public class Game {

    private int tileDimension = 75;
    private int tilePadding = 10;
    private int rows = 20;
    private int columns = 10;
    private int mines = 20;

    public void setDifficulty(String difficulty){
        switch (difficulty) {
            case "Easy":
                setTileDimension(190);
                setTilePadding(1);
                setRows(10);
                setColumns(5);
                setMines(5);
                break;
            case "Normal":
                setTileDimension(75);
                setTilePadding(10);
                setRows(20);
                setColumns(10);
                setMines(20);
                break;
            case "Hard":
                setTileDimension(25);
                setTilePadding(11);
                setRows(40);
                setColumns(20);
                setMines(80);
                break;
        }
    }

    public int getTileDimension() { return tileDimension; }
    public void setTileDimension(int tileDimension) { this.tileDimension = tileDimension; }

    public int getTilePadding() { return tilePadding; }
    public void setTilePadding(int tilePadding) { this.tilePadding = tilePadding; }

    public int getRows() { return rows; }
    public void setRows(int rows) { this.rows = rows; }

    public int getColumns() { return columns; }
    public void setColumns(int columns) { this.columns = columns; }

    public int getMines() { return mines; }
    public void setMines(int mines) { this.mines = mines; }
}
