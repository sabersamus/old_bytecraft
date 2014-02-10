ALTER TABLE `zone` 
ADD `zone_creative` ENUM( 'true', 'false' ) NOT NULL DEFAULT 'false' 
AFTER `zone_hostile` 