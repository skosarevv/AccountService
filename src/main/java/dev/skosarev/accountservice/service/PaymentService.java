package dev.skosarev.accountservice.service;

import dev.skosarev.accountservice.dto.PaymentDto;
import dev.skosarev.accountservice.model.Payment;
import dev.skosarev.accountservice.model.User;
import dev.skosarev.accountservice.repository.PaymentRepository;
import dev.skosarev.accountservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final SimpleDateFormat formatter = new SimpleDateFormat("MM-yyyy");

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        formatter.setLenient(false);
    }

    public Map<String, String> saveAllDto(List<PaymentDto> paymentDtos) {
        List<Payment> paymentList = convertDtoListToEntityList(paymentDtos);
        try {
            paymentRepository.saveAll(paymentList);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User + period must be unique!");
        }
        return Map.of("status", "Added successfully!");
    }

    public Map<String, String> update(PaymentDto paymentDto) {
        Payment updatedPayment = convertDtoToEntity(paymentDto);
        Payment paymentToUpdate = paymentRepository.findByPeriodAndEmployee(updatedPayment.getPeriod(), updatedPayment.getEmployee());
        if (paymentToUpdate == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No such period!");
        }
        updatedPayment.setId(paymentToUpdate.getId());
        paymentRepository.save(updatedPayment);
        return Map.of("status", "Updated successfully!");
    }

    private List<Payment> convertDtoListToEntityList(List<PaymentDto> paymentDtos) {
        List<Payment> result = new ArrayList<>();
        for (PaymentDto dto : paymentDtos) {
            result.add(convertDtoToEntity(dto));
        }
        return result;
    }

    private Payment convertDtoToEntity(PaymentDto paymentDto) {
        Optional<User> user = userRepository.findByEmailIgnoreCase(paymentDto.getEmail());
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not exists!");
        }
        try {
            return new Payment(user.get(), formatter.parse(paymentDto.getPeriod()), paymentDto.getSalary());
        } catch (ParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong date!");
        }
    }
}
