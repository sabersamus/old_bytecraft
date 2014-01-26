-- phpMyAdmin SQL Dump
-- version 4.0.4
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jan 21, 2014 at 09:53 PM
-- Server version: 5.6.12-log
-- PHP Version: 5.4.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Database: `minecraft`
--
CREATE DATABASE IF NOT EXISTS `minecraft` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin;
USE `minecraft`;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;


CREATE TABLE IF NOT EXISTS `chest_log` (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `player_name` varchar(32) NOT NULL,
  `x` int (255) NOT NULL,
  `y` int (255) NOT NULL,
  `z` int (255) NOT NULL,
  `world` varchar(32) NOT NULL DEFAULT 'world',
  `action` enum('open','take','deposit'),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `player`
--

CREATE TABLE IF NOT EXISTS `player` (
  `player_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `player_name` varchar(46) DEFAULT NULL,
  `player_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `player_wallet` bigint(20) DEFAULT '1000',
  `player_rank` enum('newcomer','settler','member','mentor','protector'
  ,'builder','admin', 'princess','elder') NOT NULL DEFAULT 'newcomer',
  `player_promoted` int(10) unsigned DEFAULT NULL,
  `player_playtime` int(10) unsigned DEFAULT '0',
  UNIQUE KEY `uid` (`player_id`),
  KEY `player` (`player_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `player_chatlog`
--

CREATE TABLE IF NOT EXISTS `player_chatlog` (
  `chatlog_id` int(10) NOT NULL AUTO_INCREMENT,
  `player_name` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `chatlog_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `chatlog_channel` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `chatlog_message` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`chatlog_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `player_home`
--

CREATE TABLE IF NOT EXISTS `player_home` (
  `home_id` int(10) NOT NULL AUTO_INCREMENT,
  `player_name` varchar(20) NOT NULL,
  `home_x` double DEFAULT NULL,
  `home_y` double DEFAULT NULL,
  `home_z` double DEFAULT NULL,
  `home_yaw` double DEFAULT NULL,
  `home_pitch` double DEFAULT NULL,
  `home_world` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`home_id`),
  KEY `player_name` (`player_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `player_property`
--

DROP TABLE IF EXISTS `player_property`;

CREATE TABLE IF NOT EXISTS `player_property` (
  `player_id` int(10) NOT NULL,
  `player_name` varchar(255) NOT NULL,
  `invisible` enum('true','false') COLLATE utf8_bin NOT NULL DEFAULT 'false',
  `tpblock` enum('true','false') COLLATE utf8_bin NOT NULL DEFAULT 'false',
  `noble` enum('true', 'false') COLLATE utf8_bin NOT NULL DEFAULT 'false',
  `god_color` enum('red','aqua','gold','yellow','dark_aqua','pink','purple','green','dark_green','dark_red','gray') COLLATE utf8_bin NOT NULL DEFAULT 'red',
  PRIMARY KEY `id` (`player_id`),
  UNIQUE KEY (`player_name`)
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

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
  UNIQUE KEY `name.unique` (`name`),
  KEY `name-index` (`name`,`x`,`y`,`z`,`pitch`,`yaw`,`world`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `zone`
--

CREATE TABLE IF NOT EXISTS `zone` (
  `zone_id` int(11) NOT NULL AUTO_INCREMENT,
  `zone_world` varchar(50) NOT NULL DEFAULT 'world',
  `zone_name` varchar(32) NOT NULL,
  `zone_whitelist` enum('true','false') NOT NULL DEFAULT 'true',
  `zone_build` enum('true','false') NOT NULL DEFAULT 'true',
  `zone_pvp` enum('true','false') NOT NULL DEFAULT 'false',
  `zone_hostile` enum('true','false') DEFAULT 'true',
  `zone_entermsg` varchar(250) NOT NULL,
  `zone_exitmsg` varchar(250) NOT NULL,
  PRIMARY KEY (`zone_id`),
  UNIQUE KEY `name` (`zone_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `zone_lot`
--

CREATE TABLE IF NOT EXISTS `zone_lot` (
  `lot_id` int(10) NOT NULL AUTO_INCREMENT,
  `zone_id` int(10) NOT NULL,
  `lot_name` varchar(50) NOT NULL,
  `lot_x1` int(10) NOT NULL,
  `lot_y1` int(10) NOT NULL,
  `lot_x2` int(10) NOT NULL,
  `lot_y2` int(10) NOT NULL,
  `special` int(11) DEFAULT NULL,
  PRIMARY KEY (`lot_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `zone_lotuser`
--

CREATE TABLE IF NOT EXISTS `zone_lotuser` (
  `lot_id` int(10) NOT NULL DEFAULT '0',
  `user_id` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`lot_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `zone_rect`
--

CREATE TABLE IF NOT EXISTS `zone_rect` (
  `rect_id` int(10) NOT NULL AUTO_INCREMENT,
  `zone_name` varchar(32) NOT NULL,
  `rect_x1` int(10) DEFAULT NULL,
  `rect_y1` int(10) DEFAULT NULL,
  `rect_x2` int(10) DEFAULT NULL,
  `rect_y2` int(10) DEFAULT NULL,
  PRIMARY KEY (`rect_id`),
  KEY `zone_name` (`zone_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

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
