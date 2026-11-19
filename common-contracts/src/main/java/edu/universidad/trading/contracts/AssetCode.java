package edu.universidad.trading.contracts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Map;
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

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static AssetCode fromJson(Object raw) {
        if (raw instanceof String code) {
            return new AssetCode(code);
        }
        if (raw instanceof Map<?, ?> previous && previous.get("value") instanceof String code) {
            return new AssetCode(code);
        }
        throw new IllegalArgumentException("asset code JSON must be a string");
    }

    @Override
    @JsonValue
    public String value() {
        return value;
    }
}
