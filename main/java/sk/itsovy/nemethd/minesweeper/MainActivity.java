package sk.itsovy.nemethd.minesweeper;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import java.util.Random;

public class MainActivity extends Activity {

    private TableLayout mineField;
    private Button btnOptions;
    private TextView txtMineCount;
    private Button btnNewGame;
    private TextView txtTime;
    private Button btnBack;

    private Tile tiles[][];
    private int tileDimension = 50;
    private int tilePadding = 10;
    private int rows = 20;
    private int columns = 15;
    private int mines = 50;

    private boolean areMinesSet;
    private boolean isGameOver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtMineCount = findViewById(R.id.MineCount);
        btnNewGame = findViewById(R.id.NewGame);
        btnNewGame.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                endGame();
                startGame();
            }});
        mineField = findViewById(R.id.MineField);
    }

    private void startGame() {
        createMineField();
        showMineField();
    }

    private void showMineField() {
        for (int row = 1; row < rows + 1; row++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new LayoutParams((tileDimension + 2 * tilePadding) * columns, tileDimension + 2 * tilePadding));
            for (int column = 1; column < columns + 1; column++) {
                tiles[row][column].setLayoutParams(new LayoutParams(tileDimension + 2 * tilePadding, tileDimension + 2 * tilePadding));
                tiles[row][column].setPadding(tilePadding, tilePadding, tilePadding, tilePadding);
                tableRow.addView(tiles[row][column]);
            }
            mineField.addView(tableRow, new TableLayout.LayoutParams((tileDimension + 2 * tilePadding) * columns, tileDimension + 2 * tilePadding));
        }
    }

    private void endGame() {
        mineField.removeAllViews();
        areMinesSet = false;
        isGameOver = false;
    }

    private void createMineField() {
        tiles = new Tile[rows + 2][columns + 2];
        for (int row = 0; row < rows + 2; row++) {
            for (int column = 0; column < columns + 2; column++) {
                tiles[row][column] = new Tile(this);
                tiles[row][column].setDefaults();
                final int currentRow = row;
                final int currentColumn = column;
                tiles[row][column].setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (!areMinesSet) {
                            setMines(currentRow, currentColumn);
                            areMinesSet = true;
                        }
                        if (!tiles[currentRow][currentColumn].isFlagged()) {
                            uncoverMultipleTiles(currentRow, currentColumn);
                            if (tiles[currentRow][currentColumn].hasMine()) failGame(currentRow,currentColumn);
                            if (checkWin()) winGame();
                        }
                    }
                });
                tiles[row][column].setOnLongClickListener(new OnLongClickListener() {
                    public boolean onLongClick(View view) {
                        if (!tiles[currentRow][currentColumn].isHidden() && (tiles[currentRow][currentColumn].getNumberOfSurroundingMines() > 0) && !isGameOver) {
                            int nearbyFlaggedTiles = 0;
                            for (int previousRow = -1; previousRow < 2; previousRow++) {
                                for (int previousColumn = -1; previousColumn < 2; previousColumn++) {
                                    if (tiles[currentRow + previousRow][currentColumn + previousColumn].isFlagged()) nearbyFlaggedTiles++;
                                }
                            }
                            if (nearbyFlaggedTiles == tiles[currentRow][currentColumn].getNumberOfSurroundingMines()) {
                                for (int previousRow = -1; previousRow < 2; previousRow++) {
                                    for (int previousColumn = -1; previousColumn < 2; previousColumn++) {
                                        if (!tiles[currentRow + previousRow][currentColumn + previousColumn].isFlagged()) {
                                            uncoverMultipleTiles(currentRow + previousRow, currentColumn + previousColumn);
                                            if (tiles[currentRow + previousRow][currentColumn + previousColumn].hasMine()) failGame(currentRow + previousRow, currentColumn + previousColumn);
                                            if (checkWin()) winGame();
                                        }
                                    }
                                }
                            }
                            return true;
                        }
                        if (tiles[currentRow][currentColumn].isEnabled() && (tiles[currentRow][currentColumn].isEnabled() || tiles[currentRow][currentColumn].isFlagged())) {
                            if (!tiles[currentRow][currentColumn].isFlagged()) {
                                tiles[currentRow][currentColumn].setDisabled(false);
                                tiles[currentRow][currentColumn].setFlag(true);
                                tiles[currentRow][currentColumn].setFlagged(true);
                            }
                            else {
                                tiles[currentRow][currentColumn].setDisabled(true);
                                tiles[currentRow][currentColumn].clearAllIcons();
                                tiles[currentRow][currentColumn].setFlagged(false);
                            }
                        }
                        return true;
                    }
                });
            }
        }
    }

    private boolean checkWin() {
        for (int row = 1; row < rows + 1; row++) {
            for (int column = 1; column < columns + 1; column++) {
                if (!tiles[row][column].hasMine() && tiles[row][column].isHidden()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void winGame() {
        isGameOver = true;
        for (int row = 1; row < rows + 1; row++) {
            for (int column = 1; column < columns + 1; column++) {
                tiles[row][column].setEnabled(false);
                if (tiles[row][column].hasMine()) {
                    tiles[row][column].setDisabled(false);
                    tiles[row][column].setFlag(true);
                }
            }
        }
    }

    private void failGame(int currentRow, int currentColumn) {
        isGameOver = true;
        for (int row = 1; row < rows + 1; row++) {
            for (int column = 1; column < columns + 1; column++) {
                tiles[row][column].setDisabled(false);
                if (tiles[row][column].hasMine() && !tiles[row][column].isFlagged()) tiles[row][column].showMine(false);
                if (!tiles[row][column].hasMine() && tiles[row][column].isFlagged()) tiles[row][column].setFlag(false);
                if (tiles[row][column].isFlagged()) tiles[row][column].setEnabled(false);
            }
        }
        tiles[currentRow][currentColumn].triggerMine();
    }

    private void setMines(int currentRow, int currentColumn) {
        Random rand = new Random();
        int mineRow, mineColumn;
        for (int row = 0; row < mines; row++) {
            mineRow = rand.nextInt(columns);
            mineColumn = rand.nextInt(rows);
            if ((mineRow + 1 != currentColumn) || (mineColumn + 1 != currentRow)) {
                if (tiles[mineColumn + 1][mineRow + 1].hasMine()) row--;
                tiles[mineColumn + 1][mineRow + 1].setMine();
            }
            else row--;
        }
        int nearbyMineCount;
        for (int row = 0; row < rows + 2; row++) {
            for (int column = 0; column < columns + 2; column++) {
                nearbyMineCount = 0;
                if ((row != 0) && (row != (rows + 1)) && (column != 0) && (column != (columns + 1))) {
                    for (int previousRow = -1; previousRow < 2; previousRow++) {
                        for (int previousColumn = -1; previousColumn < 2; previousColumn++) {
                            if (tiles[row + previousRow][column + previousColumn].hasMine()) nearbyMineCount++;
                        }
                    }
                    tiles[row][column].setSurroundingMines(nearbyMineCount);
                }
                else {
                    tiles[row][column].setSurroundingMines(9);
                    tiles[row][column].uncoverTile();
                }
            }
        }
    }

    private void uncoverMultipleTiles(int rowClicked, int columnClicked) {
        if (tiles[rowClicked][columnClicked].hasMine() || tiles[rowClicked][columnClicked].isFlagged()) return;
        tiles[rowClicked][columnClicked].uncoverTile();
        if (tiles[rowClicked][columnClicked].getNumberOfSurroundingMines() != 0 ) return;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                if (tiles[rowClicked + row - 1][columnClicked + column - 1].isHidden() && (rowClicked + row - 1 > 0) && (columnClicked + column - 1 > 0) && (rowClicked + row - 1 < rows + 1) && (columnClicked + column - 1 < columns + 1)) uncoverMultipleTiles(rowClicked + row - 1, columnClicked + column - 1 );
            }
        }
        return;
    }
}