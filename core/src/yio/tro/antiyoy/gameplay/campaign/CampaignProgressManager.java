package yio.tro.antiyoy.gameplay.campaign;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.gameplay.DebugFlags;
import yio.tro.antiyoy.gameplay.rules.GameRules;

import java.util.Arrays;
import java.util.StringTokenizer;

public class CampaignProgressManager {

    public static final String PROGRESS_PREFS_GENERIC = "antiyoy.progress";
    public static final String PROGRESS_PREFS_SLAY = "antiyoy.progress.slay"; // maybe will be used later
    public static final int INDEX_OF_LAST_LEVEL = 155;

    private static CampaignProgressManager instance;
    public int currentLevelIndex;
    boolean progress[];


    public static void initialize() {
        instance = null;
    }


    public static CampaignProgressManager getInstance() {
        if (instance == null) {
            instance = new CampaignProgressManager();
            instance.loadProgress();
        }

        return instance;
    }


    private CampaignProgressManager() {
        progress = new boolean[getIndexOfLastLevel() + 1];
        //
    }


    public boolean areCampaignLevelCompletionConditionsSatisfied(int winFraction) {
        return GameRules.campaignMode && winFraction == 0;
    }


    public static int getIndexOfLastLevel() {
        return INDEX_OF_LAST_LEVEL;
    }


    public void markLevelAsCompleted(int index) {
        progress[index] = true;
        saveProgress();
    }


    public boolean isLevelLocked(int index) {
        if (DebugFlags.unlockLevels) return false;
        if (index == CampaignLevelFactory.NORMAL_LEVELS_START) return false;
        if (index == CampaignLevelFactory.HARD_LEVELS_START) return false;
        if (index == CampaignLevelFactory.EXPERT_LEVELS_START) return false;

        return !isLevelComplete(index) && !isLevelComplete(index - 1);
    }


    public boolean isLevelComplete(int index) {
        if (index < 0) return true;
        return getProgress()[index];
    }


    private void saveProgress() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < progress.length; i++) {
            if (!progress[i]) continue;
            builder.append(i).append(" ");
        }

        Preferences preferences = getPreferences();
        preferences.putString("completed_levels", builder.toString());
        preferences.flush();
    }


    private Preferences getPreferences() {
        return Gdx.app.getPreferences(PROGRESS_PREFS_GENERIC);
    }


    private void clearProgress() {
        for (int i = 0; i < progress.length; i++) {
            progress[i] = false;
        }
    }


    public void resetProgress() {
        clearProgress();
        saveProgress();
    }


    public int getNextLevelIndex() {
        int nextLevelIndex = currentLevelIndex + 1;

        if (nextLevelIndex > getIndexOfLastLevel()) {
            nextLevelIndex = getIndexOfLastLevel();
        }

        return nextLevelIndex;
    }


    private void loadProgress() {
        clearProgress();

        Preferences preferences = getPreferences();
        String completedLevels = preferences.getString("completed_levels", "");

        StringTokenizer tokenizer = new StringTokenizer(completedLevels, " ");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            int index = Integer.valueOf(token);
            if (index >= progress.length) break;
            progress[index] = true;
        }

        checkToImportOldProgress(preferences);
    }


    private void checkToImportOldProgress(Preferences preferences) {
        boolean imported_old_progress;
        imported_old_progress = preferences.getBoolean("imported_old_progress", false);
        if (imported_old_progress) return;

        importOldProgress();
        preferences.putBoolean("imported_old_progress", true);
    }


    private void importOldProgress() {
        Preferences preferences = Gdx.app.getPreferences("main");
        int progress = preferences.getInteger("progress", 0); // 0 - default value;

        for (int i = 0; i < progress; i++) {
            markLevelAsCompleted(i);
        }
    }


    public boolean[] getProgress() {
        return progress;
    }


    public int getIndexOfRelevantLevel() {
        int index = 0;

        for (int i = 0; i < progress.length; i++) {
            if (progress[i]) {
                index = i;
            }
        }

        index++;
        if (index >= progress.length) {
            index--;
        }

        return index;
    }


    public boolean isAtLeastOneLevelCompleted() {
        for (int i = 0; i < progress.length; i++) {
            if (isLevelComplete(i)) {
                return true;
            }
        }

        return false;
    }


    public int getNumberOfCompletedLevels() {
        int c = 0;
        for (int i = 0; i < progress.length; i++) {
            if (!progress[i]) continue;
            c++;
        }
        return c;
    }


    public void setCurrentLevelIndex(int currentLevelIndex) {
        this.currentLevelIndex = currentLevelIndex;
    }


    public int getCurrentLevelIndex() {
        return currentLevelIndex;
    }


    public boolean isLastLevel() {
        return currentLevelIndex == getIndexOfLastLevel();
    }


    @Override
    public String toString() {
        return "[Progress = " + Arrays.toString(progress) + "]";
    }
}
