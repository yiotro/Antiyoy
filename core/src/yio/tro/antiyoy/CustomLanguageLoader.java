package yio.tro.antiyoy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.gameplay.name_generator.CityNameGenerator;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.LanguagesManager;

public class CustomLanguageLoader {

    public static final String prefs = "antiyoy.language";
    private static boolean autoDetect;
    private static String langName;


    public static void loadLanguage() {
        Preferences preferences = Gdx.app.getPreferences(prefs);

        autoDetect = preferences.getBoolean("auto", true);

        if (!autoDetect) {
            langName = preferences.getString("lang_name");
            LanguagesManager.getInstance().setLanguage(langName);
        }
    }


    public static void setAndSaveLanguage(String langName) {
        Preferences preferences = Gdx.app.getPreferences(prefs);

        preferences.putBoolean("auto", false);
        preferences.putString("lang_name", langName);

        preferences.flush();

        Fonts.initFonts(); // calls loadLanguage()
        CityNameGenerator.getInstance().load();
    }
}
