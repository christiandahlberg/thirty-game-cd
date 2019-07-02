package chdah.umu.thirthy_game_cd.controller;

/*
 *  GameActivity.java
 *
 *  Thirty Game - an android implementation.
 *  Course 5DV209 (Development of Mobile Applications)
 *  Umea University, Summer of 2019
 *
 *  Christian Dahlberg
 */

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

/**
 * The actual activity which resembles runtime during gameplay. Making use of GameModel and Dice.
 */
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

        // Prepare each element in the game for further usage
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
        throwCounter = findViewById(R.id.throw_counter_tv);
        roundCounter = findViewById(R.id.round_counter_tv);
        score = findViewById(R.id.score_tv);

        // After initializing and assigning, we now populate the views.
        populateTextViews();
    }

    /**
     * Populates each TextView with specific data depending on current state of fields.
     */
    private void populateTextViews() {
        throwCounter.setText(getString(R.string.throws_left) + model.getThrowsLeft());
        roundCounter.setText(getString(R.string.rounds_left) + (TOTAL_ROUNDS - model.getRoundCount()));
        score.setText(getString(R.string.score) + model.getFullScore());
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

    /**
     * This method is split up in four different parts:
     * PART ONE: Adding each view (RadioButton) depending on their id to our choices list.
     * PART TWO: Sets the text of each choice from a straing array holding each choice.
     * PART THREE: Links an onClickListener for each RadioButton (choice).
     * PART FOUR: Checks if we are to enable or disable a choice, so users can't cheat.
     */
    private void prepareChoices() {
        // PART ONE
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

        // PART TWO
        String[] choicesList = getResources().getStringArray(R.array.choices);
        for (int i = 0; i < choices.size(); i++) {
            choices.get(i).setText(choicesList[i]);
        }

        // PART THREE
        for (RadioButton rb : choices) {
            rb.setOnClickListener(new radioButtonListener());
        }

        // TODO: Fix isDisabledSCoreChoice (i GameModel)
        // PART FOUR
        for (int i = 0; i < choices.size(); i++) {
            if(model.isDisabledScoreChoice(i)){
                choices.get(i).setEnabled(false);
            }
        }
    }

    /**
     * Adding the dices to our dice-list ('dices'), making usage of Dice-class constructor.
     * Also checks dice availability with checkDiceAvailability() usage.
     */
    private void prepareDiceButtons() {
        dices.add(new Dice(findViewById(R.id.diceImageButton1)));
        dices.add(new Dice(findViewById(R.id.diceImageButton2)));
        dices.add(new Dice(findViewById(R.id.diceImageButton3)));
        dices.add(new Dice(findViewById(R.id.diceImageButton4)));
        dices.add(new Dice(findViewById(R.id.diceImageButton5)));
        dices.add(new Dice(findViewById(R.id.diceImageButton6)));

        checkDiceAvailability();
    }

    /**
     * Prepares every dice with it's background color. Using background color to see if dice
     * is selected or not. If dice gets selected: background color changes to black and sets
     * to non-available for rolling.
     */
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

    /**
     * Prepares the dice throwing button in three stages.
     * PART ONE: Sets the onClickListener to our initiated button. See RoundEnding();.
     * PART TWO: Assigns the button to our field and sets the onClickListener. See ThrowDice();.
     * PART THREE: Sets texts to appropriate textfields.
     */
    private void prepareThrowButton() {
        // PART ONE
        Button calculateScore = findViewById(R.id.calculate_score_b);
        calculateScore.setOnClickListener(new RoundEnding());

        // PART TWO
        throwDices = findViewById(R.id.roll_dice_b);
        throwDices.setOnClickListener(new ThrowDice());

        // PART THREE
        calculateScore.setText(R.string.end_round);
        throwDices.setText(R.string.roll_dice);

        // Checks if user should be able to throw more dices on current round.
        if (!model.isRollable()) {
            throwDices.setEnabled(false);
        }
    }

    /**
     * onClickListener for RadioButtons to see what choice the user chooses.
     * Depending on what choice, then points will be assigned accordingly.
     */
    class radioButtonListener implements View.OnClickListener{
        // TODO: Fix unchecking of radiobuttons
        @Override
        public void onClick(View view) {
            RadioButton rb = (RadioButton) view;
            String choice = rb.getText().toString();
            model.setChoiceMode(choice);
        }
    }

    /**
     * onClickListener for the Dice-images.
     * onClick() disables or enables the dice buttons depending on availability (selection).
     * Last row sets the image to opposite of what it was before clicking (available > unavailable).
     */
    class DiceImageListener implements View.OnClickListener{
        private Dice diceImage;
        private int modelIndex;

        /**
         * Constructor for listener.
         * @param dice is the image.
         * @param index is what index the dice has in the model.
         */
        private DiceImageListener(Dice dice, int index) {
            this.diceImage = dice;
            this.modelIndex = index;
        }

        @Override
        public void onClick(View view) {
            if(diceImage.isAvailable()){
                diceImage.getDiceButton().setBackgroundColor(COLOR_DISABLED);
                model.selectDice(modelIndex);
            } else {
                diceImage.getDiceButton().setBackgroundColor(COLOR_ENABLED);
                model.deselectDice(modelIndex);
            }
            diceImage.setIsAvailable(!diceImage.isAvailable());
        }
    }

    /***
     * onClickListener for End Round-button (method), which will end the round and go to
     * the score-screen if the next upcoming round is greater than the max round value.
     * The heading depends on the return value of GameModel.isGameFinished() method.
     */
    class RoundEnding implements View.OnClickListener{

        /**
         * Ends the current round. Starts of with making all dices available for rolling (and thus
         * rolls the dices one time, making the 'throws left'-variable value to 2).
         * It then changes the score, makes a re-roll of available dices and changes text to
         * the appropriate amount of rounds there are left in the game.
         * @param view resembles the 'end game' button.
         */
        @Override
        public void onClick(View view) {
            for (Dice d : dices) {
                if (!d.isAvailable()) {
                    d.getDiceButton().setBackgroundColor(COLOR_ENABLED);
                    d.setIsAvailable(!d.isAvailable());
                }
            }

            // Makes runtime changes
            model.incrementRound();
            throwDices.callOnClick();
            throwDices.setEnabled(true);

            // Set appropriate text depending on values
            throwCounter.setText(getString(R.string.throws_left) + model.getThrowsLeft());
            roundCounter.setText(getString(R.string.rounds_left) + (TOTAL_ROUNDS - model.getRoundCount()));
            score.setText(getString(R.string.score) + model.getFullScore());

            if(model.isGameFinished()) {
                endGame();
            }

            // When you end the round, you are yet again allowed to roll all the buttons.
            model.deselectAllDice();

            // Pre-clicks the next available choice
            if(model.getAvailableChoice() != SCORES_DONE) {
                choices.get(model.getAvailableChoice()).performClick();
            }

            // Checks all the choices and disables the choices that has already been used.
            for (int i = 0; i < choices.size(); i++) {
                if(model.isDisabledScoreChoice(i)){
                    choices.get(i).setEnabled(false);
                }
            }
        }
    }

    /**
     * onClickListener for Throw Dice-button. Starts of with updating each image that
     * d.isAvailable() returns true (if the button is available to be thrown, ie not selected).
     * And lastly just update the throw counter text to appropriate value, as well as sets the
     * button to false if the dices can't be thrown again.
     */
    class ThrowDice implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            int index = 0;
            int value;
            model.throwDices();
            boolean canClickAgain = model.isRollable();

            for (Dice d : dices) {
                if (d.isAvailable()) {
                    value = model.getDiceRolls()[index] - 1;
                    d.getDiceButton().setImageDrawable(diceImages[value]);
                }
                index++;
            }

            throwCounter.setText(getString(R.string.throws_left) + model.getThrowsLeft());
            if(!canClickAgain){
                view.setEnabled(false);
            }
        }
    }

    /**
     * If reset or if rotated, this will stop program from crashing.
     * @param state resembles Bundle
     */
    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putParcelable(MODEL, model);
    }

    /**
     * End the game and return to previous screen.
     */
    private void endGame() {
        Toast.makeText(GameActivity.this,
                getString(R.string.score) + model.getFullScore(),
                Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.putExtra("model", model);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
