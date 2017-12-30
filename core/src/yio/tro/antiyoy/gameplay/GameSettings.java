package yio.tro.antiyoy.gameplay;

import yio.tro.antiyoy.YioGdxGame;


public class GameSettings {

    private final YioGdxGame yioGdxGame;
    private int speed;
    public static final int SPEED_SLOW = 0;
    public static final int SPEED_NORMAL = 1;
    public static final int SPEED_FAST = 2;
    private int difficulty;
    public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_NORMAL = 1;
    public static final int DIFFICULTY_HARD = 2;


    public GameSettings(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
    }


    public void switchSpeed() {
        speed++;
        if (speed > SPEED_FAST) speed = 0;
    }


    public void switchDifficulty() {
        difficulty++;
        if (difficulty > DIFFICULTY_HARD) difficulty = 0;
    }
}
