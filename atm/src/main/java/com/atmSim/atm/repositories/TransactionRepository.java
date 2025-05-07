    package com.atmSim.atm.repositories;

    import com.atmSim.atm.entities.Transaction;
    import org.springframework.data.repository.CrudRepository;

    import java.util.List;
    import java.util.Optional;

    public interface TransactionRepository extends CrudRepository<Transaction, Long> {
        List<Transaction> findByUserId(Long userId); // Correct method name
    }
