package dev.rosewood.rosedisplays.argument;

import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemStackArgumentHandler extends ArgumentHandler<ItemStack> {

    public ItemStackArgumentHandler() {
        super(ItemStack.class);
    }

    @Override
    public ItemStack handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        return new ItemStack(Material.DIAMOND); // TODO
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        return List.of("<unimplemented>"); // TODO
    }

}
