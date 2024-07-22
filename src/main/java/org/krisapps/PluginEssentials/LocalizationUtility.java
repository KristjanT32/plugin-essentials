package org.krisapps.PluginEssentials;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class LocalizationUtility {

    public final String defaultLanguage;
    private String currentLanguage = "en-US";
    private FileConfiguration language;
    
    private Logger logger;


    JavaPlugin plugin;

    File localizationConfigFile;
    FileConfiguration localizationConfig;

    private ArrayList<String> availableLanguages = new ArrayList<>();


    public LocalizationUtility(JavaPlugin plugin, String defaultLanguageCode) {
        this.plugin = plugin;
        this.defaultLanguage = defaultLanguageCode;
        this.localizationConfigFile = new File(plugin.getDataFolder(), "/localization/config.yml");
        this.logger = plugin.getLogger();
    }

    /**
     * Initializes the localization utility.
     */
    public void initialize() {
        if (!localizationConfigFile.getParentFile().exists() || !localizationConfigFile.exists()) {
            try {
                localizationConfigFile.getParentFile().mkdirs();
                localizationConfigFile.createNewFile();
                InputStream configurationFileResource = PluginEssentials.class.getResourceAsStream(
                        "/localization/config.yml");

                if (configurationFileResource == null) {
                    logger.warning("[!] Failed to find the embedded configuration file to copy. That is bad!");
                    return;
                }

                FileUtils.copyInputStreamToFile(configurationFileResource, localizationConfigFile);
            } catch (IOException e) {
                logger.warning("[!] Failed to create a localization configuration file - " + e.getMessage());
                e.printStackTrace();
                return;
            }
        }

        this.localizationConfig = new YamlConfiguration();
        try {
            this.localizationConfig.load(localizationConfigFile);
        } catch (Exception e) {
            logger.warning("[!] Localization initialization failed - " + e.getMessage());
            return;
        }

        // Find all localization files the plugin has.
        findLocalizationFiles();

        this.currentLanguage = localizationConfig.getString("currentLanguage", defaultLanguage);
        loadCurrentLanguage();
    }

    public void findLocalizationFiles() {
        // Localization discovery
        long start = System.currentTimeMillis();
        int unloadableFiles = 0;

        File localizationsDirectory = new File(Path.of(plugin.getDataFolder().toString(), "/localization/").toUri());
        if (localizationsDirectory.listFiles() == null) {
            logger.warning("[!] Localization discovery failed - missing localization directory");
            return;
        }


        for (File localizationFile: Objects.requireNonNull(localizationsDirectory.listFiles())) {
            if (localizationFile.getName().equals("config.yml")) continue;

            YamlConfiguration conf = new YamlConfiguration();
            try {
                conf.load(localizationFile);
            } catch (Exception e) {
                logger.info("[!] " + localizationFile.getName() + " could not be loaded: " + e.getMessage());
                unloadableFiles++;
                continue;
            }

            logger.info("[+] Found language: " + conf.getString("languageName"));
            String filename = localizationFile.getName();
            availableLanguages.add(filename.substring(0, filename.lastIndexOf(".")));
            localizationConfig.set("languages", availableLanguages);
            try {
                localizationConfig.save(localizationConfigFile);
            } catch (IOException e) {
                logger.warning("[!] Couldn't apply localization config changes: " + e.getMessage());
            }
        }

        logger.info("Localization discovery completed in " + (System.currentTimeMillis() - start) + "ms - found " + availableLanguages.size() + " localization file(s)" + (unloadableFiles > 0 ? ", failed to load an additional " + unloadableFiles + " file(s)." : ""));

    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }

    public FileConfiguration getCurrentLanguageConfig() {
        return language;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
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

    /**
     * Deletes the default language file from the localization folder, and replaces it with a fresh copy
     * from the plugin's resources folder.
     */
    public void resetDefaultLanguageFile() throws FileNotFoundException {

        InputStream embeddedFile = plugin.getResource("localization/" + defaultLanguage + ".yml");

        if (embeddedFile == null) {
            throw new FileNotFoundException("The plugin does not provide a default language file!");
        }

        try {
            Files.delete(Path.of(plugin.getDataFolder().toPath() + "/localization/" + defaultLanguage + ".yml"));
        } catch (IOException e) {
            logger.warning("Failed to delete the existing default language file: " + e.getMessage());
        }

        try {
            Files.copy(embeddedFile, Path.of(plugin.getDataFolder().toPath() + "/localization/" + defaultLanguage + ".yml"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.warning("Failed to copy the replacement localization file: " + e.getMessage());
        }
    }

    public void changeLanguage(String languageCode) {
        this.currentLanguage = languageCode;
        loadCurrentLanguage();
    }

    public boolean languageFileExists(String languageCode) {
        return new File(plugin.getDataFolder() + "/localization/" + languageCode + ".yml").exists();
    }

    public List<String> getRecognizedLanguages() {
        return localizationConfig.getStringList("languages");
    }

    public void loadCurrentLanguage() {
        if (this.currentLanguage == null) {
            this.currentLanguage = defaultLanguage;
        }

        File currentLanguageFile = new File(plugin.getDataFolder(), "/localization/" + currentLanguage + ".yml");
        language = new YamlConfiguration();

        try {
            language.load(currentLanguageFile);
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().warning("[!] Failed to load " + currentLanguageFile.getName() + ": " + e.getMessage());
        }
    }

    /**
     * Gets a string from the currently set language file.
     * @param id The identifier of the string within the localization file.
     * @return The string in the current language.
     */

    public String getLocalizedString(String id) {
        if (currentLanguage == null) {
            loadCurrentLanguage();
        }
        return language.getString(id) != null ? language.getString(id) : "[localized string '" + id + "' not found]";
    }



}
