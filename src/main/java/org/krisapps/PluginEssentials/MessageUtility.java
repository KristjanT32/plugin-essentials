package org.krisapps.PluginEssentials;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MessageUtility {

    private JavaPlugin plugin;

    public MessageUtility(JavaPlugin plugin) {
        this.plugin = plugin;
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
     * Send a message (with color codes) to a player using BaseComponents
     *
     * @param target  the player who receives the message
     * @param messageComponents the message components to send
     * @
     */

    // TODO: make it work, currently does not
    public void sendComplexMessage(CommandSender target, BaseComponent... messageComponents) {
        List<BaseComponent> out = new ArrayList<>();

        for (BaseComponent com: messageComponents) {
            if (com instanceof TextComponent) {
                TextComponent finalComponent = new TextComponent();

                String text = ChatColor.translateAlternateColorCodes('&', ((TextComponent) com).getText());
                for (BaseComponent c: TextComponent.fromLegacyText(text)) {
                    finalComponent.addExtra(c);
                }

                if (com.getHoverEvent() != null) {
                    // Copy all contents (but now colorized) to the new content list.
                    List<Content> colorizedHoverEventContents = new ArrayList<>();
                    for (Content content : com.getHoverEvent().getContents()) {
                        if (content instanceof Text) {
                            Object textContent = ((Text) content).getValue();
                            if (textContent instanceof TextComponent) {
                                colorizedHoverEventContents.add(new Text(ChatColor.translateAlternateColorCodes('&',
                                        ((TextComponent) textContent).getText()
                                )));
                            } else if (textContent instanceof String) {
                                colorizedHoverEventContents.add(new Text(ChatColor.translateAlternateColorCodes('&',
                                        textContent.toString()
                                )));
                            }
                        }
                    }
                    finalComponent.setHoverEvent(new HoverEvent(com.getHoverEvent().getAction(), colorizedHoverEventContents));
                }

                if (com.getClickEvent() != null) {
                    finalComponent.setClickEvent(com.getClickEvent());
                }
                out.add(finalComponent);
            }
        }



        target.spigot().sendMessage(out.toArray(new BaseComponent[0]));
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
