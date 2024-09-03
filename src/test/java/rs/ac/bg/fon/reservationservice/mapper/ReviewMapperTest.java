package rs.ac.bg.fon.reservationservice.mapper;

import rs.ac.bg.fon.reservationservice.domain.ReviewDomain;
import rs.ac.bg.fon.reservationservice.dto.CreateReviewDto;
import rs.ac.bg.fon.reservationservice.dto.ReviewDto;
import rs.ac.bg.fon.reservationservice.dto.UpdateReviewDto;
import rs.ac.bg.fon.reservationservice.model.Review;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Tag("springboot")
public class ReviewMapperTest {

    @Autowired
    ReviewMapper reviewMapper;


    @Test
    public void mapReviewDomainToEntity_expectSuccess(){
        ReviewDomain reviewDomain = newReviewDomain();

        Review review = reviewMapper.fromDomainToEntity(reviewDomain);

        assertEquals(reviewDomain.getComment(), review.getComment());
        assertEquals(reviewDomain.getTitle(), review.getTitle());
        assertEquals(reviewDomain.getRating(), review.getRating());

    }

    @Test
    public void mapCreateDtoToDomain_expectSuccess(){
        CreateReviewDto createReviewDto = newCreateReviewDto();

        ReviewDomain reviewDomain = reviewMapper.fromCreateDtoToDomain(createReviewDto);

        assertEquals(reviewDomain.getComment(), createReviewDto.getComment());
        assertEquals(reviewDomain.getTitle(), createReviewDto.getTitle());
        assertEquals(reviewDomain.getRating(), createReviewDto.getRating());
    }
    @Test
    public void mapEntityToDomain_expectSuccess(){
        Review review = newReview();

        ReviewDomain reviewDomain = reviewMapper.fromEntityToDomain(review);

        assertEquals(reviewDomain.getComment(), review.getComment());
        assertEquals(reviewDomain.getTitle(), review.getTitle());
        assertEquals(reviewDomain.getRating(), review.getRating());

    }

    @Test
    public void mapReviewDomainToDto_expectSuccess(){
        ReviewDomain reviewDomain = newReviewDomain();

        ReviewDto reviewDto = reviewMapper.fromDomainToDto(reviewDomain);

        assertEquals(reviewDomain.getComment(), reviewDto.getComment());
        assertEquals(reviewDomain.getTitle(), reviewDto.getTitle());
        assertEquals(reviewDomain.getRating(), reviewDto.getRating());

    }

    @Test
    public void whenCalled_shouldMapUpdateReviewDtoToDomain(){
        UpdateReviewDto updateReviewDto = newUpdateReviewDto();

        ReviewDomain reviewDomain = reviewMapper.fromUpdateReviewDtoToDomain(updateReviewDto);

        assertEquals(reviewDomain.getComment(), updateReviewDto.getComment());
        assertEquals(reviewDomain.getTitle(), updateReviewDto.getTitle());
        assertEquals(reviewDomain.getRating(), updateReviewDto.getRating());

    }


    Review newReview(){
        return Review.builder()
                .title("Exceptional")
                .comment("This guest didn't leave a comment")
                .rating(8.0)
                .build();
    }

    ReviewDomain newReviewDomain(){
        return ReviewDomain.builder()
                .title("Exceptional")
                .comment("This guest didn't leave a comment")
                .rating(8.00)
                .build();
    }
    CreateReviewDto newCreateReviewDto(){
        return CreateReviewDto.builder()
                .title("Exceptional")
                .comment("This guest didn't leave a comment")
                .rating(8.0)
                .build();
    }
    UpdateReviewDto newUpdateReviewDto(){
        return UpdateReviewDto.builder()
                .title("Exceptional")
                .comment("This guest didn't leave a comment")
                .rating(8.0)
                .build();
    }


}
