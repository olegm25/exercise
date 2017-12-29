# Value schema

# --- !Ups
create table `persisted_values` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `string_value` TEXT NOT NULL
)

# --- !Downs
drop table `persisted_values`