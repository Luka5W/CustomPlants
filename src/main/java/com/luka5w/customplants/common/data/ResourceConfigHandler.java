package com.luka5w.customplants.common.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;

public class ResourceConfigHandler {
    
    private static final Pattern PATTERN_CUSTOM_JSON = Pattern.compile("([a-z0-9]+_)*[a-z0-9]+\\.json$");
    private static final Pattern PATTERN_CUSTOM_SOUND = Pattern.compile("^([a-z0-9]+_)*[a-z0-9]+\\.(ogg)$");
    private static final Pattern PATTERN_CUSTOM_TEXTS = Pattern.compile("^([a-z0-9]+_)*[a-z0-9]+\\.(png)$");
    private static final Pattern PATTERN_LANG = Pattern.compile("^[a-z0-9]{2}_[a-z0-9]{2}\\.lang$");
    private final Logger logger;
    
    /** Directory for all custom effects. */
    private final File dirEffects;
    
    /** Directory for all custom lang files (translations). */
    private final File dirLangs;
    
    /** Directory for all custom plants (of any type). */
    private final File dirPlants;
    
    /** Directory for all custom sounds. */
    private final File dirSounds;
    
    /** Directory for all custom textures. */
    private final File dirTexts;
    
    /** All translations. */
    private HashMap<String, HashMap<String, String>> langs;
    private ArrayList<Plant> plants;
    
    public ResourceConfigHandler(Logger logger) {
        this.logger = logger;
        this.dirEffects = new File(MainConfig.dirResources, "effects");
        this.dirLangs = new File(MainConfig.dirResources, "lang");
        this.dirPlants = new File(MainConfig.dirResources, "plants");
        this.dirSounds = new File(MainConfig.dirResources, "sounds");
        this.dirTexts = new File(MainConfig.dirResources, "textures");
    
    
        if (this.dirEffects.mkdirs()) this.copyResources("effects", "README.md", "example.json");
        if (this.dirLangs.mkdirs()) this.copyResources("langs", "README.md", "en_us.lang");
        if (this.dirPlants.mkdirs()) this.copyResources("plants", "README.md", "example.json");
        if (this.dirSounds.mkdirs()) this.copyResources("sounds", "README.md", "example.ogg");
        if (this.dirTexts.mkdirs()) this.copyResources("textures", "README.md", "example.png");
        
        this.readConfigs();
    }
    
    private void copyResources(String dir, String... res) {
        for (int i = 0; i < res.length; i++) {
            try {
                this.copyResource(dir, res[i]);
            }
            catch (IOException e) {
                this.logger.error("Exception while creating configuration directories and copying ReadMes and Example configurations: " + e.getMessage());
            }
        }
    }
    
    private void copyResource(String dir, String res) throws IOException, FileNotFoundException {
        InputStream in = this.getClass().getResourceAsStream("resources" + File.pathSeparator + dir + File.pathSeparator + res);
        if (in == null) throw new FileNotFoundException("Missing resource file in JAR");
        Files.copy(in, Paths.get(MainConfig.dirResources, dir), StandardCopyOption.REPLACE_EXISTING);
    }
    
    /**
     * Reads configuration into usable data.
     */
    private void readConfigs() {
        File[] effects = this.scanDir(this.dirEffects, PATTERN_CUSTOM_JSON);
        File[] langs = this.scanDir(this.dirLangs, PATTERN_LANG);
        File[] plants = this.scanDir(this.dirPlants, PATTERN_CUSTOM_JSON);
        File[] sounds = this.scanDir(this.dirSounds, PATTERN_CUSTOM_SOUND);
        File[] texts = this.scanDir(this.dirTexts, PATTERN_CUSTOM_TEXTS);
        
        this.langs = new HashMap<>();
        for (File lang : langs) {
            String langKey = lang.getName().split("\\.")[0];
            try {
                ArrayList<String> translations = FileUtils.readFileAsList(lang);
                for (String translation : translations) {
                    String[] pair = translation.split("=");
                    if (!this.langs.containsKey(langKey)) this.langs.put(langKey, new HashMap<>());
                    this.langs.get(langKey).put(pair[0], pair[1]);
                }
            }
            catch (ArrayIndexOutOfBoundsException | IOException e) {
                this.logger.error("Error while reading langs/{}: {}", langKey, e.getMessage());
            }
        }
    
        this.plants = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        for (File plant : plants) {
            String plantName = plant.getName().split("\\.")[0];
            try {
                Plant plant1 = mapper.readValue(plant, Plant.class);
                plant1.setName(plantName);
                this.plants.add(plant1);
            }
            catch (IOException e) {
                this.logger.error("Error while reading plants/{}: {}", plantName, e.getMessage());
            }
        }
    }
    
    /**
     * Scans the passed directory.
     * @param dir The directory to scan
     * @param pattern The pattern, scanned files need to match to
     * @return The files in the scanned directory matching the passed pattern
     */
    private File[] scanDir(File dir, Pattern pattern) {
        return dir.listFiles(file -> pattern.matcher(file.getName()).matches());
    }
    
    /**
     * Returns the translation for the passed (fallback) language code and translation key.
     * If neither lang nor fallbackLang contains a translation, the passed key is returned.
     * @param lang The language code to get the translation for (e.g. `en_us`)
     * @param fallbackLang The language code to use as fallback
     * @param key The translation key
     * @return The translation value or the key if no value was found
     */
    public String getTranslation(String lang, @Nullable String fallbackLang, String key) {
        if (this.langs.containsKey(lang) && this.langs.get(lang).containsKey(key)) return this.langs.get(lang).get(key);
        if (fallbackLang != null && this.langs.containsKey(fallbackLang) && this.langs.get(fallbackLang).containsKey(key)) {
            this.logger.warn("Attempted to retrieve the translation for {} in {} but the language and/ or key was not found. Using fallback language ({}).", lang, key, fallbackLang);
            return this.langs.get(fallbackLang).get(key);
        }
        else {
            this.logger.warn("Attempted to retrieve the translation for {} in {} but the language and/ or key was not found.", lang, key);
            return key;
        }
    }
    
    /**
     * Returns the plant which has the passed name or null if none exists.
     * @param name The name of the plant to return
     * @return The plant with the passed name
     */
    @Nullable
    public Plant getPlant(String name) {
        return this.plants.stream().filter(it -> it.getName() == name).findFirst().orElse(null);
    }
    
    public Iterator<Plant> getPlants() {
        return this.plants.iterator();
    }
}
