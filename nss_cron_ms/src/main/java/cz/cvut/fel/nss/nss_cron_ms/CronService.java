package cz.cvut.fel.nss.nss_cron_ms;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * This service is responsible for scheduling tasks using cron expressions.
 * It uses the singleton scope and is proxied at the target class level.
 */
@Service
@RequiredArgsConstructor
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CronService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${NSS_APP_URL}")
    private String nssAppUrl;

    @Value("${NSS_APP_APIKEY}")
    private String apiKey;

    private static final Logger LOG = LoggerFactory.getLogger(CronService.class);

    /**
     * This method is scheduled to run every day at midnight.
     * It sends a POST request to the NSS_APP_URL with the NSS_APP_APIKEY in the headers.
     * The purpose of this method is to call the clean up service.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void callCleanUp() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apiKey", this.apiKey);

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        LOG.info("Calling clean up service");

        ResponseEntity<String> response = restTemplate.exchange(nssAppUrl, HttpMethod.POST, entity, String.class);
    }
}
