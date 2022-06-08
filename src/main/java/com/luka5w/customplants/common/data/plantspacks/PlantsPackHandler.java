package com.luka5w.customplants.common.data.plantspacks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.luka5w.customplants.common.data.FileUtils;
import com.luka5w.customplants.common.data.plantspacks.plants.Config;
import com.luka5w.customplants.common.data.plantspacks.plants.Type;
import com.luka5w.customplants.common.data.plantspacks.plants.serialization.Serializer;
import com.luka5w.customplants.common.util.Registry;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class PlantsPackHandler {
    
    protected static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(PlantsPackMeta.class, new PlantsPackMeta.Serializer())
            .setPrettyPrinting()
            .create();
    protected static final Pattern PATTERN_PLANT_FILE = Pattern.compile("^[a-z]([a-z\\d_]*[a-z\\d])?\\.json$");
    protected static final Pattern PATTERN_TREE_FILE = Pattern.compile("^[a-z]([a-z\\d_]*[a-z\\d])?\\.tree.txt$");
    protected static final boolean ZIP_SEPARATOR_DIFFERENT = !"/".equals(File.separator);
    protected final Logger logger;
    protected final File packsDir;
    protected final HashMap<PlantsPackMeta, File> packs;
    private final Registry registry;
    private final int maxDrops;
    private ArrayList<Config> plantConfigs;
    
    public PlantsPackHandler(Logger logger, File packsDir, Registry registry, int maxDrops) {
        this.logger = logger;
        this.packsDir = packsDir;
        this.registry = registry;
        this.maxDrops = maxDrops;
        this.packs = new HashMap<>();
    }
    
    /**
     * Checks and loads all packs in the specified directory.
     * @throws IllegalStateException When the method is called a second time.
     */
    public void loadPacks() throws IllegalStateException {
        if (this.packs.size() != 0) throw new IllegalStateException("loadPacks has already been called");
        File[] packFiles = this.packsDir.listFiles();
        if (packFiles == null || packFiles.length == 0) {
            this.logger.info("No plants packs found in location " + this.packsDir);
        }
        else {
            this.logger.info("Loading plants packs from location " + this.packsDir);
            PlantsPackMeta[] packs = new PlantsPackMeta[packFiles.length];
            ArrayList<String> ids = new ArrayList<>();
            // load metadata of all packs and ensure no packs with same ID are loaded
            for (int i = 0; i < packFiles.length; i++) {
                try {
                    String metaFile = readPackFile(packFiles[i], "pack.mcmeta");
                    PlantsPackMeta pack = GSON.fromJson(metaFile, PlantsPackMeta.class);
                    int index = ids.indexOf(pack.getID());
                    if (index == -1) {
                        packs[i] = pack;
                        ids.add(pack.getID());
                    } else {
                        packs[i] = null;
                        this.logger.warn("Ignoring pack with duplicate ID {}: (Files: '{}' '{}') ", pack.getID(), packFiles[index], packFiles[i]);
                    }
                }
                catch (IOException e) {
                    this.logger.error("Unable to load pack: ", e);
                }
            }
            // check if dependencies for packs are loaded. if so, load the packs
            for (int i = 0; i < packFiles.length; i++) {
                if (packs[i] == null) continue; // skip duplicates
                // check if all dependencies (mods, other packs) for the preloaded pack are present.
                try {
                    List<String> missingPacks = new ArrayList<>();
                    for (String res : packs[i].getRequiredPacks()) if (!ids.contains(res)) missingPacks.add(res);
                    if (missingPacks.size() != 0) throw new MissingResourcesException(packs[i], "Mod(s)", missingPacks);
                    List<String> missingMods = new ArrayList<>();
                    // TODO: 26.04.22 is this working? Loader#isModLoaded docs says the argument is modNAME not ID...
                    for (String res : packs[i].getRequiredMods()) if (!Loader.isModLoaded(res)) missingMods.add(res);
                    if (missingMods.size() != 0) throw new MissingResourcesException(packs[i], "Mod(s)", missingMods);
                } catch (MissingResourcesException e) {
                    this.logger.warn(e.getMessage());
                    continue;
                }
                try {
                    this.loadPacks_plants(packs[i], packFiles[i]);
                }
                catch (IOException e) {
                    this.logger.error("IOException while loading plant pack: ", e);
                }
                    this.registerResourcePack(packFiles[i]);
            }
            this.refreshResources();
            this.logger.info("Finished loading plants packs");
        }
    }
    
    /**
     * Loads the plants pack. Outsourced from {@link #loadPacks()} for better readability.
     * @param pack The metadata of the pack.
     * @param packFile The resources of the pack on disk.
     * @throws IOException If an I/O Error occurs in {@link #packFileExists(File, String)}, {@link #readPackFilePaths(File, String, FileNameFilter)}
     */
    protected void loadPacks_plants(PlantsPackMeta pack, File packFile) throws IOException {
        this.logger.info("Loading plants pack from location " + packFile);
        if (!packFileExists(packFile, "customplants")) {
            this.logger.warn("Empty plants pack detected: ID: {} File: {}", pack.getID(), packFile.getName());
            return;
        }
        // treeConfigs[<int/version[0]>].<name>[<variant>]>
        ArrayList<HashMap<String, String>> treeConfigs = new ArrayList<>();
        if (packFileExists(packFile, "customplants/trees")) {
            List<String> configPaths = readPackFilePaths(packFile, "customplants/trees", (filename) -> PATTERN_TREE_FILE.matcher(filename).matches());
            treeConfigs.add(new HashMap<>());
            if (configPaths != null) {
                for (String configPath : configPaths) {
                    String name = FilenameUtils.getBaseName(FilenameUtils.getBaseName(configPath));
                    for (int i = 0; treeConfigs.get(0).containsKey(name); ++i) name += "_" + i;
                    treeConfigs.get(0).put(name, readPackFile(packFile, configPath));
                }
            }
        }
        this.plantConfigs = new ArrayList<>();
        this.loadPlantConfigs(packFile, "customplants/bushes", Config.class, new Serializer(this.logger, Type.EnumType.Bush, this.maxDrops));
        this.loadPlantConfigs(packFile, "customplants/crops", Config.class, new Serializer(this.logger, Type.EnumType.Crops, this.maxDrops));
        this.loadPlantConfigs(packFile, "customplants/extendables", Config.class, new Serializer(this.logger, Type.EnumType.Extendable, this.maxDrops));
        this.loadPlantConfigs(packFile, "customplants/saplings", Config.class, new Serializer(this.logger, Type.EnumType.Sapling, this.maxDrops));
        
        for (Config config : this.plantConfigs) {
            config.addRegistryEntries(this.registry, treeConfigs);
        }
    }
    
    /**
     * Loads all configs of a type. Requires {@link #plantConfigs} to be initialized!
     * <p>
     *     For {@param type} and {@param typeAdapter}, see descriptions in
     *     {@link GsonBuilder#registerTypeAdapter(java.lang.reflect.Type, Object)}and {@link Gson#fromJson(String, Class)}.
     * </p>
     * @param packFile The resources of the pack on disk.
     * @param path The path of the directory where the configs are in.
     * @param type The plant config holder class.
     * @param typeAdapter The (de)serialization class for the config holder class.
     * @throws IOException When an I/O Error occurs in {@link #packFileExists(File, String)}, {@link #readPackFilePaths(File, String, FileNameFilter)} or {@link #readPackFile(File, String)}.
     */
    protected void loadPlantConfigs(File packFile, String path, Class<? extends Config> type, Object typeAdapter) throws IOException {
        if (packFileExists(packFile, path)) {
            List<String> configPaths = readPackFilePaths(packFile, path, (filename) -> PATTERN_PLANT_FILE.matcher(filename).matches());
            if (configPaths != null) {
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(type, typeAdapter)
                        .setPrettyPrinting()
                        .create();
                for (String configPath : configPaths)
                    this.plantConfigs.add(gson.fromJson(readPackFile(packFile, configPath), type)
                                              .finalize(FilenameUtils.getBaseName(configPath)));
            }
        }
    }
    
    /**
     * Loads the resource pack. Outsourced from {@link #loadPacks()} for better readability.
     * @param packFile The resources of the pack on disk.
     */
    protected void registerResourcePack(File packFile) { /* clientside */ }
    
    /**
     * Refreshes the default resourcepacks after loading them. Called after the loop which calls {@link #registerResourcePack(File)} finished.
     */
    protected void refreshResources() { /* clientside */ }
    
    /**
     * Searches a pack for a file or directory.
     * @param pack The pack to search in.
     * @param path The path to the file to search for.
     * @return Whether the file exists. Returns false when the pack is not a zip file.
     * @throws IOException If an I/O error has occurred in {@link ZipFile#ZipFile(File)}.
     */
    protected static boolean packFileExists(File pack, String path) throws IOException {
        if (pack.isDirectory()) {
            return new File(pack, path).exists();
        }
        else {
            try (final ZipFile zipFile = new ZipFile(pack)) {
                path = patchPathForZip(path);
                ZipEntry entry = zipFile.getEntry(path);
                return entry != null;
            }
            catch (ZipException e) {
                // ignore, file is no zip
            }
            return false;
            
        }
    }
    
    /**
     * Reads a file in a pack.
     * @param pack The pack where the file is in.
     * @param path The path to the file to read.
     * @return The contents of the file.
     * @throws IOException If an I/O error has occurred in {@link FileUtils#readFile(File)}, {@link ZipFile#ZipFile(File)}, {@link ZipFile#getInputStream(ZipEntry)} or {@link BufferedReader#readLine()}.
     */
    protected static String readPackFile(File pack, String path) throws IOException {
        if (pack.isDirectory()) {
            return FileUtils.readFile(new File(pack, path));
        }
        else {
            try (final ZipFile zipFile = new ZipFile(pack)) {
                path = patchPathForZip(path);
                ZipEntry entry = zipFile.getEntry(path);
                if (entry == null) return "";
                try (final BufferedReader reader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry), Charset.defaultCharset()))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) sb.append(line);
                    return sb.toString();
                }
            }
        }
    }
    
    /**
     * Reads a directory non-recursively (files only).
     * @param pack The pack where the file is in.
     * @param path The path to search into.
     * @param filter The filter for the files.
     * @return null if the passed path does not exist or is no directory,
     *         otherwise the paths of the files inside the directory (including the passed path).
     * @throws IOException If an I/O Exception occurs in {@link ZipFile#ZipFile(File)}.
     */
    @Nullable
    protected static List<String> readPackFilePaths(File pack, String path, FileNameFilter filter) throws IOException {
        if (pack.isDirectory()) {
            File dir = new File(pack, path);
            if (!dir.isDirectory()) return null;
            String finalPath = path;
            File[] files = dir.listFiles((file, name) -> new File(file, name).isFile() && filter.accept(name));
            if (files == null) return null;
            return Arrays.stream(files).map(it -> finalPath + "/" + it.getName()).collect(Collectors.toList());
        }
        else {
            try (final ZipFile zipFile = new ZipFile(pack)) {
                path = patchPathForZip(path);
                ZipEntry entry = zipFile.getEntry(path);
                if (entry == null || !entry.isDirectory()) return null;
                Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
                List<String> paths = new ArrayList<>();
                while (zipEntries.hasMoreElements()) {
                    ZipEntry entry1 = zipEntries.nextElement();
                    if (entry1.isDirectory()) continue;
                    String entryPath = entry1.getName();
                    if (!(entryPath.startsWith(path) && filter.accept(entryPath))) continue;
                    entryPath = patchPathForZip(entryPath);
                    paths.add(entryPath);
                }
                return paths;
            }
        }
    }
    
    // TODO: 30.04.22 see javadoc/return
    /**
     * Additions++ is using this for OS compatibility. But IDK what this is doing...
     * @param path The path to patch.
     * @return The patched path. (Redundant because of Object reference but... whatever.)
     */
    protected static String patchPathForZip(String path) {
        return ZIP_SEPARATOR_DIFFERENT ? path.replace(File.separatorChar, '/') : path;
    }
    
    // TODO: 30.04.22 remove it if unused
    /**
     * Thrown by ???
     */
    protected static class LoaderException extends Throwable {
        public LoaderException(String s) {
            super(s);
        }
    }
    
    /**
     * Thrown when either PlantsPacks or Mods are set as dependency for a PlantsPack but is not (pre)loaded.
     */
    protected static class MissingResourcesException extends Throwable {
        public MissingResourcesException(PlantsPackMeta pack, String type, List<String> missing) {
            super(pack.getID() + " is missing following " + type + ":\n  " + String.join(",\n  ", missing));
        }
    }
    
    /**
     * Functional interface, used for {@link #readPackFilePaths(File, String, FileNameFilter)}.
     */
    protected interface FileNameFilter {
        boolean accept(String name);
    }
}
