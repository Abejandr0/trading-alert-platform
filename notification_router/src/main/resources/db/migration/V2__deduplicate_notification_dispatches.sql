ALTER TABLE notification_dispatches ADD COLUMN event_id UUID;
UPDATE notification_dispatches SET event_id = gen_random_uuid() WHERE event_id IS NULL;
ALTER TABLE notification_dispatches ALTER COLUMN event_id SET NOT NULL;
CREATE UNIQUE INDEX uq_notification_dispatch_event_channel
    ON notification_dispatches (event_id, channel);
