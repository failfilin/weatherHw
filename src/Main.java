import java.net.URI;
import java.net.http.*;

public class Main {
    void main(String[] args) {
        String accessToken = "";
        //Широта точки на карте
        String lat = "43.1204";
        //Долгота точки на карте
        String lon = "131.9225";
        //Количество дней выводимых в ответ. Лимит по длительности для тестового 7 дней
        int limit = 7;
        // Сумма среднихзначений температур
        double avgForecastsum = 0;
        // Среднее значение средних температур за период указанный в limit
        String query = String.format("lat=%s&lon=%s&limit=%d", lat, lon, limit);
        URI url = URI.create(String.format("https://api.weather.yandex.ru/v2/forecast?%s", query));
        try {
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder().GET().setHeader("X-Yandex-Weather-Key", accessToken).uri(url).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            System.out.println(responseBody);
            System.out.println("Текущая температура " + jsonExtractor(responseBody, "temp") + " градусов");
            for (int i = 0; i < limit; i++) {
                responseBody = responseBody.substring(responseBody.indexOf("date_ts") + 1);
                avgForecastsum += Double.parseDouble(jsonExtractor(responseBody, "temp_avg"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.printf("Средняя температура за выбранный период %.1f градусов%n", avgForecastsum / limit);
    }

    // По общему паттерну - достать значение первого подходящего параметра
    public String jsonExtractor(String stringToExtract, String startString) {
        String substring = stringToExtract.substring(stringToExtract.indexOf(startString));
        return substring.substring(substring.indexOf(":") + 1, substring.indexOf(","));
    }
}