package sk.itsovy.nemethd.minesweeper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;

public class Tile extends androidx.appcompat.widget.AppCompatButton {

	private boolean isEnabled;
	private boolean isFlagged;
	private boolean isHidden;
	private boolean isMine;
	private int surroundingMines;

	public Tile(Context context) { super(context); }

	public Tile(Context context, AttributeSet attrs) { super(context, attrs); }

	public Tile(Context context, AttributeSet attrs, int defStyle) { super(context, attrs, defStyle); }

	public void setDefaults() {
		isEnabled = true;
		isFlagged = false;
		isHidden = true;
		isMine = false;
		surroundingMines = 0;
		this.setBackgroundResource(R.drawable.tile_covered);
		this.setGravity(Gravity.CENTER);
		this.setTypeface(Typeface.DEFAULT_BOLD);
	}

	public void setNumberOfSurroundingMines(int number) {
		this.setBackgroundResource(R.drawable.tile_uncovered);
		updateNumber(number);
	}

	public void showMine(boolean enabled) {
		this.setText("@");
		if (!enabled) {
			this.setBackgroundResource(R.drawable.tile_uncovered);
			this.setTextColor(Color.RED);
		}
	}

	public void setFlag(boolean enabled) {
		this.setTextColor(Color.BLACK);
		this.setText("P");
		this.setBackgroundResource(R.drawable.tile_covered);
	}

	public void setDisabled(boolean enabled) {
		if (!this.getText().equals("P") && !enabled) this.setBackgroundResource(R.drawable.tile_uncovered);
		else this.setBackgroundResource(R.drawable.tile_covered);
	}

	public void clearFlag() { this.setText(""); }

	public void uncoverTile() {
		if (!isHidden) return;
		setDisabled(false);
		isHidden = false;
		if (hasMine()) showMine(false);
		else setNumberOfSurroundingMines(surroundingMines);
	}

	public void updateNumber(int text) {
		if (text != 0) {
			this.setText(Integer.toString(text));
			switch (text) {
				case 1:
					this.setTextColor(Color.BLUE);
					break;
				case 2:
					this.setTextColor(Color.rgb(0, 100, 0));
					break;
				case 3:
					this.setTextColor(Color.RED);
					break;
				case 4:
					this.setTextColor(Color.rgb(85, 25, 140));
					break;
				case 5:
					this.setTextColor(Color.rgb(140, 30, 100));
					break;
				case 6:
					this.setTextColor(Color.rgb(240, 175, 15));
					break;
				case 7:
					this.setTextColor(Color.rgb(45, 80, 80));
					break;
				case 8:
					this.setTextColor(Color.rgb(70, 70, 70));
					break;
			}
		}
	}

	public void triggerMine() {
		showMine(true);
		this.setTextColor(Color.RED);
	}

	public boolean isHidden() { return isHidden; }

	public void setMine() { isMine = true; }
	public boolean hasMine() { return isMine; }

	public void setSurroundingMines(int number) { surroundingMines = number; }
	public int getNumberOfSurroundingMines() { return surroundingMines; }

	public boolean isFlagged() { return isFlagged; }
	public void setFlagged(boolean flagged) { isFlagged = flagged; }

	public boolean isEnabled() { return isEnabled; }
	public void setEnabled(boolean enabled) { isEnabled = enabled; }
}
