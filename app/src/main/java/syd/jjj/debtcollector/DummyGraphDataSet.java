package syd.jjj.debtcollector;

import java.util.Date;

public class DummyGraphDataSet {
    public float[] dummyData;

    public DummyGraphDataSet(){
        dummyData = new float[] {
                0, 0,
                100000000, 10025,
                100000000, 10025,
                200000000, 80098,
                200000000, 80098,
                350000000, 170988,
                350000000, 170988,
                400000000, 150000,
                400000000, 150000,
                575000000, 129999,
                575000000, 129999,
                600000000, 0
        };
    }

    public float[] getDummyData() {
        return dummyData;
    }

    public DebtValue getHighestDebtValue() {
        DebtValue highestDebtValue = new DebtValue(new Date(),"1709", "88");
        return highestDebtValue;
    }

    @Override
    public String toString() {
        return "January 2019";
    }
}
