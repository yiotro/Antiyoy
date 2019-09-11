package yio.tro.antiyoy.menu;

import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.stuff.RectangleYio;

public interface UiChildrenHolder {

    RectangleYio getHookPosition();

    RectangleYio getTargetPosition();

    FactorYio getFactor();

}
