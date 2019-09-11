package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.menu.fireworks_element.FeParticle;
import yio.tro.antiyoy.menu.fireworks_element.FireworksElement;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class RenderFireworksElement extends MenuRender {


    private FireworksElement fireworksElement;
    TextureRegion pTextures[];


    @Override
    public void loadTextures() {
        String names[] = new String[]{
                "man0",
                "man1",
                "man2",
                "man3",
                "grave",
                "house",
                "palm",
                "pine",
                "tower",
        };

        pTextures = new TextureRegion[names.length];
        for (int i = 0; i < names.length; i++) {
            pTextures[i] = GraphicsYio.loadTextureRegion("field_elements/" + names[i] + "_lowest.png", false);
        }
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        fireworksElement = (FireworksElement) element;

        for (FeParticle particle : fireworksElement.particles) {
            renderSingleParticle(particle);
        }
    }


    private void renderSingleParticle(FeParticle particle) {
        if (particle.viewRadius == 0) return;

        GraphicsYio.drawFromCenterRotated(
                batch,
                getParticleTexture(particle),
                particle.position.x,
                particle.position.y,
                particle.viewRadius,
                particle.viewAngle
        );
    }


    private TextureRegion getParticleTexture(FeParticle particle) {
//        return pTextures[particle.viewType];

        switch (particle.viewType) {
            default:
            case 0: return getGameView().texturesManager.manTextures[0].getLowest();
            case 1: return getGameView().texturesManager.manTextures[1].getLowest();
            case 2: return getGameView().texturesManager.manTextures[2].getLowest();
            case 3: return getGameView().texturesManager.manTextures[3].getLowest();
            case 4: return getGameView().texturesManager.graveTexture.getLowest();
            case 5: return getGameView().texturesManager.houseTexture.getLowest();
            case 6: return getGameView().texturesManager.palmTexture.getLowest();
            case 7: return getGameView().texturesManager.pineTexture.getLowest();
            case 8: return getGameView().texturesManager.towerTexture.getLowest();
        }
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
