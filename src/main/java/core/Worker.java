package core;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Worker {

    private String name;
    private int level;
    private double baseCost;
    private double baseIncome;
    private double cost;
    private double income;
    private double costMultiplier;
    private double incomeMultiplier;
    private List<Upgrade> upgrades;
    private IdleGame game;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getBaseCost() {
        return this.baseCost;
    }

    public void setBaseCost(double baseCost) {
        this.baseCost = baseCost;
    }

    public double getBaseIncome() {
        return this.baseIncome;
    }

    public void setBaseIncome(double baseIncome) {
        this.baseIncome = baseIncome;
    }

    public double getCost() {
        return this.cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getIncome() {
        if (this.level > 0) {
            return this.income;
        } else {
            return 0;
        }
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public double getCostMultiplier() {
        return this.costMultiplier;
    }

    public void setCostMultiplier(double costMultiplier) {
        this.costMultiplier = costMultiplier;
    }

    public double getIncomeMultiplier() {
        return this.incomeMultiplier;
    }

    public void setIncomeMultiplier(double incomeMultiplier) {
        this.incomeMultiplier = incomeMultiplier;
    }

    public List<Upgrade> getUpgrades() {
        return this.upgrades;
    }

    public void setUpgrades(List<Upgrade> upgrades) {
        this.upgrades = upgrades;
    }

    @JsonIgnore public IdleGame getGame() { return  this.game; }

    @JsonIgnore public void setGame(IdleGame game) { this.game = game; }

    public Worker(String name,
                  int level,
                  double baseCost,
                  double baseIncome,
                  double cost,
                  double income,
                  double costMultiplier,
                  double incomeMultiplier,
                  List<Upgrade> upgrades,
                  IdleGame game) {
        this.name = name;
        this.level = level;
        this.baseCost = baseCost;
        this.baseIncome = baseIncome;
        this.cost = cost;
        this.income = income;
        this.costMultiplier = costMultiplier;
        this.incomeMultiplier = incomeMultiplier;
        this.upgrades = upgrades;
        this.game = game;
        this.updateWorker();
    }

    @JsonCreator public Worker(
            @JsonProperty("name")String name,
            @JsonProperty("level")int level,
            @JsonProperty("baseCost")double baseCost,
            @JsonProperty("baseIncome")double baseIncome,
            @JsonProperty("cost")double cost,
            @JsonProperty("income")double income,
            @JsonProperty("costMultiplier")double costMultiplier,
            @JsonProperty("incomeMultiplier")double incomeMultiplier) {
        this.name = name;
        this.level = level;
        this.baseCost = baseCost;
        this.baseIncome = baseIncome;
        this.cost = cost;
        this.income = income;
        this.costMultiplier = costMultiplier;
        this.incomeMultiplier = incomeMultiplier;
        this.upgrades = upgrades;
        this.game = game;
        this.updateWorker();
    }

    public void levelUp() {
        this.level++;
        System.out.println(this.name + " Leveled up to [" + this.level + "]" + " for " + this.cost);
        this.income = this.baseIncome * (this.level * this.incomeMultiplier);
        this.cost = this.cost + (this.cost * this.level * this.costMultiplier);
        this.game.updateIncome();
    }

    private void updateWorker() {
        this.income = this.baseIncome * (1 + this.level * this.incomeMultiplier);
        this.cost = this.baseCost + (this.baseCost * this.level * this.costMultiplier);
    }
}
