#!/bin/bash

psql -U "postgres" -d "postgres" << 'EOSQL'
    CREATE TABLE IF NOT EXISTS mail (
        id SERIAL PRIMARY KEY,
        recipient TEXT,
        sender TEXT,
        subject TEXT,
        body TEXT,
        received_at TIMESTAMP DEFAULT (NOW() AT TIME ZONE 'UTC')
    );
    CREATE TABLE IF NOT EXISTS lcg_seed (
        value BIGINT NOT NULL
    );
    INSERT INTO lcg_seed (value)
    SELECT 1
    WHERE NOT EXISTS (SELECT 1 FROM lcg_seed);
    CREATE OR REPLACE FUNCTION notify_email() RETURNS trigger AS $$
    BEGIN

    PERFORM pg_notify('notification_channel',
        json_build_object(
            'recipient', NEW.recipient
        )::text
    );
    RETURN NEW;
    END;
    $$ LANGUAGE plpgsql;

    CREATE TRIGGER notify_email_trigger
    AFTER INSERT ON mail
    FOR EACH ROW EXECUTE FUNCTION notify_email();
'EOSQL'
