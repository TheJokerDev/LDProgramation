package me.j0keer.types;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class Place {
    private final String name;
    private String description;
    private String location;
    private final List<String> excludedDays = new ArrayList<>();

    public Place(String name) {
        this.name = name;
    }

    public void setExcludedDays(ArrayList<String> strings) {
        excludedDays.clear();
        excludedDays.addAll(strings);
    }
}
