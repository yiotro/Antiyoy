package yio.tro.antiyoy.menu.fireworks_element;

import com.badlogic.gdx.Gdx;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.*;
import yio.tro.antiyoy.stuff.object_pool.ObjectPoolYio;

import java.util.ArrayList;

public class FireworksElement extends InterfaceElement{


    MenuControllerYio menuControllerYio;
    public RectangleYio position;
    public FactorYio appearFactor;
    public ArrayList<FeParticle> particles;
    ObjectPoolYio<FeParticle> poolParticles;
    PointYio touchPoint, tempPoint;
    RepeatYio<FireworksElement> repeatCheckForDeadParticles, repeatExplode;
    double defGravityAngle, gravityAngleDelta;
    private float accelX;
    private double gravityAngle;
    RepeatYio<FireworksElement> repeatUpdateAccelerometer, repeatMakeSequence;
    ObjectPoolYio<FeSequence> poolSequences;
    ArrayList<FeSequence> sequences;


    public FireworksElement(MenuControllerYio menuControllerYio, int id) {
        super(id);
        this.menuControllerYio = menuControllerYio;

        position = new RectangleYio();
        appearFactor = new FactorYio();
        particles = new ArrayList<>();
        touchPoint = new PointYio();
        tempPoint = new PointYio();
        sequences = new ArrayList<>();

        defGravityAngle = - Math.PI / 2;

        initPools();
        initRepeats();
    }


    private void initPools() {
        poolParticles = new ObjectPoolYio<FeParticle>() {
            @Override
            public FeParticle makeNewObject() {
                return new FeParticle();
            }
        };

        poolSequences = new ObjectPoolYio<FeSequence>() {
            @Override
            public FeSequence makeNewObject() {
                return new FeSequence();
            }
        };
    }


    private void initRepeats() {
        repeatCheckForDeadParticles = new RepeatYio<FireworksElement>(this, 60) {
            @Override
            public void performAction() {
                parent.checkForDeadParticles();
            }
        };

        repeatExplode = new RepeatYio<FireworksElement>(this, 30) {
            @Override
            public void performAction() {
                parent.makeExplosionInRandomPlace();
                repeatExplode.setCountDown(15 + YioGdxGame.random.nextInt(45));
            }
        };

        repeatUpdateAccelerometer = new RepeatYio<FireworksElement>(this, 6) {
            @Override
            public void performAction() {
                parent.updateAccelerometer();
            }
        };

        repeatMakeSequence = new RepeatYio<FireworksElement>(this, 2, 5 * 60) {
            @Override
            public void performAction() {
                parent.makeSequence();
            }
        };
    }


    @Override
    public void move() {
        appearFactor.move();

        moveParticles();
        repeatCheckForDeadParticles.move();

        repeatExplode.move();
        repeatMakeSequence.move();
        moveSequences();
    }


    private void moveSequences() {
        if (sequences.size() == 0) return;

        FeSequence feSequence = sequences.get(0);
        if (!feSequence.isReady()) return;

        sequences.remove(0);
        poolSequences.add(feSequence);

        makeExplosion(feSequence.position, 3);
    }


    private void makeSequence() {
        repeatMakeSequence.setCountDown((7 + YioGdxGame.random.nextInt(8)) * 60);
        int n = 5 + YioGdxGame.random.nextInt(6);

        putTempPointInRandomPlace();
        for (int i = 0; i < n; i++) {
            FeSequence next = poolSequences.getNext();

            next.time = System.currentTimeMillis() + 100 * i;
            next.position.setBy(tempPoint);

            sequences.add(next);
        }
    }


    private void makeExplosionInRandomPlace() {
        if (appearFactor.get() < 1) return;

        putTempPointInRandomPlace();

        makeExplosion(tempPoint);
    }


    private void putTempPointInRandomPlace() {
        tempPoint.x = YioGdxGame.random.nextFloat() * GraphicsYio.width;
        tempPoint.y = YioGdxGame.random.nextFloat() * 0.9f * GraphicsYio.height;
    }


    private void makeExplosion(PointYio pos) {
        int n = 10 + YioGdxGame.random.nextInt(10);

        makeExplosion(pos, n);
    }


    private void makeExplosion(PointYio pos, int n) {
        for (int i = 0; i < n; i++) {
            spawnParticle(pos);
        }
    }


    private void checkForDeadParticles() {
        for (int i = particles.size() - 1; i >= 0; i--) {
            FeParticle feParticle = particles.get(i);
            if (feParticle.isAlive()) continue;

            Yio.removeByIterator(particles, feParticle);
            poolParticles.add(feParticle);
        }
    }


    private void moveParticles() {
        repeatUpdateAccelerometer.move();

        gravityAngleDelta = - accelX / 5f;
        if (gravityAngleDelta > 0.5) {
            gravityAngleDelta = 0.5;
        }
        if (gravityAngleDelta < -0.5) {
            gravityAngleDelta = -0.5;
        }
        if (Math.abs(gravityAngleDelta) < 0.3) {
            gravityAngleDelta = 0;
        }
        gravityAngle = defGravityAngle + gravityAngleDelta;
        for (FeParticle particle : particles) {
            particle.move();
            particle.applyGravity(gravityAngle);
        }
    }


    private void updateAccelerometer() {
        accelX = Gdx.input.getAccelerometerX();
    }


    private void spawnParticle(PointYio spawnPos) {
        FeParticle next = poolParticles.getNext();

        next.position.setBy(spawnPos);
        next.speed.relocateRadial(0.02f * GraphicsYio.width, Yio.getRandomAngle());
        next.speed.y += 0.01f * GraphicsYio.width;
        next.viewType = YioGdxGame.random.nextInt(9);
        next.appear();

        Yio.addByIterator(particles, next);
    }


    @Override
    public FactorYio getFactor() {
        return appearFactor;
    }


    @Override
    public void destroy() {
        appearFactor.destroy(1, 1);

        for (FeParticle particle : particles) {
            particle.kill();
        }

        for (FeSequence sequence : sequences) {
            poolSequences.add(sequence);
        }
        sequences.clear();
    }


    @Override
    public void appear() {
        appearFactor.setValues(0.01, 0);
        appearFactor.appear(3, 1);

        clearParticles();
    }


    private void clearParticles() {
        for (FeParticle particle : particles) {
            poolParticles.add(particle);
        }

        particles.clear();
    }


    @Override
    public boolean isVisible() {
        return appearFactor.get() > 0;
    }


    @Override
    public boolean checkToPerformAction() {
        return false;
    }


    @Override
    public boolean isTouchable() {
        return true;
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touchPoint.set(screenX, screenY);
        if (touchPoint.y > 0.89f * GraphicsYio.height) return false;

        makeExplosion(touchPoint);

        return true;
    }


    @Override
    public boolean touchDrag(int screenX, int screenY, int pointer) {
        return false;
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }


    @Override
    public void setTouchable(boolean touchable) {

    }


    @Override
    public void setPosition(RectangleYio position) {

    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderFireworksElement;
    }
}
