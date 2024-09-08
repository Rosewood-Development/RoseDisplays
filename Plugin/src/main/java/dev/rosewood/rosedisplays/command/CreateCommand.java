package dev.rosewood.rosedisplays.command;

import dev.rosewood.rosedisplays.hologram.DisplayEntityType;
import dev.rosewood.rosedisplays.hologram.Hologram;
import dev.rosewood.rosedisplays.hologram.property.HologramProperties;
import dev.rosewood.rosedisplays.hologram.property.HologramPropertyContainer;
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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CreateCommand extends BaseRoseCommand {

    public CreateCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, String name, DisplayEntityType type) {
        Player player = (Player) context.getSender();

        LocaleManager localeManager = this.rosePlugin.getManager(LocaleManager.class);
        HologramManager hologramManager = this.rosePlugin.getManager(HologramManager.class);
        if (hologramManager.getHologram(name) != null) {
            localeManager.sendMessage(context.getSender(), "command-create-already-exists", StringPlaceholders.of("name", name));
            return;
        }

        Hologram hologram = hologramManager.createDisplayEntityHologram(name, type, player.getLocation().add(0, 1, 0));
        HologramPropertyContainer properties = hologram.getProperties();
        switch (type) {
            case TEXT -> properties.set(HologramProperties.TEXT, "New Hologram [" + name + "]");
            case ITEM -> properties.set(HologramProperties.ITEM, new ItemStack(Material.DIAMOND));
            case BLOCK -> properties.set(HologramProperties.BLOCK_DATA, Material.GRASS_BLOCK.createBlockData());
        }

        properties.set(HologramProperties.BILLBOARD_CONSTRAINT, BillboardConstraint.CENTER);

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
                        .required("type", ArgumentHandlers.forEnum(DisplayEntityType.class))
                        .build())
                .build();
    }

}
