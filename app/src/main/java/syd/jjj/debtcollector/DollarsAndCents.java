package syd.jjj.debtcollector;

public class DollarsAndCents {

    public DollarsAndCents(){
        dollars = "0";
        cents = "00";
    }

    public DollarsAndCents(String dollars, String cents){
        this.dollars = dollars;
        this.cents = cents;
    }

    private String dollars;
    private String cents;

}
