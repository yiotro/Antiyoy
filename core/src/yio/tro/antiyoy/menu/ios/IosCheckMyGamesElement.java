package yio.tro.antiyoy.menu.ios;

import com.badlogic.gdx.Gdx;
import yio.tro.antiyoy.SoundManagerYio;
import yio.tro.antiyoy.menu.AbstractRectangularUiElement;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.*;

import java.util.ArrayList;

public class IosCheckMyGamesElement extends AbstractRectangularUiElement {

    public VisualTextContainer visualTextContainer;
    public RectangleYio showRoomPosition;
    private float verOffset;
    public ArrayList<IcmgIcon> icons;
    public boolean touched;
    boolean readyToPerform;
    IcmgIcon targetIcon;


    public IosCheckMyGamesElement(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        visualTextContainer = new VisualTextContainer();
        showRoomPosition = new RectangleYio();
        verOffset = 0.05f * GraphicsYio.width;
        initIcons();
    }


    private void initIcons() {
        icons = new ArrayList<>();
        for (IcmgType icmgType : IcmgType.values()) {
            IcmgIcon icmgIcon = new IcmgIcon(this);
            icmgIcon.setType(icmgType);
            icons.add(icmgIcon);
        }

        icons.get(0).setTitle("Opacha-mda");
        icons.get(1).setTitle("Achikaps");
        icons.get(2).setTitle("Bleentoro");
        icons.get(3).setTitle("Vodobanka");

        icons.get(0).setUrl("https://apps.apple.com/ua/app/opacha-mda/id1515105386");
        icons.get(1).setUrl("https://apps.apple.com/ua/app/achikaps/id1515537717");
        icons.get(2).setUrl("https://apps.apple.com/ua/app/bleentoro/id1516651107");
        icons.get(3).setUrl("https://apps.apple.com/us/app/vodobanka/id1516815669");
    }


    @Override
    protected void onMove() {
        updateVisualTextContainer();
        updateShowRoomPosition();
        moveIcons();
    }


    private void moveIcons() {
        for (IcmgIcon icon : icons) {
            icon.move();
        }
    }


    private void updateShowRoomPosition() {
        showRoomPosition.width = visualTextContainer.position.width;
        showRoomPosition.x = position.x + position.width / 2 - showRoomPosition.width / 2;
        showRoomPosition.height = position.height - verOffset - visualTextContainer.position.height - 2 * verOffset;
        showRoomPosition.y = position.y + verOffset;
    }


    private void updateVisualTextContainer() {
        visualTextContainer.position.y = viewPosition.height - verOffset - visualTextContainer.position.height;
        visualTextContainer.move(viewPosition);
    }


    @Override
    protected void onDestroy() {
        touched = false;
    }


    @Override
    public void setPosition(RectangleYio position) {
        super.setPosition(position);
        initVisualTextContainer();
    }


    private void initVisualTextContainer() {
        float delta = 0.02f * GraphicsYio.width;
        visualTextContainer.position.x = delta;
        visualTextContainer.position.width = position.width - 2 * delta;
        visualTextContainer.applyManyTextLines(Fonts.smallerMenuFont, LanguagesManager.getInstance().getString("article_my_games"));
        visualTextContainer.suppressEmptyLinesInTheEnd();
        visualTextContainer.updateHeightToMatchText(delta);
    }


    @Override
    protected void onAppear() {
        initIconDeltas();
        touched = false;
        readyToPerform = false;
        targetIcon = null;
    }


    private void initIconDeltas() {
        initSingleIconDelta(icons.get(0), 0, 1);
        initSingleIconDelta(icons.get(1), 1, 1);
        initSingleIconDelta(icons.get(2), 0, 0);
        initSingleIconDelta(icons.get(3), 1, 0);
    }


    private void initSingleIconDelta(IcmgIcon icon, int xIndex, int yIndex) {
        updateShowRoomPosition();
        icon.delta.x = (float) ((0.25 + 0.5 * xIndex) * showRoomPosition.width);
        icon.delta.y = (float) ((0.25 + 0.5 * yIndex) * showRoomPosition.height);
        icon.targetRadius = (float) Math.min(0.1 * showRoomPosition.width, 0.1 * showRoomPosition.height);
    }


    @Override
    protected void onTouchDown() {
        touched = viewPosition.isPointInside(currentTouch);
        if (touched) {
            checkForSelection();
        }
    }


    private void checkForSelection() {
        IcmgIcon currentlyTouchedIcon = getCurrentlyTouchedIcon();
        if (currentlyTouchedIcon == null) return;

        currentlyTouchedIcon.selectionEngineYio.select();
        SoundManagerYio.playSound(SoundManagerYio.soundKeyboardPress);
    }


    private IcmgIcon getCurrentlyTouchedIcon() {
        for (IcmgIcon icon : icons) {
            if (icon.isTouchedBy(currentTouch)) return icon;
        }
        return null;
    }


    @Override
    protected void onTouchDrag() {

    }


    @Override
    protected void onTouchUp() {
        touched = false;
    }


    private void applyTargetIcon() {
        Gdx.net.openURI(targetIcon.url);
    }


    @Override
    protected void onClick() {
        IcmgIcon currentlyTouchedIcon = getCurrentlyTouchedIcon();
        if (currentlyTouchedIcon == null) return;

        readyToPerform = true;
        targetIcon = currentlyTouchedIcon;
    }


    @Override
    public boolean checkToPerformAction() {
        if (readyToPerform) {
            readyToPerform = false;
            applyTargetIcon();
            return true;
        }

        return false;
    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderIosCheckMyGamesElement;
    }
}
