package dev.rosewood.rosedisplays.argument;

import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemStackArgumentHandler extends ArgumentHandler<ItemStack> {

    private static final Map<String, Material> ITEMS = Arrays.stream(Material.values())
            .filter(Material::isItem)
            .collect(Collectors.toUnmodifiableMap(x -> x.getKey().getKey(), Function.identity()));
    private static final List<String> ITEM_NAMES = List.copyOf(ITEMS.keySet());

    public ItemStackArgumentHandler() {
        super(ItemStack.class);
    }

    @Override
    public ItemStack handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        String input = inputIterator.next();

        Material material = ITEMS.get(input.toLowerCase());
        if (material == null)
            throw new HandledArgumentException("argument-handler-itemstack", StringPlaceholders.of("input", input));

        return new ItemStack(material);
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        return ITEM_NAMES;
    }

}
