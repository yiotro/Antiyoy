package yio.tro.antiyoy.menu.keyboard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.ClickDetector;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.*;

public class NativeKeyboardElement extends InterfaceElement {


    MenuControllerYio menuControllerYio;
    public RectangleYio position, viewPosition;
    public FactorYio appearFactor;
    PointYio currentTouch;
    public BitmapFont font;
    AbstractKbReaction reaction;
    public RectangleYio blackoutPosition;
    private TextField.TextFieldStyle textFieldStyle;
    private TextField textField;
    public RectangleYio tfPosition;
    public RectangleYio tfFrame;
    boolean touched;
    private float frameOffset;
    public FactorYio tfFactor;
    boolean readyToShowTextField;
    RepeatYio<NativeKeyboardElement> repeatCheckToShowTextField;
    String tfPreparedValue;
    ClickDetector clickDetector;


    public NativeKeyboardElement(MenuControllerYio menuControllerYio) {
        super(-1);
        this.menuControllerYio = menuControllerYio;

        position = new RectangleYio();
        viewPosition = new RectangleYio();
        appearFactor = new FactorYio();
        font = Fonts.gameFont;
        reaction = null;
        blackoutPosition = new RectangleYio(0, 0, GraphicsYio.width, GraphicsYio.height);
        tfPosition = new RectangleYio();
        tfFrame = new RectangleYio();
        frameOffset = 0.02f * GraphicsYio.width;
        tfFactor = new FactorYio();
        tfPreparedValue = "";
        currentTouch = new PointYio();
        clickDetector = new ClickDetector();

        initTextFieldStyle();
        initRepeats();
    }


    private void initRepeats() {
        repeatCheckToShowTextField = new RepeatYio<NativeKeyboardElement>(this, 300) {
            @Override
            public void performAction() {
                parent.checkToShowTextField();
            }
        };
    }


    void checkToShowTextField() {
        if (!readyToShowTextField) return;

        readyToShowTextField = false;
        showTextField();
    }


    private void initTextFieldStyle() {
        textFieldStyle = new TextField.TextFieldStyle(
                Fonts.smallerMenuFont,
                Color.BLACK,
                new TextureRegionDrawable(GraphicsYio.loadTextureRegion("pixels/black_pixel.png", false)),
                new TextureRegionDrawable(GraphicsYio.loadTextureRegion("pixels/translucent.png", false)),
                new TextureRegionDrawable(GraphicsYio.loadTextureRegion("pixels/white_pixel.png", false))
        );
    }


    public void setReaction(AbstractKbReaction reaction) {
        this.reaction = reaction;
    }


    @Override
    public void move() {
        appearFactor.move();
        updateViewPosition();
        tfFactor.move();
        moveTfFrame();
        repeatCheckToShowTextField.move();
    }


    private void updateViewPosition() {
        viewPosition.setBy(position);
        viewPosition.y -= (1 - appearFactor.get()) * 0.1f * GraphicsYio.width;
    }


    @Override
    public FactorYio getFactor() {
        return appearFactor;
    }


    @Override
    public void destroy() {
        appearFactor.destroy(1, 2.5);
        onDestroy();
    }


    private void onDestroy() {
        touched = false;
        Gdx.input.setOnscreenKeyboardVisible(false);
        textField.remove();
        tfFactor.reset();
    }


    @Override
    public void appear() {
        appearFactor.setValues(0.001, 0);
        appearFactor.appear(3, 1.6);
        onAppear();
    }


    private void onAppear() {
        touched = false;
        updateTfPosition();
        Gdx.input.setOnscreenKeyboardVisible(true);
        tfFactor.appear(3, 3);

        repeatCheckToShowTextField.setCountDown(15);
        readyToShowTextField = true;
    }


    public void onPcKeyPressed(int keycode) {
        switch (keycode) {
            case Input.Keys.ENTER:
                reaction.onInputFromKeyboardReceived(getResultTextFromTextField());
                Scenes.sceneNativeKeyboard.hide();
                break;
            case Input.Keys.BACK:
            case Input.Keys.ESCAPE:
                Scenes.sceneNativeKeyboard.hide();
                break;
        }
    }


    private String getResultTextFromTextField() {
        String text = textField.getText();
        if (text.length() < 2) return text;
        String tempResult = text.substring(0, 1).toUpperCase() + text.substring(1);
        return getFilteredResult(tempResult);
    }


    private String getFilteredResult(String src) {
        StringBuilder builder = new StringBuilder();
        for (char c : src.toCharArray()) {
            if (!isCharValid(c)) continue;
            builder.append(c);
        }
        return builder.toString();
    }


    private boolean isCharValid(char c) {
        if (c == '@') return false;
        if (c == '#') return false;
        if (c == ':') return false;
        if (c == ',') return false;
        return c == ' ' || Fonts.getAllCharacters().contains("" + c);
    }


    private void updateTfPosition() {
        tfPosition.width = 0.8f * GraphicsYio.width;
        tfPosition.height = 0.05f * GraphicsYio.height;
        tfPosition.x = GraphicsYio.width / 2 - tfPosition.width / 2;
        tfPosition.y = 0.5f * GraphicsYio.height;
    }


    private void moveTfFrame() {
        tfFrame.setBy(tfPosition);
        tfFrame.increase(-tfPosition.height / 2);
        tfFrame.increase(tfFactor.get() * (tfPosition.height / 2 + frameOffset));
    }


    void showTextField() {
        textField = new TextField("", textFieldStyle);
        textField.setPosition((float) tfPosition.x, (float) tfPosition.y);
        textField.setSize((float) tfPosition.width, (float) tfPosition.height);

        getStage().addActor(textField);

        getStage().setKeyboardFocus(textField);
        textField.selectAll();

        if (tfPreparedValue.length() > 0) {
            textField.setText(tfPreparedValue);
            textField.selectAll();
        }
    }


    private Stage getStage() {
        return menuControllerYio.yioGdxGame.stage;
    }


    @Override
    public boolean isVisible() {
        return appearFactor.get() > 0;
    }


    @Override
    public boolean checkToPerformAction() {
        return false;
    }


    @Override
    public boolean isTouchable() {
        return true;
    }


    private void updateCurrentTouch(int screenX, int screenY) {
        currentTouch.set(screenX, screenY);
    }


    private boolean isCurrentlyTouched() {
        return appearFactor.get() == 1;
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        updateCurrentTouch(screenX, screenY);
        touched = isCurrentlyTouched();

        if (!tfPosition.isPointInside(currentTouch)) {
            clickDetector.onTouchDown(currentTouch);
        }

        return true;
    }


    @Override
    public boolean touchDrag(int screenX, int screenY, int pointer) {
        if (!touched) return false;
        updateCurrentTouch(screenX, screenY);
        clickDetector.onTouchDrag(currentTouch);
        return true;
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (!touched) return false;
        updateCurrentTouch(screenX, screenY);
        clickDetector.onTouchUp(currentTouch);

        if (clickDetector.isClicked()) {
            onClick();
        }

        return true;
    }


    private void onClick() {
        Scenes.sceneNativeKeyboard.hide();
    }


    public void setValue(String value) {
        tfPreparedValue = value;
    }


    @Override
    public void setTouchable(boolean touchable) {

    }


    @Override
    public void setPosition(RectangleYio position) {
        this.position.setBy(position);
    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderNativeKeyboard;
    }}
