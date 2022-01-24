package yio.tro.antiyoy.gameplay.data_storage;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.Province;
import yio.tro.antiyoy.gameplay.Unit;
import yio.tro.antiyoy.gameplay.rules.GameRules;

public class DecodeManager {

    GameController gameController;
    private String source;


    public DecodeManager(GameController gameController) {
        this.gameController = gameController;
    }


    protected boolean isValidLevelCode(String levelCode) {
        if (levelCode == null) return false;
        if (!levelCode.contains("antiyoy_level_code")) return false;
        if (!levelCode.contains("level_size")) return false;
        if (!levelCode.contains("units")) return false;
        if (!levelCode.contains("land")) return false;
        return true;
    }


    public void perform(String levelCode) {
        this.source = levelCode;

        applyGeneralInfo();
        applyLand();
        applyUnits();
        applyEditorInfo();
        applyMapName();
        applyGoal();

        end();
    }


    public void applyRealMoney() {
        String realMoneySection = getSection("real_money");
        if (realMoneySection == null) return;

        for (String token : realMoneySection.split(",")) {
            String[] split = token.split(" ");
            int index1 = Integer.valueOf(split[0]);
            int index2 = Integer.valueOf(split[1]);
            int money = Integer.valueOf(split[2]);
            Hex hex = gameController.fieldManager.getHex(index1, index2);
            if (hex == null) continue;
            Province province = gameController.fieldManager.getProvinceByHex(hex);
            if (province == null) continue;
            province.money = money;
        }
    }


    private void applyGoal() {
        String goalSection = getSection("goal");
        if (goalSection == null) return;

        gameController.finishGameManager.decode(goalSection);
    }


    private void applyMapName() {
        String mapNameSection = getSection("map_name");
        if (mapNameSection == null) return;

        System.out.println("Map name: " + mapNameSection);
    }


    private void applyEditorInfo() {
        String editorInfoSection = getSection("editor_info");
        if (editorInfoSection == null) return;

        String[] split = editorInfoSection.split(" ");

        GameRules.setEditorChosenColor(Integer.valueOf(split[0]));
        GameRules.editorDiplomacy = Boolean.valueOf(split[1]);
        GameRules.editorFog = Boolean.valueOf(split[2]);

        if (split.length > 3) {
            GameRules.diplomaticRelationsLocked = Boolean.valueOf(split[3]);
        }
    }


    private void applyUnits() {
        String unitsSection = getSection("units");
        if (unitsSection == null) return;

        for (String token : unitsSection.split(",")) {
            encodeSingleUnit(token);
        }
    }


    private void encodeSingleUnit(String token) {
        String[] split = token.split(" ");
        int index1 = Integer.valueOf(split[0]);
        int index2 = Integer.valueOf(split[1]);
        int strength = Integer.valueOf(split[2]);
        Hex hex = gameController.fieldManager.getHex(index1, index2);
        Unit unit = gameController.fieldManager.addUnit(hex, strength);
        unit.decode(token);
    }


    private void applyLand() {
        String landSection = getSection("land");
        if (landSection == null) return;

        gameController.fieldManager.decode(landSection);
    }


    private void applyGeneralInfo() {
        String generalSection = getSection("general");
        if (generalSection == null) return;

        String[] split = generalSection.split(" ");
        GameRules.setDifficulty(Integer.valueOf(split[0]));
        gameController.setPlayersNumber(Integer.valueOf(split[1]));
        GameRules.setFractionsQuantity(Integer.valueOf(split[2]));
    }


    private void end() {

    }


    public String extractMapName(String levelCode) {
        source = levelCode;
        String section = getSection("map_name");
        if (section == null) return "unknown";

        return section;
    }


    public int extractChosenColor(String levelCode) {
        source = levelCode;
        String section = getSection("editor_info");
        if (section == null) return 0;

        String[] split = section.split(" ");
        return Integer.valueOf(split[0]);
    }


    public boolean extractFogOfWar(String levelCode) {
        source = levelCode;
        String section = getSection("editor_info");
        if (section == null) return false;

        String[] split = section.split(" ");
        return Boolean.valueOf(split[2]);
    }


    public boolean extractDiplomacy(String levelCode) {
        source = levelCode;
        String section = getSection("editor_info");
        if (section == null) return false;

        String[] split = section.split(" ");
        return Boolean.valueOf(split[1]);
    }


    public int extractPlayersNumber(String levelCode) {
        source = levelCode;
        String section = getSection("general");
        if (section == null) return 1;

        String[] split = section.split(" ");
        return Integer.valueOf(split[1]);
    }


    public int extractDifficulty(String levelCode) {
        source = levelCode;
        String section = getSection("general");
        if (section == null) return 1;

        String[] split = section.split(" ");
        return Integer.valueOf(split[0]);
    }


    public int extractFractionsQuantity(String levelCode) {
        source = levelCode;
        String section = getSection("general");
        if (section == null) return 1;

        String[] split = section.split(" ");
        return Integer.valueOf(split[2]);
    }


    public int extractLevelSize(String levelCode) {
        source = levelCode;
        String section = getSection("level_size");
        if (section == null) return -1;

        return Integer.valueOf(section);
    }


    public String getSection(String name) {
        int nameIndex = source.indexOf("#" + name);
        if (nameIndex == -1) return null;
        nameIndex++;

        int colonIndex = source.indexOf(":", nameIndex);
        int hashIndex = source.indexOf("#", colonIndex);
        if (hashIndex - colonIndex < 2) return null;

        return source.substring(colonIndex + 1, hashIndex);
    }


    public void setSource(String source) {
        this.source = source;
    }
}
