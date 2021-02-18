package sk.itsovy.nemethd.minesweeper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button bntStartGame;
    AlertDialog.Builder exitDialog;
    AlertDialog.Builder difficultyDialog;
    CharSequence[] values = {"Easy", "Normal", "Hard"};
    Game game = new Game();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bntStartGame = findViewById(R.id.StartGame);
        bntStartGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), BoardActivity.class);
                v.getContext().startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_difficulty:
                createDifficultyDialog();
                return false;
            case R.id.menu_leaderboard:
                Intent i = new Intent(this, LeaderboardActivity.class);
                startActivity(i);
                return true;
            case R.id.menu_exit:
                createExitDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void createExitDialog() {
        exitDialog = new AlertDialog.Builder(this);
        exitDialog.setTitle("Are you sure?")
                .setCancelable(true)
                .setPositiveButton(Html.fromHtml("<font color='#ffffff'>Exit</font>"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface d, int i) {
                        finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton(Html.fromHtml("<font color='#ffffff'>Cancel</font>"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        exitDialog.create();
        exitDialog.show();
    }

    public void createDifficultyDialog(){
        difficultyDialog = new AlertDialog.Builder(this);
        difficultyDialog.setTitle("Select the difficulty: ")
                .setCancelable(true)
                .setSingleChoiceItems(values, 1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which) {
                            case 0:
                                game.setDifficulty("Easy");
                                break;
                            case 1:
                                game.setDifficulty("Normal");
                                break;
                            case 2:
                                game.setDifficulty("Hard");
                                break;
                        }
                    }
                })
                .setPositiveButton(Html.fromHtml("<font color='#ffffff'>Ok</font>"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        difficultyDialog.create();
        difficultyDialog.show();

    }
}