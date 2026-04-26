CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE short_links (
                             id SERIAL PRIMARY KEY,
                             short_code VARCHAR(10) UNIQUE NOT NULL,
                             original_url TEXT NOT NULL,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             expires_at TIMESTAMP,
                             click_count INT DEFAULT 0,
                             user_id INT REFERENCES users(id) ON DELETE CASCADE
);
