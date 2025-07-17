package com.test.ciptakoindigital.repository;

import com.test.ciptakoindigital.entity.Transaction;
import com.test.ciptakoindigital.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser(User user);
    List<Transaction> findByUserUsername(String username);
}
