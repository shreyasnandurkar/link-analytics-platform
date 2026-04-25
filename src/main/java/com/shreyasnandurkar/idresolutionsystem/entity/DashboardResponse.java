package com.shreyasnandurkar.idresolutionsystem.entity;

import java.util.List;

public record DashboardResponse(
        long totalClicks,
        long uniqueClicks,
        List<ClickStats> totals,
        List<CountryStats> topCountries,
        List<CityStats> topCities
) {
}
