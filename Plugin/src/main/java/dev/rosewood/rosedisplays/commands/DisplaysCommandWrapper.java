package dev.rosewood.rosedisplays.commands;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import java.util.List;

public class DisplaysCommandWrapper extends RoseCommandWrapper {

    public DisplaysCommandWrapper(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public String getDefaultName() {
        return "rosedisplays";
    }

    @Override
    public List<String> getDefaultAliases() {
        return List.of("rd", "displays", "holo", "hologram");
    }

    @Override
    public List<String> getCommandPackages() {
        return List.of("dev.rosewood.rosedisplays.commands");
    }

    @Override
    public boolean includeBaseCommand() {
        return true;
    }

    @Override
    public boolean includeHelpCommand() {
        return true;
    }

    @Override
    public boolean includeReloadCommand() {
        return true;
    }

}
