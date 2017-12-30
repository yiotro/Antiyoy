package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
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

        menuControllerYio.spawnBackButton(76, Reaction.rbMainMenu);

        menuControllerYio.endMenuCreation();
    }


    private void createInvisibleButton() {
        double size = 0.07;
        ButtonYio invisButton = buttonFactory.getButton(generateSquare(1 - size, 1 - GraphicsYio.convertToHeight(size), size), 78, null);
        invisButton.setVisible(false);
        invisButton.setReaction(Reaction.rbShowCheatSceen);
    }


    private void createEditorButton() {
        ButtonYio editorButton = buttonFactory.getButton(generateRectangle(0.1, 0.54, 0.8, 0.08), 77, getString("editor"));
        editorButton.setShadow(false);
        editorButton.setAnimation(Animation.FROM_CENTER);
        editorButton.setReaction(EditorReactions.rbEditorSlotMenu);
    }


    private void createLoadGameButton() {
        ButtonYio loadGameButton = buttonFactory.getButton(generateRectangle(0.1, 0.3, 0.8, 0.08), 75, getString("choose_game_mode_load"));
        loadGameButton.setShadow(false);
        loadGameButton.setAnimation(Animation.FROM_CENTER);
        loadGameButton.setReaction(Reaction.rbLoadGame);
        loadGameButton.disableTouchAnimation();
    }


    private void createCampaignButton() {
        ButtonYio campaignButton = buttonFactory.getButton(generateRectangle(0.1, 0.38, 0.8, 0.08), 74, getString("choose_game_mode_campaign"));
        campaignButton.setReaction(Reaction.rbCampaignMenu);
        campaignButton.setShadow(false);
        campaignButton.setAnimation(Animation.FROM_CENTER);
        campaignButton.disableTouchAnimation();
    }


    private void createTutorialButton() {
        ButtonYio tutorialButton = buttonFactory.getButton(generateRectangle(0.1, 0.46, 0.8, 0.08), 73, getString("choose_game_mode_tutorial"));
        tutorialButton.setShadow(false);
        tutorialButton.setReaction(Reaction.rbTutorialIndex);
        tutorialButton.setAnimation(Animation.FROM_CENTER);
    }


    private void createSkirmishButton() {
        skirmishButton = buttonFactory.getButton(generateRectangle(0.1, 0.62, 0.8, 0.08), 72, getString("choose_game_mode_skirmish"));
        skirmishButton.setReaction(Reaction.rbSkirmishMenu);
        skirmishButton.setShadow(false);
        skirmishButton.setAnimation(Animation.FROM_CENTER);
    }


    private void createBasePanel() {
        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(0.1, 0.3, 0.8, 0.4), 70, null);
        basePanel.setTouchable(false);
        basePanel.onlyShadow = true;
        basePanel.setAnimation(Animation.FROM_CENTER);
    }
}