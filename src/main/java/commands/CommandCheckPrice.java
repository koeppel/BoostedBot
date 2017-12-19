package commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import priceChecker.PriceChecker;

public class CommandCheckPrice implements Command{

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if(args.length < 3) {
            System.out.println("Not enough arguments!");
        }
        else {
            PriceChecker pc = new PriceChecker(args[0], args[1], Integer.parseInt(args[2]));
            System.out.println(pc.getPrice());
        }
    }

    @Override
    public void executed(boolean sucess, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return null;
    }
}
