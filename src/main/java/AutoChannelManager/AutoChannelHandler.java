package AutoChannelManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import Utils.CONFIG;

import java.io.*;
import java.util.*;

public class AutoChannelHandler {

    private String fileName;
    private static final String filePath = CONFIG.SAVEPATH;
    private Guild guild;
    private HashMap<String, VoiceChannel> autoChannelHashMap = new HashMap<>();
    private HashMap<VoiceChannel, List<VoiceChannel>> createdChannelsByAutoChannel = new HashMap<>();
    private HashMap<String, VoiceChannel> createdChannelHashMap = new HashMap<>();

    public HashMap<String, VoiceChannel> getAutoChannelHashMap() {
        return this.autoChannelHashMap;
    }

    public HashMap<VoiceChannel, List<VoiceChannel>> getCreatedChannelsByAutoChannel() {
        return createdChannelsByAutoChannel;
    }

    public HashMap<String, VoiceChannel> getCreatedChannelHashMap() {
        return createdChannelHashMap;
    }

    public void addCreatedChannel(VoiceChannel vc, VoiceChannel createdChannel) {
        try {
            if(!createdChannelsByAutoChannel.get(vc).contains(createdChannel)) {
                List<VoiceChannel> createdChannels = new ArrayList<VoiceChannel>();
                createdChannels = createdChannelsByAutoChannel.get(vc);
                createdChannels.add(createdChannel);

                createdChannelHashMap.put(createdChannel.getId(), createdChannel);
                createdChannelsByAutoChannel.put(vc, createdChannels);
            }
        }
        catch(NullPointerException e) {
            List<VoiceChannel> createdChannels = new ArrayList<VoiceChannel>();
            createdChannels.add(createdChannel);

            createdChannelHashMap.put(createdChannel.getId(), createdChannel);
            createdChannelsByAutoChannel.put(vc, createdChannels);
        }
        saveAutoChannelsToFile();
    }

    public void dropCreatedChannel(VoiceChannel createdChannel) {
        createdChannelHashMap.remove(createdChannel.getId(), createdChannel);

        for (VoiceChannel vc : createdChannelsByAutoChannel.keySet()) {
            if(createdChannelsByAutoChannel.get(vc).contains(createdChannel)) {
                createdChannelsByAutoChannel.get(vc).remove(createdChannel);
            }
        }
        saveAutoChannelsToFile();
    }

    public void addAutoChannel(VoiceChannel vc) {
        if(!autoChannelHashMap.containsKey(vc.getId()) || !autoChannelHashMap.containsValue(vc)) {
            autoChannelHashMap.put(vc.getId(), vc);
        }
        saveAutoChannelsToFile();
    }

    public void dropAutoChannel(VoiceChannel vc) {
        if(autoChannelHashMap.containsKey(vc.getId()) || autoChannelHashMap.containsValue(vc)) {
            autoChannelHashMap.remove(vc.getId(), vc);
        }
        saveAutoChannelsToFile();
    }

    public VoiceChannel getAutoChannelByCreatedChannel(VoiceChannel createdChannel) {
        for (VoiceChannel vc : createdChannelsByAutoChannel.keySet()) {
            if(createdChannelsByAutoChannel.get(vc).equals(createdChannel)) {
                return vc;
            }
        }
        return null;
    }

    public void saveAutoChannelsToFile() {
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, String> outPut = new HashMap<>();
        try {
            for (Map.Entry<String, VoiceChannel> msg : autoChannelHashMap.entrySet()) {
                outPut.put(msg.getKey(), mapper.writeValueAsString(msg.getValue().getId()));
            }
        }
        catch(JsonProcessingException e) {
            e.printStackTrace();
        }
        try {
            ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream(this.fileName)
            );
            out.writeObject(outPut);
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAutoChannelsFromFile() {
        try {
            ObjectInputStream in = new ObjectInputStream(
                    new FileInputStream(this.fileName)
            );
            HashMap<String, String> temp = (HashMap<String, String>) in.readObject();
            for (Map.Entry<String, String> entry : temp.entrySet()) {
                VoiceChannel vc = guild.getVoiceChannelById(entry.getKey());
                autoChannelHashMap.put(vc.getId(), vc);
                System.out.println("Loaded AutoChannel: " + vc.getId() + " in Guild: " + guild.getName());
            }
            in.close();
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public AutoChannelHandler(Guild guild) {
        this.guild = guild;
        this.fileName = filePath + guild.getId() + "/autochannels.dat";
    }
}
