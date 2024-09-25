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
import org.bukkit.block.data.BlockData;

public class BlockDataArgumentHandler extends ArgumentHandler<BlockData> {

    private static final Map<String, Material> BLOCKS = Arrays.stream(Material.values())
            .filter(Material::isBlock)
            .collect(Collectors.toUnmodifiableMap(x -> x.getKey().getKey(), Function.identity()));
    private static final List<String> BLOCK_NAMES = List.copyOf(BLOCKS.keySet());

    public BlockDataArgumentHandler() {
        super(BlockData.class);
    }

    @Override
    public BlockData handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        String input = inputIterator.next();

        int bracketIndex = input.indexOf("[");

        Material material = BLOCKS.get(input.toLowerCase());
        if (material == null)
            throw new HandledArgumentException("argument-handler-blockdata", StringPlaceholders.of("input", input));

        return material.createBlockData("");
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        return BLOCK_NAMES;
    }

}
