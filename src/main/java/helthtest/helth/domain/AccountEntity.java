package helthtest.helth.domain;

import helthtest.helth.type.Bank;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@AuditOverride(forClass = BaseEntity.class)
public class AccountEntity extends BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserEntity user;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Bank bank;

    @NotNull
    @Column(unique = true)
    private String accountNumber;

    @NotNull
    private String accountName;

    @NotNull
    @Min(value = 0, message = "계좌의 잔액은 0원 이상이어야 합니다.")
    private Long amount;

    @NotNull
    private Boolean isDeleted;

    private LocalDateTime deletedAt;

    public AccountEntity withIsDeleted(Boolean isDeleted) {
        return AccountEntity.builder()
                .id(this.id)
                .user(this.user)
                .bank(this.bank)
                .accountNumber(this.accountNumber)
                .accountName(this.accountName)
                .amount(this.amount)
                .isDeleted(isDeleted)
                .deletedAt(this.deletedAt)
                .build();
    }

    public AccountEntity withDeletedAt(LocalDateTime deletedAt) {
        return AccountEntity.builder()
                .id(this.id)
                .user(this.user)
                .bank(this.bank)
                .accountNumber(this.accountNumber)
                .accountName(this.accountName)
                .amount(this.amount)
                .isDeleted(this.isDeleted)
                .deletedAt(deletedAt)
                .build();
    }
}

