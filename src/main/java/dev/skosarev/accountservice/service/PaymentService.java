package dev.skosarev.accountservice.service;

import dev.skosarev.accountservice.dto.PaymentDto;
import dev.skosarev.accountservice.dto.PaymentForOutput;
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
import java.util.*;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final SimpleDateFormat inputFormatter = new SimpleDateFormat("MM-yyyy");
    private final SimpleDateFormat outputFormatter = new SimpleDateFormat("MMMM-yyyy");

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        inputFormatter.setLenient(false);
    }

    public PaymentForOutput getPayment(String period, User user) {
        Date datePeriod = convertPeriodToDate(period);

        Payment payment = paymentRepository.findByPeriodAndEmployee(datePeriod, user);
        String salary = String.format("%d dollar(s) %d cent(s)", payment.getSalary() / 100, payment.getSalary() % 100);
        return new PaymentForOutput(user.getName(), user.getLastname(), outputFormatter.format(datePeriod), salary);
    }

    public List<PaymentForOutput> getAllPayments(User user) {
        List<Payment> paymentList = paymentRepository.findAllByEmployee(user);

        List<PaymentForOutput> result = new ArrayList<>();
        for (Payment payment : paymentList) {
            String salary = String.format("%d dollar(s) %d cent(s)", payment.getSalary() / 100, payment.getSalary() % 100);
            result.add(new PaymentForOutput(user.getName(), user.getLastname(), outputFormatter.format(payment.getPeriod()), salary));
        }

        return result;
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
        return new Payment(user.get(), convertPeriodToDate(paymentDto.getPeriod()), paymentDto.getSalary());
    }

    private Date convertPeriodToDate(String period) {
        try {
            return inputFormatter.parse(period);
        } catch (ParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong date!");
        }
    }
}
