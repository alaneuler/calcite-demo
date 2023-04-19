create table if not exists tpch_nation  (
  n_nationkey integer not null,
  n_name char(25) not null,
  n_regionkey integer not null,
  n_comment varchar(152)
)
