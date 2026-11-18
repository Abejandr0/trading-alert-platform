package edu.universidad.trading.contracts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AssetCodeTest {

    @Test
    void acceptsCryptoStockAndCurrencyPair() {
        assertEquals("BTC", new AssetCode("BTC").value());
        assertEquals("GOOGL", new AssetCode("GOOGL").value());
        assertEquals("EUR/USD", new AssetCode("EUR/USD").value());
    }

    @Test
    void rejectsNullBlankAndMalformedCodes() {
        for (String invalid : new String[] {null, "", " ", "btc", "BTC-USD", "A", "/USD"}) {
            assertThrows(IllegalArgumentException.class, () -> new AssetCode(invalid));
        }
    }
}
