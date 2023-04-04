package dev.skosarev.accountservice.controller;

import dev.skosarev.accountservice.model.SecurityEvent;
import dev.skosarev.accountservice.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/security/")
public class AuditorController {
    private final LogService logService;

    @Autowired
    public AuditorController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping("events")
    public ResponseEntity<List<SecurityEvent>> getEvents() {
        return ResponseEntity.ok(logService.getAllEvents());
    }
}
