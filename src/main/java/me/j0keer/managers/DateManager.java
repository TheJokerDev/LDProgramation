package me.j0keer.managers;

import lombok.Getter;
import me.j0keer.LDProgramation;
import me.j0keer.types.Day;
import me.j0keer.types.Person;

import java.util.*;

@Getter
public class DateManager {
    private final LDProgramation plugin;
    private HashMap<Integer, List<Day>> days = new HashMap<>();
    private final Calendar date = Calendar.getInstance();

    public DateManager(LDProgramation plugin) {
        this.plugin = plugin;
        load();
    }

    public List<String> getDaysString(){
        List<String> list = new ArrayList<>(Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"));
        list.removeAll(new ArrayList<>(plugin.getConfig().getStringList("excluded.days")));
        return list;
    }

    public void load() {
        days.clear();
        String month = plugin.getConfig().getString("month", getMonth(date.get(Calendar.MONTH)));
        int year = plugin.getConfig().getInt("year", date.get(Calendar.YEAR));

        List<String> excludedDays = new ArrayList<>(plugin.getConfig().getStringList("excluded.days"));

        date.set(Calendar.MONTH, Arrays.asList("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December").indexOf(month));
        date.set(Calendar.YEAR, year);

        plugin.console("{info}Loading days for {month} - {year}...".replace("{month}", month).replace("{year}", String.valueOf(year)));
        for (int i = 1; i <= date.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            date.set(Calendar.DAY_OF_MONTH, i);
            if (excludedDays.contains(getDayName(date.get(Calendar.DAY_OF_WEEK)))) {
                continue;
            }
            Day day = new Day(plugin, getDayName(date.get(Calendar.DAY_OF_WEEK))+ " "+ i + " " + month + " " + year);
            String dayStr = i < 10 ? "0" + i : String.valueOf(i);
            String monthStr = date.get(Calendar.MONTH) < 10 ? "0" + (date.get(Calendar.MONTH) + 1) : String.valueOf(date.get(Calendar.MONTH) + 1);
            day.setDateFormat(dayStr + "/" + monthStr + "/" + year);
            day.setDayLong(date.getTimeInMillis());
            // put week of month number and day
            if (days.containsKey(date.get(Calendar.WEEK_OF_MONTH))) {
                days.get(date.get(Calendar.WEEK_OF_MONTH)).add(day);
            } else {
                List<Day> dayList = new ArrayList<>();
                dayList.add(day);
                days.put(date.get(Calendar.WEEK_OF_MONTH), dayList);
            }
        }

        plugin.console("{info}Loaded " + days.size() + " days.");
    }

    public String getDayName(int day) {
        return switch (day) {
            case 1 -> "Sunday";
            case 2 -> "Monday";
            case 3 -> "Tuesday";
            case 4 -> "Wednesday";
            case 5 -> "Thursday";
            case 6 -> "Friday";
            case 7 -> "Saturday";
            default -> "Sunday";
        };
    }

    public String getMonth(int month) {
        return switch (month) {
            case 1 -> "February";
            case 2 -> "March";
            case 3 -> "April";
            case 4 -> "May";
            case 5 -> "June";
            case 6 -> "July";
            case 7 -> "August";
            case 8 -> "September";
            case 9 -> "October";
            case 10 -> "November";
            case 11 -> "December";
            default -> "January";
        };
    }

    public void assignDays() {
        for (List<Day> value : days.values()) {
            plugin.getPersonManager().getPersons().values().forEach(Person::resetParticipations);
            value.forEach(Day::assign);
        }

        plugin.console("{info}Assigned persons to days.");
    }
}
