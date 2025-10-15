-- 添加report_dec字段用于存储多选误报类型
ALTER TABLE event_list ADD COLUMN report_dec TEXT NULL;