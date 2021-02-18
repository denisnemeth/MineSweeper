package sk.itsovy.nemethd.minesweeper;

import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class BoardActivity extends AppCompatActivity {

    private ScaleGestureDetector scd;
    private float scaleFactor = 1.0f;

    Game g = new Game();

    Tile[][] tiles;

    private TableLayout mineField;
    private TextView txtMineCount;
    private Button btnNewGame;
    private TextView txtTime;
    private int hiddenMines;

    private boolean isGameOver;
    private boolean areMinesSet;
    private boolean isTimerStarted;

    private Handler timer = new Handler();
    private int seconds = 0;
    private int score;

    private int rows = g.getRows();
    private int columns = g.getColumns();
    private int mines = g.getMines();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_activity);
        scd = new ScaleGestureDetector(this, new ScaleListener());
        mineField = findViewById(R.id.MineField);
        txtMineCount = findViewById(R.id.MineCount);
        btnNewGame = findViewById(R.id.NewGame);
        txtTime = findViewById(R.id.Time);
        btnNewGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                endGame();
                startGame();
            }
        });
        startGame();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        scd.onTouchEvent(motionEvent);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            scaleFactor *= scaleGestureDetector.getScaleFactor();
            scaleFactor = Math.max(1.0f, Math.min(scaleFactor, 3.0f));
            mineField.setScaleX(scaleFactor);
            mineField.setScaleY(scaleFactor);
            return true;
        }
    }

    public void createMineField() {
        tiles = new Tile[g.getRows() + 2][g.getColumns() + 2];
        for (int row = 0; row < g.getRows() + 2; row++) {
            for (int column = 0; column < g.getColumns() + 2; column++) {
                tiles[row][column] = new Tile(getApplicationContext());
                tiles[row][column].setDefaults();
                final int currentRow = row;
                final int currentColumn = column;
                tiles[row][column].setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (!isTimerStarted) {
                            startTimer();
                            isTimerStarted = true;
                        }
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
                tiles[row][column].setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
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
                                hiddenMines--;
                                updateMines();
                            }
                            else {
                                tiles[currentRow][currentColumn].setDisabled(true);
                                tiles[currentRow][currentColumn].clearFlag();
                                if (tiles[currentRow][currentColumn].isFlagged()) {
                                    hiddenMines++;
                                    updateMines();
                                }
                                tiles[currentRow][currentColumn].setFlagged(false);
                            }
                            updateMines();
                        }
                        return true;
                    }
                });
            }
        }
    }

    private void showMineField() {
        for (int row = 1; row < g.getRows() + 1; row++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams((g.getTileDimension() + 2 * g.getTilePadding()) * g.getColumns(), g.getTileDimension() + 2 * g.getTilePadding()));
            for (int column = 1; column < g.getColumns() + 1; column++) {
                tiles[row][column].setLayoutParams(new TableRow.LayoutParams(g.getTileDimension() + 2 * g.getTilePadding(), g.getTileDimension() + 2 * g.getTilePadding()));
                tiles[row][column].setPadding(g.getTilePadding(), g.getTilePadding(), g.getTilePadding(), g.getTilePadding());
                tableRow.addView(tiles[row][column]);
            }
            mineField.addView(tableRow, new TableLayout.LayoutParams((g.getTileDimension() + 2 * g.getTilePadding()) * g.getColumns(), g.getTileDimension() + 2 * g.getTilePadding()));
        }
    }

    private void startGame() {
        createMineField();
        showMineField();
        hiddenMines = mines;
        isGameOver = false;
        seconds = 0;
        score = 0;
        txtMineCount.setText("0" + g.getMines());
    }

    private void endGame() {
        stopTimer();
        txtTime.setText("000");
        txtMineCount.setText("0" + g.getMines());
        mineField.removeAllViews();
        areMinesSet = false;
        isGameOver = false;
        isTimerStarted = false;
        hiddenMines = 0;
    }

    public void startTimer() {
        if (seconds == 0) {
            timer.removeCallbacks(updateTimeElapsed);
            timer.postDelayed(updateTimeElapsed, 1000);
        }
    }

    public void stopTimer() { timer.removeCallbacks(updateTimeElapsed); }

    private Runnable updateTimeElapsed = new Runnable() {
        public void run() {
            if (seconds == 999) stopTimer();
            long currentMilliseconds = System.currentTimeMillis();
            seconds++;
            if (seconds < 10) txtTime.setText("00" + seconds);
            else if (seconds < 100) txtTime.setText("0" + seconds);
            else txtTime.setText(seconds);
            timer.postAtTime(this, currentMilliseconds);
            timer.postDelayed(updateTimeElapsed, 1000);
        }
    };

    public void updateMines() {
        if (hiddenMines < 0) txtMineCount.setText(hiddenMines);
        else if (hiddenMines < 10) txtMineCount.setText("00" + hiddenMines);
        else if (hiddenMines < 100) txtMineCount.setText("0" + hiddenMines);
        else txtMineCount.setText(hiddenMines);
    }

    public void setMines(int currentRow, int currentColumn) {
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

    public void uncoverMultipleTiles(int rowClicked, int columnClicked) {
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

    public boolean checkWin() {
        for (int row = 1; row < rows + 1; row++) {
            for (int column = 1; column < columns + 1; column++) {
                if (!tiles[row][column].hasMine() && tiles[row][column].isHidden()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void winGame() {
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
        stopTimer();
        calculateScore();
        Toast toast = Toast.makeText(this, "CONGRATULATIONS!\nYou won in " + seconds + " seconds.\nYour score is " + score + ".", Toast.LENGTH_LONG);
        toast.show();
    }

    public void failGame(int currentRow, int currentColumn) {
        isGameOver = true;
        for (int row = 1; row < rows + 1; row++) {
            for (int column = 1; column < columns + 1; column++) {
                tiles[row][column].setDisabled(false);
                if (tiles[row][column].hasMine() && !tiles[row][column].isFlagged()) tiles[row][column].showMine(false);
                if (!tiles[row][column].hasMine() && tiles[row][column].isFlagged()) tiles[row][column].setFlag(false);
                if (tiles[row][column].isFlagged()) tiles[row][column].setEnabled(false);
            }
        }
        stopTimer();
        tiles[currentRow][currentColumn].triggerMine();
        calculateScore();
        Toast toast = Toast.makeText(this, "GAME OVER!\nYou lost after " + seconds + " seconds.", Toast.LENGTH_LONG);
        toast.show();
    }

    private void calculateScore() {
        score = 999 - seconds + ((mines - hiddenMines) * 10);
    }
}
