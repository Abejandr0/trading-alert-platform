package edu.universidad.trading.contracts;

import java.util.regex.Pattern;

/** Identificador canónico de un activo o par, apto para claves de partición. */
public record AssetCode(String value) {

    private static final Pattern VALID_CODE =
            Pattern.compile("[A-Z][A-Z0-9]{1,9}(?:/[A-Z][A-Z0-9]{1,9})?");

    public AssetCode {
        if (value == null || !VALID_CODE.matcher(value).matches()) {
            throw new IllegalArgumentException("asset code must be uppercase and well formed");
        }
    }
}
