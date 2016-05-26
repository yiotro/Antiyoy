package yio.tro.antiyoy;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import java.util.Random;

/**
 * Created by ivan on 25.04.2016.
 */
public class FrameBufferYio extends FrameBuffer{

    public float f;

    public static FrameBufferYio getInstance(Pixmap.Format format, int width, int height, boolean hasDepth) {
        try {
            return new FrameBufferYio(format, width, height, hasDepth, 1);
        } catch (Exception e) {
            return new FrameBufferYio(format, width / 2, height / 2, hasDepth, 0.5f);
        }
    }


    public FrameBufferYio(Pixmap.Format format, int width, int height, boolean hasDepth, float f) {
        super(format, width, height, hasDepth);
        this.f = f;
    }


    public FrameBufferYio(Pixmap.Format format, int width, int height, boolean hasDepth, boolean hasStencil) {
        super(format, width, height, hasDepth, hasStencil);
    }


    @Override
    protected void disposeColorTexture(Texture colorTexture) {
        //
    }
}
