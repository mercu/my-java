
select *
from log_info
order by id desc
limit 100
;

select count(1)
from bl_set_item
;


select itemNo
	, count(itemNo)
from bl_set_item
group by itemNo
having count(itemNo) between 2 and 3
-- order by count(itemNo) asc
limit 100
;

select *
from log_info
where type = 'crawlSetInventories'
order by id desc
limit 100
;

select count(1)
from bl_part_info
;

select *
from bl_color_info
order by CAST(id AS UNSIGNED)
;

select *
from bl_part_info
where partNo = '3957b'
;
where setNo = '75055'
limit 100
;

select *
from bl_minifig_info
;

select *
from bl_set_info
where setBrief like '%2018'
limit 10
;


select *
from bl_set_category
-- where id = '167.433'
where depth = '1'
	and parts = '(1)'
;


CREATE TABLE `bl_minifig_info` (
	`id` VARCHAR(24) NOT NULL,
	`categoryId` VARCHAR(24) NULL DEFAULT NULL,
	`img` VARCHAR(256) NULL DEFAULT NULL,
	`minifigNo` VARCHAR(64) NULL DEFAULT NULL,
	`minifigName` VARCHAR(256) NULL DEFAULT NULL,
	PRIMARY KEY (`id`)
)
COLLATE='utf8mb4_0900_ai_ci'
ENGINE=InnoDB
;


