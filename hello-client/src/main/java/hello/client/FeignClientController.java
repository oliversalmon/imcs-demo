package hello.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableFeignClients
@RestController
public class FeignClientController {

    @FeignClient(name = "hello-service", fallback = UserClientFallback.class)
    public interface UserClient {
        // @RequestLine("GET /hi_getall")
        // List<String> getGreetings();

        @RequestMapping(method = RequestMethod.GET, value = "/hi_getall", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
        List<String> getGreetings();
    }

    @Component
    class UserClientFallback implements UserClient {
        @Override
        public List<String> getGreetings() {
            return Arrays.asList("Hello", "tryGetGreetingsElse");
        }
    }

    @Autowired
    private UserClient userClient;

    @RequestMapping("/getall-hi")
    public List<String> getAllGreetings() {
        List<String> greetings = userClient.getGreetings();
        return greetings;
    }


    public static void main(String[] args) {
        SpringApplication.run(FeignClientController.class, args);
    }
}
