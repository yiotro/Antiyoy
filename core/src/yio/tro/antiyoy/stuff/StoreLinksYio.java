package yio.tro.antiyoy.stuff;

import yio.tro.antiyoy.YioGdxGame;

import java.util.HashMap;
import java.util.Set;

public class StoreLinksYio {

    private static StoreLinksYio instance;
    private HashMap<String, String> androidLinks;
    private HashMap<String, String> iosLinks;
    private HashMap<String, String> proLinks;


    public StoreLinksYio() {
        createAndroidLinks();
        createIosLinks();
        createProLinks();
    }


    private void createProLinks() {
        proLinks = new HashMap<>();
        proLinks.put("android_achikaps", "https://play.google.com/store/apps/details?id=yio.tro.achikaps_pro");
        proLinks.put("android_bleentoro", "https://play.google.com/store/apps/details?id=yio.tro.bleentoro_pro");
        proLinks.put("android_vodobanka", "https://play.google.com/store/apps/details?id=yio.tro.vodobanka_pro");
        proLinks.put("ios_achikaps", "https://apps.apple.com/us/app/achikaps-pro/id1515544461");
        proLinks.put("ios_bleentoro", "https://apps.apple.com/us/app/bleentoro-pro/id1516651884");
        proLinks.put("ios_vodobanka", "https://apps.apple.com/us/app/vodobanka-pro/id1516816529");
    }


    private void createIosLinks() {
        iosLinks = new HashMap<>();
        iosLinks.put("antiyoy", "https://apps.apple.com/app/antiyoy/id1415296141");
        iosLinks.put("achikaps", "https://apps.apple.com/app/achikaps/id1515537717");
        iosLinks.put("bleentoro", "https://apps.apple.com/app/bleentoro/id1516651107");
        iosLinks.put("opacha-mda", "https://apps.apple.com/app/opacha-mda/id1515105386");
        iosLinks.put("vodobanka", "https://apps.apple.com/app/vodobanka/id1516815669");
    }


    private void createAndroidLinks() {
        androidLinks = new HashMap<>();
        androidLinks.put("antiyoy", "https://play.google.com/store/apps/details?id=yio.tro.antiyoy.android");
        androidLinks.put("achikaps", "https://play.google.com/store/apps/details?id=yio.tro.achikaps");
        androidLinks.put("bleentoro", "https://play.google.com/store/apps/details?id=yio.tro.bleentoro");
        androidLinks.put("shmatoosto", "https://play.google.com/store/apps/details?id=yio.tro.shmatoosto");
        androidLinks.put("opacha-mda", "https://play.google.com/store/apps/details?id=yio.tro.opacha");
        androidLinks.put("vodobanka", "https://play.google.com/store/apps/details?id=yio.tro.vodobanka");
        androidLinks.put("cheepaska", "https://play.google.com/store/apps/details?id=yio.tro.cheepaska");
        androidLinks.put("onliyoy", "https://play.google.com/store/apps/details?id=yio.tro.onliyoy");
    }


    public static void initialize() {
        instance = null;
    }


    public static StoreLinksYio getInstance() {
        if (instance == null) {
            instance = new StoreLinksYio();
        }
        return instance;
    }


    public String getProLink(String key) {
        String modifiedKey;
        switch (YioGdxGame.platformType) {
            default:
            case pc:
            case android:
                modifiedKey = "android_" + key;
                break;
            case ios:
                modifiedKey = "ios_" + key;
                break;
        }
        if (!proLinks.containsKey(modifiedKey)) return "-";
        return proLinks.get(modifiedKey);
    }


    public String getLink(String key) {
        switch (YioGdxGame.platformType) {
            default:
                return "-";
            case pc:
                return "https://drive.google.com/drive/folders/1p36opkblTxY_Pc6SUZ_zTh8g4iJsm3jV?usp=sharing";
            case android:
                if (!androidLinks.containsKey(key)) return "-";
                return androidLinks.get(key);
            case ios:
                if (!iosLinks.containsKey(key)) return "-";
                return iosLinks.get(key);
        }
    }


    public Set<String> getKeys() {
        switch (YioGdxGame.platformType) {
            default:
            case pc:
            case android:
                return androidLinks.keySet();
            case ios:
                return iosLinks.keySet();
        }
    }

}
