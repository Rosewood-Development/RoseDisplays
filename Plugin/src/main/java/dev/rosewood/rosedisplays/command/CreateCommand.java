package dev.rosewood.rosedisplays.command;

import dev.rosewood.rosedisplays.hologram.Hologram;
import dev.rosewood.rosedisplays.hologram.HologramLine;
import dev.rosewood.rosedisplays.hologram.HologramLineType;
import dev.rosewood.rosedisplays.hologram.property.HologramProperty;
import dev.rosewood.rosedisplays.manager.HologramManager;
import dev.rosewood.rosedisplays.manager.LocaleManager;
import dev.rosewood.rosedisplays.model.BillboardConstraint;
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
    public void execute(CommandContext context, String name, String text) {
        Player player = (Player) context.getSender();
        if (text == null)
            text = "New Hologram [" + name + "]";

        LocaleManager localeManager = this.rosePlugin.getManager(LocaleManager.class);
        HologramManager hologramManager = this.rosePlugin.getManager(HologramManager.class);
        if (hologramManager.getHologram(name) != null) {
            localeManager.sendMessage(context.getSender(), "command-create-already-exists", StringPlaceholders.of("name", name));
            return;
        }

        Hologram hologram = hologramManager.createHologram(name, player.getLocation().add(0, 1, 0));
        HologramLine line = new HologramLine(HologramLineType.TEXT);
        line.getProperties().set(HologramProperty.TEXT, text);
        line.getProperties().set(HologramProperty.BILLBOARD_CONSTRAINT, BillboardConstraint.CENTER);
        hologram.addLine(line);

        localeManager.sendMessage(context.getSender(), "command-create-success", StringPlaceholders.of("name", name));
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("create")
                .descriptionKey("command-create-description")
                .permission("rosedisplays.hologram")
                .playerOnly()
                .arguments(ArgumentsDefinition.builder()
                        .required("name", ArgumentHandlers.STRING)
                        .optional("text", ArgumentHandlers.GREEDY_STRING)
                        .build())
                .build();
    }

}
