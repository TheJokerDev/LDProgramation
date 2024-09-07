package me.j0keer;

import lombok.Getter;
import me.j0keer.config.file.FileConfiguration;
import me.j0keer.config.file.YamlConfiguration;
import me.j0keer.excel.ExcelUtilities;
import me.j0keer.managers.DateManager;
import me.j0keer.managers.PersonManager;
import me.j0keer.managers.PlaceManager;
import me.j0keer.managers.TurnManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Logger;

@Getter
public class LDProgramation {
    @Getter
    private static LDProgramation instance;
    private Logger logger;

    private FileConfiguration config;

    private ExcelUtilities excelUtilities;
    private DateManager dateManager;
    private PlaceManager placeManager;
    private TurnManager turnManager;
    private PersonManager personManager;

    public void onEnable(){
        double ms = System.currentTimeMillis();
        instance = this;
        logger = Logger.getLogger("LDProgramation");
        saveDefaultConfig();
        console("{info}LDProgramation is starting...");

        excelUtilities = new ExcelUtilities(this);
        dateManager = new DateManager(this);
        placeManager = new PlaceManager(this);
        turnManager = new TurnManager(this);
        personManager = new PersonManager(this);

        dateManager.assignDays();

        ms = System.currentTimeMillis() - ms;
        console("{info}LDProgramation has been enabled in " + ms + "ms.");
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
    }

    public void onCommand(String alias, String[] args) {
        switch (alias) {
            case "help" -> console("{info}Commands: help, exit, version, save, reload");
            case "version" -> console("{info}LDProgramation v"+Main.VERSION + " by J0keer.");
            case "save" -> {
                if (args.length == 1) {
                    String fileName = args[0];
                    if (!fileName.isEmpty()) {
                        if (!fileName.endsWith(".xlsx")) {
                            fileName += ".xlsx";
                        }
                        File file = new File(getDataFolder(), fileName);
                        excelUtilities.setFile(file).save();
                    }
                }
                excelUtilities.save();
            }
            case "reload" -> {
                reloadConfig();

                dateManager.load();
                placeManager.load();
                turnManager.loadTurns();
                personManager.loadPersons();

                dateManager.assignDays();
                console("{info}Config reloaded.");
            }
            default -> console("{error}Unknown command. Type 'help' for a list of commands.");
        }
    }

    public void console(String... msg){
        for(String m : msg){
            if (m.startsWith("{error}")) {
                m = m.replace("{error}", "");
                System.out.printf("[ERROR] %s%n", m);
            } else if (m.startsWith("{warning}")) {
                m = m.replace("{warning}", "");
                System.out.printf("[WARNING] %s%n", m);
            } else if (m.startsWith("{info}")) {
                m = m.replace("{info}", "");
                System.out.printf("[INFO] %s%n", m);
            } else {
                System.out.println(m);
            }
        }
    }

    public File getDataFolder() {
        return new File("config/");
    }

    public void saveDefaultConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
        }
        if (!configFile.exists()) {
            saveResource("config.yml", configFile);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void saveResource(String file, File f){
        if (!f.exists()){
            try (InputStream in = getClass().getClassLoader().getResourceAsStream(file)){
                if (in == null) {
                    console("Could not find " + file + " in jar file. Please report this to the mod author.");
                    return;
                };
                Files.copy(in, f.toPath());
            } catch (IOException ignored) {
            }
        }
    }
}
