package dev.rosewood.rosedisplays.text;

import dev.rosewood.rosegarden.hook.PlaceholderAPIHook;
import dev.rosewood.rosegarden.utils.HexUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public interface RenderedText {

    String getNext(Player player);

    default BaseComponent[] getNextComponents(Player player) {
        return TextComponent.fromLegacyText(this.getNext(player));
    }

    static RenderedText parse(String text) {
        return new SimpleRenderedText(text);
    }

    class SimpleRenderedText implements RenderedText {

        private final String text;

        private SimpleRenderedText(String text) {
            this.text = text;
        }

        @Override
        public String getNext(Player player) {
            return HexUtils.colorify(PlaceholderAPIHook.applyPlaceholders(player, this.text));
        }

    }

}
