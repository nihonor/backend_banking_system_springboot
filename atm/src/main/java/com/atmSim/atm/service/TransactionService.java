package com.atmSim.atm.service;

import com.atmSim.atm.entities.Transaction;
import com.atmSim.atm.repositories.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> getAllTransactionsByUserId(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

}
