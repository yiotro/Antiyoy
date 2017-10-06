package yio.tro.antiyoy.stuff;

import yio.tro.antiyoy.YioGdxGame;

public abstract class RepeatYio<ProviderType> {

    protected ProviderType parent;
    int frequency, countDown;


    public RepeatYio(ProviderType parent, int frequency) {
        this(parent, frequency, YioGdxGame.random.nextInt(Math.max(1, frequency)));
    }


    public RepeatYio(ProviderType parent, int frequency, int defCount) {
        this.parent = parent;
        this.frequency = frequency;
        countDown = defCount;
    }


    public void move() {
        if (countDown <= 0) {
            countDown = frequency;
            performAction();
        } else countDown--;
    }


    public void move(int speed) {
        if (countDown <= 0) {
            countDown = frequency;
            performAction();
        } else countDown -= speed;
    }


    public abstract void performAction();


    public void setCountDown(int countDown) {
        this.countDown = countDown;
    }


    public int getCountDown() {
        return countDown;
    }


    public int getFrequency() {
        return frequency;
    }


    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}
