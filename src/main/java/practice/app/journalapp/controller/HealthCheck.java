package practice.app.journalapp.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthCheck {

    public String healthCheck(){
        return "ok";
    }
}
