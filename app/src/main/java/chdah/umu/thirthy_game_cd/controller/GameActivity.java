package chdah.umu.thirthy_game_cd.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

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

        prepareThrowButton();
        prepareTextViews();
        prepareDices();
        prepareChoices();
        prepareDiceButtons();
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
        roundCounter.setText("Rounds to go: " + (TOTAL_ROUNDS - model.getRoundCount()));
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
        choices.add(findViewById(R.id.lowRadioButton));
        choices.add(findViewById(R.id.fourRadioButton));
        choices.add(findViewById(R.id.fiveRadioButton));
        choices.add(findViewById(R.id.sixRadioButton));
        choices.add(findViewById(R.id.sevenRadioButton));
        choices.add(findViewById(R.id.eightRadioButton));
        choices.add(findViewById(R.id.nineRadioButton));
        choices.add(findViewById(R.id.tenRadioButton));
        choices.add(findViewById(R.id.elevenRadioButton));
        choices.add(findViewById(R.id.twelveRadioButton));

        // PART TWO (sets the text of each element in list from string array)
        String[] choicesList = getResources().getStringArray(R.array.choices);
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
        dices.add(new Dice(findViewById(R.id.diceImageButton1)));
        dices.add(new Dice(findViewById(R.id.diceImageButton2)));
        dices.add(new Dice(findViewById(R.id.diceImageButton3)));
        dices.add(new Dice(findViewById(R.id.diceImageButton4)));
        dices.add(new Dice(findViewById(R.id.diceImageButton5)));
        dices.add(new Dice(findViewById(R.id.diceImageButton6)));

        checkDiceAvailability();
    }

    private void checkDiceAvailability() {
        int index = 0;
        for (Dice diceImage : dices) {
            diceImage.getDiceButton().setBackgroundColor(COLOR_ENABLED);
            diceImage.getDiceButton().setOnClickListener(new DiceImageListener(diceImage, index));
            diceImage.getDiceButton().setImageDrawable(diceImages[model.getDiceRolls()[index] - 1]);
            if(model.isDiceSelected(index)){
                diceImage.setIsAvailable(false);
                diceImage.getDiceButton().setBackgroundColor(COLOR_DISABLED);
            }
            index++;
        }
    }

    private void prepareThrowButton() {
        // Set onClickListener to button
        Button calculateScore = findViewById(R.id.calculateScoreButton);
        calculateScore.setOnClickListener(new RoundEnding());

        // Prepare Throw Dices button
        throwDices = findViewById(R.id.rollDiceButton);
        throwDices.setOnClickListener(new ThrowDice());

        // Set texts for buttons
        calculateScore.setText(R.string.end_round);
        throwDices.setText(R.string.roll_dice);

        if (!model.isRollable()) {
            throwDices.setEnabled(false);
        }
    }

    // TODO: ////////////////////////////////////
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
            model.setChoiceMode(scoreMode);
        }
    }

    /**
     * If a image is clicked, toggle if it is rollable.
     */
    class DiceImageListener implements View.OnClickListener{
        private Dice diceImage;
        private int indexInModel = 0;

        private DiceImageListener(Dice linkedDiceImage, int indexInModel) {
            this.diceImage = linkedDiceImage;
            this.indexInModel = indexInModel;
        }

        //Disable or enable dice buttons.
        @Override
        public void onClick(View view) {
            //If image is rollable, lock it.
            if(diceImage.isAvailable()){
                diceImage.getDiceButton().setBackgroundColor(COLOR_DISABLED);
                model.selectDice(indexInModel);
            }
            //otherwise unlock it.
            else{
                diceImage.getDiceButton().setBackgroundColor(COLOR_ENABLED);
                model.unselectDice(indexInModel);
            }
            //change to opposite of current rollable.
            diceImage.setIsAvailable(!diceImage.isAvailable());
        }
    }

    /***
     * End round and if it is the final round go to score screen.
     */
    class RoundEnding implements View.OnClickListener{

        /**
         * Ends the round
         * @param view the button that ends the round.
         */
        @Override
        public void onClick(View view) {
            // Make all buttons available for rolling
            for (Dice d : dices) {
                if (!d.isAvailable()) {
                    d.getDiceButton().setBackgroundColor(COLOR_ENABLED);
                    d.setIsAvailable(!d.isAvailable()); // Varför inte bara true?
                }
            }

            model.incrementRound();
            throwDices.callOnClick();
            throwDices.setEnabled(true);

            //change score, reroll and round left text.
            throwCounter.setText(getString(R.string.throws_left) + model.getThrowsLeft());
            roundCounter.setText(getString(R.string.rounds_left )+ (TOTAL_ROUNDS - model.getRoundCount()));
            score.setText(getString(R.string.score )+ model.getScore());

            //check if Game is done, if so: return to start screen with score.
            if(model.isGameFinished()) {
                endGame();
            }

            //Unlock all dice.
            model.deselectAllDice();

            //disable current scorechoice button.
            if(model.getAvailableScoreMode() != SCORES_DONE) {
                choices.get(model.getAvailableScoreMode()).performClick();
            }
            for (int i = 0; i < choices.size(); i++) {
                if(model.isDisabledScoreChoice(i)){
                    choices.get(i).setEnabled(false);
                }
            }
        }
    }

    class ThrowDice implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            int index = 0;
            int value;
            model.throwDices();
            boolean canClickAgain = model.isRollable();

            //Update images.
            for (Dice d : dices) {
                if (d.isAvailable()) {
                    value = model.getDiceRolls()[index] - 1;
                    d.getDiceButton().setImageDrawable(diceImages[value]);
                }
                index++;
            }

            //Update reroll count
            throwCounter.setText(getString(R.string.throws_left) + model.getThrowsLeft());
            if(!canClickAgain){
                view.setEnabled(false);
            }
        }
    }

    /**
     * End the game and return to previous screen.
     */
    private void endGame() {
        Toast.makeText(GameActivity.this,
                getString(R.string.score) + model.getScore(),
                Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        //intent.putExtra("model", model);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
