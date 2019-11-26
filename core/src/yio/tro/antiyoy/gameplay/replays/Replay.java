package yio.tro.antiyoy.gameplay.replays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.SettingsManager;
import yio.tro.antiyoy.gameplay.*;
import yio.tro.antiyoy.gameplay.data_storage.GameSaver;
import yio.tro.antiyoy.gameplay.replays.actions.RepAction;
import yio.tro.antiyoy.gameplay.replays.actions.RepActionFactory;
import yio.tro.antiyoy.gameplay.rules.GameRules;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Replay {

    private final GameController gameController;
    public ArrayList<RepAction> actions, buffer;
    public String initialLevelString;
    public boolean tempSlayRules;
    public int tempColorOffset;
    public int realNumberOfHumans;
    RepActionFactory factory;
    int currentStepIndex;
    String stringActions;
    boolean go, tempGo;


    public Replay(GameController gameController) {
        this.gameController = gameController;
        actions = new ArrayList<>();
        buffer = new ArrayList<>();
        factory = new RepActionFactory();
        tempSlayRules = false;
        tempColorOffset = 0;
        currentStepIndex = 0;
        realNumberOfHumans = 0;
        stringActions = null;
        go = true;
        tempGo = true;
    }


    public void recreateInitialSituation() {
        GameSaver gameSaver = gameController.gameSaver;
        int indexOf = initialLevelString.indexOf("/");
        gameSaver.setActiveHexesString(initialLevelString.substring(indexOf + 1));
        gameController.fieldManager.clearField();
        gameController.fieldManager.cleanOutAllHexesInField();
        gameSaver.createHexStrings();
        gameSaver.recreateMap();
    }


    public void updateInitialLevelString() {
        if (!SettingsManager.replaysEnabled) return;

        initialLevelString = gameController.gameSaver.legacyExportManager.getFullLevelString();
    }


    void addAction(RepAction repAction) {
        repAction.initType();

        buffer.add(repAction);

        if (repAction.type == RepAction.TURN_ENDED) {
            applyBuffer();
        }
    }


    public void prepare() {
        currentStepIndex = 0;
        go = true;
        onTacticalPause();

        if (GameRules.replayMode) {
            gameController.speedManager.setSpeed(SpeedManager.SPEED_PAUSED);
        } else {
            realNumberOfHumans = gameController.playersNumber;
        }
    }


    public void performStep() {
        while (go) {
            if (actions.size() == 0) return;
            if (currentStepIndex < 0 || currentStepIndex >= actions.size()) return;

            RepAction repAction = actions.get(currentStepIndex);
            repAction.perform(gameController);

            increaseStepIndex();

            if (repAction.type == RepAction.TURN_ENDED) {
                break;
            }
        }
    }


    public void onTacticalPause() {
        if (!go) return;

        tempGo = go;
        go = false;
    }


    public void onResumeNormalSpeed() {
        go = tempGo;
    }


    private void increaseStepIndex() {
        currentStepIndex++;
        if (currentStepIndex >= actions.size()) {
            go = false;
        }
    }


    private void applyBuffer() {
        for (RepAction repAction : buffer) {
            actions.add(repAction);
        }

        buffer.clear();
    }





    public void saveToPreferences(String prefsKey) {
        Preferences preferences = Gdx.app.getPreferences(prefsKey);
        preferences.putString("initial", initialLevelString);

        preferences.putBoolean("slay_rules", tempSlayRules);
        preferences.putInteger("color_offset", tempColorOffset);
        preferences.putInteger("real_human_number", realNumberOfHumans);

        preferences.putString("actions", convertActionsToString());

        preferences.flush();
    }


    private String convertActionsToString() {
        StringBuilder builder = new StringBuilder();
        for (RepAction action : actions) {
            builder.append(action.type).append("-");
            builder.append(action.saveInfo()).append("#");
        }
        return builder.toString();
    }


    public void loadFromPreferences(String prefsKey) {
        Preferences preferences = Gdx.app.getPreferences(prefsKey);

        initialLevelString = preferences.getString("initial");
        tempSlayRules = preferences.getBoolean("slay_rules");
        tempColorOffset = preferences.getInteger("color_offset", 0);
        realNumberOfHumans = preferences.getInteger("real_human_number", 0);

        stringActions = preferences.getString("actions");
    }


    public void updateActionsFromString(FieldManager fieldManager) {
        if (stringActions == null) return;

        StringTokenizer tokenizer = new StringTokenizer(stringActions, "#");
        actions.clear();
        buffer.clear();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            int indexOfMinus = token.indexOf("-");
            int actionType = Integer.valueOf(token.substring(0, indexOfMinus));
            RepAction action = factory.createAction(actionType);
            action.loadInfo(fieldManager, token.substring(indexOfMinus + 1));
            addAction(action);
        }

        applyBuffer();
    }


    public void recreateBufferFromSnapshot(ArrayList<RepAction> src) {
        buffer.clear();

        for (RepAction repAction : src) {
            buffer.add(repAction);
        }
    }


    public void setTempSlayRules(boolean tempSlayRules) {
        this.tempSlayRules = tempSlayRules;
    }


    public void setTempColorOffset(int tempColorOffset) {
        this.tempColorOffset = tempColorOffset;
    }


    public void setRealNumberOfHumans(int realNumberOfHumans) {
        this.realNumberOfHumans = realNumberOfHumans;
    }


    public void showInConsole() {
        System.out.println();

        if (actions.size() < 50) {
            System.out.println("Replay actions:");
            for (RepAction action : actions) {
                System.out.println("- " + action);
            }
        } else {
            System.out.println("Replay actions size = " + actions.size());
        }

        System.out.println("Replay buffer:");
        for (RepAction repAction : buffer) {
            System.out.println("- " + repAction);
        }
    }
}
