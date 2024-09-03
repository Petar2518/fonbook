package rs.ac.bg.fon.bookinggateway.config;

import rs.ac.bg.fon.bookinggateway.filters.AuthenticationFilter;
import rs.ac.bg.fon.bookinggateway.filters.AuthorizationFilter;
import rs.ac.bg.fon.bookinggateway.filters.DeleteProfileFilter;
import rs.ac.bg.fon.bookinggateway.filters.RegistrationRedirectionFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration

public class BookingGatewayConfig {

    @Value("${service.authentication}")
    private String authenticationServiceHost;
    @Value("${service.user}")
    private String userServiceHost;
    @Value("${service.host}")
    private String hostServiceHost;
    @Value("${service.accommodation}")
    private String accommodationServiceHost;
    @Value("${service.reservation}")
    private String reservationServiceHost;
    @Value("${service.search}")
    private String searchServiceHost;
    @Autowired
    private RegistrationRedirectionFilter registrationFilter;
    @Autowired
    private AuthenticationFilter authFilter;
    @Autowired
    private DeleteProfileFilter deleteFilter;
    @Autowired
    private AuthorizationFilter authorizationFilter;

    private GatewayFilter authenticationFilter;
    private GatewayFilter deleteProfileFilter;
    private GatewayFilter hostAuthorizationFilter;
    private GatewayFilter userAuthorizationFilter;

    @Bean
    public RouteLocator bookingRoutes(RouteLocatorBuilder builder) {
        authenticationFilter = authFilter.apply(new AuthenticationFilter.Config(authenticationServiceHost));
        deleteProfileFilter = deleteFilter.apply(new DeleteProfileFilter.Config(authenticationServiceHost));
        hostAuthorizationFilter = authorizationFilter.apply(new AuthorizationFilter.Config("HOST"));
        userAuthorizationFilter = authorizationFilter.apply(new AuthorizationFilter.Config("USER"));

        return builder.routes()
                .route(this::registrationRoute)
                .route(this::loginRoute)
                .route(this::logoutRoute)
                .route(this::updatePasswordRoute)

                .route(this::forgotPasswordRoute)
                .route(this::resetPasswordRoute)

                .route(this::getAndUpdateUserRoute)
                .route(this::getAndUpdateHostRoute)
                .route(this::deleteUserRoute)
                .route(this::deleteHostRoute)

                .route(this::postPutAccommodationUnitRoute)
                .route(this::getDeleteAccommodationUnitRoute)

                .route(this::getAccommodationRoute)
                .route(this::postPutDeleteAccommodationRoute)
                .route(this::getHostsAccommodationsRoute)

                .route(this::getAccommodationAddressRoute)
                .route(this::postPutAccommodationAddressRoute)
                .route(this::postAccommodationImagesRoute)
                .route(this::deleteAccommodationImagesAndPriceRoute)
                .route(this::getAccommodationReviews)

                .route(this::getAccommodationUnitPricesRoute)
                .route(this::postAccommodationUnitPricesRoute)

                .route(this::postReservationRequestAndReviewRoute)
                .route(this::deleteReservationRoute)
                .route(this::getReservationsByProfileId)
                .route(this::updateRequestStatus)
                .route(this::updateReservationPaidStatus)
                .route(this::getReservationPendingRequest)

                .route(this::searchRoute)

                .build();
    }

    private Buildable<Route> registrationRoute(PredicateSpec p) {
        return p.method(HttpMethod.POST).and()
                .readBody(String.class, i -> true).and()
                .path("/register")
                .filters(f -> f
                        .filter(registrationFilter.apply(new RegistrationRedirectionFilter.Config(userServiceHost, hostServiceHost, authenticationServiceHost)))
                        .setPath("/accounts"))
                .uri(authenticationServiceHost);
    }

    private Buildable<Route> loginRoute(PredicateSpec p) {
        return createRoute(p,
                new HttpMethod[]{HttpMethod.POST},
                new String[]{"/login"},
                "/auth/login",
                authenticationServiceHost);
    }

    private Buildable<Route> logoutRoute(PredicateSpec p) {
        return createRoute(p,
                new HttpMethod[]{HttpMethod.POST},
                new String[]{"/logout"},
                "/auth/logout",
                authenticationServiceHost);
    }

    private Buildable<Route> updatePasswordRoute(PredicateSpec p) {
        return createRoute(p,
                new HttpMethod[]{HttpMethod.PUT},
                new String[]{"/update-password"},
                "/accounts/update-password",
                authenticationServiceHost);
    }

    private Buildable<Route> forgotPasswordRoute(PredicateSpec p) {
        return createRoute(p,
                new HttpMethod[]{HttpMethod.POST},
                new String[]{"/forgot-password"},
                "/accounts/forgot-password",
                authenticationServiceHost);
    }

    private Buildable<Route> resetPasswordRoute(PredicateSpec p) {
        return createRoute(p,
                new HttpMethod[]{HttpMethod.PUT},
                new String[]{"/reset-password/{token}"},
                "/accounts/reset-password/{token}",
                authenticationServiceHost);
    }

    private Buildable<Route> getAndUpdateUserRoute(PredicateSpec p) {
        return createRoute(p,
                new HttpMethod[]{HttpMethod.GET, HttpMethod.PUT},
                new String[]{"/users/{id}", "/users"},
                null,
                userServiceHost,
                authenticationFilter);
    }

    private Buildable<Route> getAndUpdateHostRoute(PredicateSpec p) {
        return createRoute(p,
                new HttpMethod[]{HttpMethod.GET, HttpMethod.PUT},
                new String[]{"/hosts/{id}", "/hosts"},
                null,
                hostServiceHost,
                authenticationFilter);
    }

    private Buildable<Route> deleteUserRoute(PredicateSpec p) {
        return createRoute(p,
                new HttpMethod[]{HttpMethod.DELETE},
                new String[]{"/users/**"},
                null,
                userServiceHost,
                deleteProfileFilter);
    }

    private Buildable<Route> deleteHostRoute(PredicateSpec p) {
        return createRoute(p,
                new HttpMethod[]{HttpMethod.DELETE},
                new String[]{"/hosts/**"},
                null,
                hostServiceHost,
                deleteProfileFilter);
    }

    private Buildable<Route> getAccommodationRoute(PredicateSpec p) {
        return createRoute(p,
                new HttpMethod[]{HttpMethod.GET},
                new String[]{"/accommodations/{id}", "/accommodations/{id}/rooms", "/accommodations/{id}/images"},
                null,
                accommodationServiceHost,
                authenticationFilter);
    }

    private Buildable<Route> getAccommodationAddressRoute(PredicateSpec p) {
        return createRoute(p,
                new HttpMethod[]{HttpMethod.GET},
                new String[]{"/accommodations/{id}/address"},
                "/addresses/{id}",
                accommodationServiceHost,
                authenticationFilter);
    }

    private Buildable<Route> postPutAccommodationAddressRoute(PredicateSpec p) {
        return createRoute(p,
                new HttpMethod[]{HttpMethod.POST, HttpMethod.PUT},
                new String[]{"/accommodations/{id}/address"},
                "/addresses",
                accommodationServiceHost,
                authenticationFilter,
                hostAuthorizationFilter);
    }

    private Buildable<Route> postAccommodationImagesRoute(PredicateSpec p) {
        return createRoute(p,
                new HttpMethod[]{HttpMethod.POST},
                new String[]{"/accommodations/{id}/images"},
                "/images",
                accommodationServiceHost,
                authenticationFilter,
                hostAuthorizationFilter);
    }

    private Buildable<Route> deleteAccommodationImagesAndPriceRoute(PredicateSpec p) {
        return createRoute(p,
                new HttpMethod[]{HttpMethod.DELETE},
                new String[]{"/images/{imageId}", "/prices/{priceId}"},
                null,
                accommodationServiceHost,
                authenticationFilter,
                hostAuthorizationFilter);
    }

    private Buildable<Route> getAccommodationReviews(PredicateSpec p) {
        return createRoute(p,
                new HttpMethod[]{HttpMethod.GET},
                new String[]{"/accommodations/{id}/reviews"},
                null,
                reservationServiceHost,
                authenticationFilter
        );
    }

    private Buildable<Route> getAccommodationUnitPricesRoute(PredicateSpec p) {
        return createRoute(p,
                new HttpMethod[]{HttpMethod.GET},
                new String[]{"/accommodations/rooms/{id}/prices"},
                "/prices/{id}/price",
                accommodationServiceHost,
                authenticationFilter);
    }

    private Buildable<Route> postAccommodationUnitPricesRoute(PredicateSpec p) {
        return createRoute(p,
                new HttpMethod[]{HttpMethod.POST},
                new String[]{"/accommodations/rooms/{id}/prices"},
                "/prices",
                accommodationServiceHost,
                authenticationFilter,
                hostAuthorizationFilter);
    }

    private Buildable<Route> postPutAccommodationUnitRoute(PredicateSpec p) {
        return createRoute(p,
                new HttpMethod[]{HttpMethod.POST, HttpMethod.PUT},
                new String[]{"/accommodations/rooms"},
                "/rooms",
                accommodationServiceHost,
                authenticationFilter,
                hostAuthorizationFilter);
    }

    private Buildable<Route> getDeleteAccommodationUnitRoute(PredicateSpec p) {
        return createRoute(p,
                new HttpMethod[]{HttpMethod.DELETE, HttpMethod.GET},
                new String[]{"/accommodations/rooms/{id}"},
                "/rooms/{id}",
                accommodationServiceHost,
                authenticationFilter,
                hostAuthorizationFilter);
    }

    private Buildable<Route> postPutDeleteAccommodationRoute(PredicateSpec p) {
        HttpMethod[] methods = new HttpMethod[]{HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE};
        String[] paths = new String[]{"/accommodations", "/accommodations/{id}"};

        return createRoute(p,
                methods,
                paths,
                null,
                accommodationServiceHost,
                authenticationFilter,
                hostAuthorizationFilter);
    }

    private Buildable<Route> getHostsAccommodationsRoute(PredicateSpec p) {
        return createRoute(p,
                new HttpMethod[]{HttpMethod.GET},
                new String[]{"/my-accommodations/{segment}"},
                "/accommodations/hosts/{segment}",
                accommodationServiceHost,
                authenticationFilter,
                hostAuthorizationFilter);
    }

    private Buildable<Route> deleteReservationRoute(PredicateSpec p) {
        return createRoute(p,
                new HttpMethod[]{HttpMethod.DELETE},
                new String[]{"/reservations/{reservationId}"},
                "/reservations/{reservationId}",
                reservationServiceHost,
                authenticationFilter);
    }

    private Buildable<Route> postReservationRequestAndReviewRoute(PredicateSpec p) {
        return createRoute(p,
                new HttpMethod[]{HttpMethod.POST},
                new String[]{"/reservations", "/reservations/{resId}/pendingRequests", "/reservations/{resId}/reviews"},
                null,
                reservationServiceHost,
                authenticationFilter,
                userAuthorizationFilter);
    }

    private Buildable<Route> getReservationsByProfileId(PredicateSpec p) {
        return createRoute(p,
                new HttpMethod[]{HttpMethod.GET},
                new String[]{"/reservations/{profileId}"},
                "/my-reservations",
                reservationServiceHost,
                authenticationFilter);
    }

    private Buildable<Route> updateRequestStatus(PredicateSpec p) {
        return createRoute(p,
                new HttpMethod[]{HttpMethod.PUT},
                new String[]{"/reservations/{resId}/pendingRequests"},
                null,
                reservationServiceHost,
                authenticationFilter,
                hostAuthorizationFilter);
    }

    private Buildable<Route> updateReservationPaidStatus(PredicateSpec p) {
        return createRoute(p,
                new HttpMethod[]{HttpMethod.PATCH},
                new String[]{"/reservations/{resId}/process-payment"},
                "/reservations/{resId}/pay",
                reservationServiceHost,
                authenticationFilter,
                userAuthorizationFilter);
    }

    private Buildable<Route> getReservationPendingRequest(PredicateSpec p) {
        return createRoute(p,
                new HttpMethod[]{HttpMethod.GET},
                new String[]{"/reservations/{resId}/pendingRequests"},
                null,
                reservationServiceHost,
                authenticationFilter);
    }

    private Buildable<Route> searchRoute(PredicateSpec p) {
        return createRoute(p,
                new HttpMethod[]{HttpMethod.GET},
                new String[]{"/search/"},
                null,
                searchServiceHost,
                authenticationFilter,
                userAuthorizationFilter);
    }

    private Buildable<Route> createRoute(PredicateSpec p, HttpMethod[] methods, String[] paths, String setPath, String uri, GatewayFilter... filters) {
        return p.method(methods).and()
                .path(paths)
                .filters(f -> addFilters(f, setPath, filters))
                .uri(uri);
    }

    private UriSpec addFilters(GatewayFilterSpec f, String setPath, GatewayFilter... filters) {
        GatewayFilterSpec filterSpec = f;
        for (GatewayFilter filter : filters) {
            filterSpec = filterSpec.filter(filter);
        }
        if (setPath != null)
            filterSpec = filterSpec.setPath(setPath);

        return filterSpec;
    }
}