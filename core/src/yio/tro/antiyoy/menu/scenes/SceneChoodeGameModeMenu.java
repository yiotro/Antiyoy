package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.editor.EditorReactions;

public class SceneChoodeGameModeMenu extends AbstractScene{


    public ButtonYio skirmishButton;


    public SceneChoodeGameModeMenu(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, true, true);

        createBasePanel();

        createSkirmishButton();
        createTutorialButton();
        createCampaignButton();
        createLoadGameButton();
        createEditorButton();

        createInvisibleButton();

        menuControllerYio.spawnBackButton(76, ReactBehavior.rbMainMenu);

        menuControllerYio.endMenuCreation();
    }


    private void createInvisibleButton() {
        double size = 0.07;
        ButtonYio invisButton = buttonFactory.getButton(generateSquare(1 - size, 1 - GraphicsYio.convertToHeight(size), size), 78, null);
        invisButton.setVisible(false);
        invisButton.setReactBehavior(ReactBehavior.rbShowCheatSceen);
    }


    private void createEditorButton() {
        ButtonYio editorButton = buttonFactory.getButton(generateRectangle(0.1, 0.54, 0.8, 0.08), 77, getString("editor"));
        editorButton.setShadow(false);
        editorButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);
        editorButton.setReactBehavior(EditorReactions.rbEditorSlotMenu);
    }


    private void createLoadGameButton() {
        ButtonYio loadGameButton = buttonFactory.getButton(generateRectangle(0.1, 0.3, 0.8, 0.08), 75, getString("choose_game_mode_load"));
        loadGameButton.setShadow(false);
        loadGameButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);
        loadGameButton.setReactBehavior(ReactBehavior.rbLoadGame);
        loadGameButton.disableTouchAnimation();
    }


    private void createCampaignButton() {
        ButtonYio campaignButton = buttonFactory.getButton(generateRectangle(0.1, 0.38, 0.8, 0.08), 74, getString("choose_game_mode_campaign"));
        campaignButton.setReactBehavior(ReactBehavior.rbCampaignMenu);
        campaignButton.setShadow(false);
        campaignButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);
        campaignButton.disableTouchAnimation();
    }


    private void createTutorialButton() {
        ButtonYio tutorialButton = buttonFactory.getButton(generateRectangle(0.1, 0.46, 0.8, 0.08), 73, getString("choose_game_mode_tutorial"));
        tutorialButton.setShadow(false);
        tutorialButton.setReactBehavior(ReactBehavior.rbTutorialIndex);
        tutorialButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);
    }


    private void createSkirmishButton() {
        skirmishButton = buttonFactory.getButton(generateRectangle(0.1, 0.62, 0.8, 0.08), 72, getString("choose_game_mode_skirmish"));
        skirmishButton.setReactBehavior(ReactBehavior.rbSkirmishMenu);
        skirmishButton.setShadow(false);
        skirmishButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);
    }


    private void createBasePanel() {
        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(0.1, 0.3, 0.8, 0.4), 70, null);
        basePanel.setTouchable(false);
        basePanel.onlyShadow = true;
        basePanel.setAnimType(ButtonYio.ANIM_FROM_CENTER);
    }
}