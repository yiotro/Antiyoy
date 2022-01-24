package yio.tro.antiyoy.gameplay.name_generator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.gameplay.FieldManager;

import java.util.ArrayList;

public class CustomCityNamesManager {

    private static CustomCityNamesManager instance = null;
    public static final String PREFS = "yio.tro.antiyoy.custom_city_names";
    ArrayList<String> names;


    public CustomCityNamesManager() {
        names = new ArrayList<>();
        loadValues();
    }


    public static void initialize() {
        instance = null;
    }


    public static CustomCityNamesManager getInstance() {
        if (instance == null) {
            instance = new CustomCityNamesManager();
        }

        return instance;
    }


    public boolean isNameUsed(String string) {
        for (String name : names) {
            if (name.equals(string)) return true;
        }
        return false;
    }


    public void addName(String string) {
        if (isNameUsed(string)) return;
        names.add(string);
        saveValues();
    }


    public void removeName(String string) {
        if (!isNameUsed(string)) return;
        names.remove(string);
        saveValues();
    }


    public String getUnusedName(FieldManager fieldManager) {
        if (!isThereAtLeastOneUnusedName(fieldManager)) return null;
        int c = 100;
        while (c > 0) {
            c--;
            int index = YioGdxGame.random.nextInt(names.size());
            String randomName = names.get(index);
            if (fieldManager.isCityNameUsed(randomName)) continue;
            return randomName;
        }
        for (String name : names) {
            if (fieldManager.isCityNameUsed(name)) continue;
            return name;
        }
        return null;
    }


    public boolean isThereAtLeastOneUnusedName(FieldManager fieldManager) {
        for (String name : names) {
            if (fieldManager.isCityNameUsed(name)) continue;
            return true;
        }
        return false;
    }


    public void changeName(String oldName, String newName) {
        if (!isNameUsed(oldName)) return;
        if (isNameUsed(newName)) return;
        removeName(oldName);
        addName(newName);
    }


    public ArrayList<String> getNames() {
        return names;
    }


    void loadValues() {
        Preferences preferences = getPreferences();
        String source = preferences.getString("value");
        for (String token : source.split("@")) {
            if (token.length() == 0) continue;
            names.add(token);
        }
    }


    void saveValues() {
        Preferences preferences = getPreferences();

        StringBuilder builder = new StringBuilder();
        for (String name : names) {
            builder.append(name).append("@");
        }

        if (builder.length() == 0) {
            builder.append("@");
        }

        preferences.putString("value", builder.toString());
        preferences.flush();
    }


    private Preferences getPreferences() {
        return Gdx.app.getPreferences(PREFS);
    }

}
