package yio.tro.antiyoy.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.gameplay.campaign.CampaignProgressManager;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.RepeatYio;

import java.util.HashMap;
import java.util.Map;

public class SkipLevelManager {


    public static final String PREFS = "yio.tro.antiyoy.skip_campaign";
    public static final int FRAMES_IN_ONE_MINUTE = 60 * 60;
    public static final int MINUTES_TO_WAIT_FOR_SKIP = 60;
    public static final int MINUTES_TO_STOP_REMIND = 65;
    GameController gameController;
    HashMap<Integer, Integer> timeTable;
    RepeatYio<SkipLevelManager> repeatIncreaseTimer;
    boolean readyToShowNotificationAboutSkip;


    public SkipLevelManager(GameController gameController) {
        this.gameController = gameController;

        timeTable = new HashMap<>();
        load();

        initRepeats();
    }


    private void initRepeats() {
        repeatIncreaseTimer = new RepeatYio<SkipLevelManager>(this, FRAMES_IN_ONE_MINUTE) {
            @Override
            public void performAction() {
                parent.checkToIncreaseTimer();
            }
        };
    }


    void checkToIncreaseTimer() {
        if (!GameRules.campaignMode) return;

        int currentLevelIndex = getCurrentLevelIndex();
        if (currentLevelIndex < 1) return;

        if (CampaignProgressManager.getInstance().isLevelComplete(currentLevelIndex)) return;

        int value = timeTable.get(currentLevelIndex);
        timeTable.put(currentLevelIndex, value + 1);
        if (isTimeToRemindAboutSkipping()) {
            readyToShowNotificationAboutSkip = true;
        }

        save();
    }


    private boolean isTimeToRemindAboutSkipping() {
        return canSkipCurrentLevel() && timeTable.get(getCurrentLevelIndex()) <= MINUTES_TO_STOP_REMIND;
    }


    public boolean canSkipCurrentLevel() {
        return canSkipLevel(CampaignProgressManager.getInstance().currentLevelIndex);
    }


    public boolean canSkipLevel(int levelIndex) {
        if (levelIndex < 1) return false;
        if (levelIndex > CampaignProgressManager.INDEX_OF_LAST_LEVEL - 1) return false; // last level can't be skipped
        if (CampaignProgressManager.getInstance().isLevelComplete(levelIndex)) return false;
        if (timeTable.get(levelIndex) < MINUTES_TO_WAIT_FOR_SKIP) return false;

        return true;
    }


    private void load() {
        Preferences preferences = getPreferences();

        for (int i = 0; i < CampaignProgressManager.INDEX_OF_LAST_LEVEL + 1; i++) {
            int value = preferences.getInteger("" + i, 0);
            timeTable.put(i, value);
        }
    }


    public void forceSkipAvailability() {
        timeTable.put(getCurrentLevelIndex(), 2 * MINUTES_TO_WAIT_FOR_SKIP);
    }


    void save() {
        Preferences preferences = getPreferences();

        for (Map.Entry<Integer, Integer> entry : timeTable.entrySet()) {
            preferences.putInteger(entry.getKey() + "", entry.getValue());
        }

        preferences.flush();
    }


    void onEndCreation() {
        if (!GameRules.campaignMode) return;

        if (!isTimeToRemindAboutSkipping()) return;

        readyToShowNotificationAboutSkip = true;
    }


    private int getCurrentLevelIndex() {
        return CampaignProgressManager.getInstance().getCurrentLevelIndex();
    }


    void defaultValues() {
        readyToShowNotificationAboutSkip = false;
    }


    void move() {
        repeatIncreaseTimer.move();
        checkToShowNotification();
    }


    private void checkToShowNotification() {
        if (!readyToShowNotificationAboutSkip) return;

        readyToShowNotificationAboutSkip = false;
        Scenes.sceneNotification.show("can_skip_level");
    }


    private Preferences getPreferences() {
        return Gdx.app.getPreferences(PREFS);
    }
}
