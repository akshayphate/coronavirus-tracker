package io.akshay.coronavirustracker.services;

import io.akshay.coronavirustracker.models.LocationStat;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoronaVirusDataService {

    public static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv";

    public List<LocationStat> allStats = new ArrayList<>();

    @PostConstruct
    @Scheduled(cron = "5 * * * * *")
    public void fetchVirusData() throws IOException, InterruptedException {
        List<LocationStat> newStat = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(VIRUS_DATA_URL))
                .build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        StringReader reader = new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);
        for (CSVRecord record : records) {
            LocationStat stat = new LocationStat();
            stat.setState(record.get("Province/State"));
            stat.setCountry(record.get("Country/Region"));
            stat.setLatestReportedCases(Integer.parseInt(record.get(record.size()-1)));
            System.out.println(stat);
            newStat.add(stat);
        }
        allStats = newStat;
    }
}
