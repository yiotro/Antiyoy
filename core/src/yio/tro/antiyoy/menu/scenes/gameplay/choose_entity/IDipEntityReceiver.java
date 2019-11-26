package yio.tro.antiyoy.menu.scenes.gameplay.choose_entity;

import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticEntity;

public interface IDipEntityReceiver {


    void onDiplomaticEntityChosen(DiplomaticEntity entity);


    boolean canDiplomaticEntityBeChosen(DiplomaticEntity entity);


}
