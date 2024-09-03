package rs.ac.bg.fon.searchservice.constraint.validator;

import rs.ac.bg.fon.searchservice.constraint.PriceConstraint;
import rs.ac.bg.fon.searchservice.dto.SearchRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PriceValidator implements ConstraintValidator<PriceConstraint, SearchRequest> {
    @Override
    public void initialize(PriceConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(SearchRequest searchRequest, ConstraintValidatorContext constraintValidatorContext) {
        if (searchRequest.getMinPrice() != null && searchRequest.getMaxPrice() != null)
            return searchRequest.getMaxPrice().compareTo(searchRequest.getMinPrice()) > 0;

        return true;
    }


}