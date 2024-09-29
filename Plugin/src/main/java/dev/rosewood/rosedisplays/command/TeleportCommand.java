package dev.rosewood.rosedisplays.command;

import dev.rosewood.rosedisplays.argument.DisplaysArgumentHandlers;
import dev.rosewood.rosedisplays.hologram.HologramGroup;
import dev.rosewood.rosedisplays.manager.LocaleManager;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;

public class TeleportCommand extends BaseRoseCommand {

    public TeleportCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, HologramGroup hologram) {
        Player player = (Player) context.getSender();
        if (NMSUtil.isPaper()) {
            player.teleportAsync(hologram.getOrigin());
        } else {
            player.teleport(hologram.getOrigin());
        }
        this.rosePlugin.getManager(LocaleManager.class).sendCommandMessage(context.getSender(), "command-teleport-success", StringPlaceholders.of("name", hologram.key()));
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("teleport")
                .descriptionKey("command-teleport-description")
                .playerOnly()
                .arguments(ArgumentsDefinition.builder()
                        .required("hologram", DisplaysArgumentHandlers.HOLOGRAM_GROUP)
                        .build())
                .build();
    }

}
