package yio.tro.antiyoy.stuff;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;


public class FrameBufferYio extends FrameBuffer{

    public float f;

    public static FrameBufferYio getInstance(Pixmap.Format format, int width, int height, boolean hasDepth) {
        try {
            return new FrameBufferYio(format, width, height, hasDepth, 1);
        } catch (Exception e) {
            try {
                return new FrameBufferYio(Pixmap.Format.RGBA8888, width, height, hasDepth, 1);
            } catch (Exception e2) {
                try {
                    return new FrameBufferYio(Pixmap.Format.RGB565, width, height, true, 1);
                } catch (Exception e3) {
                    try {
                        return new FrameBufferYio(format, width / 2, height / 2, hasDepth, 0.5f);
                    } catch (Exception e4) {
                        System.out.println("Fake FrameBuffer created");
                        return new FakeFbYio();
                    }
                }
            }
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
