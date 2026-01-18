CREATE TABLE IF NOT EXISTS job_post (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255),
    company_name VARCHAR(255),
    description TEXT NOT NULL,
    location VARCHAR(255) NOT NULL,
    salary DOUBLE NOT NULL ,
    time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    recruiter_id BIGINT,
    application_count INT DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE INDEX idx_recruiter_id ON job_post (recruiter_id);
CREATE INDEX idx_job_id ON job_post (id);
CREATE INDEX idx_job_id_and_recruiter_id ON job_post (id , recruiter_id);


CREATE TABLE IF NOT EXISTS processed_events (
    event_id BIGINT NOT NULL,
    job_id BIGINT NOT NULL,
    processed_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);