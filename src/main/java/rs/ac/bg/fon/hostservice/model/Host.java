package rs.ac.bg.fon.hostservice.model;

import rs.ac.bg.fon.hostservice.constraints.BankAccountNumberConstraint;
import rs.ac.bg.fon.hostservice.constraints.PhoneNumberConstraint;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@Data
@Entity
@Builder
@NoArgsConstructor
@Table(name = "hosts")
public class Host {

    @Id
    private Long id;

    @NotBlank
    private String name;

    @Column(name = "phone_number")
    @PhoneNumberConstraint
    @NotBlank
    private String phoneNumber;


    @Column(name = "bank_account_number")
    @BankAccountNumberConstraint
    @NotBlank
    private String bankAccountNumber;

    private boolean active;
}
