-- auto-generated definition
create table event_list
(
    event_code           varchar(255)         not null
        primary key,
    event_type           varchar(255)         null,
    event_picurl         varchar(1024)        null,
    event_detail         text                 null,
    start_time           datetime             null,
    end_time             datetime             null,
    link_camera_id       varchar(255)         null,
    mec_event_id         int                  null,
    worker_id            varchar(255)         null,
    false_report         tinyint(1) default 0 null,
    video_url            varchar(1024)        null,
    event_picurl_gallery text                 null,
    location             varchar(1024)        null,
    trigger_type         int        default 1 null
);

create index end_time
    on event_list (end_time);

create index event_type
    on event_list (event_type);

create index false_report
    on event_list (false_report);

create index link_camera_id
    on event_list (link_camera_id);

create index start_time
    on event_list (start_time);

create index trigger_type
    on event_list (trigger_type);

create index worker_id
    on event_list (worker_id);