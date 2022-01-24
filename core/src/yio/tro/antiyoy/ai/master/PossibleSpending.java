package yio.tro.antiyoy.ai.master;

public class PossibleSpending {

    public PsType psType;
    public boolean valid;
    public double thirst;


    public PossibleSpending(PsType psType) {
        this.psType = psType;
        valid = false;
        thirst = 0;
    }

}
