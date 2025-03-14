-- TBL tbl_user
create table whale_one.tbl_user
(
    id                 integer generated by default as identity
        constraint tbl_user_pk
            primary key,
    version            integer                  not null,
    created_at         timestamp with time zone not null,
    created_by_id      integer                  not null,
    last_updated_at    timestamp with time zone not null,
    last_updated_by_id integer                  not null,
    username           text                     not null,
    password           text                     not null,
    enabled            boolean                  not null
);

create unique index tbl_user_username_key
    on whale_one.tbl_user (username);

-- TBL tbl_user_authority
create table whale_one.tbl_user_authority
(
    user_id integer not null
        constraint tbl_user_authority_pk
            primary key
        constraint tbl_user_authority_user_id_tbl_user_id_fk
            references whale_one.tbl_user,
    name    text    not null
);

-- TBL project
create table whale_one.project
(
    id                 integer generated by default as identity
        constraint project_pk
            primary key,
    version            integer                  not null,
    created_at         timestamp with time zone not null,
    created_by_id      integer                  not null
        constraint project_created_by_id_fk
            references whale_one.tbl_user,
    last_updated_at    timestamp with time zone not null,
    last_updated_by_id integer                  not null
        constraint project_last_updated_by_id_fk
            references whale_one.tbl_user,
    name               text                     not null,
    description        text,
    client             text,
    ownership          text,
    region             text,
    type               text,
    goal               text
);

-- TBL project_campaign
create table whale_one.project_campaign
(
    id         integer generated by default as identity
        constraint project_campaign_pk
            primary key,
    project_id integer not null
        constraint project_campaign_project_id_fk
            references whale_one.project,
    name       text    not null
);

-- TBL project_site
create table whale_one.project_site
(
    id         integer generated by default as identity
        constraint project_site_pk
            primary key,
    project_id integer not null
        constraint project_site_project_id_fk
            references whale_one.project,
    name       text    not null,
    longitude  double precision,
    latitude   double precision,
    depth      double precision
);

-- TBL equipment_type
create table whale_one.equipment_type
(
    id                 integer generated by default as identity
        constraint equipment_type_pk
            primary key,
    version            integer                  not null,
    created_at         timestamp with time zone not null,
    created_by_id      integer                  not null
        constraint equipment_type_created_by_id_fk
            references whale_one.tbl_user,
    last_updated_at    timestamp with time zone not null,
    last_updated_by_id integer                  not null
        constraint equipment_type_last_updated_by_id_fk
            references whale_one.tbl_user,
    name               text                     not null
);

-- TBL equipment_type_equipment_attribute
create table whale_one.equipment_type_equipment_attribute
(
    id                integer generated by default as identity
        constraint equipment_type_equipment_attribute_pk
            primary key,
    equipment_type_id integer not null
        constraint equipment_type_equipment_attribute_equipment_type_id_fk
            references whale_one.equipment_type,
    version           integer not null,
    name              text    not null,
    description       text,
    "order"           integer not null,
    type              text,
    metadata          jsonb
);

-- TBL equipment_type_deployment_attribute
create table whale_one.equipment_type_deployment_attribute
(
    id                integer generated by default as identity
        constraint equipment_type_deployment_attribute_pk
            primary key,
    equipment_type_id integer
        constraint equipment_type_deployment_attribute_equipment_type_id_fk
            references whale_one.equipment_type,
    version           integer not null,
    name              text    not null,
    description       text,
    "order"           integer not null,
    type              text,
    metadata          jsonb
);

-- TBL deployment
create table whale_one.deployment
(
    id                     integer generated by default as identity
        constraint deployment_pk
            primary key,
    version                integer                  not null,
    created_at             timestamp with time zone not null,
    created_by_id          integer                  not null
        constraint deployment_created_by_id_fk
            references whale_one.tbl_user,
    last_updated_at        timestamp with time zone not null,
    last_updated_by_id     integer                  not null
        constraint deployment_last_updated_by_id_fk
            references whale_one.tbl_user,
    name                   text                     not null,
    description            text,
    status                 text                     not null,
    project_id             integer
        constraint deployment_project_id_fk
            references whale_one.project,
    project_site_id        integer
        constraint deployment_project_site_id_fk
            references whale_one.project_site,
    platform               text,
    provider_organisations text,
    provider_participants  text,

    latitude               double precision,
    longitude              double precision,
    bathymetry             double precision,
    deployed_at            timestamp with time zone,
    deployment_campaign_id integer
        constraint deployment_campaign_id_fk
            references whale_one.project_campaign,
    first_file_at          timestamp with time zone,
    last_file_at           timestamp with time zone,

    recovery_status        text,
    recovered_at           timestamp with time zone,
    recovery_campaign_id   integer
        constraint recovery_campaign_id_fk
            references whale_one.project_campaign
);

-- TBL equipment
create table whale_one.equipment
(
    id                 integer generated by default as identity
        constraint equipment_pk
            primary key,
    version            integer                  not null,
    created_at         timestamp with time zone not null,
    created_by_id      integer                  not null
        constraint equipment_created_by_id_fk
            references whale_one.tbl_user,
    last_updated_at    timestamp with time zone not null,
    last_updated_by_id integer                  not null
        constraint equipment_last_updated_by_id_fk
            references whale_one.tbl_user,
    name               text                     not null,
    type_id            integer                  not null
        constraint equipment_type_id_fk
            references whale_one.equipment_type,
    deployment_id      integer
        constraint equipment_deployment_id_fk
            references whale_one.deployment,
    active             boolean                  not null
);

-- TBL equipment_attribute
create table whale_one.equipment_attribute
(
    id                          integer generated by default as identity
        constraint equipment_attribute_pk
            primary key,
    equipment_id                integer not null
        constraint equipment_attribute_equipment_id_fk
            references whale_one.equipment,
    equipment_type_attribute_id integer not null
        constraint equipment_attribute_equipment_type_attribute_id_fk
            references whale_one.equipment_type_equipment_attribute,
    value                       text
);

-- TBL deployment_attribute
create table whale_one.deployment_equipment
(
    deployment_id integer not null
        constraint deployment_equipment_deployment_id_fk
            references whale_one.deployment,
    equipment_id  integer not null
        constraint deployment_equipment_equipment_id_fk
            references whale_one.equipment,
    primary key (deployment_id, equipment_id)
);
