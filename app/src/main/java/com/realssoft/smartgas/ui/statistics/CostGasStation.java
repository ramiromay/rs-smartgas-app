package com.realssoft.smartgas.ui.statistics;


import com.realssoft.smartgas.util.Option;

public class CostGasStation {

    private String id = "";
    private String name = "";
    private String costMagna = "";
    private String costPremium = "";
    private String  costDiesel = "";

    public CostGasStation() {}

    public  String getOptionCost(int option) {
        switch (option) {
            case Option.OPTION_MAGNA:
                return getCostMagna();
            case Option.OPTION_PREMIUM:
                return getCostPremium();
            case Option.OPTION_DIESEL:
                return getCostDiesel();
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCostMagna() {
        return costMagna;
    }

    public void setCostMagna(String costMagna) {
        this.costMagna = costMagna;
    }

    public String getCostPremium() {
        return costPremium;
    }

    public void setCostPremium(String costPremium) {
        this.costPremium = costPremium;
    }

    public String getCostDiesel() {
        return costDiesel;
    }

    public void setCostDiesel(String costDiesel) {
        this.costDiesel = costDiesel;
    }

}
