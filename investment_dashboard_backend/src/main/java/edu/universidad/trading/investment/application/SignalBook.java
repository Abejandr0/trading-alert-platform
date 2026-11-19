package edu.universidad.trading.investment.application;

import edu.universidad.trading.contracts.AssetCode;
import edu.universidad.trading.contracts.SignalPayloadRecord;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public final class SignalBook {
    private final Map<AssetCode, SignalPayloadRecord> latest = new ConcurrentHashMap<>();

    public void record(SignalPayloadRecord signal) {
        latest.put(signal.assetCode(), signal);
    }

    public List<SignalPayloadRecord> snapshot() {
        return List.copyOf(latest.values());
    }
}
