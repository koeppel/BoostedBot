package IdleGame;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import Utils.CONFIG;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class IdleGame {

    private int timeMultiplier = CONFIG.TIMERMULTIPLYER;
    private double cash;
    private double baseIncome;
    private double income;
    private List<Worker> workers = new ArrayList<>();
    private JDA jda;
    private String msgID;
    private String channelID;
    private Timer gameTick = new Timer();
    private GameHandler gHandler;

    private TextChannel gameChannel;
    private Message msg;

    public double getCash() {
        return this.cash;
    }

    public void setCash(double cash) {
        this.cash = cash;
    }

    public double getIncome() {
        return this.income;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public int getTimeMultiplier() {
        return this.timeMultiplier;
    }

    public void setTimeMultiplier(int timeMultiplier) {
        this.timeMultiplier = timeMultiplier;
    }

    public double getBaseIncome() { return this.baseIncome; }

    public void setBaseIncome(double baseIncome) {
        this.baseIncome = baseIncome;
    }

    public List<Worker> getWorkers() { return this.workers; }

    public void setWorkers(List<Worker> workers) {
        this.workers = workers;
    }

    public String getChannelID() { return  this.channelID; }

    public void setChannelID(String channelID) {
        this.channelID = channelID;
        this.gameChannel = this.jda.getTextChannelById(this.channelID);
    }

    public String getMsgID() { return this.msgID; }

    public void setMsgID(String msgID) {
        this.msgID = msgID;
        this.msg = this.gameChannel.getMessageById(this.msgID).complete();
    }

    @JsonIgnore public JDA getJDA() {
        return this.jda;
    }

    @JsonIgnore public void setgHandler(GameHandler gHandler) {
        this.gHandler = gHandler;
    }

    @JsonIgnore public GameHandler getgHandler() {
        return this.gHandler;
    }

    @JsonIgnore public void setJda(JDA jda) {
        this.jda = jda;
        this.gameChannel = jda.getTextChannelById(this.channelID);
        this.msg = this.gameChannel.getMessageById(this.msgID).complete();
    }

    public IdleGame(double startCash,
                    double baseIncome,
                    JDA jda,
                    String msgID,
                    String channelID,
                    GameHandler gameHandler) {
        this.cash = startCash;
        this.baseIncome = baseIncome;
        this.income = baseIncome;
        this.jda = jda;
        this.msgID = msgID;
        this.channelID = channelID;
        this.gHandler = gameHandler;

        this.gameChannel = jda.getTextChannelById(this.channelID);
        this.msg = this.gameChannel.getMessageById(this.msgID).complete();
    }


    @JsonCreator public IdleGame(
            @JsonProperty("cash")double startCash,
            @JsonProperty("baseIncome")double baseIncome,
            @JsonProperty("msgID")String msgID,
            @JsonProperty("channelID")String channelID) {
        this.cash = startCash;
        this.baseIncome = baseIncome;
        this.income = baseIncome;
        this.msgID = msgID;
        this.channelID = channelID;
    }

    @Override
    public void finalize(){}

    public void startIdleGame() {
        loadWorkers();
        updateIncome();
        this.gameTick.schedule(new TimerTask() {
            @Override
            public void run() {
                updateCash();
                saveGame();
            }
        },0, 1000 * timeMultiplier);
    }

    public void endIdleGame() {
        this.gameTick.cancel();
        this.finalize();
    }

    private void loadWorkers() {
        if (this.workers.isEmpty()) {
            List<Upgrade> dirkUpgrades = new ArrayList<>();
            Upgrade dirkUpgrade1 = new Upgrade("SuperDirk", "simple", 10, 0.2);
            dirkUpgrades.add(dirkUpgrade1);
            Worker dirk = new Worker("Dirk",
                    1,
                    10.0,
                    1.0,
                    10.0,
                    1.0,
                    0.3,
                    1,
                    dirkUpgrades,
                    this);
            this.workers.add(dirk);
        }
        else {
            for (Worker worker: this.workers) {
                worker.setGame(this);
            }
        }
    }

    private void updateCash() {
        this.cash += this.income;
        updateMessage();
    }

    public void updateIncome() {
        this.income = this.baseIncome;
        for (Worker worker:this.workers) {
            this.income += worker.getIncome();
        }
        this.income = this.income * (double)this.timeMultiplier;
        updateMessage();
    }

    private void levelWorker(int workerIndex) {
        if (this.cash > this.workers.get(workerIndex).getCost()){
            this.cash = this.cash - this.workers.get(workerIndex).getCost();
            this.workers.get(0).levelUp();
        }
    }

    private void levelDirk() {
        levelWorker(0);
    }

    private void updateMessage(){
        String temp = "Cash: " + this.cash + "\nCurrent Income: " + this.income + " / " + this.timeMultiplier + "\nWORKERS:";
        for (Worker worker: this.workers) {
            temp = temp + "\n" + worker.getName() + " [" + worker.getLevel() + "]";
        }
        this.msg.editMessage(temp).queue();
    }

    private void saveGame() {
        this.gHandler.saveGame(this.gameChannel, this);
    }
}
