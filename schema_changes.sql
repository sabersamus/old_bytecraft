ALTER TABLE `player_property` ADD `immortal` ENUM( 'true', 'false' ) NOT NULL DEFAULT 'true' AFTER `can_fly`;


CREATE TABLE IF NOT EXISTS `playerinventory` (
  `playerinventory_id` int(10) NOT NULL AUTO_INCREMENT,
  `player_id` int(10) NOT NULL,
  `playerinventory_name` varchar(255) DEFAULT NULL,
  `playerinventory_type` varchar(255) NOT NULL,
  PRIMARY KEY (`playerinventory_id`),
  INDEX idx_player (player_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `playerinventory_item` (
  `item_id` int(10) NOT NULL AUTO_INCREMENT,
  `playerinventory_id` int(10) DEFAULT NULL,
  `item_slot` int(10) DEFAULT NULL,
  `item_material` int(10) DEFAULT NULL,
  `item_data` int(11) DEFAULT NULL,
  `item_meta` text,
  `item_count` int(10) DEFAULT NULL,
  PRIMARY KEY (`item_id`),
  INDEX idx_inv (playerinventory_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `player` ADD COLUMN `player_inventory` varchar(32) DEFAULT NULL AFTER `player_name`;
ALTER TABLE `player` ADD COLUMN `player_uuid` char(52) NOT NULL AFTER `player_name`;

ALTER TABLE playerinventory_item ADD COLUMN item_durability INT;