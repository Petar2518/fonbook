package rs.ac.bg.fon.hostservice.constraints;

import rs.ac.bg.fon.hostservice.constraints.validator.BankAccountNumberValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BankAccountNumberValidatorTest {

    @Test
    void isValid_withValidBankAccount_returnsTrue() {

        BankAccountNumberValidator validator = new BankAccountNumberValidator();
        String invalidField = "12345678901234567";


        boolean isValid = validator.isValid(invalidField, null);

        assertTrue(isValid, "Expected bank account number to be true");
    }

    @Test
    void isValid_withInvalidBankAccount_returnsFalse() {

        BankAccountNumberValidator validator = new BankAccountNumberValidator();
        String invalidField = "invalid123";


        boolean isValid = validator.isValid(invalidField, null);


        assertFalse(isValid, "Expected bank account number to be false");
    }

    @Test
    void isValid_withShortBankAccount_returnsFalse() {

        BankAccountNumberValidator validator = new BankAccountNumberValidator();
        String invalidField = "1234";


        boolean isValid = validator.isValid(invalidField, null);


        assertFalse(isValid, "Expected bank account number to be false");
    }
}