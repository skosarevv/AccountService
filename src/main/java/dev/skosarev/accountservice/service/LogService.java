package dev.skosarev.accountservice.service;

import dev.skosarev.accountservice.model.SecurityAction;
import dev.skosarev.accountservice.model.SecurityEvent;
import dev.skosarev.accountservice.repository.SecurityEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogService {
    private final SecurityEventRepository securityEventRepository;

    @Autowired
    public LogService(SecurityEventRepository securityEventRepository) {
        this.securityEventRepository = securityEventRepository;
    }

    public void addEvent(SecurityAction action, String subject, String object, String path) {
        securityEventRepository.save(new SecurityEvent(action, subject, object, path));
    }

    public List<SecurityEvent> getAllEvents() {
        return securityEventRepository.findAll();
    }
}
