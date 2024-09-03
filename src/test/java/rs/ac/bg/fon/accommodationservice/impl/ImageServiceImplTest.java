package rs.ac.bg.fon.accommodationservice.service.impl;

import rs.ac.bg.fon.accommodationservice.adapters.serviceRepositoryAdapters.AccommodationDomainEntityAdapter;
import rs.ac.bg.fon.accommodationservice.adapters.serviceRepositoryAdapters.ImageDomainEntityAdapter;
import rs.ac.bg.fon.accommodationservice.domain.AccommodationDomain;
import rs.ac.bg.fon.accommodationservice.domain.ImageDomain;
import rs.ac.bg.fon.accommodationservice.exception.specific.ImageNotFoundException;
import rs.ac.bg.fon.accommodationservice.model.AccommodationType;
import rs.ac.bg.fon.accommodationservice.model.JwtReceiver.Role;
import rs.ac.bg.fon.accommodationservice.model.JwtReceiver.UserInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceImplTest {

    @Mock
    AccommodationDomainEntityAdapter accommodationDomainEntityAdapter;

    @InjectMocks
    AccommodationServiceImpl accommodationService;
    @Mock
    ImageDomainEntityAdapter imageDomainEntityAdapter;

    @InjectMocks
    ImageServiceImpl imageService;


    @Test
    void createImageSuccessfully() {
        UserInfo userInfo = UserInfo.builder()
                .role(Role.HOST)
                .id(5L)
                .build();
        String name = "Hilton Belgrade";
        AccommodationDomain accommodationDomain = AccommodationDomain.builder()
                .name(name)
                .accommodationType(AccommodationType.APARTMENT)
                .description("Nice accommodation in Center of city")
                .hostId(5L)
                .build();

        when(accommodationDomainEntityAdapter.save(accommodationDomain)).thenReturn(1L);

        ArgumentCaptor<AccommodationDomain> captor = ArgumentCaptor.forClass(AccommodationDomain.class);

        accommodationService.save(accommodationDomain, userInfo);

        verify(accommodationDomainEntityAdapter, times(1)).save(captor.capture());

        AccommodationDomain capturedAccommodation = captor.getValue();
        assertThat(capturedAccommodation).isEqualTo(accommodationDomain);

        ImageDomain imageDomain = ImageDomain.builder()
                .accommodation(accommodationDomain)
                .image("We are checking. . .".getBytes())
                .build();

        when(imageDomainEntityAdapter.save(imageDomain)).thenReturn(1L);

        ArgumentCaptor<ImageDomain> captorUnit = ArgumentCaptor.forClass(ImageDomain.class);

        imageService.save(imageDomain);

        verify(imageDomainEntityAdapter, times(1)).save(captorUnit.capture());

        ImageDomain capturedImage = captorUnit.getValue();
        assertThat(capturedImage).isEqualTo(imageDomain);

    }

    @Test
    void findImageById() {
        String name = "Fancy Accommodation";
        Long id = 1L;
        AccommodationDomain accommodationDomain = AccommodationDomain.builder()
                .id(id)
                .name(name)
                .accommodationType(AccommodationType.APARTMENT)
                .description("Nice accommodation in Center of city")
                .hostId(5L)
                .build();
        ImageDomain imageDomain = ImageDomain.builder()
                .id(id)
                .accommodation(accommodationDomain)
                .image("We are checking. . .".getBytes())
                .build();
        when(imageDomainEntityAdapter.findById(id)).thenReturn(Optional.of(imageDomain));

        ImageDomain actualImage = imageService.findById(id);

        assertThat(actualImage).isNotNull();
        assertThat(actualImage.getId()).isEqualTo(id);
        assertThat(actualImage.getImage()).isEqualTo("We are checking. . .".getBytes());

    }

    @Test
    void findImageWhenIdDoesNotExist() {
        Long id = 1L;
        when(imageDomainEntityAdapter.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> imageService.findById(id))
                .isInstanceOf(ImageNotFoundException.class)
                .hasMessage("Image with id: " + id + " doesn't exist");
    }

    @Test
    void deleteImageById() {
        String name = "Fancy Accommodation";
        Long id = 1L;
        AccommodationDomain accommodationDomain = AccommodationDomain.builder()
                .id(id)
                .name(name)
                .accommodationType(AccommodationType.APARTMENT)
                .description("Nice accommodation in Center of city")
                .hostId(5L)
                .build();

        ImageDomain imageDomain = ImageDomain.builder()
                .accommodation(accommodationDomain)
                .image("We are checking. . .".getBytes())
                .build();

        imageService.deleteById(id);
        verify(imageDomainEntityAdapter).deleteById(id);

    }
}