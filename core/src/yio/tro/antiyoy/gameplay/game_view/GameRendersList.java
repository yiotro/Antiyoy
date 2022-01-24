package yio.tro.antiyoy.gameplay.game_view;

import java.util.ArrayList;

public class GameRendersList {


    GameView gameView;
    public ArrayList<GameRender> list;

    public RenderFogOfWar renderFogOfWar;
    public RenderCityNames renderCityNames;
    public RenderMoveZone renderMoveZone;
    public RenderBackgroundCache renderBackgroundCache;
    public RenderSelectionShadows renderSelectionShadows;
    public RenderDefenseTips renderDefenseTips;
    public RenderBlackout renderBlackout;
    public RenderSelectedUnit renderSelectedUnit;
    public RenderForefinger renderForefinger;
    public RenderTip renderTip;
    public RenderSelectedHexes renderSelectedHexes;
    public RenderExclamationMarks renderExclamationMarks;
    public RenderResponseAnimHex renderResponseAnimHex;
    public RenderAnimHexes renderAnimHexes;
    public RenderUnits renderUnits;
    public RenderSolidObjects renderSolidObjects;
    public RenderHexLines renderHexLines;
    public RenderDebug renderDebug;
    public RenderTmEditProvinces renderTmEditProvinces;
    public RenderHighlights renderHighlights;
    public RenderAiData renderAiData;
    public RenderTextHintItems renderTextHintItems;
    public RenderDiplomaticIndicators renderDiplomaticIndicators;
    // init them lower


    public GameRendersList(GameView gameView) {
        this.gameView = gameView;

        list = new ArrayList<>();
    }


    public void create() {
        renderFogOfWar = new RenderFogOfWar(this);
        renderCityNames = new RenderCityNames(this);
        renderMoveZone = new RenderMoveZone(this);
        renderBackgroundCache = new RenderBackgroundCache(this);
        renderSelectionShadows = new RenderSelectionShadows(this);
        renderDefenseTips = new RenderDefenseTips(this);
        renderBlackout = new RenderBlackout(this);
        renderSelectedUnit = new RenderSelectedUnit(this);
        renderForefinger = new RenderForefinger(this);
        renderTip = new RenderTip(this);
        renderSelectedHexes = new RenderSelectedHexes(this);
        renderExclamationMarks = new RenderExclamationMarks(this);
        renderResponseAnimHex = new RenderResponseAnimHex(this);
        renderAnimHexes = new RenderAnimHexes(this);
        renderUnits = new RenderUnits(this);
        renderSolidObjects = new RenderSolidObjects(this);
        renderHexLines = new RenderHexLines(this);
        renderDebug = new RenderDebug(this);
        renderTmEditProvinces = new RenderTmEditProvinces(this);
        renderHighlights = new RenderHighlights(this);
        renderAiData = new RenderAiData(this);
        renderTextHintItems = new RenderTextHintItems(this);
        renderDiplomaticIndicators = new RenderDiplomaticIndicators(this);
    }


    public void loadTextures() {
        for (GameRender gameRender : list) {
            gameRender.loadTextures();
        }
    }


    public void disposeTextures() {
        for (GameRender gameRender : list) {
            gameRender.disposeTextures();
        }
    }
}
