package cz.cvut.fel.nss.nss_cron_ms;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class CronService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${NSS_APP_URL}")
    private String nssAppUrl;

    @Scheduled(cron = "0 0 0 * * ?")
    public void callCleanUp() {
        String url = nssAppUrl;
        restTemplate.getForObject(url, Void.class);
    }
}
