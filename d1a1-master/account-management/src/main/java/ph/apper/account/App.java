package ph.apper.account;

import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableConfigurationProperties({App.ActivityUrl.class})
public class App {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(App.class);
        application.addListeners(new ApplicationPidFileWriter());
        application.run(args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Data
    @ConfigurationProperties(prefix = "activity")
    public static class ActivityUrl {
        private String url;
    }

    @RestController
    @RequestMapping("account")
    public static class AccountController {

        private final App.ActivityUrl url;
        private final RestTemplate restTemplate;

        public AccountController(RestTemplate restTemplate, App.ActivityUrl activityUrl) {
            this.restTemplate = restTemplate;
            this.url = activityUrl;
        }

        @PostMapping
        public ResponseEntity create(@RequestBody Request request) {
            System.out.println(request);

            Activity activity = new Activity();
            activity.setAction("REGISTRATION");
            activity.setIdentifier("email="+request.getEmail());
            System.out.println(url.getUrl());
            ResponseEntity<Object> response = restTemplate.postForEntity(url.getUrl(), activity, Object.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Success");
            }
            else {
                System.out.println("Err: " + response.getStatusCode());
            }

            return ResponseEntity.ok().build();
        }
    }

    @Data
    public static class Activity {
        private String action;
        private String identifier;
    }

    @Data
    public static class Request {
        private String firstName;
        private String lastName;
        private String email;
        private String password;
    }
}


