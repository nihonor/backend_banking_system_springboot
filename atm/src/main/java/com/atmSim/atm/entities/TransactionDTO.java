package com.atmSim.atm.entities;

import com.atmSim.atm.entities.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TransactionDTO {
    private Long id;
    private String type;
    private Double amount;
    private Instant timestamp;

    // constructor, getters, setters

    public TransactionDTO(Transaction transaction) {
        this.id = Long.valueOf(transaction.getId());
        this.type = transaction.getType();
        this.amount = transaction.getAmount();
        this.timestamp = transaction.getTimestamp();
    }
}