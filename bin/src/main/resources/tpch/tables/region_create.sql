create table if not exists tpch_region (
  r_regionkey integer not null,
  r_name char(25) not null,
  r_comment varchar(152)
)