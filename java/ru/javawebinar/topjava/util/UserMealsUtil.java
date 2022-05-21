package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

//        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExcess> meal=new ArrayList<>();
        TreeMap<Integer,Integer> list = new TreeMap<>();
        int calories = 0;

        LocalDateTime time=meals.get(0).getDateTime();
        UserMeal userMeal=meals.get(0);
        for (int i = 0; i < meals.size(); i++) {
            if (meals.get(i).getDateTime().getDayOfYear() <= time.getDayOfYear()) {
                calories = calories + meals.get(i).getCalories();
                if (i > 0) {
                    time = meals.get(i - 1).getDateTime();
                }
                if(i==meals.size()-1)
                {
                    list.put(time.getDayOfYear(), calories);
                }
            } else {
                list.put(time.getDayOfYear(), calories);
                calories = 0;
                calories = calories + meals.get(i).getCalories();
                time = meals.get(i).getDateTime();
            }
        }
        for (int i = 0; i < meals.size(); i++) {
            if (meals.get(i).getDateTime().getHour() > startTime.getHour() && meals.get(i).getDateTime().getHour() < endTime.getHour()) {
                for (Map.Entry<Integer, Integer> map : list.entrySet()) {
                    if (meals.get(i).getDateTime().getDayOfYear() == map.getKey() && map.getValue() > caloriesPerDay)
                        meal.add(new UserMealWithExcess(meals.get(i).getDateTime(), meals.get(i).getDescription(), meals.get(i).getCalories(), true));
                    else if (meals.get(i).getDateTime().getDayOfYear() == map.getKey() && map.getValue() <= caloriesPerDay) {
                        meal.add(new UserMealWithExcess(meals.get(i).getDateTime(), meals.get(i).getDescription(), meals.get(i).getCalories(), false));
                    }
                }
            }
        }
        return meal;
    }
//
private static UserMealWithExcess createUserMealWithExcessByMeal(UserMeal meal, Map<Integer, Integer> map, int caloriesPerDay) {
    Integer dayOfYear = meal.getDateTime().getDayOfYear();
    return new UserMealWithExcess(
            meal.getDateTime(),
            meal.getDescription(),
            meal.getCalories(),
            map.get(dayOfYear) > caloriesPerDay);
}
    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        Map<Integer, Integer> caloriesPerDayOfYear=new HashMap<>();
        meals.forEach(el -> caloriesPerDayOfYear.merge(el.getDateTime().getDayOfYear(), el.getCalories(), Integer::sum));
        return meals.stream().filter(el -> el.getDateTime().toLocalTime().isAfter(startTime)
                        && el.getDateTime().toLocalTime().isBefore(endTime))
                .map(meal -> createUserMealWithExcessByMeal(meal,caloriesPerDayOfYear,caloriesPerDay))
                .collect(Collectors.toList());


    }
}
//При реализации через циклы посмотрите в Map на методы getOrDefault или merge
//    переименовал класс UserMealWithExceed и его поле exceed в UserMealWithExcess.excess
//        в UserMeals/UserMealWithExcess поля изменились на private
//обновил данные UserMealsUtil.meals и переименовал некоторые пременные, поля и методы
//        добавил UserMealWithExcess.toString() и метод для выполнения Optional домашнего задания
//        метод фильтрации в TimeUtil переименовали в isBetweenHalfOpen (также изменилась логика сравнения
//        - startTime включается в интервал)