package cz.cvut.fel.nss.nss_cron_ms;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class CronService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${NSS_APP_URL}")
    private String nssAppUrl;

    @Value("${NSS_APP_APIKEY}")
    private String apiKey;

    @Scheduled(cron = "0 0 0 * * ?")
    public void callCleanUp() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apiKey", this.apiKey);

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        ResponseEntity<String> response = restTemplate.exchange(nssAppUrl, HttpMethod.GET, entity, String.class);
    }
}
