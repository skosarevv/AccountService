package dev.skosarev.accountservice.controller;

import dev.skosarev.accountservice.dto.PaymentDto;
import dev.skosarev.accountservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/acct/")
@Validated
public class AccountantController {
    private final PaymentService paymentService;

    @Autowired
    public AccountantController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("payments")
    public ResponseEntity<Map<String, String>> uploadPayrolls(@RequestBody List<@Valid PaymentDto> paymentList) {
        return ResponseEntity.ok(paymentService.saveAllDto(paymentList));
    }

    @PutMapping("payments")
    public ResponseEntity<Map<String, String>> changeSalary(@Valid @RequestBody PaymentDto paymentDto) {
        return ResponseEntity.ok(paymentService.update(paymentDto));
    }
}
