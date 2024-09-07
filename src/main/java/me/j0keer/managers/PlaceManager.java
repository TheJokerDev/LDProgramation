package me.j0keer.managers;

import lombok.Getter;
import me.j0keer.LDProgramation;
import me.j0keer.types.Place;

import java.util.ArrayList;
import java.util.LinkedHashMap;

@Getter
public class PlaceManager {
    private final LDProgramation plugin;
    private LinkedHashMap<String, Place> places = new LinkedHashMap<>();

    public PlaceManager(LDProgramation plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        places.clear();
        plugin.console("{info}Loading places...");
        if (!plugin.getConfig().contains("places")) {
            plugin.console("{warning}No places found.");
            return;
        }
        plugin.getConfig().getConfigurationSection("places").getKeys(false).forEach(key -> {
            Place place = new Place(key);
            place.setDescription(plugin.getConfig().getString("places." + key + ".description"));
            place.setLocation(plugin.getConfig().getString("places." + key + ".location"));
            if (plugin.getConfig().get("places."+key+".excluded.days") != null) {
                place.setExcludedDays(new ArrayList<>(plugin.getConfig().getStringList("places."+key+".excluded.days")));
            }
            places.put(key, place);
        });
        plugin.console("{info}Loaded " + places.size() + " places.");
    }

    public Place getPlace(String name) {
        return places.get(name);
    }
}
