-- V2__Add_indexes.sql
CREATE INDEX idx_user_email ON tb_user(email);
CREATE INDEX idx_project_user_id ON tb_user_external_project(user_id);