package me.j0keer.managers;

import lombok.Getter;
import me.j0keer.LDProgramation;
import me.j0keer.types.Person;

import java.util.LinkedHashMap;

@Getter
public class PersonManager {
    private LDProgramation plugin;
    private LinkedHashMap<String, Person> persons;

    public PersonManager(LDProgramation plugin) {
        this.plugin = plugin;
        this.persons = new LinkedHashMap<>();

        loadPersons();
    }

    public void loadPersons() {
        persons.clear();
        plugin.console("{info}Loading persons...");

        if (!plugin.getConfig().contains("persons")) {
            plugin.console("{warning}No persons found.");
            return;
        }

        plugin.getConfig().getConfigurationSection("persons").getKeys(false).forEach(key -> {
            Person person = new Person(plugin, key);
            persons.put(key, person);
        });

        plugin.console("{info}Loaded " + persons.size() + " persons.");
    }

    public Person getPerson(String name) {
        return persons.get(name);
    }
}
