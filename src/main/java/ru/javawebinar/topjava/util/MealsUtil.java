package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealWithExceed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

/**
 * GKislin
 * 31.05.2015.
 */
public class MealsUtil {
    public static void main(String[] args) {
        List<Meal> mealList = Arrays.asList(
                new Meal(LocalDateTime.of(2017, Month.MAY, 30,10,0), "Завтрак", 500),
                new Meal(LocalDateTime.of(2017, Month.MAY, 30,13,0), "Обед", 1000),
                new Meal(LocalDateTime.of(2017, Month.MAY, 30,20,0), "Ужин", 500),
                new Meal(LocalDateTime.of(2017, Month.MAY, 31,10,0), "Завтрак", 1000),
                new Meal(LocalDateTime.of(2017, Month.MAY, 31,13,0), "Обед", 500),
                new Meal(LocalDateTime.of(2017, Month.MAY, 31,20,0), "Ужин", 510)
        );
        List<MealWithExceed> filteredWithExceeded = getFilteredWithExceeded(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        filteredWithExceeded.forEach(System.out::println);
//
    }

    public static List<MealWithExceed>  getFilteredWithExceeded(List<Meal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesSumByDate = mealList.stream().collect(Collectors.groupingBy(um -> um.getDateTime().toLocalDate(),
                Collectors.summingInt(Meal::getCalories)));

        return mealList.stream()
                .filter(um->TimeUtil.isBetween(um.getDateTime().toLocalTime(), startTime, endTime))
                .map(um->new MealWithExceed(um.getDateTime(), um.getDescription(), um.getCalories(), caloriesSumByDate.get(um.getDateTime().toLocalDate())>caloriesPerDay))
                .collect(Collectors.toList());

    }

    public static List<MealWithExceed> getFilteredMealsWithExceededByCycle(List<Meal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay){
        Map<LocalDate, Integer> caloriesSumPerDay = new HashMap<>();
        for(Meal meal: mealList){
            LocalDate mealDate = meal.getDateTime().toLocalDate();
            caloriesSumPerDay.put(mealDate,caloriesSumPerDay.getOrDefault(mealDate, 0) + meal.getCalories());
        }

        List<MealWithExceed> mealExceeded = new ArrayList<>();
        for (Meal meal : mealList){
            LocalDateTime dateTime = meal.getDateTime();
            if(TimeUtil.isBetween(dateTime.toLocalTime(), startTime, endTime)){
                mealExceeded.add(new MealWithExceed(dateTime,meal.getDescription(),meal.getCalories(),
                        caloriesSumPerDay.get(dateTime.toLocalDate())>caloriesPerDay));
            }
        }
        return mealExceeded;
    }
}
