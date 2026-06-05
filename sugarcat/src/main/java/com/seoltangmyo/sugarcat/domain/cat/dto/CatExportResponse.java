package com.seoltangmyo.sugarcat.domain.cat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seoltangmyo.sugarcat.domain.bloodsugar.entity.SugarStatus;
import com.seoltangmyo.sugarcat.domain.meal.entity.MealStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record CatExportResponse(
        List<Row> rows
) {
    public record Row(
            LocalDate recordDate,
            List<BloodSugar> bloodSugars,
            List<Meal> meals,
            Insulin insulin
    ) {
    }

    public record BloodSugar(
            @JsonFormat(pattern = "HH:mm")
            LocalTime recordTime,

            int sugarValue,
            SugarStatus sugarStatus
    ){
    }

    public record Meal(
            @JsonFormat(pattern = "HH:mm")
            LocalTime recordTime,

            MealStatus mealStatus
    ) {
    }

    public record Insulin(
            List<Integer> missedIndexes
    ) {
    }
}
