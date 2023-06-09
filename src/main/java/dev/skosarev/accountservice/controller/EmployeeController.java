package dev.skosarev.accountservice.controller;

import dev.skosarev.accountservice.dto.PaymentForOutput;
import dev.skosarev.accountservice.model.User;
import dev.skosarev.accountservice.service.PaymentService;
import io.micrometer.core.lang.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/empl/")
public class EmployeeController {
    private final PaymentService paymentService;
    Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    public EmployeeController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("payment")
    public ResponseEntity<?> getPayments(@RequestParam @Nullable String period, @AuthenticationPrincipal UserDetails details) {
        if (details == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This api only for authenticated user");
        }

        List<PaymentForOutput> result;
        if (period == null) {
            result = paymentService.getAllPayments((User) details);
        } else {
            result = List.of(paymentService.getPayment(period, (User) details));
        }

        logger.info("Get payments for period {}: {}", period, details.getUsername());
        if (result.size() == 1) {
            return ResponseEntity.ok(result.get(0));
        }
        return ResponseEntity.ok(result);
    }
}
