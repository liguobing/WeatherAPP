package cn.lixyz.weatherapp.bean;

import java.util.List;

/**
 * Created by LGB on 2016/4/7.
 */
public class WeatherInfo {

    private Basic basic;
    private List<DailyForecast> daily_forecast;
    private List<HourlyForecast> hourly_forecast;
    private Now now;
    private String status;
    private Suggestion suggestion;

    public Basic getBasic() {
        return basic;
    }

    public void setBasic(Basic basic) {
        this.basic = basic;
    }

    public List<DailyForecast> getDaily_forecast() {
        return daily_forecast;
    }

    public void setDaily_forecast(List<DailyForecast> daily_forecast) {
        this.daily_forecast = daily_forecast;
    }

    public List<HourlyForecast> getHourly_forecast() {
        return hourly_forecast;
    }

    public void setHourly_forecast(List<HourlyForecast> hourly_forecast) {
        this.hourly_forecast = hourly_forecast;
    }

    public Now getNow() {
        return now;
    }

    public void setNow(Now now) {
        this.now = now;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Suggestion getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(Suggestion suggestion) {
        this.suggestion = suggestion;
    }
}
