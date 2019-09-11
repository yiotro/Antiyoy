package yio.tro.antiyoy.menu.fireworks_element;

import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.RectangleYio;
import yio.tro.antiyoy.stuff.Yio;
import yio.tro.antiyoy.stuff.object_pool.ReusableYio;

public class FeParticle implements ReusableYio{


    RectangleYio limits;
    public PointYio position;
    PointYio speed;
    public float radius, viewRadius;
    float gravity;
    FactorYio appearFactor;
    public int viewType;
    public double viewAngle, aDelta;
    int lifeEnergy;


    public FeParticle() {
        limits = new RectangleYio();
        position = new PointYio();
        speed = new PointYio();
        appearFactor = new FactorYio();
    }


    @Override
    public void reset() {
        limits.set(0, 0, GraphicsYio.width, GraphicsYio.height);
        viewType = 0;
        position.set(0, 0);
        speed.set(0, 0);
        radius = 0.05f * GraphicsYio.width;
        lifeEnergy = 5 * 60;
        gravity = radius / 125;
        viewAngle = Yio.getRandomAngle();
        aDelta = (2 * YioGdxGame.random.nextDouble() - 1) * 0.25;
        viewRadius = 0;
        appearFactor.setValues(0, 0);
        appearFactor.stopMoving();
    }


    void move() {
        appearFactor.move();
        updateViewRadius();
        movePosition();
        slowDown();
        applyLimits();
        moveAngle();
        moveLifeEnergy();
    }


    private void moveAngle() {
        aDelta *= 0.98;

        viewAngle += aDelta;
    }


    private void moveLifeEnergy() {
        if (lifeEnergy > 0) {
            lifeEnergy--;
        } else {
            kill();
        }
    }


    void kill() {
        lifeEnergy = 0;
        destroy();
    }


    boolean isAlive() {
        return lifeEnergy > 0 || appearFactor.get() > 0;
    }


    private void slowDown() {
        speed.x *= 0.99f;
        speed.y *= 0.99f;
    }


    private void applyLimits() {
        if (position.x > limits.x + limits.width - viewRadius) {
            position.x = (float) (limits.x + limits.width - viewRadius);
            speed.x = - Math.abs(speed.x);
            aDelta *= 0.9;
        }

        if (position.x < limits.x + viewRadius) {
            position.x = (float) (limits.x + viewRadius);
            speed.x = Math.abs(speed.x);
            aDelta *= 0.9;
        }

        if (position.y < limits.y + viewRadius) {
            position.y = (float) (limits.y + viewRadius);
            speed.y = 0.6f * Math.abs(speed.y);
            lifeEnergy -= 5;
            aDelta *= 0.7;
        }
    }


    void applyGravity(double gravityAngle) {
        speed.relocateRadial(gravity, gravityAngle);
    }


    private void movePosition() {
        position.x += speed.x;
        position.y += speed.y;
    }


    private void updateViewRadius() {
        viewRadius = appearFactor.get() * radius;
    }


    void appear() {
        appearFactor.appear(3, 1);
    }


    void destroy() {
        appearFactor.destroy(1, 3);
    }
}
