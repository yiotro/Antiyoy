package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.gameplay.DebugFlags;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.Province;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.stuff.GraphicsYio;

import java.util.ArrayList;

public class SceneCheatsMenu extends AbstractScene{


    private Reaction openInEditorReaction;
    private ArrayList<Hex> list;
    public Reaction rbCaptureHexes;
    private Reaction rbBack;
    double y;
    private Reaction rbGiveMoney;
    private Reaction rbTakeControl;
    private ButtonYio backButton;
    private Reaction rbCharisma;
    private Reaction rbDebugExport;


    public SceneCheatsMenu(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        list = new ArrayList<>();

        initReactions();
    }


    private void initReactions() {
        rbBack = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.scenePauseMenu.create();
            }
        };

        openInEditorReaction = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                getGameController(buttonYio).getLevelEditor().launchEditLevelMode();
            }
        };

        rbCaptureHexes = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                doCaptureRandomHexes();
                Scenes.sceneNotification.show("Captured some nearby hexes (" + getPercentageOfCapturedLands() + "%)");
            }
        };

        rbGiveMoney = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                doGiveSomeMoneyToPlayer();
                Scenes.sceneNotification.show("All your provinces got $1000");
            }
        };

        rbTakeControl = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneTakeControl.create();
            }
        };

        rbCharisma = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                if (!DebugFlags.cheatCharisma) {
                    DebugFlags.cheatCharisma = true;
                    Scenes.sceneNotification.show("Charisma enabled");
                } else {
                    DebugFlags.cheatCharisma = false;
                    Scenes.sceneNotification.show("Charisma disabled");
                }
            }
        };

        rbDebugExport = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                getGameController(buttonYio).debugActionsManager.doSaveFullLevelToClipboard();
            }
        };
    }


    private int getPercentageOfCapturedLands() {
        ArrayList<Hex> activeHexes = getGameController().fieldManager.activeHexes;
        int c = 0;
        for (Hex activeHex : activeHexes) {
            if (!getGameController().isCurrentTurn(activeHex.fraction)) continue;

            c++;
        }

        return (int) (100f * ((float) c) / ((float) activeHexes.size()));
    }


    private void doGiveSomeMoneyToPlayer() {
        GameController gameController = getGameController();

        for (Province province : gameController.fieldManager.provinces) {
            if (province.getFraction() != gameController.turn) continue;

            province.money += 1000;
        }
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(3, true, true);

        backButton = menuControllerYio.spawnBackButton(1831287361, rbBack);

        y = 0.6;

        addInnerButton(71263781, "Open in editor", openInEditorReaction);
        addInnerButton(8213718, "Capture hexes", rbCaptureHexes);
        addInnerButton(8213719, "Give $1000", rbGiveMoney);
        addInnerButton(8213720, "Take control", rbTakeControl);
        addInnerButton(8213721, "Charisma", rbCharisma);
        addInnerButton(8213722, "Debug export", rbDebugExport);

        menuControllerYio.endMenuCreation();
    }


    private void addInnerButton(int id, String title, Reaction reaction) {
        ButtonYio button = buttonFactory.getButton(generateRectangle(0.15, y, 0.7, 0.06), id, title);
        button.setReaction(reaction);
        button.setAnimation(Animation.from_center);
        button.setTouchOffset(0.01f * GraphicsYio.height);
        y -= 0.08;
    }


    private void doCaptureRandomHexes() {
        GameController gameController = getGameController();
        list.clear();

        for (Hex activeHex : gameController.fieldManager.activeHexes) {
            if (activeHex.sameFraction(0)) continue;
            if (!hasAtLeastOnePlayerHexNearby(activeHex)) continue;
            if (gameController.getRandom().nextDouble() < 0.25) continue;

            list.add(activeHex);
        }

        for (Hex hex : list) {
            gameController.fieldManager.setHexFraction(hex, 0);
            gameController.replayManager.onHexChangedFractionWithoutObviousReason(hex);
        }
        list.clear();
    }


    private boolean hasAtLeastOnePlayerHexNearby(Hex hex) {
        for (int dir = 0; dir < 6; dir++) {
            Hex adjacentHex = hex.getAdjacentHex(dir);
            if (adjacentHex == null) continue;
            if (!adjacentHex.active) continue;
            if (!adjacentHex.sameFraction(0)) continue;

            return true;
        }

        return false;
    }
}
