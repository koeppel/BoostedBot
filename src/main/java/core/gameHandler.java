package core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import utils.CONFIG;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class gameHandler {
    private HashMap<TextChannel, IdleGame> games = new HashMap<>();
    private static final String filePath = CONFIG.SAVEPATH;
    private static final String fileName = filePath + "games.dat";
    private JDA jda;
    private TextChannel gameChannel;

    public HashMap<TextChannel, IdleGame> getGames() {
        return this.games;
    }

    public gameHandler(JDA jda, TextChannel gameChannel) {
        this.jda = jda;
        this.gameChannel = gameChannel;
    }

    public void run(){
        IdleGame tempGame = newGame(this.gameChannel);
        if (tempGame == null) {
            tempGame = loadGame(this.gameChannel);
        }
        saveGame(this.gameChannel, tempGame);
        tempGame.startIdleGame();
    }

    public IdleGame newGame(TextChannel gameChannel) {
        if (gameExists(gameChannel)) {
            return null;
        }
        else {
            Message msg = gameChannel.sendMessage("New Game started!").complete();
            return new IdleGame(0, 1, this.jda, msg.getId(), gameChannel.getId(), this);
        }

    }

    public IdleGame loadGame(TextChannel gameChannel) {
        loadGamesFromFile();
        return this.games.get(gameChannel);
    }

    public void saveGame(TextChannel gameChannel, IdleGame game) {
        this.games.put(gameChannel, game);
        saveGamesToFile();
    }

    private boolean gameExists(TextChannel gameChannel) {
        loadGamesFromFile();

        if(games.containsKey(gameChannel)) {
            return true;
        }
        else {
            return false;
        }
    }

    private void saveGamesToFile(){
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, String> outPut = new HashMap<>();
        try {
            for (Map.Entry<TextChannel, IdleGame> game : games.entrySet()) {
                outPut.put(game.getKey().getId(), mapper.writeValueAsString(game.getValue()));
            }
        }
        catch(JsonProcessingException e) {
            e.printStackTrace();
        }

        try {
            ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(fileName)
            );
            out.writeObject(outPut);
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadGamesFromFile(){
        ObjectMapper mapper = new ObjectMapper();

        try {
            ObjectInputStream in = new ObjectInputStream(
                    new FileInputStream(fileName)
            );
            HashMap<String, String> temp = (HashMap<String, String>) in.readObject();
            for (Map.Entry<String, String> entry : temp.entrySet()) {
                IdleGame game = mapper.readValue(entry.getValue(), IdleGame.class);
                game.setJda(this.jda);
                game.setgHandler(this);
                this.games.put(this.jda.getTextChannelById(entry.getKey()), game);
            }
            in.close();
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
