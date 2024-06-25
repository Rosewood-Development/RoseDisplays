package dev.rosewood.rosedisplays.command;

import dev.rosewood.rosedisplays.argument.DisplaysArgumentHandlers;
import dev.rosewood.rosedisplays.hologram.Hologram;
import dev.rosewood.rosedisplays.hologram.HologramLine;
import dev.rosewood.rosedisplays.hologram.property.HologramProperty;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;

public class EditCommand extends BaseRoseCommand {

    public EditCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, Hologram hologram, HologramLine line) {

    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("edit")
                .descriptionKey("command-edit-description")
                .permission("rosedisplays.hologram")
                .playerOnly()
                .arguments(ArgumentsDefinition.builder()
                        .required("hologram", DisplaysArgumentHandlers.HOLOGRAM)
                        .required("line", DisplaysArgumentHandlers.HOLOGRAM_LINE)
                        .optionalSub(
                                new SetCommand(this.rosePlugin),
                                new UnsetCommand(this.rosePlugin)
                        ))
                .build();
    }

    public static class SetCommand extends BaseRoseCommand {

        public SetCommand(RosePlugin rosePlugin) {
            super(rosePlugin);
        }

        @RoseExecutable
        public <T> void execute(CommandContext context, Hologram hologram, HologramLine line, HologramProperty<T> property, T value) {
            line.getProperties().set(property, value);
        }

        @Override
        protected CommandInfo createCommandInfo() {
            return CommandInfo.builder("set")
                    .arguments(ArgumentsDefinition.builder()
                            .required("property", DisplaysArgumentHandlers.HOLOGRAM_PROPERTY)
                            .required("value", DisplaysArgumentHandlers.HOLOGRAM_PROPERTY_VALUE)
                            .build())
                    .build();
        }

    }

    public static class UnsetCommand extends BaseRoseCommand {

        public UnsetCommand(RosePlugin rosePlugin) {
            super(rosePlugin);
        }

        @RoseExecutable
        public void execute(CommandContext context, Hologram hologram, HologramLine line, HologramProperty<?> property) {
            line.getProperties().unset(property);
        }

        @Override
        protected CommandInfo createCommandInfo() {
            return CommandInfo.builder("unset")
                    .arguments(ArgumentsDefinition.builder()
                            .required("property", DisplaysArgumentHandlers.HOLOGRAM_PROPERTY)
                            .build())
                    .build();
        }

    }

}
