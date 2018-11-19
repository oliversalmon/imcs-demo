package com.example.mu.positionqueryservice;

import com.example.mu.domain.PositionAccount;
import cucumber.api.TypeRegistry;
import cucumber.api.TypeRegistryConfigurer;
import io.cucumber.datatable.DataTableType;

import java.util.Locale;
import java.util.Map;

public class PositionAccountTransformer implements TypeRegistryConfigurer {
    @Override
    public Locale locale() {
        return Locale.ENGLISH;
    }

    @Override
    public void configureTypeRegistry(TypeRegistry typeRegistry) {

        typeRegistry.defineDataTableType(new DataTableType(PositionAccount.class,
                        (Map<String, String> row) -> {
                            String accountId = row.get("accountId");
                            String instrumentid = row.get("instrumentid");
                            Integer size = Integer.parseInt(row.get("size"));
                            Double pnl = Double.parseDouble(row.get("pnl"));

                            return new PositionAccount(accountId, instrumentid, size, pnl);
                        }
                )
        );

    }
}
