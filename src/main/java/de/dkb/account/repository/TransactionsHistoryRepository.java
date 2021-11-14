package de.dkb.account.repository;

import de.dkb.account.model.TransactionsHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionsHistoryRepository extends JpaRepository<TransactionsHistory, Long> {

    List<TransactionsHistory> findAllByIban(String iban);
}
