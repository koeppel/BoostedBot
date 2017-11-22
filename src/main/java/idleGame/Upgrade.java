package idleGame;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Upgrade {
    private String name;
    private String type;
    private boolean purchased;
    private double cost;
    private double multiplier;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getCost() {
        return this.cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getMulitplier() {
        return this.multiplier;
    }

    public void setMulitplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public boolean getPurchased() {
        return this.purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    public Upgrade(String name, String type, double cost, double multiplier) {
        this.name = name;
        this.type = type;
        this.cost = cost;
        this.multiplier = multiplier;
        this.purchased = false;
    }

    public Upgrade(@JsonProperty("name")String name,
                   @JsonProperty("type")String type,
                   @JsonProperty("cost")double cost,
                   @JsonProperty("multiplier")double multiplier,
                   @JsonProperty("purchased")boolean isPurchased) {
        this.name = name;
        this.type = type;
        this.cost = cost;
        this.multiplier = multiplier;
        this.purchased = isPurchased;
    }
}
