package helthtest.helth.domain;

import helthtest.helth.type.Transaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class TransactionEntity {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private AccountEntity account;

    @Enumerated(EnumType.STRING)
    private Transaction transactionType;

    @NotNull
    private Long amount;

    private String depositName;

    private String withdrawName;

    private String receivedName;

    private String receivedAccount;

    @CreatedDate
    private LocalDateTime transactedAt;


}
