package dev.skosarev.accountservice.repository;


import dev.skosarev.accountservice.model.Payment;
import dev.skosarev.accountservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional(readOnly = true)
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Override
    @Transactional
    <S extends Payment> List<S> saveAll(Iterable<S> entities);

    @Override
    @Transactional
    <S extends Payment> S save(S entity);

    Payment findByPeriodAndEmployee(Date period, User employee);

    List<Payment> findAllByEmployee(User employee);
}
