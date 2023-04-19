//package dev.rosewood.rosedisplays.commands.command;
//
//import dev.rosewood.rosedisplays.hologram.Hologram;
//import dev.rosewood.rosedisplays.manager.LocaleManager;
//import dev.rosewood.rosedisplays.hologram.property.HologramProperty;
//import dev.rosewood.rosegarden.RosePlugin;
//import dev.rosewood.rosegarden.command.framework.CommandContext;
//import dev.rosewood.rosegarden.command.framework.RoseCommand;
//import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
//import dev.rosewood.rosegarden.command.framework.RoseSubCommand;
//import dev.rosewood.rosegarden.command.framework.annotation.Inject;
//import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
//
//public class EditCommand extends RoseCommand {
//
//    public EditCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
//        super(rosePlugin, parent);
//    }
//
//    @RoseExecutable
//    public void execute(CommandContext context, Hologram hologram, int line, RoseSubCommand subCommand) {
//        this.rosePlugin.getManager(LocaleManager.class).sendMessage(context.getSender(), "command-edit-usage");
//    }
//
//    @Override
//    protected String getDefaultName() {
//        return "edit";
//    }
//
//    @Override
//    public String getDescriptionKey() {
//        return "command-edit-description";
//    }
//
//    @Override
//    public String getRequiredPermission() {
//        return "rosedisplays.hologram";
//    }
//
//    public static class SetCommand extends RoseSubCommand {
//
//        public SetCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
//            super(rosePlugin, parent);
//        }
//
//        @RoseExecutable
//        public void execute(@Inject CommandContext context, @Inject Hologram hologram, @Inject int line, HologramProperty<?> property, String value) {
//
//        }
//
//        @Override
//        protected String getDefaultName() {
//            return "set";
//        }
//
//    }
//
//    public static class UnsetCommand extends RoseSubCommand {
//
//        public UnsetCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
//            super(rosePlugin, parent);
//        }
//
//        @RoseExecutable
//        public void execute(@Inject CommandContext context, @Inject Hologram hologram, @Inject int line, HologramProperty<?> property) {
//
//        }
//
//        @Override
//        protected String getDefaultName() {
//            return "unset";
//        }
//
//    }
//
//}
