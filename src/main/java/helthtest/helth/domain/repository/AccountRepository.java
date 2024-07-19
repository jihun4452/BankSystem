package helthtest.helth.domain.repository;

import helthtest.helth.domain.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity,Long> {
    Optional<AccountEntity> findFirstByOrderByIdDesc();

    Optional<AccountEntity> findByAccountNumber(String accountNumber);
}
//조회하고 없다면 빈 optinonal 반환