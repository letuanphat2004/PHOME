ALTER TABLE room
    ADD COLUMN moderation_note VARCHAR(500) NULL AFTER is_approval;
