package com.test.ciptakoindigital.service;

import com.test.ciptakoindigital.dto.TransactionRequest;
import com.test.ciptakoindigital.entity.Transaction;
import com.test.ciptakoindigital.entity.User;
import com.test.ciptakoindigital.repository.TransactionRepository;
import com.test.ciptakoindigital.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    public Transaction createTransaction(String username, TransactionRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Transaction tx = new Transaction();
        tx.setAmount(request.getAmount());
        tx.setUser(user);
        tx.setCreatedAt(LocalDateTime.now());
        return transactionRepository.save(tx);
    }

    public List<Transaction> getUserTransactions(String username) {
        return transactionRepository.findByUserUsername(username);
    }

    public Transaction create(Transaction t) {
        t.setCreatedAt(LocalDateTime.now());
        return transactionRepository.save(t);
    }

    public Transaction update(Long id, Transaction t) {
        Transaction existing = transactionRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Transaksi tidak ditemukan")
        );
        existing.setDescription(t.getDescription());
        existing.setAmount(t.getAmount());
        existing.setUpdatedAt(LocalDateTime.now());
        return transactionRepository.save(existing);
    }
}
