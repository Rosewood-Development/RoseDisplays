package dev.rosewood.rosedisplays.argument;

import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

public class BlockDataArgumentHandler extends ArgumentHandler<BlockData> {

    public BlockDataArgumentHandler() {
        super(BlockData.class);
    }

    @Override
    public BlockData handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        return Material.GRASS_BLOCK.createBlockData(); // TODO
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        return List.of("<unimplemented>"); // TODO
    }

}
