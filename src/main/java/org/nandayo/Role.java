package org.nandayo;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class Role {

    private static final String ns = "roles.";
    private static File file;
    private static FileConfiguration config;

    private final String id;
    private String name;

    public Role(String roleID) {
        this.id = roleID;
        if(!config.contains(ns + roleID)) {
            magicianAcademy.inst().getLogger().warning("Unknown role call: " + roleID);
            return;
        }
        this.name = config.getString(ns + roleID + ".name", roleID);
    }
    public static void loadConfigurations() {
        file = new File(magicianAcademy.inst().getDataFolder(), "roles.yml");
        if(!file.exists()) magicianAcademy.inst().saveResource("roles.yml", false);
        config = YamlConfiguration.loadConfiguration(file);
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public static List<String> listID() {
        return config.getStringList("role_order");
    }
}
