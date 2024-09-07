package me.j0keer.types;

import lombok.Getter;
import me.j0keer.LDProgramation;
import me.j0keer.config.ConfigurationSection;
import me.j0keer.config.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class Person {
    private final LDProgramation plugin;
    private final String id;
    private String name;
    private String phone;
    private String gender;

    private final List<Availability> availability = new ArrayList<>();
    private List<String> turns = new ArrayList<>();
    private List<String> days = new ArrayList<>();

    private boolean justPartners = false;
    private String partner = "none";

    public int participationAtWeek = 0;
    public int maxParticipationAtWeek;

    public boolean disabled = false;

    public Person(LDProgramation plugin, String id) {
        this.plugin = plugin;
        this.id = id;

        load();
    }

    public void addTurn(String turn){
        turns.add(turn);
    }

    public boolean containsTurn(String turn){
        return turns.contains(turn);
    }

    public void clearTurns(){
        turns.clear();
    }

    public void addDay(String day){
        days.add(day);
    }

    public boolean containsDay(String day){
        return days.contains(day);
    }

    public void clearDays(){
        days.clear();
    }

    public void load() {
        FileConfiguration config = plugin.getConfig();

        name = config.getString("persons." + id + ".name");
        phone = config.getString("persons." + id + ".phone", "No Number");
        gender = config.getString("persons." + id + ".gender", "M").toUpperCase();

        if (config.contains("persons." + id + ".availability")) {
            ConfigurationSection section = config.getConfigurationSection("persons." + id + ".availability");
            for (String key : section.getKeys(false)) {
                plugin.console("{info}Loading availability for " + key + " for person " + name + "...");
                Availability avail = new Availability(key);

                List<String> placesStr = new ArrayList<>(section.getStringList(key + ".places"));
                if (placesStr.isEmpty()) continue;
                List<Place> places = placesStr.get(0).equals("*") ? new ArrayList<>(plugin.getPlaceManager().getPlaces().values()) : new ArrayList<>(placesStr.stream().map(plugin.getPlaceManager()::getPlace).toList());

                List<String> turnsStr = new ArrayList<>(section.getStringList(key + ".turns"));
                if (turnsStr.isEmpty()) continue;
                List<Turn> turns = turnsStr.get(0).equals("*") ? new ArrayList<>(plugin.getTurnManager().getTurns().values()) : new ArrayList<>(turnsStr.stream().map(plugin.getTurnManager()::getTurn).toList());

                places.stream().filter(Objects::nonNull).forEach(avail::addPlace);
                turns.stream().filter(Objects::nonNull).forEach(avail::addTurn);

                List<String> blacklist = new ArrayList<>(section.getStringList("blacklist"));
                avail.setBlacklist(blacklist);

                if (key.equals("*")) {
                    for (String day : plugin.getDateManager().getDaysString()) {
                        Availability av = new Availability(day);
                        av.getPlaces().addAll(avail.getPlaces());
                        av.getTurns().addAll(avail.getTurns());
                        availability.add(av);
                    }
                    break;
                }
                availability.add(avail);
            }
        }

        maxParticipationAtWeek = config.getInt("persons."+id+"maxAtWeek", -1);
        justPartners = config.getBoolean("persons." + id + ".justPartners", false);
        partner = config.getString("persons." + id + ".partner", "none");
        disabled = config.getBoolean("persons."+id+".disabled", false);

        plugin.console("{info}Loaded person " + name + " with " + availability.size() + " availabilities.");
        if (availability.isEmpty()) {
            plugin.console("{warning}No availabilities found for person " + name + ".");
        } else {
            availability.forEach(avail -> plugin.console("{info}Availability for " + avail.getDay() + " has " + avail.getPlaces().size() + " places and " + avail.getTurns().size() + " turns."));
        }
    }

    public void participate(){
        participationAtWeek++;
    }

    public void resetParticipations(){
        participationAtWeek = 0;
    }

    public boolean canParticipate() {
        if (disabled) return false;
        if (maxParticipationAtWeek > 0) {
            if (participationAtWeek > maxParticipationAtWeek) {
                return false;
            }
        }
        return participationAtWeek < plugin.getConfig().getInt("settings.maxParticipationAtWeek", 2);
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", gender='" + gender + '\'' +
                ", availability=" + availability.toString() +
                ", justPartners=" + justPartners +
                ", partner='" + partner + '\'' +
                ", participationAtWeek=" + participationAtWeek +
                '}';
    }
}
