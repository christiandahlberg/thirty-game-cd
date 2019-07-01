package chdah.umu.thirthy_game_cd.model;

/*
 *  GameModel.java
 *
 *  Thirty Game - an android implementation.
 *  Course 5DV209 (Development of Mobile Applications)
 *  Umea University, Summer of 2019
 *
 *  Christian Dahlberg
 */

import android.os.Parcel;
import android.os.Parcelable;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GameModel implements Parcelable {
    // Static and final fields (public)
    private static final int DICE_QUANTITY = 6;
    private static final int POINTS_MIN = 2;
    private static final int POINTS_MAX = 13;
    private static final int POINTS_LOW = 3;
    private static final int POINTS_FOUR = 4;
    private static final int POINTS_FIVE = 5;
    private static final int POINTS_SIX = 6;
    private static final int POINTS_SEVEN = 7;
    private static final int POINTS_EIGHT = 8;
    private static final int POINTS_NINE = 9;
    private static final int POINTS_TEN = 10;
    private static final int POINTS_ELEVEN = 11;
    private static final int POINTS_TWELVE = 12;

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
    private int[] diceRolls = new int[DICE_QUANTITY];
    private boolean[] selectedDices = new boolean[DICE_QUANTITY];
    private int[] scores = new int [ROUND_MAX];
    private String[] choices = new String[ROUND_MAX];
    private boolean[] usedChoices = new boolean[ROUND_MAX];

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
        for (int i = 0; i < diceRolls.length; i++) {
            diceRolls[i] = random.nextInt(DICE_QUANTITY) + 1;
        }
        throwCounter++;

        checkRollCounter();
    }

    /**
     * Checks if user has played the final round. That is, if the throw counter exceeds/is less
     * than the maximum amount of allowed throws (THROWS_MAX field).
     */
    private void checkRollCounter() {
        if (throwCounter < THROWS_MAX) {
            isRollable = true;
        } else {
            isRollable = false;
        }
    }

    /**
     * A method which will increment the round counter and calculate what score user received
     * depending on what values and choice user had. Then the choice gets unabled to be chosen
     * again.
     * @return Method is returning false if next round is the last. Otherwise true.
     */
    public boolean incrementRound() {
        // Can't go to next round if current round is the last
        if (roundCounter == ROUND_MAX) {
            return false;
        }

        calculateRoundScore();
        usedChoices[choice - POINTS_LOW] = true;

        // Reset throw counter and increment round counter in preparation for next round.
        roundCounter++;
        throwCounter = 0;
        return true;
    }

    /**
     * Calculates the points received from each round using an algorithm that automatically
     * chooses the combination which gain highest score depending on what dices are present
     * and what choice the user makes.
     */
    private void calculateRoundScore() {
        // When choosing Low: adds every dice with its value of 3 and below.
        if (choice == POINTS_LOW) {
            for (int dice : diceRolls) {
                if (dice <= POINTS_LOW) {
                    scores[roundCounter] += dice;
                }
            }
        } else {
            ArrayList<Integer> algTempDice = new ArrayList<>();
            for (int dice : diceRolls) {
                if (dice <= choice) {
                    algTempDice.add(dice);
                }
            }
            // Sorts list using lambda expression before using selection algorithm.
            Collections.sort(algTempDice, (dice1, dice2) -> dice2 - dice1);
            findBestSelection(algTempDice,0, choice);

        }
    }

    /**
     * Recursive algorithm for automatic points allocation.
     * @param nbrs A list of numbers where no number exceed the value of 'choice' argument.
     * @param summary Starting integer of 0, will get appended for each recursion. Resembles
     *                the summary of choice values added together.
     * @param choice Resembles the choice (ie the target) for the summary to reach.
     */
    private void findBestSelection(ArrayList<Integer> nbrs, int summary, int choice) {
        if (summary == choice) {
            scores[roundCounter] += summary;
            return;
        }

        for (int i = 0; i < nbrs.size(); i++) {
            if(summary + nbrs.get(i) <= choice){
                summary += nbrs.get(i);
                nbrs.remove(i);
                findBestSelection(nbrs, summary, choice);

                // If summary is equal the the choice value, then step back and assign summary to 0.
                i = -1;
                summary = 0;
            }
        }
    }

    /**
     * Method that selects the dice and locks it in place.
     * @param index The argument resembles what index in the array to lock.
     */
    public void selectDice(int index){
        selectedDices[index] = true;
    }

    /**
     * Method that de-selects the dice and locks it in place.
     * @param index The argument resembles what index in the array to unlock.
     */
    public void deselectDice(int index){
        selectedDices[index] = false;
    }

    /**
     * Method that will deselct all the dices. Mainly used when ending a round to get a fresh start.
     */
    public void deselectAllDice() {
        for (int i = 0; i < selectedDices.length; i++) {
            selectedDices[i] = false;
        }
    }

    /**
     * Get-method that returns the first available choice
     * @return the index of the first available choice.
     *         Returns -1 if there are no available choices left.
     */
    public int getAvailableChoice(){
        for (int i = 0; i < usedChoices.length; i++) {
            if(!usedChoices[i]){
                return i;
            }
        }
        return -1;
    }

    /**
     * Checking every element of scores-array to sequentially add each score
     * @return return the summary of all scores in the array.
     */
    public int getFullScore() {
        int tempScore = 0;
        for (int i : scores) {
            tempScore += i;
        }
        return tempScore;
    }

    /**
     * Get-method for receiving the list of all scores.
     * @return returns the scores-array.
     */
    public int[] getScoresList() {
        return scores;
    }


    /**
     * Method that sets the choice depending on user action.
     * @param choice String that resembles the user choice, which the switch-case is based on.
     */
    public void setChoiceMode(String choice){
        choices[roundCounter] = choice;
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
     * Overloaded method that assures that no points can be set wrongly.
     * @param score Argument for a score to be tested and set.
     * @throws InvalidParameterException A message with information regarding what went wrong.
     */
    private void setChoiceMode(int score) throws InvalidParameterException {
        //Make sure illegal score modes can not be set.
        if(score < POINTS_MAX){
            if (score > POINTS_MIN ) {
                if (!usedChoices[score - POINTS_LOW]) {
                    this.choice = score;
                } else {
                    throw new InvalidParameterException("Score has to be available and between 3 and 13.");
                }
            }
        }
    }

    /**
     * Method for getting the dice values.
     * @return Returning the full list containing the values.
     */
    public int[] getDiceRolls() {
        return diceRolls;
    }



    /**
     * Simple method that will check the amount of throws left for user action.
     * @return Returns a number of how many throws are left (max 3 per round).
     */
    public int getThrowsLeft() {
        return THROWS_MAX - throwCounter;
    }

    /**
     * Simple method which checks what round is current.
     * @return Returns a number of how many rounds user has played (max 10 rounds).
     */
    public int getRoundCount() {
        return roundCounter;
    }

    /**
     * Simple method which checks dice throwing availability.
     * @return Returns either True or False depending on state.
     */
    public boolean isRollable() {
        return isRollable;
    }

    /**
     * Method that will check if the current choice from user is available or disabled.
     * @param index Integer that resembles the index for which choice to check.
     * @return Method will return either True or False depending on availability.
     */
    public boolean isDisabledScoreChoice(int index){
        return usedChoices[index];
    }

    /**
     * Method that will check if the game is finished or not depending on round counter.
     * @return Returns either True or False depending on the current round state.
     */
    public boolean isGameFinished(){
        return roundCounter >= ROUND_MAX;
    }

    /**
     * Method that checks if a dice is selected by the user or not.
     * @param index Integer that resembles what index in the list to check.
     * @return Returns either True or False if the dice with said index (argument) is selected
     *         or not.
     */
    public boolean isDiceSelected(int index){
        return selectedDices[index];
    }

    /**
     * Method that asks for the list of choices available.
     * @return A full string array containing all the choices.
     */
    public String[] getScoreChoices() {
        return choices;
    }

    /**
     * A method that is used to bind everything together.
     * Parcelable method that will save data and in itself make sure that the program is parcelable.
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

    /**
     * A must-have method for implementing the Parcelable interface. Can either return hashCode()
     * or a custom value.
     * @return In this case a custom value has beend chosen.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Method where every class properties is added to the parcel, which are needed to transfer.
     * @param dest parcel
     * @param i flags
     */
    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeIntArray(diceRolls);
        dest.writeIntArray(scores);
        dest.writeStringArray(choices);
        dest.writeBooleanArray(usedChoices);
        dest.writeBooleanArray(selectedDices);
        dest.writeInt(isRollable ? 1 : 0);
        dest.writeInt(roundCounter);
        dest.writeInt(throwCounter);
        dest.writeInt(choice);
    }

    /**
     * A constructor which are to be called on receiving activity where you will be
     * receiving values. Can also be used to restore and reset the model if it were to be
     * destroyed. (In short a constructor used for parcel).
     * @param parcel -
     */
    private GameModel (Parcel parcel) {
        parcel.readIntArray(diceRolls);
        parcel.readIntArray(scores);
        parcel.readStringArray(choices);
        parcel.readBooleanArray(usedChoices);
        parcel.readBooleanArray(selectedDices);
        isRollable = parcel.readInt() != 0;
        roundCounter = parcel.readInt();
        throwCounter = parcel.readInt();
        choice = parcel.readInt();
        random = new Random();
    }

}
