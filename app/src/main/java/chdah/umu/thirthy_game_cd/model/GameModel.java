package chdah.umu.thirthy_game_cd.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.appcompat.app.AppCompatActivity;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GameModel extends AppCompatActivity {
    // Static and final fields (public)
    public static final int DICE_QUANTITY = 6;
    public static final int POINTS_MIN = 2;
    public static final int POINTS_MAX = 13;
    public static final int POINTS_LOW = 3;
    public static final int POINTS_FOUR = 4;
    public static final int POINTS_FIVE = 5;
    public static final int POINTS_SIX = 6;
    public static final int POINTS_SEVEN = 7;
    public static final int POINTS_EIGHT = 8;
    public static final int POINTS_NINE = 9;
    public static final int POINTS_TEN = 10;
    public static final int POINTS_ELEVEN = 11;
    public static final int POINTS_TWELVE = 12;

    // Non-static final fields
    private final int THROWS_MAX = 3;
    private final int ROUND_MAX = 10;

    // Normal fields
    private int choice = 4;
    private boolean isRollable = true;
    private int roundCounter;
    private int throwCounter;
    private Random random;

    // Arrays
    private int DiceRolls[] = new int[DICE_QUANTITY];
    private boolean SelectedDices[] = new boolean[DICE_QUANTITY];
    private int Scores[] = new int [ROUND_MAX];
    private String Choices[] = new String[ROUND_MAX];
    private boolean UsedChoices[] = new boolean[ROUND_MAX];

    /**
     * Constructor which populates empty fields and sets the game in motion, rolling dices.
     */
    public GameModel() {
        random = new Random();
        roundCounter = 0;
        throwCounter = 0;
        throwDices();

    }

    public void throwDices() {
        for (int i = 0; i < DiceRolls.length; i++) {
            DiceRolls[i] = random.nextInt(DICE_QUANTITY) + 1;
        }
        throwCounter++;

        checkRollCounter();
    }

    /**
     * Checks if user has played the final round.
     */
    public void checkRollCounter() {
        if (throwCounter < THROWS_MAX) {
            isRollable = true;
        } else {
            isRollable = false;
        }
    }

    public boolean incrementRound() {
        // Can't go to next round if current round is the last
        if (roundCounter == ROUND_MAX) {
            return false;
        }

        //Calculate round score.
        calculateRoundScore();

        //make sure user cant use same score choice again.
        UsedChoices[choice - POINTS_LOW] = true;

        // Reset throw counter and increment round counter in preparation for next round.
        roundCounter++;
        throwCounter = 0;
        return true;
    }

    public void calculateRoundScore() {
        if (choice == POINTS_LOW) {
            for (int dice : DiceRolls) {
                // Selects and adds every dice that has a value of three and below
                if (dice <= POINTS_LOW) {
                    Scores[roundCounter] += dice;
                }
            }
        } else {
            ArrayList<Integer> algTempDice = new ArrayList<>();
            for (int dice : DiceRolls) {
                if (dice <= choice) {
                    algTempDice.add(dice);
                }
            }
            // Sort list before using selection algorithm.
            Collections.sort(algTempDice, (dice1, dice2) -> dice2 - dice1);
            findBestSelection(algTempDice,0, choice);

        }
    }

    private void findBestSelection(ArrayList<Integer> nbrs, int summary, int mode) {
        if (summary == mode) {
            Scores[roundCounter] += summary;
            return;
        }

        for (int i = 0; i < nbrs.size(); i++) {

            if(summary + nbrs.get(i) <= mode){
                summary += nbrs.get(i);
                nbrs.remove(i);
                findBestSelection(nbrs, summary, mode);
                i = -1;
                summary = 0;
            }
        }
    }


    private void setDiceRoll(int[] diceRoll) {
        this.DiceRolls = diceRoll;
    }

    private void setChoiceMode(int score) throws InvalidParameterException {
        //Make sure illegal score modes can not be set.
        if(score < POINTS_MAX && score > POINTS_MIN && !UsedChoices[score - POINTS_LOW]){
            this.choice = score;
        } else{
            throw new InvalidParameterException("Score has to be available and between 3 and 13.");
        }
    }

    /**
     * Lock the selected dice.
     * @param index the index of the dice to lock.
     */
    public void selectDice(int index){
        SelectedDices[index] = true;
    }

    /**
     * Unlock the selected dice for rolling
     * @param index the index of the dice to unlock.
     */
    public void unselectDice(int index){
        SelectedDices[index] = false;
    }

    /**
     * Iterate all scores and add them to a score int.
     * @return return the score
     */
    public int getScore() {
        int tempScore = 0;
        for (int i : Scores) {
            tempScore += i;
        }
        return tempScore;
    }

    public int[] getScores() {
        return Scores;
    }

    /**
     * Return the first available score mode.
     * @return the index of an available score mode, or -1 if none.
     */
    public int getAvailableScoreMode(){
        for (int i = 0; i < UsedChoices.length; i++) {
            if(UsedChoices[i] == false){
                return i;
            }
        }
        return -1;
    }

    /**
     * Sets the score choice based on the given string.
     * @param choice the string that sets the score mode.
     */
    public void setChoiceMode(String choice){
        Choices[roundCounter] = choice;
        switch(choice){
            case "Low":
                setChoiceMode(POINTS_LOW);
                break;
            case "Four":
                setChoiceMode(POINTS_FOUR);
                break;
            case "Five":
                setChoiceMode(POINTS_FIVE);
                break;
            case "Six":
                setChoiceMode(POINTS_SIX);
                break;
            case "Seven":
                setChoiceMode(POINTS_SEVEN);
                break;
            case "Eight":
                setChoiceMode(POINTS_EIGHT);
                break;
            case "Nine":
                setChoiceMode(POINTS_NINE);
                break;
            case "Ten":
                setChoiceMode(POINTS_TEN);
                break;
            case "Eleven":
                setChoiceMode(POINTS_ELEVEN);
                break;
            case "Twelve":
                setChoiceMode(POINTS_TWELVE);
                break;
            default:
                setChoiceMode(POINTS_LOW);
                break;
        }
    }

    /**
     * Check if the score choice is disabled or not
     * @param index the index of the score choice.
     * @return
     */
    public boolean isDisabledScoreChoice(int index){
        return UsedChoices[index];
    }

    public int[] getDiceRolls() {
        return DiceRolls;
    }

    public boolean isRollable() {
        return isRollable;
    }

    public int getThrowsLeft() {
        return THROWS_MAX - throwCounter;
    }

    public int getRoundCount() {
        return roundCounter;
    }

    public boolean isGameFinished(){
        return roundCounter >= ROUND_MAX;
    }

    public boolean isDiceSelected(int index){
        return SelectedDices[index];
    }

    /**
     * Make sure program is parcable, i.e. save all the data.
     */
    public static final Parcelable.Creator<GameModel> CREATOR
            = new Parcelable.Creator<GameModel>() {
        public GameModel createFromParcel(Parcel in) {
            return new GameModel(in);
        }

        public GameModel[] newArray(int size) {
            return new GameModel[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    /**
     * Save data to parcel.
     * @param dest
     * @param i
     */
    public void writeToParcel(Parcel dest, int i) {
        dest.writeInt(isRollable ? 1 : 0);
        dest.writeInt(roundCounter);
        dest.writeInt(throwCounter);
        dest.writeInt(choice);
        dest.writeIntArray(DiceRolls);
        dest.writeIntArray(Scores);
        dest.writeStringArray(Choices);
        dest.writeBooleanArray(UsedChoices);
        dest.writeBooleanArray(SelectedDices);
    }

    /**
     * Unlocks all the dice.
     */
    public void deselectAllDice() {
        for (int i = 0; i < SelectedDices.length; i++) {
            SelectedDices[i] = false;
        }
    }












    /**
     * If the model is destroyed it can be reset in this constructor.
     * @param in
     */
    public GameModel (Parcel in) {
        in.readIntArray(DiceRolls);
        in.readIntArray(Scores);
        in.readStringArray(Choices);
        in.readBooleanArray(UsedChoices);
        in.readBooleanArray(SelectedDices);
        isRollable = in.readInt() != 0;
        roundCounter = in.readInt();
        throwCounter = in.readInt();
        choice = in.readInt();
        random = new Random();
    }

}
