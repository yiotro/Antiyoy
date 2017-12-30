package yio.tro.antiyoy.stuff.tabs_engine;

import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RepeatYio;
import yio.tro.antiyoy.stuff.scroll_engine.SegmentYio;

import java.util.ArrayList;

public class TabsEngineYio {


    private SegmentYio limits;
    private SegmentYio slider;
    private double speed, maxSpeed;
    private double friction;
    private double softLimitOffset;
    private double cutSpeed, cutOffset;
    private double correction;
    private boolean inTouchMode;
    private int numberOfTabs;
    private double tabWidth;
    ArrayList<TeMagnet> magnets;
    TeMagnet leftMagnet, rightMagnet;
    FactorYio ignoreFactor;
    RepeatYio<TabsEngineYio> repeatUpdateNearbyMagnets;
    private double sliderCenter, magnetMaxPower,
            magnetMaxDistance, magnetPullDistance, magnetCatchDistance;
    int swipeDelay;
    private long touchDownTime, touchDownPos;


    public TabsEngineYio() {
        limits = new SegmentYio();
        slider = new SegmentYio();

        friction = 0.05;
        softLimitOffset = 0;
        cutSpeed = 1;
        cutOffset = 1;
        inTouchMode = false;
        numberOfTabs = 1;
        tabWidth = 0;
        maxSpeed = 0.15 * GraphicsYio.width;
        swipeDelay = 300;
        touchDownPos = 0;
        ignoreFactor = new FactorYio();

        magnets = new ArrayList<>();
        leftMagnet = null;
        rightMagnet = null;
        magnetMaxPower = 0.01 * GraphicsYio.width;;
        magnetMaxDistance = 0;

        resetToBottom();

        repeatUpdateNearbyMagnets = new RepeatYio<TabsEngineYio>(this, 4) {
            @Override
            public void performAction() {
                parent.updateNearbyMagnets();
            }
        };
    }


    public void resetToBottom() {
        resetSpeed();
        correction = limits.a - slider.a;
        if (correction == 0) return;

        relocate(correction);
    }


    public void resetToTop() {
        resetSpeed();
        correction = limits.b - slider.b;
        if (correction == 0) return;

        relocate(correction);
    }


    public void giveImpulse(double impulse) {
        if (impulse > 0 && isOverTop()) return;
        if (impulse < 0 && isBelowBottom()) return;

        speed += impulse;

        if (impulse > 0) {
            relocate(1.1 * magnetCatchDistance);
        } else {
            relocate(-1.1 * magnetCatchDistance);
        }
    }


    private void relocate(double delta) {
        slider.a += delta;
        slider.b += delta;
    }


    public void move() {
        ignoreFactor.move();
        applyNearbyMagnetsPower();

        if (speed == 0) {
            softCorrection();
            return;
        }

        limitSpeed();
        relocate(speed);
        updateSpeed();
        hardCorrection();
        repeatUpdateNearbyMagnets.move();
    }


    private void applyNearbyMagnetsPower() {
        if (inTouchMode) return;
        if (ignoreFactor.get() > 0) return;

        updateSliderCenter();
        applyMagnetPower(leftMagnet);
        applyMagnetPower(rightMagnet);
    }


    private void applyMagnetPower(TeMagnet magnet) {
        if (magnet == null) return;

        double distance = Math.abs(magnet.x - sliderCenter);
        if (distance > magnetMaxDistance) return;
        if (speed == 0 && distance == 0) return;

        if (distance > magnetPullDistance) {
            double ratio = 1 - distance / magnetMaxDistance;
            if (ratio < 0.25) {
                ratio = 0.25;
            }
            double power = magnetMaxPower * ratio;

            if (magnet.x < sliderCenter) {
                speed -= power;
            } else {
                speed += power;
            }
            return;
        }

        if (distance > magnetCatchDistance) {
            relocate(0.2 * (magnet.x - sliderCenter));
            if ((sliderCenter - magnet.x) * speed < 0) {
                resetSpeed();
            }
            return;
        }

        relocate(magnet.x - sliderCenter);
        resetSpeed();
    }


    public int getCurrentTabIndex() {
        updateSliderCenter();

        return (int) (sliderCenter / slider.getLength());
    }


    private void updateNearbyMagnets() {
        leftMagnet = null;
        rightMagnet = null;
        updateSliderCenter();

        for (TeMagnet magnet : magnets) {
            if (magnet.x < sliderCenter) {
                leftMagnet = magnet;
                continue;
            }

            rightMagnet = magnet;
            break;
        }
    }


    public void swipeTab(int direction) {
        ignoreFactor.setValues(1, 0);
        ignoreFactor.destroy(1, 5);

        giveImpulse(direction * 7 * magnetMaxPower);
        setSpeed(direction * 11 * magnetMaxPower);
    }


    private void updateSliderCenter() {
        sliderCenter = (slider.a + slider.b) / 2;
    }


    public TeMagnet getLeftMagnet() {
        return leftMagnet;
    }


    public TeMagnet getRightMagnet() {
        return rightMagnet;
    }


    private void limitSpeed() {
        if (speed > maxSpeed) {
            speed = maxSpeed;
        }

        if (speed < -maxSpeed) {
            speed = -maxSpeed;
        }
    }


    private void updateSpeed() {
        speed *= (1 - friction);

        if (speed != 0 && (inTouchMode || Math.abs(speed) < cutSpeed)) {
            resetSpeed();
        }
    }


    public void hardCorrection() {
        // bottom
        correction = (limits.a - softLimitOffset) - slider.a;
        if (correction > 0) {
            relocate(correction);
            resetSpeed();
            return;
        }

        // top
        correction = (limits.b + softLimitOffset) - slider.b;
        if (correction < 0) {
            relocate(correction);
            resetSpeed();
        }
    }


    public boolean isInSoftCorrectionMode() {
        if (slider.a < limits.a) return true;
        if (slider.b > limits.b) return true;

        return false;
    }


    private boolean softCorrection() {
        if (inTouchMode || speed != 0) return false;

        // bottom
        correction = limits.a - slider.a;
        if (correction > 0) {
            if (correction < cutOffset) {
                resetToBottom();
            } else {
                relocate(0.1 * correction);
            }
            return true;
        }

        // top
        correction = limits.b - slider.b;
        if (correction < 0) {
            if (correction > -cutOffset) {
                resetToTop();
            } else {
                relocate(0.1 * correction);
            }
            return true;
        }

        return false;
    }


    private void resetSpeed() {
        setSpeed(0);
    }


    public SegmentYio getSlider() {
        return slider;
    }


    public double getSpeed() {
        return speed;
    }


    public void setFriction(double friction) {
        this.friction = friction;
    }


    public void setLimits(double a, double b) {
        limits.set(a, b);
    }


    public SegmentYio getLimits() {
        return limits;
    }


    public void setSoftLimitOffset(double softLimitOffset) {
        this.softLimitOffset = softLimitOffset;
    }


    public void setCutSpeed(double cutSpeed) {
        this.cutSpeed = cutSpeed;
    }


    public boolean isBelowBottom() {
        return slider.a < limits.a;
    }


    public boolean isOverTop() {
        return slider.b > limits.b;
    }


    public void onTouchDown() {
        touchDownTime = System.currentTimeMillis();
        updateSliderCenter();
        touchDownPos = (long) sliderCenter;
        inTouchMode = true;
    }


    public void onTouchUp() {
        if (!inTouchMode) return;

        inTouchMode = false;
        if (System.currentTimeMillis() - touchDownTime < swipeDelay) {
            swipe();
        }
    }


    private void swipe() {
        updateSliderCenter();
        if (sliderCenter - touchDownPos > 0) {
            setSpeed(6 * magnetMaxPower);
        } else {
            setSpeed(-6 * magnetMaxPower);
        }
    }


    public void setSpeed(double speed) {
        if (ignoreFactor.get() > 0) return;

        this.speed = speed;
    }


    public void setSlider(double a, double b) {
        slider.set(a, b);
    }


    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }


    public void setNumberOfTabs(int numberOfTabs) {
        this.numberOfTabs = numberOfTabs;

        tabWidth = limits.getLength() / numberOfTabs;

        createMagnets();
    }


    private void createMagnets() {
        float x = (float) (limits.a + tabWidth / 2);
        for (int i = 0; i < numberOfTabs; i++) {
            TeMagnet teMagnet = new TeMagnet();
            teMagnet.x = x;
            magnets.add(teMagnet);
            x += tabWidth;
        }

        magnetMaxDistance = tabWidth / 2;
        magnetPullDistance = magnetMaxDistance / 5;
        magnetCatchDistance = Math.max(magnetPullDistance / 25, 1d);

        updateNearbyMagnets();
    }


    public ArrayList<TeMagnet> getMagnets() {
        return magnets;
    }


    public void setInTouchMode(boolean inTouchMode) {
        this.inTouchMode = inTouchMode;
    }


    public void setMagnetMaxPower(double magnetMaxPower) {
        this.magnetMaxPower = magnetMaxPower;
    }


    public void teleportToTab(int tabIndex) {
        resetToBottom();
        relocate(tabIndex * tabWidth);
    }


    public void setSwipeDelay(int swipeDelay) {
        this.swipeDelay = swipeDelay;
    }


    @Override
    public String toString() {
        return "[TabsEngine" +
                " limit" + limits +
                ", slider" + slider +
                "]";
    }
}
