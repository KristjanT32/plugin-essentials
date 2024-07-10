package krisapps.PluginEssentials.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class MessageUtility {

    public MessageUtility() {

    }


    /**
     * Send a message (with color codes) to a player.
     * Messages with a placeholder will not get
     * the placeholder replaced.
     *
     * @param target  the player who receives the message
     * @param message the message to send
     * @
     */
    public void sendMessage(CommandSender target, String message) {
        target.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    /**
     * Sends a title with the color codes interpreted.
     *
     * @param target   the player to send the title to.
     * @param title    the title content
     * @param subtitle the subtitle content
     * @param fadeIn   time for the title to fade in for (in ticks)
     * @param stay     time for the title to stay on screen for (in ticks)
     * @param fadeOut  time for the title to fade out for (in ticks)
     */
    public void sendTitle(Player target, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        target.sendTitle(ChatColor.translateAlternateColorCodes('&', title), ChatColor.translateAlternateColorCodes('&', subtitle), fadeIn, stay, fadeOut);
    }

    public void sendActionbarMessage(Player target, String text) {
        target.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', text)));
    }

    public BaseComponent createClickableTeleportButton(String text, Location target, @Nullable String hoverText) {
        BaseComponent[] component = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', text));
        TextComponent out = new TextComponent(component);
        out.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp @p " + target.getBlockX() + " " + target.getBlockY() + " " + target.getBlockZ()));
        if (hoverText != null) {
            out.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.translateAlternateColorCodes('&', hoverText))));
        }
        return out;
    }

    public TextComponent createClickableButton(String text, String command, @Nullable String hoverText) {
        BaseComponent[] component = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', text));
        TextComponent out = new TextComponent(component);
        out.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        if (hoverText != null) {
            out.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.translateAlternateColorCodes('&', hoverText))));
        }
        return out;
    }

}
