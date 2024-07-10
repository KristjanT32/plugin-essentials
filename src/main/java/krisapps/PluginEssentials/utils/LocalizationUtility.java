package krisapps.PluginEssentials.utils;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class LocalizationUtility {

    String currentLanguage = "en-US";
    File languageFile;
    FileConfiguration lang;


    JavaPlugin plugin;
    FileConfiguration localizationConfig;


    public LocalizationUtility(JavaPlugin plugin, FileConfiguration localizationConfig) {
        this.plugin = plugin;
        this.localizationConfig = localizationConfig;
    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }

    public FileConfiguration getCurrentLanguageFile() {
        return lang;
    }

    public FileConfiguration getDefaultLanguageFile() {
        File out = new File(Path.of(plugin.getDataFolder() + "/core-data/temp.yml").toString());
        try {
            FileUtils.copyInputStreamToFile(plugin.getResource("en-US.yml"), out);
        } catch (IOException e) {
            e.printStackTrace();
            Bukkit.broadcastMessage(ChatColor.RED + "Failed to load internal language file. Error: " + e.getMessage());
        }
        return YamlConfiguration.loadConfiguration(out);
    }

    @SuppressWarnings("DataFlowIssue")
    public void resetDefaultLanguageFile() {
        try {
            Files.delete(Path.of(plugin.getDataFolder().toPath() + "/localization/en-US.yml"));
        } catch (IOException e) {
        }

        try {
            Files.copy(plugin.getResource("en-US.yml"), Path.of(plugin.getDataFolder().toPath() + "/localization/en-US.yml"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Failed to replace en-US.yml with an internal copy: " + e.getMessage());
        }
    }

    public void changeLanguage(String languageCode) {
        this.currentLanguage = languageCode;
        setupCurrentLanguageFile();
    }

    public boolean languageFileExists(String languageCode) {
        return new File(plugin.getDataFolder() + "/localization/" + languageCode + ".yml").exists();
    }

    public List<String> getLanguages() {
        return localizationConfig.getStringList("languages");
    }

    public void setupCurrentLanguageFile() {
        this.lang = null;
        languageFile = new File(plugin.getDataFolder(), "/localization/" + currentLanguage + ".yml");
        lang = new YamlConfiguration();

        try {
            lang.load(languageFile);
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().warning("Failed to load " + languageFile.getName() + " due to: " + e.getMessage());
        }
    }

    /**
     * Gets a string from the currently set language file.
     * @param id The identifier of the string within the localization file.
     * @return The string in the current language.
     */

    public String getLocalizedPhrase(String id) {
        if (lang == null) {
            setupCurrentLanguageFile();
        }
        return lang.getString(id) != null ? lang.getString(id) : "Localized string not found: " + id;
    }


}
