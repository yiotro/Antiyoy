package yio.tro.antiyoy.gameplay;

import yio.tro.antiyoy.gameplay.replays.Replay;

public class SpeedManager {

    public static final int SPEED_PAUSED = 0;
    public static final int SPEED_NORMAL = 1;
    public static final int SPEED_FAST_FORWARD = 2;

    GameController gameController;
    int speed;


    public SpeedManager(GameController gameController) {
        this.gameController = gameController;
    }


    public void stop() {
        gameController.replayManager.onStopButtonPressed();
        setSpeed(SPEED_PAUSED);
    }


    public void setSpeed(int speed) {
        if (this.speed != speed) {
            onSpeedChange(this.speed, speed);
        }

        this.speed = speed;
    }


    private void onSpeedChange(int last, int next) {
        Replay replay = gameController.replayManager.getReplay();
        if (replay == null) return;

        if (next == SPEED_PAUSED) {
            replay.onTacticalPause();
        } else {
            replay.onResumeNormalSpeed();
        }
    }


    public void defaultValues() {
        resetSpeed();
    }


    public void applyPause() {
        setSpeed(SPEED_PAUSED);
    }


    private void resetSpeed() {
        setSpeed(SPEED_FAST_FORWARD);
    }


    public void onPlayPauseButtonPressed() {
        if (speed == SPEED_PAUSED) {
            setSpeed(SPEED_NORMAL);
        } else {
            applyPause();
        }
    }


    public void onFastForwardButtonPressed() {
        if (speed == SPEED_FAST_FORWARD) {
            setSpeed(SPEED_NORMAL);
        } else {
            setSpeed(SPEED_FAST_FORWARD);
        }
    }


    public int getSpeed() {
        return speed;
    }
}
