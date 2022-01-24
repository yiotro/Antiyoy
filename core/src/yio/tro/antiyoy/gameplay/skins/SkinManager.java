package yio.tro.antiyoy.gameplay.skins;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.SettingsManager;
import yio.tro.antiyoy.YioGdxGame;
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
            case katuri:
                return GraphicsYio.loadTextureRegion("skins/katuri/hex_" + name + ".png", false);
            case aww2:
                return GraphicsYio.loadTextureRegion("skins/aww2/hex_" + name + ".png", false);
        }
    }


    public String getExclamationMarkPath() {
        switch (skinType) {
            default:
                return "exclamation_mark.png";
            case shroomarts:
                return "skins/ant/exclamation_mark.png";
            case jannes_peters:
                return "skins/jannes/exclamation_mark.png";
            case matveismi_art:
                return "skins/matvej/exclamation_mark.png";
            case aww2:
                return "skins/aww2/exclamation_mark.png";
        }
    }


    public String getCoinTexturePath() {
        switch (skinType) {
            default:
                return "coin.png";
            case shroomarts:
                return "skins/ant/coin.png";
            case slimes:
                return "skins/bubbles/coin.png";
            case world_war:
                return "skins/aww/coin.png";
            case matveismi_art:
                return "skins/matvej/coin.png";
            case aww2:
                return "skins/aww2/coin.png";
        }
    }


    public String getFieldElementsFolderPath() {
        switch (skinType) {
            default:
                return "field_elements";
            case shroomarts:
                return "skins/ant/field_elements";
            case slimes:
                return "skins/bubbles/field_elements";
            case jannes_peters:
                return "skins/jannes/field_elements";
            case world_war:
                return "skins/aww/field_elements";
            case katuri:
                return "skins/katuri/field_elements";
            case matveismi_art:
                return "skins/matvej/field_elements";
            case aww2:
                return "skins/aww2/field_elements";
        }
    }


    public String getWaterTexturePath() {
        switch (skinType) {
            default:
                return "game_background_water.png";
            case jannes_peters:
                return "skins/jannes/game_background_water.png";
            case world_war:
                return "skins/aww/game_background_water.png";
            case katuri:
                return "skins/katuri/game_background_water.png";
            case aww2:
                return "skins/aww2/game_background_water.png";
        }
    }


    public String getMoveZoneTexturePath() {
        switch (skinType) {
            default:
                return "move_zone_pixel.png";
            case jannes_peters:
                return "skins/jannes/move_zone_pixel.png";
            case katuri:
                return "skins/katuri/move_zone_pixel.png";
        }
    }


    public String getSelectionBorderTexturePath() {
        switch (skinType) {
            default:
                return "selection_border.png";
            case jannes_peters:
                return "skins/jannes/selection_border.png";
            case katuri:
                return "skins/katuri/selection_border.png";
        }
    }


    public String getColorPixelsFolderPath() {
        switch (skinType) {
            default:
                return "pixels/colors";
            case jannes_peters:
                return "skins/jannes/pixels";
            case katuri:
                return "skins/katuri/pixels";
        }
    }


    public String getDiplomacyFolderPath() {
        switch (skinType) {
            default:
                return "diplomacy/";
            case jannes_peters:
                return "skins/jannes/diplomacy/";
            case katuri:
                return "skins/katuri/diplomacy/";
        }
    }


    public String getDefenseIconPath() {
        switch (skinType) {
            default:
                return "defense_icon.png";
            case aww2:
                return "skins/aww2/defense_icon.png";
        }
    }


    public String getShadowHexPath() {
        switch (skinType) {
            default:
                return "shadow_hex.png";
            case aww2:
                return "skins/aww2/shadow_hex.png";
        }
    }


    public String getGameBackgroundTexturePath() {
        switch (skinType) {
            default:
                return "game_background.png";
            case aww2:
                return "skins/aww2/game_background.png";
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
