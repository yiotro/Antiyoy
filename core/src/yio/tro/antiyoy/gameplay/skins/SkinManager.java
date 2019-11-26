package yio.tro.antiyoy.gameplay.skins;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.SettingsManager;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.gameplay.game_view.GameRender;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.AtlasLoader;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class SkinManager {

    YioGdxGame yioGdxGame;
    public SkinType skinType;


    public SkinManager(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;

        skinType = SkinType.original;

        if (SettingsManager.skinIndex != 0) {
            onSkinChanged();
        }
    }


    public void onSkinChanged() {
        skinType = SkinType.values()[SettingsManager.skinIndex];

        if (Scenes.sceneSelectionOverlay == null) return;
        Scenes.sceneSelectionOverlay.onSkinChanged();
        Scenes.sceneAiOnlyOverlay.onSkinChanged();
        MenuRender.renderFastConstructionPanel.onSkinChanged();
        MenuRender.renderIncomeGraphElement.onSkinChanged();
        Scenes.sceneFinances.onSkinChanged();

        if (yioGdxGame != null && yioGdxGame.menuControllerYio != null) {
            yioGdxGame.menuControllerYio.onSkinChanged();
            MenuRender.renderDiplomacyElement.loadBackgroundColors();
        }
    }


    public TextureRegion loadHexTexture(String name) {
        switch (skinType) {
            default:
            case original:
                return GraphicsYio.loadTextureRegion("hex_" + name + ".png", false);
            case points:
                return GraphicsYio.loadTextureRegion("skins/points/points_hex_" + name + ".png", false);
            case grid:
                return GraphicsYio.loadTextureRegion("skins/grid/hex_" + name + "_grid.png", false);
            case jannes_peters:
                return GraphicsYio.loadTextureRegion("skins/jannes/hex_" + name + ".png", false);
        }
    }


    public TextureRegion loadExclamationMark() {
        switch (skinType) {
            default:
                return GraphicsYio.loadTextureRegion("exclamation_mark.png", true);
            case shroomarts:
                return GraphicsYio.loadTextureRegion("skins/ant/exclamation_mark.png", true);
            case jannes_peters:
                return GraphicsYio.loadTextureRegion("skins/jannes/exclamation_mark.png", true);
        }
    }


    public String getCoinTexturePath() {
        switch (skinType) {
            default:
                return "coin.png";
            case shroomarts:
                return "skins/ant/coin.png";
            case bubbles:
                return "skins/bubbles/coin.png";
        }
    }


    public String getFieldElementsFolderPath() {
        switch (skinType) {
            default:
                return "field_elements";
            case shroomarts:
                return "skins/ant/field_elements";
            case bubbles:
                return "skins/bubbles/field_elements";
            case jannes_peters:
                return "skins/jannes/field_elements";
        }
    }


    public String getWaterTexturePath() {
        switch (skinType) {
            default:
                return "game_background_water.png";
            case jannes_peters:
                return "skins/jannes/game_background_water.png";
        }
    }


    public String getMoveZoneTexturePath() {
        switch (skinType) {
            default:
                return "move_zone_pixel.png";
            case jannes_peters:
                return "skins/jannes/move_zone_pixel.png";
        }
    }


    public String getSelectionBorderTexturePath() {
        switch (skinType) {
            default:
                return "selection_border.png";
            case jannes_peters:
                return "skins/jannes/selection_border.png";
        }
    }


    public String getColorPixelsFolderPath() {
        switch (skinType) {
            default:
                return "pixels/colors";
            case jannes_peters:
                return "skins/jannes/pixels";
        }
    }


    public AtlasLoader createAtlasLoader() {
        String path = getFieldElementsFolderPath() + "/";
        return new AtlasLoader(path + "atlas_texture.png", path + "atlas_structure.txt", true);
    }


    public String getFarmTexturePath() {
        return getFieldElementsFolderPath() + "/house.png";
    }


    public String getTowerTexturePath() {
        return getFieldElementsFolderPath() + "/tower.png";
    }


    public String getPeasantTexturePath() {
        return getFieldElementsFolderPath() + "/man0.png";
    }


    public boolean currentSkinRequiresWater() {
        return skinType == SkinType.jannes_peters;
    }
}
