CREATE TABLE IF NOT EXISTS job_applications (
    application_id BIGINT AUTO_INCREMENT PRIMARY KEY,

    resume_url VARCHAR(500),
    applied_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    job_post_id BIGINT NOT NULL,
    seeker_id BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS outbox_events (
    id BIGINT NOT NULL AUTO_INCREMENT,
    topic VARCHAR(255) NOT NULL,
    message_key VARCHAR(255) NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX idx_seeker_id ON job_applications (seeker_id);

CREATE INDEX idx_jobpost_id ON job_applications (job_post_id);

CREATE INDEX idx_jobpost_id ON job_applications (job_post_id , seeker_id);
