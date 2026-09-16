CREATE TABLE content_report (
    id BIGINT NOT NULL AUTO_INCREMENT,
    reporter_user_id BIGINT NOT NULL,
    target_type VARCHAR(20) NOT NULL,
    target_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    reason VARCHAR(50) NOT NULL,
    details VARCHAR(1000) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    resolution_note VARCHAR(1000),
    resolved_by BIGINT,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    resolved_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_content_report_reporter FOREIGN KEY (reporter_user_id) REFERENCES user(id) ON DELETE CASCADE,
    CONSTRAINT fk_content_report_room FOREIGN KEY (room_id) REFERENCES room(id) ON DELETE CASCADE,
    CONSTRAINT fk_content_report_resolver FOREIGN KEY (resolved_by) REFERENCES user(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE INDEX idx_content_report_status_created ON content_report (status, created_at);
CREATE INDEX idx_content_report_target ON content_report (target_type, target_id);
