package syd.jjj.debtcollector;

public class DollarsAndCents {

    public DollarsAndCents(String currentDollars, String currentCents, String uIDollars, String uICents){
        this.currentDollars = currentDollars;
        this.currentCents = currentCents;
        this.uIDollars = uIDollars;
        this.uICents = uICents;
    }

    private String currentDollars;
    private String currentCents;
    private String uIDollars;
    private String uICents;

    public String getCurrentDollars(){
        return currentDollars;
    }

    public String getCurrentCents(){
        return currentCents;
    }

    public void newDebtValue() {
        correctZeroValues();
        currentDollars = uIDollars;
        currentCents = uICents;
    }

    public void addDebt(){

        correctZeroValues();
        String currentTotal = currentDollars.concat(currentCents);
        String uITotal = uIDollars.concat(uICents);
        int currentTotalInt = Integer.parseInt(currentTotal);
        int uITotalInt = Integer.parseInt(uITotal);
        int newTotalInt = currentTotalInt + uITotalInt;
        String newTotal = Integer.toString(newTotalInt);
        setNewTotal(newTotal);
    }

    public void payOffDebt (){

        correctZeroValues();
        String currentTotal = currentDollars.concat(currentCents);
        String uITotal = uIDollars.concat(uICents);
        int currentTotalInt = Integer.parseInt(currentTotal);
        int uITotalInt = Integer.parseInt(uITotal);
        int newTotalInt = currentTotalInt - uITotalInt;

        if (newTotalInt >= 0) {
            String newTotal = Integer.toString(newTotalInt);
            setNewTotal(newTotal);
        } else {
            setNewTotal("0");
        }
    }

    private void correctZeroValues() {
        //Sets a String value for dollars if none set by user.
        if (uIDollars.length() == 0) {
            uIDollars = "0";
        }
        //Sets a String value for second dp of cents if none set by user.
        if (uICents.length() == 1) {
            uICents = uICents.concat("0");
        }
        //Sets a String value for cents if none set by user.
        if (uICents.length() == 0) {
            uICents = ("00");
        }
    }


    private void setNewTotal(String newTotal){

        int totalLength = newTotal.length();

        if(totalLength == 0) {
            currentDollars = "0";
            currentCents = "00";
        }

        if(totalLength == 1) {
            currentDollars = "0";
            currentCents = "0" + newTotal;
        }

        if(totalLength == 2) {
            currentDollars = "0";
            currentCents = newTotal;
        }

        if(totalLength > 2) {
            currentDollars = newTotal.substring(0, totalLength - 2);
            currentCents = newTotal.substring(totalLength - 2);
        }
    }
}
