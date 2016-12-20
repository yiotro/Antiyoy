package yio.tro.antiyoy.ai;

import yio.tro.antiyoy.GameController;
import yio.tro.antiyoy.Hex;
import yio.tro.antiyoy.Province;

public abstract class ArtificialIntelligenceGeneric extends ArtificialIntelligence{


    ArtificialIntelligenceGeneric(GameController gameController, int color) {
        super(gameController, color);
    }


    @Override
    protected void spendMoney(Province province) {
        tryToBuildFarms(province);
        tryToBuildTowers(province);
        tryToBuildUnits(province);
    }


    protected void tryToBuildFarms(Province province) {
        if (province.getExtraFarmCost() > province.getIncome()) return;
        if (province.getExtraFarmCost() > 20) return;

        while (province.hasMoneyForFarm()) {
            Hex hex = findGoodHexForFarm(province);
            if (hex == null) return;
            gameController.buildFarm(province, hex);
        }
    }


    protected Hex findGoodHexForFarm(Province province) {
        if (!hasProvinceGoodHexForFarm(province)) return null;

        while (true) {
            Hex hex = province.hexList.get(random.nextInt(province.hexList.size()));
            if (isHexGoodForFarm(hex)) return hex;
        }
    }


    protected boolean hasProvinceGoodHexForFarm(Province province) {
        for (Hex hex : province.hexList) {
            if (!isHexGoodForFarm(hex)) continue;
            return true;
        }
        return false;
    }


    protected boolean isHexGoodForFarm(Hex hex) {
        if (!hex.isFree()) return false;
        if (!hex.hasThisObjectNearby(Hex.OBJECT_TOWN) && !hex.hasThisObjectNearby(Hex.OBJECT_FARM)) return false;
        return true;
    }
}
