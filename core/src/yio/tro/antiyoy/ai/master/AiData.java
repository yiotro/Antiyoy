package yio.tro.antiyoy.ai.master;

import yio.tro.antiyoy.PlatformType;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.Unit;
import yio.tro.antiyoy.stuff.*;
import yio.tro.antiyoy.stuff.object_pool.ReusableYio;

import java.util.ArrayList;

public class AiData implements ReusableYio{

    Hex hex;
    public RenderableTextYio renderableTextYio;
    public RectangleYio incBounds;
    public double loneliness;
    public double attractiveness;
    public int propCastValue; // propagation caster value
    public boolean firstLine;
    public boolean secondLine;
    public double importance;
    public int solidDefense;
    public double armyPresense;
    public boolean attack1;
    public boolean attack2;
    public double tastiness;
    public int ownedLandsNearby;
    public double vicinity; // helps to not stretch too far too fast
    public boolean inReadyArea;
    public ArrayList<Unit> potentialAttackers;
    public boolean currentlyOwned;
    public boolean reached;
    public boolean unitPotentiallyUsed;
    public boolean canBeCaptured;
    public Hex referenceHex;
    public ArrayList<Unit> dependentUnits;
    public int walkDistance;


    public AiData(Hex hex) {
        this.hex = hex;
        renderableTextYio = new RenderableTextYio();
        renderableTextYio.setFont(Fonts.microFont);
        incBounds = new RectangleYio();
        potentialAttackers = new ArrayList<>();
        dependentUnits = new ArrayList<>();
        propCastValue = -1;
    }


    @Override
    public void reset() {
        // this method is called once per turn
        // so loneliness and attractiveness shouldn't be touched
        // and also other things that are calculated on loading
        propCastValue = 0;
        firstLine = false;
        secondLine = false;
        importance = 0;
        solidDefense = 0;
        armyPresense = 0;
        attack1 = false;
        attack2 = false;
        tastiness = 0;
        ownedLandsNearby = 0;
        vicinity = 0;
        inReadyArea = false;
        potentialAttackers.clear();
        currentlyOwned = false;
        reached = false;
        canBeCaptured = false;
        unitPotentiallyUsed = false;
        referenceHex = null;
        dependentUnits.clear();
        walkDistance = 0;
    }


    public void setViewValue(double viewValue) {
        if (YioGdxGame.platformType != PlatformType.pc) return;
        setViewValue("" + Yio.roundUp(viewValue, 1));
    }


    public void setViewValue(String string) {
        if (YioGdxGame.platformType != PlatformType.pc) return;

        renderableTextYio.setString(string);
        renderableTextYio.updateMetrics();

        renderableTextYio.position.x = hex.pos.x - renderableTextYio.width / 2;
        renderableTextYio.position.y = hex.pos.y + renderableTextYio.height / 2;
        renderableTextYio.updateBounds();

        incBounds.setBy(renderableTextYio.bounds);
        incBounds.increase(GraphicsYio.borderThickness);
    }

}
