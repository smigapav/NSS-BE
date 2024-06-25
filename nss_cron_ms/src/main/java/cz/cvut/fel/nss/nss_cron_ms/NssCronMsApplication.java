package cz.cvut.fel.nss.nss_cron_ms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NssCronMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(NssCronMsApplication.class, args);
    }

}
