package rs.ac.bg.fon.reservationservice.util;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class WireMockTestHelper {
    private static WireMockServer wireMockServer;

    public static void start() {
        wireMockServer = new WireMockServer(8082);
        wireMockServer.start();
    }

    public static void setUp() {
        WireMockTestHelper.stubAccommodationUnitInfo();
        WireMockTestHelper.stubPricesForDatesBetween9and11();
        WireMockTestHelper.stubPricesForDatesBetween11and17();
        WireMockTestHelper.stubPricesForDatesBetween17and19();

    }

    public static void stop() {
        wireMockServer.stop();
    }

    public static void stubPricesInfo( String startDate, String endDate, String jsonFileName) {
        String url = "/prices/1/price?startDate=" + startDate + "&endDate=" + endDate;
        wireMockServer.stubFor(
                WireMock.get(urlEqualTo(url))
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBodyFile(jsonFileName)));
    }

    public static void stubPricesForDatesBetween11and17() {
        stubPricesInfo("4/11/00", "4/17/00", "pricesBetween12and17.json");
    }

    public static void stubPricesForDatesBetween9and11() {
        stubPricesInfo("4/9/00", "4/11/00", "pricesBetween5and20.json");
    }
    public static void stubPricesForDatesBetween17and19() {
        stubPricesInfo("4/17/00", "4/19/00","pricesBetween5and20.json" );
    }

    public static void stubAccommodationUnitInfo() {
        wireMockServer.stubFor(
                WireMock.get(urlEqualTo("/rooms/1"))
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBodyFile("accommodationUnitResponse.json")));
    }

}
