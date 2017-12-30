package yio.tro.antiyoy.stuff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Masking {

    // IMPORTANT
    // ShapeRenderer should exist in only one instance
    // Otherwise it becomes buggy on android

    public static final void begin() {
        Gdx.gl.glClearDepthf(1f);
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glDepthFunc(GL20.GL_LESS);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthMask(true);
        Gdx.gl.glColorMask(false, false, false, false);
    }


    public static final void continueAfterBatchBegin() {
        Gdx.gl.glColorMask(true, true, true, true);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthFunc(GL20.GL_EQUAL);
    }


    public static final void end(SpriteBatch batch) {
        // this is actually necessary to avoid bugs with masking
        // batch should be activated at this point
        batch.end();
        batch.begin();

        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    }
}
