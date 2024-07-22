package org.krisapps.PluginEssentials;

import org.bukkit.plugin.java.JavaPlugin;

public class PluginEssentials {

    public final LocalizationUtility localization;
    public final MessageUtility messages;

    public PluginEssentials(JavaPlugin plugin, String defaultLanguage) {
        this.messages = new MessageUtility(plugin);
        this.localization = new LocalizationUtility(plugin, defaultLanguage);
    }



}
