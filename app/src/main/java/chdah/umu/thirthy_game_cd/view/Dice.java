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
 * Resembles every dice and it's image in the game. Sets an image of a dice depending on the value.
 * Also checking whether or not the dice is available to roll (if user has selected it or not).
 */
public class Dice {
    private ImageButton mDice;
    private Boolean isAvailable;

    public Dice (ImageButton ib) {
        isAvailable = true;
        mDice = ib;
    }

    // SET / GET

    public void setDiceButton(ImageButton ib) {
        mDice = ib;
    }

    public ImageButton getDiceButton () {
        return mDice;
    }

    public void setIsAvailable (Boolean b) {
        isAvailable = b;
    }

    public Boolean getIsAvailable () {
        return isAvailable;
    }

}
