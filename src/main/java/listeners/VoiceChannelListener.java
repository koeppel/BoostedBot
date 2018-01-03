package listeners;

import autoChannelManager.AutoChannelHandler;
import commands.CommandAutoChannel;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class VoiceChannelListener extends ListenerAdapter {
    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        Guild guild = event.getGuild();
        AutoChannelHandler ach = CommandAutoChannel.getAutoChannelHandler(guild);
        VoiceChannel vc = event.getChannelJoined();

        joinVoiceChannel(vc, ach, guild, event.getMember());

        super.onGuildVoiceJoin(event);
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        AutoChannelHandler ach = CommandAutoChannel.getAutoChannelHandler(event.getGuild());
        VoiceChannel vc = event.getChannelLeft();

        leaveVoiceChannel(vc, ach);

        super.onGuildVoiceLeave(event);
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        AutoChannelHandler ach = CommandAutoChannel.getAutoChannelHandler(event.getGuild());
        VoiceChannel newChannel = event.getChannelJoined();
        VoiceChannel oldChannel = event.getChannelLeft();

        leaveVoiceChannel(oldChannel, ach);
        joinVoiceChannel(newChannel, ach, event.getGuild(), event.getMember());

        super.onGuildVoiceMove(event);
    }

    @Override
    public void onVoiceChannelDelete(VoiceChannelDeleteEvent event) {
        AutoChannelHandler ach = CommandAutoChannel.getAutoChannelHandler(event.getGuild());
        VoiceChannel vc = event.getChannel();

        if(ach.getAutoChannelHashMap().containsValue(vc)) {
            ach.dropAutoChannel(vc);
        }

        super.onVoiceChannelDelete(event);
    }

    private void joinVoiceChannel(VoiceChannel vc, AutoChannelHandler ach, Guild guild, Member member){
        if(ach.getAutoChannelHashMap().containsValue(vc)) {
            int amount;

            try {
                amount = ach.getCreatedChannelsByAutoChannel().get(vc).size();
            }
            catch (NullPointerException e) {
                amount = 0;
            }

            VoiceChannel createdChannel;

            createdChannel = (VoiceChannel) guild.getController().createVoiceChannel(vc.getName() + " [" + (amount + 1) + "]")
                    .setBitrate(vc.getBitrate())
                    .setUserlimit(vc.getUserLimit())
                    .complete();

            if(vc.getParent() != null) {
                createdChannel.getManager().setParent(vc.getParent()).complete();
            }

            guild.getController().modifyVoiceChannelPositions().selectPosition(createdChannel).moveTo(vc.getPosition() + 1).queue();
            guild.getController().moveVoiceMember(member, createdChannel).queue();

            ach.addCreatedChannel(vc, createdChannel);
        }
    }

    private void leaveVoiceChannel(VoiceChannel vc, AutoChannelHandler ach) {
        if(ach.getCreatedChannelHashMap().containsValue(vc) && vc.getMembers().isEmpty()) {
            ach.dropCreatedChannel(vc);
            vc.delete().queue();
        }
    }
}
