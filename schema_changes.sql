ALTER TABLE `zone` 
ADD `zone_creative` ENUM( 'true', 'false' ) NOT NULL DEFAULT 'false' 
AFTER `zone_hostile` 


CREATE TABLE IF NOT EXISTS `player_password`(
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `player_name` varchar(32) NOT NULL,
  `password` varchar(32) NOT NULL,
  PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;