package chdah.umu.thirthy_game_cd.view;

/**
 *  Dice.java
 *
 *  Thirty Game - an android implementation.
 *  Course 5DV209 (Development of Mobile Applications)
 *  Umea University, Summer of 2019
 *
 *  Christian Dahlberg
 */

import android.widget.ImageButton;

/**
 * Resembles every dice and it's image in the game (the ImageButton-view components themselves).
 * Sets an image of a dice depending on the value.
 * Also checking whether or not the dice is available to roll (if user has selected it or not).
 */
public class Dice {
    private ImageButton mDice;
    private Boolean isAvailable;

    /**
     * Constructor for a Dice
     * @param ib sets the Dice-values to be used in later stages.
     */
    public Dice (ImageButton ib) {
        this.setIsAvailable(true);
        this.setDiceButton(ib);
    }

    // SET / GET

    /**
     *  Links the argument ImageButton to this Dice's ImageButton field.
     * @param ib resembles an object with specific data to be set to this class' field.
     */
    public void setDiceButton(ImageButton ib) {
        mDice = ib;
    }

    /**
     * Returns specific Dice (ImageButton)
     * @return
     */
    public ImageButton getDiceButton () {
        return mDice;
    }

    /**
     * Determines whether or not the Dice object should be available for re-rolling or not.
     * @param b is either True or False, depending on usage, which sets the availability.
     */
    public void setIsAvailable (Boolean b) {
        isAvailable = b;
    }

    /**
     * Returns whether or not the Dice object is available for re-rolling or not.
     * @return
     */
    public Boolean getIsAvailable () {
        return isAvailable;
    }

}
