package rs.ac.bg.fon.accommodationservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import rs.ac.bg.fon.accommodationservice.adapters.controllerServiceAdapters.PriceDtoDomainAdapter;
import rs.ac.bg.fon.accommodationservice.dto.PriceDto;
import rs.ac.bg.fon.accommodationservice.dto.dateFilter.PricesDate;
import rs.ac.bg.fon.accommodationservice.dto.update.PriceDtoUpdate;

import java.util.List;

@RestController
@RequestMapping("prices")
@RequiredArgsConstructor
public class PriceController {

    private final PriceDtoDomainAdapter priceAdapter;

    @GetMapping("{accommodationId}/price")
    public List<PriceDto> getAllByPage(
            @PathVariable Long accommodationId,
            PricesDate pricesDate){
        return priceAdapter.getAll(accommodationId, pricesDate);
    }

    @GetMapping("/{id}")
    public PriceDto findById(@PathVariable Long id) {
        return priceAdapter.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long save(@Valid @RequestBody PriceDto priceDto) {
        return priceAdapter.save(priceDto);
    }

    @PutMapping
    public Long updatePrice(@Valid @RequestBody PriceDtoUpdate priceDto) {
        return priceAdapter.update(priceDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        priceAdapter.deleteById(id);
    }
}
