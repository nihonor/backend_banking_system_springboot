package com.atmSim.atm.repositories;

import com.atmSim.atm.entities.Account;
import com.atmSim.atm.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserId(Long userId);

    @Transactional
    void deleteByFromAccountOrToAccount(Account fromAccount, Account toAccount);
}
