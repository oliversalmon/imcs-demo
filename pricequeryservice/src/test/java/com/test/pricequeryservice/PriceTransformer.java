package com.test.pricequeryservice;

import com.example.mu.domain.Price;
import cucumber.api.TypeRegistry;
import cucumber.api.TypeRegistryConfigurer;
import io.cucumber.datatable.DataTableType;

import java.util.Locale;
import java.util.Map;

public class PriceTransformer implements TypeRegistryConfigurer {
    @Override
    public Locale locale() {
        return Locale.ENGLISH;
    }

    @Override
    public void configureTypeRegistry(TypeRegistry typeRegistry) {

        typeRegistry.defineDataTableType(new DataTableType(Price.class,
                        (Map<String, String> row) -> {
                            String priceId = row.get("priceId");
                            String instrumentid = row.get("instrumentId");
                            Double price = Double.parseDouble(row.get("price"));
                            Long ts = System.currentTimeMillis();

                            return new Price(priceId, instrumentid, price.doubleValue(), ts.longValue());
                        }
                )
        );

    }
}
