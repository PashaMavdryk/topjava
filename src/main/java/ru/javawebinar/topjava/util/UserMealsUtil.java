package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExceed;

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
public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> mealList = Arrays.asList(
                new UserMeal(LocalDateTime.of(2017, Month.MAY, 30,10,0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2017, Month.MAY, 30,13,0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2017, Month.MAY, 30,20,0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2017, Month.MAY, 31,10,0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2017, Month.MAY, 31,13,0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2017, Month.MAY, 31,20,0), "Ужин", 510)
        );
        List<UserMealWithExceed> filteredWithExceeded = getFilteredWithExceeded(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        filteredWithExceeded.forEach(System.out::println);
//
    }

    public static List<UserMealWithExceed>  getFilteredWithExceeded(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesSumByDate = mealList.stream().collect(Collectors.groupingBy(um -> um.getDateTime().toLocalDate(),
                Collectors.summingInt(UserMeal::getCalories)));

        return mealList.stream()
                .filter(um->TimeUtil.isBetween(um.getDateTime().toLocalTime(), startTime, endTime))
                .map(um->new UserMealWithExceed(um.getDateTime(), um.getDescription(), um.getCalories(), caloriesSumByDate.get(um.getDateTime().toLocalDate())>caloriesPerDay))
                .collect(Collectors.toList());

    }

    public static List<UserMealWithExceed> getFilteredMealsWithExceededByCycle(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay){
        Map<LocalDate, Integer> caloriesSumPerDay = new HashMap<>();
        for(UserMeal meal: mealList){
            LocalDate mealDate = meal.getDateTime().toLocalDate();
            caloriesSumPerDay.put(mealDate,caloriesSumPerDay.getOrDefault(mealDate, 0) + meal.getCalories());
        }

        List<UserMealWithExceed> mealExceeded = new ArrayList<>();
        for (UserMeal meal : mealList){
            LocalDateTime dateTime = meal.getDateTime();
            if(TimeUtil.isBetween(dateTime.toLocalTime(), startTime, endTime)){
                mealExceeded.add(new UserMealWithExceed(dateTime,meal.getDescription(),meal.getCalories(),
                        caloriesSumPerDay.get(dateTime.toLocalDate())>caloriesPerDay));
            }
        }
        return mealExceeded;
    }
}
