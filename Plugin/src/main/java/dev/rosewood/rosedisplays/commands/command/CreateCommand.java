package dev.rosewood.rosedisplays.commands.command;

import dev.rosewood.rosedisplays.hologram.Hologram;
import dev.rosewood.rosedisplays.hologram.HologramLine;
import dev.rosewood.rosedisplays.hologram.HologramLineType;
import dev.rosewood.rosedisplays.hologram.property.HologramProperty;
import dev.rosewood.rosedisplays.manager.HologramManager;
import dev.rosewood.rosedisplays.manager.LocaleManager;
import dev.rosewood.rosedisplays.model.BillboardConstraint;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;

public class CreateCommand extends RoseCommand {

    public CreateCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, String name) {
        if (!(context.getSender() instanceof Player player))
            throw new IllegalStateException();

        LocaleManager localeManager = this.rosePlugin.getManager(LocaleManager.class);
        HologramManager hologramManager = this.rosePlugin.getManager(HologramManager.class);
        if (hologramManager.getHologram(name) != null) {
            localeManager.sendMessage(context.getSender(), "command-create-already-exists", StringPlaceholders.of("name", name));
            return;
        }

        Hologram hologram = hologramManager.createHologram(name, player.getLocation().add(0, 1, 0));
        HologramLine line = new HologramLine(HologramLineType.TEXT, hologram.getLocation());
        line.setProperty(HologramProperty.TEXT, "New Hologram");
        line.setProperty(HologramProperty.BILLBOARD_CONSTRAINT, BillboardConstraint.CENTER);
        hologram.addLine(line);

        localeManager.sendMessage(context.getSender(), "command-create-success", StringPlaceholders.of("name", name));
    }

    @Override
    protected String getDefaultName() {
        return "create";
    }

    @Override
    public String getDescriptionKey() {
        return "command-create-description";
    }

    @Override
    public String getRequiredPermission() {
        return "rosedisplays.hologram";
    }

}
