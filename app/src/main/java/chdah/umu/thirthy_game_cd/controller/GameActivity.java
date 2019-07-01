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
        throwCounter.setText(R.string.throws_left + model.getThrowsLeft());
        roundCounter.setText(R.string.rounds_left + (TOTAL_ROUNDS - model.getRoundCount()));
        score.setText(R.string.score + model.getScore());
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

    private void prepareThrowButton() {
        // Set onClickListener to button
        Button calculateScore = findViewById(R.id.calculate_score_b);
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
        private int indexInModel;

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
                    d.setIsAvailable(!d.isAvailable());
                }
            }

            model.incrementRound();
            throwDices.callOnClick();
            throwDices.setEnabled(true);

            //change score, reroll and round left text.
            throwCounter.setText(R.string.throws_left + model.getThrowsLeft());
            roundCounter.setText(R.string.rounds_left + (TOTAL_ROUNDS - model.getRoundCount()));
            score.setText(R.string.score + model.getScore());

            //check if Game is done, if so: return to start screen with score.
            if(model.isGameFinished()) {
                endGame();
            }

            //Unlock all dice.
            model.deselectAllDice();

            //disable current choice button.
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
            throwCounter.setText(R.string.throws_left + model.getThrowsLeft());
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
        intent.putExtra("model", model);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
