package chdah.umu.thirthy_game_cd.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import chdah.umu.thirthy_game_cd.R;
import chdah.umu.thirthy_game_cd.model.GameModel;
import chdah.umu.thirthy_game_cd.view.Dice;

public class GameActivity extends AppCompatActivity {

    // Lists and arrays
    private List<Dice> dices = new ArrayList<>();
    private List<RadioButton> choices = new ArrayList<>();
    private Drawable[] diceImages = new Drawable[6];
    private String[] choicesList;

    // Static fields
    private static final int TOTAL_ROUNDS = 10;
    private static final int SCORES_DONE = -1;
    private static final int COLOR_DISABLED = Color.BLACK;
    private static final int COLOR_ENABLED = Color.WHITE;
    private static final String MODEL = "model";

    // Non-static fields
    private Button throwDices;
    private TextView roundCounter;
    private TextView throwCounter;
    private TextView score;
    private GameModel model;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Handle saved instance states
        if (savedInstanceState != null) {
            model = savedInstanceState.getParcelable(MODEL);
        } else {
            model = new GameModel();
        }

        setContentView(R.layout.activity_game);

        prepareTextViews();
        prepareDices();
        prepareChoices();
        prepareDiceButtons();
        prepareThrowButton();
    }

    /**
     * Sets each TextView to specific UI element depending on id.
     */
    private void prepareTextViews() {
        // TODO: Ändra namn på id-variablerna
        throwCounter = findViewById(R.id.rerollCountTextView);
        roundCounter = findViewById(R.id.roundNumberTextView);
        score = findViewById(R.id.scoreTextView);

        populateTextViews();
    }

    /**
     * Populates each TextView with specific data depending on current state of fields.
     */
    private void populateTextViews() {
        // TODO: Fixa funktioner i GameModel
        throwCounter.setText("Throws left: " + model.getThrowsLeft());
        roundCounter.setText("Rounds to go: " + (TOTAL_ROUNDS - model.getRoundsCount()));
        score.setText("Current score: " + model.getScore());
    }

    /**
     * Sets each dice to specific image (drawable).
     */
    private void prepareDices() {
        diceImages[0] = getResources().getDrawable(R.drawable.white1);
        diceImages[1] = getResources().getDrawable(R.drawable.white2);
        diceImages[2] = getResources().getDrawable(R.drawable.white3);
        diceImages[3] = getResources().getDrawable(R.drawable.white4);
        diceImages[4] = getResources().getDrawable(R.drawable.white5);
        diceImages[5] = getResources().getDrawable(R.drawable.white6);
    }

    private void prepareChoices() {
        // PART ONE (adds each choice to choices ArrayList
        choices.add((RadioButton) findViewById(R.id.lowRadioButton));
        choices.add((RadioButton) findViewById(R.id.fourRadioButton));
        choices.add((RadioButton) findViewById(R.id.fiveRadioButton));
        choices.add((RadioButton) findViewById(R.id.sixRadioButton));
        choices.add((RadioButton) findViewById(R.id.sevenRadioButton));
        choices.add((RadioButton) findViewById(R.id.eightRadioButton));
        choices.add((RadioButton) findViewById(R.id.nineRadioButton));
        choices.add((RadioButton) findViewById(R.id.tenRadioButton));
        choices.add((RadioButton) findViewById(R.id.elevenRadioButton));
        choices.add((RadioButton) findViewById(R.id.twelveRadioButton));

        // PART TWO (sets the text of each element in list from string array)
        choicesList = getResources().getStringArray(R.array.choices);
        for (int i = 0; i < choices.size(); i++) {
            choices.get(i).setText(choicesList[i]);
        }

        // PART THREE (links the onClickListener for each choice)
        for (RadioButton rb : choices) {
            rb.setOnClickListener(new radioButtonListener());
        }

        // TODO: Fixa isDisabledSCoreChoice (i GameModel)
        // PART FOUR (Enables/disables choices)
        for (int i = 0; i < choices.size(); i++) {
            if(model.isDisabledScoreChoice(i)){
                choices.get(i).setEnabled(false);
            }
        }
    }

    private void prepareDiceButtons() {
        dices.add(new Dice((ImageButton) findViewById(R.id.diceImageButton1)));
        dices.add(new Dice((ImageButton) findViewById(R.id.diceImageButton2)));
        dices.add(new Dice((ImageButton) findViewById(R.id.diceImageButton3)));
        dices.add(new Dice((ImageButton) findViewById(R.id.diceImageButton4)));
        dices.add(new Dice((ImageButton) findViewById(R.id.diceImageButton5)));
        dices.add(new Dice((ImageButton) findViewById(R.id.diceImageButton6)));

        checkDiceAvailability();
    }

    private void checkDiceAvailability() {
        int index = 0;
        for (Dice diceImage : dices) {
            diceImage.getDiceButton().setBackgroundColor(COLOR_ENABLED);
            diceImage.getDiceButton().setOnClickListener(new DiceImageListener(diceImage, index));
            diceImage.getDiceButton().setImageDrawable(diceImages[model.getDiceRoll()[index] - 1]);
            if(model.isDiceLocked(index)){
                diceImage.setRollable(false);
                diceImage.getmDiceImage().setBackgroundColor(COLOR_DISABLED);
            }
            index++;
        }
    }

    // TODO: FIXA ALLT DETTA (så det ej liknar)
    /**
     * Class for radioButtons to change the current scoremode.
     */
    class radioButtonListener implements View.OnClickListener{

        //Change the score mode
        @Override
        public void onClick(View view) {
            RadioButton rb = (RadioButton) view;
            String scoreMode = rb.getText().toString();
            model.setScoreMode(scoreMode);
        }
    }
}
