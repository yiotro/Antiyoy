package yio.tro.antiyoy.menu.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.gameplay.ColorsManager;
import yio.tro.antiyoy.menu.*;
import yio.tro.antiyoy.menu.color_picking.ColorHolderElement;
import yio.tro.antiyoy.menu.slider.SliderBehavior;
import yio.tro.antiyoy.menu.slider.SliderYio;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.stuff.LanguagesManager;
import yio.tro.antiyoy.stuff.RectangleYio;

public class SceneMoreCampaignOptions extends AbstractScene{


    private CheckButtonYio chkSlayRules;
    private ButtonYio label;
    ColorHolderElement colorHolderElement;


    public SceneMoreCampaignOptions(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        colorHolderElement = null;
        chkSlayRules = null;
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();
        menuControllerYio.getYioGdxGame().beginBackgroundChange(2, true, true);
        menuControllerYio.spawnBackButton(551, Reaction.rbExitToCampaign);

        createInternals();

        menuControllerYio.endMenuCreation();
    }


    public void createInternals() {
        createLabel();
        createCheckButtons();
        createColorHolder();

        loadValues();
    }


    public void prepare() {
        if (colorHolderElement != null) return;
        createInternals();
    }


    private void createColorHolder() {
        initColorHolder();
        colorHolderElement.appear();
    }


    private void initColorHolder() {
        if (colorHolderElement != null) return;
        colorHolderElement = new ColorHolderElement(menuControllerYio);
        colorHolderElement.setTitle(LanguagesManager.getInstance().getString("player_color") + ":");
        colorHolderElement.setAnimation(Animation.up);
        colorHolderElement.setPosition(generateRectangle(0.1, 0.73, 0.8, 0.08));
        menuControllerYio.addElementToScene(colorHolderElement);
    }


    private void loadValues() {
        Preferences prefs = Gdx.app.getPreferences("campaign_options");

        int valueIndex = prefs.getInteger("color_offset", 0);
        colorHolderElement.setValueIndex(valueIndex);

        chkSlayRules.setChecked(prefs.getBoolean("slay_rules", false));
    }


    public void saveValues() {
        Preferences prefs = Gdx.app.getPreferences("campaign_options");

        prefs.putInteger("color_offset", colorHolderElement.getValueIndex());
        prefs.putBoolean("slay_rules", chkSlayRules.isChecked());

        prefs.flush();
    }


    private void createLabel() {
        label = buttonFactory.getButton(generateRectangle(0.05, 0.6, 0.9, 0.25), 552, " ");
        label.setTouchable(false);
        label.setAnimation(Animation.up);
    }


    private void createCheckButtons() {
        initChecks();
        chkSlayRules.appear();
    }


    private void initChecks() {
        if (chkSlayRules != null) return;

        chkSlayRules = CheckButtonYio.getFreshCheckButton(menuControllerYio);
        chkSlayRules.setParent(label);
        chkSlayRules.alignTop(0.15);
        chkSlayRules.setTitle(getString("slay_rules"));
        chkSlayRules.centerHorizontal(0.05);
    }
}
