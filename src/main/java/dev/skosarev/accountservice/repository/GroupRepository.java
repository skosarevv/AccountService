package dev.skosarev.accountservice.repository;

import dev.skosarev.accountservice.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface GroupRepository extends JpaRepository<Group, Long> {
    Group findByName(String name);
}
