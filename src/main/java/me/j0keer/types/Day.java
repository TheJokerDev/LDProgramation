package me.j0keer.types;

import lombok.Getter;
import lombok.Setter;
import me.j0keer.LDProgramation;

import java.util.*;

@Getter @Setter
public class Day {
    private final LDProgramation plugin;
    private final String name;
    private String dateFormat;
    private long dayLong;

    private HashMap<Place, HashMap<Turn, List<Person>>> availability = new HashMap<>();

    public Day(LDProgramation plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }

    int maxAssigned = 2;

    public void assign() {
        plugin.console("{info}Assigning persons to day " + name + "...");
        plugin.getPersonManager().getPersons().values().forEach(Person::clearTurns);
        plugin.getPersonManager().getPersons().values().forEach(Person::clearDays);

        if (availability.isEmpty()) {
            for (Place value : plugin.getPlaceManager().getPlaces().values()) {
                if (!value.getExcludedDays().isEmpty() && value.getExcludedDays().contains(name)) continue;
                HashMap<Turn, List<Person>> map = new HashMap<>();
                for (Turn turn : plugin.getTurnManager().getTurns().values()) {
                    List<Person> personList = new ArrayList<>(plugin.getPersonManager().getPersons().values().stream().toList());
                    plugin.console("{info}Sorting persons by participation at week ("+personList.size()+")...");
                    personList.sort(Comparator.comparingInt(Person::getParticipationAtWeek));
                    //Inverse the list
                    Collections.shuffle(personList);

                    List<Person> assigned = new ArrayList<>();

                    String gender = "";
                    String day = getName().split(" ")[0];
                    for (Person person : personList) {
                        if (assigned.size() >= maxAssigned) break;

                        if (person.getAvailability().stream().anyMatch(availability -> availability.hasPlace(value) && availability.hasTurn(turn) && availability.isDay(name) && !availability.getBlacklist().contains(day))) {
                            if (!person.canParticipate()) continue;
                            if (!gender.isEmpty() && !person.getGender().equals(gender)) continue;
                            if (person.containsTurn(turn.name())) continue;
                            if (person.containsDay(day)) continue;

                            person.participationAtWeek++;
                            assigned.add(person);
                            person.addTurn(turn.name());
                            person.addDay(name);
                            if (gender.isEmpty()) {
                                gender = person.getGender();
                            }
                            if (person.isJustPartners() && !person.getPartner().equals("none") && assigned.size() < maxAssigned) {
                                Person partner = plugin.getPersonManager().getPerson(person.getPartner());
                                if (partner == null) continue;
                                partner.participationAtWeek++;
                                partner.addTurn(turn.name());
                                partner.addDay(name);
                                assigned.add(partner);
                            }
                        }
                    }

                    map.put(turn, assigned);
                }
                availability.put(value, map);
            }
        }

        //remove list when just has 1 person at turn
        availability.forEach((k, s) -> {
            for (Turn key : s.keySet()) {
                List<Person> list = s.get(key);
                if (list.size() == 1) {
                    s.put(key, Collections.emptyList());
                }
            }
        });

        for (Place place : availability.keySet()) {
            for (Turn turn : availability.get(place).keySet()) {
                List<Person> persons = availability.get(place).get(turn);
                plugin.console("{info}Assigned " + persons.size() + " persons to " + place.getName() + " at " + turn.name() + ".");
            }
        }

        plugin.console("{info}Assigned persons to day " + name + ".");
    }

}
