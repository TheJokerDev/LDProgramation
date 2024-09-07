package me.j0keer.managers;

import lombok.Getter;
import me.j0keer.LDProgramation;
import me.j0keer.types.Turn;

import java.util.LinkedHashMap;

@Getter
public class TurnManager {
    private final LDProgramation plugin;
    private LinkedHashMap<String, Turn> turns;

    public TurnManager(LDProgramation plugin) {
        this.plugin = plugin;
        this.turns = new LinkedHashMap<>();
        loadTurns();
    }

    public void loadTurns() {
        turns.clear();
        plugin.console("{info}Loading turns...");

        if (!plugin.getConfig().contains("turns")) {
            plugin.console("{warning}No turns found.");
            return;
        }

        plugin.getConfig().getConfigurationSection("turns").getKeys(false).forEach(key -> {
            Turn turn = new Turn(key);
            turns.put(key, turn);
        });

        plugin.console("{info}Loaded " + turns.size() + " turns.");
    }

    public Turn getTurn(String name) {
        return turns.get(name);
    }
}
