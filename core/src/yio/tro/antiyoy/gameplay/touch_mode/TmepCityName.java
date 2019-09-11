package yio.tro.antiyoy.gameplay.touch_mode;

import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.editor.EditorProvinceData;
import yio.tro.antiyoy.stuff.*;
import yio.tro.antiyoy.stuff.object_pool.ReusableYio;

public class TmepCityName implements ReusableYio {

    EditorProvinceData editorProvinceData;
    public RenderableTextYio renderableTextYio;
    public FactorYio appearFactor;
    public RectangleYio biggerBounds;
    RepeatYio<TmepCityName> repeatUpdateName;


    public TmepCityName() {
        renderableTextYio = new RenderableTextYio();
        appearFactor = new FactorYio();
        biggerBounds = new RectangleYio();
        initRepeats();
    }


    private void initRepeats() {
        repeatUpdateName = new RepeatYio<TmepCityName>(this, 6) {
            @Override
            public void performAction() {
                parent.updateName();
                updateTextPosition();
                updateBiggerBounds();
            }
        };
    }


    @Override
    public void reset() {
        editorProvinceData = null;
        appearFactor.reset();
        renderableTextYio.reset();
        renderableTextYio.setFont(Fonts.microFont);
    }


    public boolean isCurrentlyVisible() {
        return appearFactor.get() > 0;
    }


    void move() {
        appearFactor.move();
        updateTextPosition();
        updateBiggerBounds();
        repeatUpdateName.move();
    }


    private void updateBiggerBounds() {
        biggerBounds.setBy(renderableTextYio.bounds);
        biggerBounds.increase(2 * GraphicsYio.borderThickness);
    }


    private void updateTextPosition() {
        renderableTextYio.position.x = editorProvinceData.geometricalCenter.x - renderableTextYio.width / 2;
        renderableTextYio.position.y = editorProvinceData.geometricalCenter.y + renderableTextYio.height / 2;
        renderableTextYio.updateBounds();
    }


    public void setEditorProvinceData(EditorProvinceData editorProvinceData) {
        this.editorProvinceData = editorProvinceData;
        updateName();
        appearFactor.setValues(0.01, 0);
        appearFactor.appear(3, 1);
    }


    private void updateName() {
        if (renderableTextYio.string.equals(editorProvinceData.name)) return;
        renderableTextYio.setString(editorProvinceData.name);
        renderableTextYio.updateMetrics();
    }
}
