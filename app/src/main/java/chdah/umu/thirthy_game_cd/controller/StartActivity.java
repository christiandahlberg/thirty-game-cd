package chdah.umu.thirthy_game_cd.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import chdah.umu.thirthy_game_cd.R;
import chdah.umu.thirthy_game_cd.model.GameModel;

// TODO: Fix semantics, comments, clean the code.

public class StartActivity extends AppCompatActivity {
    // Static and final fields
    private final int GAME_ID = 0;
    private final String SCORE_ARRAY_KEY = "scoreArray";
    private final String SCORE_KEY = "score";
    private static final String SCORE_CHOICE_KEY = "scoreModes";

    // Lists and arrays
    private int[] scores;
    private String[] scoreModes;

    // Normal fields
    private int score = 0;
    private TextView scoreView;

    /**
     * On start, this creates the button which allows player to start the game, see the
     * onClickListener for mStartGameButton (starts a new Intent).
     * @param savedInstanceState, the current saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Button mStartGameButton = findViewById(R.id.start_game_b);
        scoreView = findViewById(R.id.ending_score_tv);
        mStartGameButton.setText(R.string.start_game);

        if(savedInstanceState != null){
            score = savedInstanceState.getInt(SCORE_KEY);
            scores = savedInstanceState.getIntArray(SCORE_ARRAY_KEY);
            scoreModes = savedInstanceState.getStringArray(SCORE_CHOICE_KEY);
            restoreOnSavedInstance();
        }

        mStartGameButton.setOnClickListener((View v) -> {
                Intent gameIntent = new Intent(StartActivity.this, GameActivity.class);
                startActivityForResult(gameIntent, GAME_ID);

        });
    }

    /**
     * Gets the result model from putExtra parcelable and displays the score from the finished game.
     * @param requestCode should resemble the GAME_ID if correct.
     * @param resultCode should resemble Activity.RESULT_OK if everything is correct.
     * @param data resembles the Intent from the GameActivity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GAME_ID){
            if(resultCode == Activity.RESULT_OK){
                GameModel model = data.getParcelableExtra("model");
                score = model.getScore();
                scores = model.getScores();
                scoreModes = model.getScoreChoices();
                restoreOnSavedInstance();
            }
        }
    }

    /**
     * Method to restore a saved instance. Starts with an arraylist to indicate what choice
     * received what points on what round. Finishes of with showing what score user has.
     */
    private void restoreOnSavedInstance() {

        if (scores != null && scoreModes != null) {
            ArrayList<String> scoreArrayList = new ArrayList<>();

            for (int i = 0; i < scores.length; i++) {
                scoreArrayList.add("Round #" + (i + 1) + " receieved the score of: " + scores[i] +
                        " with choosing: " + scoreModes[i]);
            }

            ListView scoreList = findViewById(R.id.scoreChoicesListView);
            ArrayAdapter<String> scoreListAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    scoreArrayList
            );

            scoreList.setAdapter(scoreListAdapter);
            scoreView.setText(R.string.total_score + score);

        }
    }

    /**
     * If score is rotated, remember the score for recreation.
     * @param outState bundle variable.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SCORE_KEY, score);
        outState.putIntArray(SCORE_ARRAY_KEY, scores);
        outState.putStringArray(SCORE_CHOICE_KEY, scoreModes);
    }
}
