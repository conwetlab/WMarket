ALTER TABLE users CHANGE registration_date created_at datetime;
ALTER TABLE descriptions MODIFY url VARCHAR(2048);