-- phpMyAdmin SQL Dump
-- version 3.5.8.2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Feb 17, 2014 at 07:19 PM
-- Server version: 5.1.73
-- PHP Version: 5.3.3

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `server_27`
--

-- --------------------------------------------------------

--
-- Table structure for table `bless`
--

CREATE TABLE IF NOT EXISTS `bless` (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `player_name` varchar(32) NOT NULL,
  `x` int(255) NOT NULL,
  `y` int(255) NOT NULL,
  `z` int(255) NOT NULL,
  `world` varchar(32) NOT NULL DEFAULT 'world',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=257 ;

-- --------------------------------------------------------

--
-- Table structure for table `book`
--

CREATE TABLE IF NOT EXISTS `book` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `book_pages`
--

CREATE TABLE IF NOT EXISTS `book_pages` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `book_name` varchar(32) NOT NULL,
  `page_index` int(5) NOT NULL,
  `page_text` varchar(250) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `donation`
--

CREATE TABLE IF NOT EXISTS `donation` (
  `donation_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `player_id` int(10) unsigned DEFAULT NULL,
  `donation_timestamp` int(10) unsigned DEFAULT NULL,
  `donation_amount` char(10) DEFAULT NULL,
  `donation_paypalid` varchar(64) DEFAULT NULL,
  `donation_payerid` varchar(64) DEFAULT NULL,
  `donation_email` varchar(255) DEFAULT NULL,
  `donation_firstname` varchar(255) DEFAULT NULL,
  `donation_lastname` varchar(255) DEFAULT NULL,
  `donation_message` text,
  PRIMARY KEY (`donation_id`),
  KEY `idx_player` (`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `enchantment_value`
--

CREATE TABLE IF NOT EXISTS `enchantment_value` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `enchant_id` int(10) NOT NULL,
  `enchant_level` int(10) NOT NULL,
  `enchant_value` int(10) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `echant_id` (`enchant_id`,`enchant_level`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=13 ;

-- --------------------------------------------------------

--
-- Table structure for table `fill_log`
--

CREATE TABLE IF NOT EXISTS `fill_log` (
  `fill_id` int(255) NOT NULL AUTO_INCREMENT,
  `player_name` varchar(255) NOT NULL,
  `action` enum('fill','replace','undo') NOT NULL DEFAULT 'fill',
  `size` int(255) NOT NULL,
  `material` varchar(32) NOT NULL,
  UNIQUE KEY `id` (`fill_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2606 ;

-- --------------------------------------------------------

--
-- Table structure for table `inventory`
--

CREATE TABLE IF NOT EXISTS `inventory` (
  `inventory_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `player_name` varchar(32) NOT NULL,
  `inventory_checksum` int(11) DEFAULT NULL,
  `inventory_x` int(11) DEFAULT NULL,
  `inventory_y` int(11) DEFAULT NULL,
  `inventory_z` int(11) DEFAULT NULL,
  `inventory_world` varchar(32) DEFAULT NULL,
  `inventory_type` enum('block','player','player_armor') DEFAULT NULL,
  PRIMARY KEY (`inventory_id`),
  KEY `idx_coords` (`inventory_x`,`inventory_y`,`inventory_z`,`inventory_world`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=569 ;

-- --------------------------------------------------------

--
-- Table structure for table `inventory_accesslog`
--

CREATE TABLE IF NOT EXISTS `inventory_accesslog` (
  `accesslog_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `inventory_id` int(10) unsigned DEFAULT NULL,
  `player_name` varchar(32) NOT NULL,
  `accesslog_timestamp` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`accesslog_id`),
  KEY `idx_inventory` (`inventory_id`,`accesslog_timestamp`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=8662 ;

-- --------------------------------------------------------

--
-- Table structure for table `inventory_changelog`
--

CREATE TABLE IF NOT EXISTS `inventory_changelog` (
  `changelog_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `inventory_id` int(10) unsigned DEFAULT NULL,
  `player_name` varchar(32) NOT NULL,
  `changelog_timestamp` int(10) unsigned DEFAULT NULL,
  `changelog_slot` int(10) unsigned DEFAULT NULL,
  `changelog_material` int(10) unsigned DEFAULT NULL,
  `changelog_data` int(11) DEFAULT NULL,
  `changelog_meta` text,
  `changelog_amount` int(10) unsigned DEFAULT NULL,
  `changelog_type` enum('add','remove') DEFAULT NULL,
  PRIMARY KEY (`changelog_id`),
  KEY `idx_inventory` (`inventory_id`,`changelog_timestamp`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=11411 ;

-- --------------------------------------------------------

--
-- Table structure for table `inventory_item`
--

CREATE TABLE IF NOT EXISTS `inventory_item` (
  `item_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `inventory_id` int(10) unsigned DEFAULT NULL,
  `item_slot` int(10) unsigned DEFAULT NULL,
  `item_material` int(10) unsigned DEFAULT NULL,
  `item_data` int(11) DEFAULT NULL,
  `item_meta` text CHARACTER SET utf8,
  `item_count` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`item_id`),
  KEY `inventory_idx` (`inventory_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_swedish_ci AUTO_INCREMENT=149459 ;

-- --------------------------------------------------------

--
-- Table structure for table `item`
--

CREATE TABLE IF NOT EXISTS `item` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `item_type` varchar(32) NOT NULL,
  `item_data` int(10) NOT NULL DEFAULT '0',
  `item_value` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `item_type` (`item_type`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=33 ;

-- --------------------------------------------------------

--
-- Table structure for table `lore_armor`
--

CREATE TABLE IF NOT EXISTS `lore_armor` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  `type` varchar(32) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=19 ;

-- --------------------------------------------------------

--
-- Table structure for table `lore_book`
--

CREATE TABLE IF NOT EXISTS `lore_book` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `lore` varchar(250) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

-- --------------------------------------------------------

--
-- Table structure for table `lore_god`
--

CREATE TABLE IF NOT EXISTS `lore_god` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=9 ;

-- --------------------------------------------------------

--
-- Table structure for table `lore_sword`
--

CREATE TABLE IF NOT EXISTS `lore_sword` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=25 ;

-- --------------------------------------------------------

--
-- Table structure for table `messages`
--

CREATE TABLE IF NOT EXISTS `messages` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `message` varchar(100) NOT NULL,
  `type` enum('death','quit') NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=16 ;

-- --------------------------------------------------------

--
-- Table structure for table `paper_log`
--

CREATE TABLE IF NOT EXISTS `paper_log` (
  `paper_id` int(10) NOT NULL AUTO_INCREMENT,
  `player_name` varchar(16) NOT NULL,
  `block_x` int(32) NOT NULL,
  `block_y` int(32) NOT NULL,
  `block_z` int(32) NOT NULL,
  `block_world` varchar(32) NOT NULL DEFAULT 'world',
  `block_type` varchar(32) NOT NULL,
  `action` enum('broke','placed') NOT NULL,
  `paper_time` int(10) unsigned DEFAULT NULL,
  UNIQUE KEY `id` (`paper_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=376929 ;

-- --------------------------------------------------------

--
-- Table structure for table `player`
--

CREATE TABLE IF NOT EXISTS `player` (
  `player_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `player_name` varchar(46) DEFAULT NULL,
  `player_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `banned` enum('true','false') NOT NULL DEFAULT 'false',
  `player_wallet` bigint(20) DEFAULT '1000',
  `player_rank` enum('newcomer','settler','member','child','noble','lord','mentor','protector','architect','admin','princess','elder') NOT NULL DEFAULT 'newcomer',
  `player_promoted` int(10) unsigned DEFAULT NULL,
  `player_playtime` int(10) unsigned DEFAULT '0',
  UNIQUE KEY `uid` (`player_id`),
  KEY `player` (`player_name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=139 ;

-- --------------------------------------------------------

--
-- Table structure for table `player_badge`
--

CREATE TABLE IF NOT EXISTS `player_badge` (
  `player_name` varchar(32) NOT NULL,
  `badge` varchar(32) NOT NULL,
  `badge_level` int(10) NOT NULL DEFAULT '1',
  PRIMARY KEY (`player_name`,`badge`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `player_chatlog`
--

CREATE TABLE IF NOT EXISTS `player_chatlog` (
  `chatlog_id` int(10) NOT NULL AUTO_INCREMENT,
  `player_name` varchar(32) COLLATE utf8_bin NOT NULL,
  `chatlog_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `chatlog_channel` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `chatlog_message` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`chatlog_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=37263 ;

-- --------------------------------------------------------

--
-- Table structure for table `player_home`
--

CREATE TABLE IF NOT EXISTS `player_home` (
  `home_id` int(10) NOT NULL AUTO_INCREMENT,
  `player_name` varchar(20) NOT NULL,
  `home_name` varchar(32) NOT NULL DEFAULT 'default',
  `home_x` double DEFAULT NULL,
  `home_y` double DEFAULT NULL,
  `home_z` double DEFAULT NULL,
  `home_yaw` double DEFAULT NULL,
  `home_pitch` double DEFAULT NULL,
  `home_world` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`home_id`),
  UNIQUE KEY `home.player` (`player_name`,`home_name`),
  KEY `player_name` (`player_name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=33 ;

-- --------------------------------------------------------

--
-- Table structure for table `player_login`
--

CREATE TABLE IF NOT EXISTS `player_login` (
  `login_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `player_name` varchar(32) CHARACTER SET utf8 NOT NULL,
  `login_timestamp` int(10) unsigned NOT NULL,
  `action` enum('login','logout') CHARACTER SET utf8 DEFAULT NULL,
  `login_ip` varchar(15) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (`login_id`),
  KEY `ip_idx` (`login_ip`),
  KEY `player_idx` (`player_name`,`login_timestamp`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_swedish_ci AUTO_INCREMENT=4472 ;

-- --------------------------------------------------------

--
-- Table structure for table `player_messages`
--

CREATE TABLE IF NOT EXISTS `player_messages` (
  `message_id` int(10) NOT NULL AUTO_INCREMENT,
  `player_name` varchar(32) NOT NULL,
  `recipient_name` varchar(32) NOT NULL,
  `message` varchar(100) NOT NULL,
  `message_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`message_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1447 ;

-- --------------------------------------------------------

--
-- Table structure for table `player_password`
--

CREATE TABLE IF NOT EXISTS `player_password` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `player_name` varchar(32) NOT NULL,
  `password` varchar(32) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

-- --------------------------------------------------------

--
-- Table structure for table `player_property`
--

CREATE TABLE IF NOT EXISTS `player_property` (
  `player_id` int(10) NOT NULL,
  `player_name` varchar(255) NOT NULL,
  `invisible` enum('true','false') CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT 'false',
  `tpblock` enum('true','false') CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT 'false',
  `hidden_location` enum('true','false') CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT 'false',
  `silent_join` enum('true','false') CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT 'false',
  `can_fly` enum('true','false') NOT NULL DEFAULT 'true',
  `god_color` enum('red','aqua','gold','yellow','dark_aqua','pink','purple','green','dark_green','dark_red','gray','dark_blue') CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT 'red',
  PRIMARY KEY (`player_id`),
  UNIQUE KEY `player_name` (`player_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `player_report`
--

CREATE TABLE IF NOT EXISTS `player_report` (
  `report_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `subject_name` varchar(32) NOT NULL,
  `issuer_name` varchar(32) NOT NULL,
  `report_action` enum('kick','softwarn','hardwarn','ban','mute','comment') NOT NULL,
  `report_message` text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `report_timestamp` int(10) unsigned NOT NULL,
  `report_validuntil` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`report_id`),
  KEY `idx_subject` (`subject_name`,`report_timestamp`),
  KEY `idx_issuer` (`issuer_name`,`report_timestamp`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=64 ;

-- --------------------------------------------------------

--
-- Table structure for table `player_webcookie`
--

CREATE TABLE IF NOT EXISTS `player_webcookie` (
  `webcookie_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `player_id` int(10) unsigned DEFAULT NULL,
  `webcookie_nonce` char(64) DEFAULT NULL,
  PRIMARY KEY (`webcookie_id`),
  UNIQUE KEY `idx_nonce` (`webcookie_nonce`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `salesign`
--

CREATE TABLE IF NOT EXISTS `salesign` (
  `salesign_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `player_name` varchar(32) NOT NULL,
  `salesign_created` int(10) unsigned DEFAULT NULL,
  `salesign_status` enum('active','deleted') DEFAULT 'active',
  `salesign_material` int(10) unsigned DEFAULT NULL,
  `salesign_data` int(11) DEFAULT NULL,
  `salesign_enchantments` text,
  `salesign_cost` int(10) unsigned DEFAULT NULL,
  `salesign_inventory` int(10) unsigned DEFAULT NULL,
  `salesign_world` varchar(50) DEFAULT NULL,
  `salesign_blockx` int(11) DEFAULT NULL,
  `salesign_blocky` int(11) DEFAULT NULL,
  `salesign_blockz` int(11) DEFAULT NULL,
  `salesign_signx` int(11) DEFAULT NULL,
  `salesign_signy` int(11) DEFAULT NULL,
  `salesign_signz` int(11) DEFAULT NULL,
  `salesign_storedenchants` enum('0','1') DEFAULT '0',
  PRIMARY KEY (`salesign_id`),
  KEY `player_namex` (`player_name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

-- --------------------------------------------------------

--
-- Table structure for table `salesign_costlog`
--

CREATE TABLE IF NOT EXISTS `salesign_costlog` (
  `costlog_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `salesign_id` int(10) unsigned DEFAULT NULL,
  `costlog_timestamp` int(10) unsigned DEFAULT NULL,
  `costlog_newcost` int(10) unsigned DEFAULT NULL,
  `costlog_oldcost` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`costlog_id`),
  KEY `idx_salesign` (`salesign_id`,`costlog_timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `salesign_transaction`
--

CREATE TABLE IF NOT EXISTS `salesign_transaction` (
  `transaction_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `salesign_id` int(10) unsigned DEFAULT NULL,
  `player_name` varchar(32) DEFAULT NULL,
  `transaction_type` enum('deposit','withdraw','buy') DEFAULT NULL,
  `transaction_timestamp` int(10) unsigned DEFAULT NULL,
  `transaction_amount` int(10) unsigned DEFAULT NULL,
  `transaction_unitcost` int(10) unsigned DEFAULT NULL,
  `transaction_totalcost` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`transaction_id`),
  KEY `idx_salesign` (`salesign_id`,`transaction_timestamp`),
  KEY `idx_player` (`player_name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

-- --------------------------------------------------------

--
-- Table structure for table `sell_log`
--

CREATE TABLE IF NOT EXISTS `sell_log` (
  `sell_id` int(10) NOT NULL AUTO_INCREMENT,
  `player_name` varchar(32) NOT NULL,
  `sell_value` int(10) NOT NULL,
  PRIMARY KEY (`sell_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=261 ;

-- --------------------------------------------------------

--
-- Table structure for table `transaction_log`
--

CREATE TABLE IF NOT EXISTS `transaction_log` (
  `transaction_id` int(32) NOT NULL AUTO_INCREMENT,
  `sender_name` varchar(32) NOT NULL,
  `reciever_name` varchar(32) NOT NULL,
  `amount` bigint(20) NOT NULL,
  UNIQUE KEY `id` (`transaction_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=316 ;

-- --------------------------------------------------------

--
-- Table structure for table `warps`
--

CREATE TABLE IF NOT EXISTS `warps` (
  `warp_id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) CHARACTER SET latin1 NOT NULL,
  `x` double NOT NULL,
  `y` double NOT NULL,
  `z` double NOT NULL,
  `pitch` float NOT NULL,
  `yaw` float NOT NULL,
  `world` varchar(16) CHARACTER SET latin1 NOT NULL,
  PRIMARY KEY (`warp_id`),
  UNIQUE KEY `name` (`name`),
  KEY `name-index` (`name`,`x`,`y`,`z`,`pitch`,`yaw`,`world`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=20 ;

-- --------------------------------------------------------

--
-- Table structure for table `zone`
--

CREATE TABLE IF NOT EXISTS `zone` (
  `zone_id` int(11) NOT NULL AUTO_INCREMENT,
  `zone_world` varchar(50) NOT NULL DEFAULT 'world',
  `zone_name` varchar(32) NOT NULL,
  `zone_whitelist` enum('true','false') NOT NULL DEFAULT 'false',
  `zone_build` enum('true','false') NOT NULL DEFAULT 'true',
  `zone_pvp` enum('true','false') NOT NULL DEFAULT 'false',
  `zone_hostile` enum('true','false') DEFAULT 'true',
  `zone_creative` enum('true','false') NOT NULL DEFAULT 'false',
  `zone_entermsg` varchar(250) NOT NULL,
  `zone_exitmsg` varchar(250) NOT NULL,
  PRIMARY KEY (`zone_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=24 ;

-- --------------------------------------------------------

--
-- Table structure for table `zone_downloads`
--

CREATE TABLE IF NOT EXISTS `zone_downloads` (
  `id` int(10) NOT NULL,
  `zone_name` varchar(32) NOT NULL,
  `downloads` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `zone_name` (`zone_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `zone_lot`
--

CREATE TABLE IF NOT EXISTS `zone_lot` (
  `lot_id` int(10) NOT NULL AUTO_INCREMENT,
  `zone_name` varchar(32) NOT NULL,
  `lot_name` varchar(50) NOT NULL,
  `lot_x1` int(10) NOT NULL,
  `lot_z1` int(10) NOT NULL,
  `lot_x2` int(10) NOT NULL,
  `lot_z2` int(10) NOT NULL,
  PRIMARY KEY (`lot_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=17 ;

-- --------------------------------------------------------

--
-- Table structure for table `zone_lotuser`
--

CREATE TABLE IF NOT EXISTS `zone_lotuser` (
  `lot_id` int(10) NOT NULL DEFAULT '0',
  `player_name` varchar(32) NOT NULL,
  PRIMARY KEY (`lot_id`,`player_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `zone_rect`
--

CREATE TABLE IF NOT EXISTS `zone_rect` (
  `rect_id` int(10) NOT NULL AUTO_INCREMENT,
  `zone_name` varchar(32) NOT NULL,
  `rect_x1` int(10) DEFAULT NULL,
  `rect_z1` int(10) DEFAULT NULL,
  `rect_x2` int(10) DEFAULT NULL,
  `rect_z2` int(10) DEFAULT NULL,
  PRIMARY KEY (`rect_id`),
  KEY `zone_name` (`zone_name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=32 ;

-- --------------------------------------------------------

--
-- Table structure for table `zone_user`
--

CREATE TABLE IF NOT EXISTS `zone_user` (
  `zone_name` varchar(32) NOT NULL,
  `player_name` varchar(32) NOT NULL,
  `player_perm` enum('owner','maker','allowed','banned') NOT NULL DEFAULT 'allowed',
  PRIMARY KEY (`zone_name`,`player_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
