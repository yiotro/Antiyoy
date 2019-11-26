package yio.tro.antiyoy.gameplay;

import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticEntity;
import yio.tro.antiyoy.gameplay.name_generator.CityNameGenerator;
import yio.tro.antiyoy.gameplay.rules.GameRules;

import java.util.HashMap;
import java.util.Map;

public class NamingManager implements SavableYio{

    GameController gameController;
    private HashMap<Hex, String> renamedHexes;
    StringBuilder stringBuilder;


    public NamingManager(GameController gameController) {
        this.gameController = gameController;
        renamedHexes = new HashMap<>();
        stringBuilder = new StringBuilder();
    }


    public void defaultValues() {
        renamedHexes.clear();
    }


    public void onEndCreation() {

    }


    public void move() {

    }


    public String getProvinceName(Province province) {
        Hex capital = province.getCapital();

        if (renamedHexes.containsKey(capital)) {
            return renamedHexes.get(capital);
        }

        return CityNameGenerator.getInstance().generateName(capital);
    }


    public boolean isThereAtLeastOneRename() {
        if (renamedHexes.entrySet().size() > 0) return true;

        return false;
    }


    public void setHexName(Hex hex, String name) {
        renamedHexes.put(hex, name);

        if (GameRules.diplomacyEnabled) {
            updateRelatedDiplomaticEntity(hex);
        }
    }


    private void updateRelatedDiplomaticEntity(Hex hex) {
        FieldManager fieldManager = gameController.fieldManager;
        DiplomacyManager diplomacyManager = fieldManager.diplomacyManager;
        DiplomaticEntity entity = diplomacyManager.getEntity(hex.fraction);
        if (entity == null) return;
        if (!entity.alive) return;

        entity.updateCapitalName();
    }


    @Override
    public String saveToString() {
        if (!isThereAtLeastOneRename()) return "-";

        stringBuilder.setLength(0);

        for (Map.Entry<Hex, String> entry : renamedHexes.entrySet()) {
            Hex hex = entry.getKey();
            String name = entry.getValue();
            stringBuilder.append(hex.index1).append(" ").append(hex.index2).append(" ").append(name).append(",");
        }

        return stringBuilder.toString();
    }


    @Override
    public void loadFromString(String src) {
        if (src.length() == 0) return;
        if (src.equals("-")) return;

        String[] renamedHexesSplit = src.split(",");
        for (String token : renamedHexesSplit) {
            String[] split = token.split(" ");
            if (split.length < 3) continue;

            int index1 = Integer.valueOf(split[0]);
            int index2 = Integer.valueOf(split[1]);
            String name = split[2];
            Hex hex = gameController.fieldManager.getHex(index1, index2);

            setHexName(hex, name);
        }

        forceProvincesToUpdateNames();
    }


    private void forceProvincesToUpdateNames() {
        for (Province province : gameController.fieldManager.provinces) {
            province.updateName();
        }
    }
}
