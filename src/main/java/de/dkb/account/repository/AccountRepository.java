package de.dkb.account.repository;

import de.dkb.account.model.Account;
import de.dkb.account.model.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByIban(String iban);

    List<Account> findByTypeIn(List<AccountType> types);
}
