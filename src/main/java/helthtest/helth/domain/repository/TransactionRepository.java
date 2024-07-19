package helthtest.helth.domain.repository;

import helthtest.helth.domain.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity,Long> {

    List<TransactionEntity> findByAccountIdAndTransactedAtBetween(
            Long accountId, LocalDateTime startDate, LocalDateTime endDate
    );
}
