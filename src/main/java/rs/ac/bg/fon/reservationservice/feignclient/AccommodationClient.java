package rs.ac.bg.fon.reservationservice.feignclient;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "accommodation-service", url = "${feign.client.config.accommodation-service.url}")
public interface AccommodationClient {

    @RequestMapping(method = RequestMethod.GET, value = "/rooms/{id}")
    public JsonNode getAccommodationUnitById(@PathVariable Long id);

    @RequestMapping(method= RequestMethod.GET, value = "/prices/{accommodationUnitId}/price")
    public List<Price> getAllPrices(@PathVariable Long accommodationUnitId, @RequestParam("startDate") LocalDate startDate,  @RequestParam("endDate") LocalDate endDate);


    @RequestMapping(method = RequestMethod.GET, value = "/rooms/my-rooms")
    public JsonNode getAccommodationUnitsByHostId(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt);

}
