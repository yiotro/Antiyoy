package yio.tro.antiyoy.menu;

import yio.tro.antiyoy.gameplay.user_levels.UserLevelFactory;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.LanguagesManager;
import yio.tro.antiyoy.stuff.VisualTextContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

public class ExceptionViewElement extends AbstractRectangularUiElement{

    Exception exception;
    public VisualTextContainer visualTextContainer;


    public ExceptionViewElement(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        visualTextContainer = new VisualTextContainer();
    }


    @Override
    protected void onMove() {
        visualTextContainer.move(viewPosition);
    }


    public void setException(Exception exception) {
        this.exception = exception;

        ArrayList<String> strings = new ArrayList<>();
        strings.add("[" + UserLevelFactory.getInstance().getLevels().size() + "]");
        strings.add(" ");
        String catchedExceptionSource = LanguagesManager.getInstance().getString("catched_exception");
        strings.addAll(menuControllerYio.getArrayListFromString(catchedExceptionSource));
        strings.add(" ");
        strings.add("Error : " + exception.toString());
        for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
            String temp = "" + stackTraceElement;
            StringBuilder builder = new StringBuilder();
            builder.append("- ");
            StringTokenizer tokenizer = new StringTokenizer(temp, ".");
            while (tokenizer.hasMoreTokens()) {
                builder.append(tokenizer.nextToken()).append(". ");
            }
            strings.add(builder.toString());
        }

        visualTextContainer.clear();
        visualTextContainer.setSize(position.width, position.height);
        visualTextContainer.applyManyTextLines(Fonts.microFont, strings);
        visualTextContainer.move(viewPosition);
    }


    @Override
    protected void onDestroy() {

    }


    @Override
    protected void onAppear() {

    }


    @Override
    protected void onTouchDown() {

    }


    @Override
    protected void onTouchDrag() {

    }


    @Override
    protected void onTouchUp() {

    }


    @Override
    protected void onClick() {

    }


    @Override
    public boolean checkToPerformAction() {
        return false;
    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderExceptionViewElement;
    }
}
