package yio.tro.antiyoy.stuff;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class FakeFbYio extends FrameBufferYio{


    private TextureRegion blackPixel;


    public FakeFbYio() {
        super(Pixmap.Format.RGB565, 1, 1, false, 1);
        blackPixel = GraphicsYio.loadTextureRegion("pixels/black.png", false);
    }


    @Override
    protected Texture createTexture(FrameBufferTextureAttachmentSpec attachmentSpec) {
        return blackPixel.getTexture();
    }


    @Override
    protected void disposeColorTexture(Texture colorTexture) {

    }


    @Override
    protected void attachFrameBufferColorTexture(Texture texture) {

    }


    @Override
    public Texture getColorBufferTexture() {
        return blackPixel.getTexture();
    }


    @Override
    protected void build() {

    }


    @Override
    public void bind() {

    }


    @Override
    public void begin() {

    }


    @Override
    public void dispose() {

    }


}
