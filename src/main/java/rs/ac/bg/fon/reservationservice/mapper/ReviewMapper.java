package rs.ac.bg.fon.reservationservice.mapper;


import rs.ac.bg.fon.reservationservice.domain.ReviewDomain;
import rs.ac.bg.fon.reservationservice.domain.UpdateReviewDomain;
import rs.ac.bg.fon.reservationservice.dto.CreateReviewDto;
import rs.ac.bg.fon.reservationservice.dto.ReviewDto;
import rs.ac.bg.fon.reservationservice.dto.UpdateReviewDto;
import rs.ac.bg.fon.reservationservice.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ReservationMapper.class)
public interface ReviewMapper {
    @Mapping(target = "reservation.id", source = "reviewDomain.id")
    @Mapping(target = "accommodationId", source = "accommodationId")
    Review fromDomainToEntity(ReviewDomain reviewDomain, Long accommodationId);

    @Mapping(target = "reservation.id", source = "reviewDomain.id")
    Review fromDomainToEntity(ReviewDomain reviewDomain);

    ReviewDomain fromCreateDtoToDomain(CreateReviewDto createReviewDto);

    ReviewDomain fromEntityToDomain(Review review);
    ReviewDto fromDomainToDto(ReviewDomain reviewDomain);

    ReviewDomain fromUpdateReviewDtoToDomain(UpdateReviewDto updateReviewDto);
    UpdateReviewDomain fromUpdateReviewDtoToUpdateReviewDomain(UpdateReviewDto updateReviewDto);


}

