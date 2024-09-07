package me.j0keer.types;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

@Getter
public class Availability {
    private final String day;
    private LinkedList<Place> places = new LinkedList<>();
    private LinkedList<Turn> turns = new LinkedList<>();
    private LinkedList<String> blacklist = new LinkedList<>();

    public Availability(String day) {
        this.day = day;
    }

    public boolean isDay(String day) {
        return day.contains(this.day);
    }

    public void addPlace(Place place) {
        places.add(place);
    }

    public void addTurn(Turn turn) {
        turns.add(turn);
    }

    public void removePlace(Place place) {
        places.remove(place);
    }

    public void removeTurn(Turn turn) {
        turns.remove(turn);
    }

    public void setBlacklist(List<String> list) {
        blacklist.addAll(list);
    }

    public Place getPlace(String name) {
        return places.stream().filter(place -> place.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Turn getTurn(String name) {
        return turns.stream().filter(turn -> turn.name().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public boolean hasPlace(String name) {
        return places.stream().anyMatch(place -> place.getName().equalsIgnoreCase(name));
    }

    public boolean hasTurn(String name) {
        return turns.stream().anyMatch(turn -> turn.name().equalsIgnoreCase(name));
    }

    public boolean hasPlace(Place place) {
        return places.contains(place);
    }

    public boolean hasTurn(Turn turn) {
        return turns.contains(turn);
    }

    public boolean hasPlaces() {
        return !places.isEmpty();
    }

    public boolean hasTurns() {
        return !turns.isEmpty();
    }

    public boolean hasContent() {
        return hasPlaces() || hasTurns();
    }

    public boolean isEmpty() {
        return !hasContent();
    }

    public void clear() {
        places.clear();
        turns.clear();
    }

    public void remove() {
        places = null;
        turns = null;
    }

    public void destroy() {
        clear();
        remove();
    }

    public String toString() {
        StringBuilder places = new StringBuilder();
        for (Place place : this.places) {
            places.append(place.getName()).append(", ");
        }
        if (!places.isEmpty()) {
            places.setLength(places.length() - 2); // Remove last comma and space
        }
        String placesString = places.toString();
        String placesStr = placesString.isEmpty() ? "No places" : placesString;

        StringBuilder turns = new StringBuilder();
        for (Turn turn : this.turns) {
            turns.append(turn.name()).append(", ");
        }
        if (!turns.isEmpty()) {
            turns.setLength(turns.length() - 2); // Remove last comma and space
        }
        String turnsString = turns.toString();
        String turnsStr = turnsString.isEmpty() ? "No turns" : turnsString;

        String blackListString = blacklist.toString();
        String blacklistStr = turnsString.isEmpty() ? "No blacklist" : blackListString;
        return "Availability{day='" + day + "', places=" + placesStr + ", turns=" + turnsStr + ", blacklist="+blacklistStr+"}";
    }
}
