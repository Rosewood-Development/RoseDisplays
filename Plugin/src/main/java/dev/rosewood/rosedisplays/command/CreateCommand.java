package dev.rosewood.rosedisplays.command;

import dev.rosewood.rosedisplays.argument.DisplaysArgumentHandlers;
import dev.rosewood.rosedisplays.hologram.Hologram;
import dev.rosewood.rosedisplays.hologram.HologramType;
import dev.rosewood.rosedisplays.manager.HologramManager;
import dev.rosewood.rosedisplays.manager.LocaleManager;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;

public class CreateCommand extends BaseRoseCommand {

    public CreateCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, String name, HologramType type) {
        LocaleManager localeManager = this.rosePlugin.getManager(LocaleManager.class);
        HologramManager hologramManager = this.rosePlugin.getManager(HologramManager.class);
        Player player = (Player) context.getSender();

        Hologram hologram = hologramManager.createHologram(name, type, player.getLocation().add(0, 1, 0));
        if (hologram != null) {
            localeManager.sendMessage(context.getSender(), "command-create-success", StringPlaceholders.of("name", name));
        } else {
            localeManager.sendMessage(context.getSender(), "command-create-already-exists", StringPlaceholders.of("name", name));
        }
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("create")
                .descriptionKey("command-create-description")
                .permission("rosedisplays.hologram")
                .playerOnly()
                .arguments(ArgumentsDefinition.builder()
                        .required("name", ArgumentHandlers.STRING)
                        .required("type", DisplaysArgumentHandlers.HOLOGRAM_TYPE)
                        .build())
                .build();
    }

}
