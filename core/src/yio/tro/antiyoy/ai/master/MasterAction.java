package yio.tro.antiyoy.ai.master;

public class MasterAction {

    public MaType maType;
    public boolean valid;
    public double thirst;


    public MasterAction(MaType maType) {
        this.maType = maType;
        valid = false;
        thirst = 0;
    }
}
