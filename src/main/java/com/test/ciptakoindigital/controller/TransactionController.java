package com.test.ciptakoindigital.controller;

import com.test.ciptakoindigital.dto.TransactionRequest;
import com.test.ciptakoindigital.entity.Transaction;
import com.test.ciptakoindigital.entity.User;
import com.test.ciptakoindigital.repository.TransactionRepository;
import com.test.ciptakoindigital.repository.UserRepository;
import com.test.ciptakoindigital.security.JwtUtil;
import com.test.ciptakoindigital.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
    @Autowired
    private TransactionRepository trxRepo;
    @Autowired private UserRepository userRepo;

    @Autowired
    private TransactionService transactionService;


    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody TransactionRequest request) {

        String token = authHeader.substring(7); // "Bearer "
        String username = jwtUtil.extractUsername(token);

        Transaction tx = transactionService.createTransaction(username, request);
        return ResponseEntity.ok(tx);
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getTransactions(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        return ResponseEntity.ok(transactionService.getUserTransactions(username));
    }

    @PostMapping("/process")
    public ResponseEntity<?> processTransaction(@RequestBody Map<String, Object> payload) {
        try {
            // Ambil user dari JWT
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

            Transaction transaction;

            if (payload.containsKey("id")) {
                // ==== UPDATE TRANSAKSI ====
                Long transactionId = Long.parseLong(payload.get("id").toString());
                transaction = trxRepo.findById(transactionId)
                        .orElseThrow(() -> new RuntimeException("Transaksi tidak ditemukan"));

                // Cek apakah transaksi ini milik user (opsional, kalau pengen ada proteksi)
                if (!transaction.getUser().getId().equals(user.getId())) {
                    throw new RuntimeException("Anda tidak berhak mengedit transaksi ini.");
                }

                // Update field yang bisa diubah
                transaction.setDescription((String) payload.get("description"));
                transaction.setAmount(Double.parseDouble(payload.get("amount").toString()));
                transaction.setType((String) payload.get("type")); // Pastikan ada field `type` kalau dibutuhkan
            } else {
                // ==== CREATE TRANSAKSI BARU ====
                transaction = new Transaction();
                transaction.setDescription((String) payload.get("description"));
                transaction.setAmount(Double.parseDouble(payload.get("amount").toString()));
                transaction.setType((String) payload.get("type"));
                transaction.setUser(user);
                transaction.setCreatedAt(LocalDateTime.now());
            }

            Transaction savedTransaction = transactionService.create(transaction);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", payload.containsKey("id") ? "Transaksi berhasil diperbarui" : "Transaksi berhasil dibuat");
            response.put("data", savedTransaction);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Terjadi kesalahan: " + e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

//    @PostMapping("/process")
//    public ResponseEntity<?> processTransaction(@RequestBody Map<String, Object> payload) {
//        try {
//
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            String username = authentication.getName(); // asumsi username adalah email atau username
//
//            User user = userRepo.findByUsername(username)
//                    .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
//
//
//            Transaction transaction = new Transaction();
//            transaction.setDescription((String) payload.get("description"));
//            transaction.setAmount(Double.parseDouble(payload.get("amount").toString()));
//            transaction.setUser(user);
//
//            Transaction savedTransaction;
//            Map<String, Object> response = new HashMap<>();
//
//            if (payload.containsKey("id")) {
//                Long id = Long.parseLong(payload.get("id").toString());
//                savedTransaction = transactionService.update(id, transaction);
//                response.put("success", true);
//                response.put("message", "Transaksi berhasil diupdate");
//                response.put("data", savedTransaction);
//            } else {
//                savedTransaction = transactionService.create(transaction);
//                response.put("success", true);
//                response.put("message", "Transaksi berhasil dibuat");
//                response.put("data", savedTransaction);
//            }
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("success", false);
//            errorResponse.put("message", "Terjadi kesalahan: " + e.getMessage());
//            errorResponse.put("data", null);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
//        }
//    }



//    @GetMapping("/transaction")
//    public ResponseEntity<?> getTransactions(Authentication auth) {
//        User user = userRepo.findByUsername(auth.getName());
//        List<Transaction> data = trxRepo.findByUser(user);
//        return ResponseEntity.ok(response(true, "Fetched", data));
//    }
//
//    @PostMapping("/transaction/process")
//    public ResponseEntity<?> process(@RequestBody Transaction trx, Authentication auth) {
//        User user = userRepo.findByUsername(auth.getName());
//        if (trx.getId() != null) {
//            Transaction existing = trxRepo.findById(trx.getId()).orElse(null);
//            if (existing == null) return ResponseEntity.ok(response(false, "Not found", null));
//            existing.setAmount(trx.getAmount());
//            existing.setType(trx.getType());
//            trxRepo.save(existing);
//            return ResponseEntity.ok(response(true, "Updated", existing));
//        } else {
//            trx.setUser(user);
//            trxRepo.save(trx);
//            return ResponseEntity.ok(response(true, "Created", trx));
//        }
//    }

    private Map<String, Object> response(boolean success, String message, Object data) {
        return Map.of("success", success, "message", message, "data", data);
    }
}
