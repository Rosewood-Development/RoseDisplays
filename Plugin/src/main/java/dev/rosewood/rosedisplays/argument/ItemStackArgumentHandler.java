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
import java.util.stream.Stream;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ItemStackArgumentHandler extends ArgumentHandler<ItemStack> {

    private static final Map<String, Material> ITEMS = Arrays.stream(Material.values())
            .filter(Material::isItem)
            .collect(Collectors.toUnmodifiableMap(x -> x.getKey().getKey(), Function.identity()));
    private static final List<String> ITEM_NAMES = List.copyOf(ITEMS.keySet());
    private static final Map<String, EquipmentSlot> SLOTS = Arrays.stream(EquipmentSlot.values())
            .collect(Collectors.toMap(x -> x == EquipmentSlot.CHEST ? "chestplate" : x.name().toLowerCase(), Function.identity()));
    private static final List<String> ITEM_NAMES_AND_SLOTS = Stream.concat(ITEM_NAMES.stream(), SLOTS.keySet().stream()).toList();

    public ItemStackArgumentHandler() {
        super(ItemStack.class);
    }

    @Override
    public ItemStack handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        String input = inputIterator.next();

        EquipmentSlot slot = SLOTS.get(input.toLowerCase());
        if (slot != null && context.getSender() instanceof Player player) {
            ItemStack item = player.getInventory().getItem(slot);
            if (item.getType() == Material.AIR)
                throw new HandledArgumentException("argument-handler-itemstack-slot", StringPlaceholders.of("input", input));
        }

        Material material = ITEMS.get(input.toLowerCase());
        if (material == null)
            throw new HandledArgumentException("argument-handler-itemstack", StringPlaceholders.of("input", input));

        return new ItemStack(material);
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        if (context.getSender() instanceof Player) {
            return ITEM_NAMES_AND_SLOTS;
        } else {
            return ITEM_NAMES;
        }
    }

}
