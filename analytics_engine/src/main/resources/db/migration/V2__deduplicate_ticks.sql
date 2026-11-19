DELETE FROM ticks duplicate
USING ticks original
WHERE duplicate.asset_code = original.asset_code
  AND duplicate.timestamp = original.timestamp
  AND duplicate.id > original.id;

CREATE UNIQUE INDEX uq_ticks_asset_timestamp ON ticks (asset_code, timestamp);
