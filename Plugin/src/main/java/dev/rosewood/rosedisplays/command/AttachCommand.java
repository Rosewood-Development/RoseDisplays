package dev.rosewood.rosedisplays.command;

import dev.rosewood.rosedisplays.argument.DisplaysArgumentHandlers;
import dev.rosewood.rosedisplays.hologram.Hologram;
import dev.rosewood.rosedisplays.manager.LocaleManager;
import dev.rosewood.rosedisplays.nms.NMSAdapter;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.List;
import org.bukkit.entity.Player;

public class AttachCommand extends BaseRoseCommand {

    public AttachCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, Hologram hologram, Player player) {
        if (player == null)
            player = (Player) context.getSender();

        NMSAdapter.getHandler().sendHologramSetVehiclePacket(hologram, player, List.of(player));
        this.rosePlugin.getManager(LocaleManager.class).sendCommandMessage(context.getSender(), "command-attach-success", StringPlaceholders.of("name", hologram.getName(), "player", player.getName()));
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("attach")
                .descriptionKey("command-attach-description")
                .playerOnly()
                .arguments(ArgumentsDefinition.builder()
                        .required("hologram", DisplaysArgumentHandlers.HOLOGRAM)
                        .optional("player", ArgumentHandlers.PLAYER)
                        .build())
                .build();
    }

}
