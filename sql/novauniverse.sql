-- phpMyAdmin SQL Dump
-- version 4.9.5deb2
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Mar 11, 2021 at 11:20 AM
-- Server version: 8.0.23-0ubuntu0.20.04.1
-- PHP Version: 7.4.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `novauniverse`
--
CREATE DATABASE IF NOT EXISTS `novauniverse` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin;
USE `novauniverse`;

-- --------------------------------------------------------

--
-- Table structure for table `chat_log`
--

DROP TABLE IF EXISTS `chat_log`;
CREATE TABLE `chat_log` (
  `id` int UNSIGNED NOT NULL,
  `player_id` int UNSIGNED NOT NULL,
  `timestamp` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `content` text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `is_command` tinyint(1) NOT NULL,
  `canceled` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `config`
--

DROP TABLE IF EXISTS `config`;
CREATE TABLE `config` (
  `id` int UNSIGNED NOT NULL,
  `data_key` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `data_value` text CHARACTER SET utf8 COLLATE utf8_bin
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `config`
--

INSERT INTO `config` (`id`, `data_key`, `data_value`) VALUES
(1, 'lobby_fireworks', '0');

-- --------------------------------------------------------

--
-- Table structure for table `players`
--

DROP TABLE IF EXISTS `players`;
CREATE TABLE `players` (
  `id` int UNSIGNED NOT NULL,
  `uuid` varchar(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `username` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `first_join_timestamp` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_join_timestamp` datetime DEFAULT CURRENT_TIMESTAMP,
  `first_ip_address` text CHARACTER SET utf8 COLLATE utf8_bin,
  `last_ip_address` text CHARACTER SET utf8 COLLATE utf8_bin,
  `is_online` tinyint(1) NOT NULL,
  `heartbeat_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `server_id` int UNSIGNED DEFAULT NULL,
  `reconnect_server` int UNSIGNED DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `servers`
--

DROP TABLE IF EXISTS `servers`;
CREATE TABLE `servers` (
  `id` int UNSIGNED NOT NULL,
  `name` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `host` text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `port` smallint UNSIGNED NOT NULL,
  `heartbeat` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `type_id` int UNSIGNED NOT NULL,
  `minigame_started` tinyint(1) NOT NULL DEFAULT '0',
  `has_failed` tinyint(1) NOT NULL DEFAULT '0',
  `request_shutdown` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `server_type`
--

DROP TABLE IF EXISTS `server_type`;
CREATE TABLE `server_type` (
  `id` int UNSIGNED NOT NULL,
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `display_name` text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `soft_player_limit` smallint UNSIGNED NOT NULL,
  `hard_player_limit` smallint UNSIGNED NOT NULL,
  `target_player_count` smallint UNSIGNED NOT NULL,
  `is_minigame` tinyint(1) NOT NULL,
  `return_to_server_type_id` int UNSIGNED DEFAULT NULL,
  `server_naming_scheme` varchar(10) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `lore` text CHARACTER SET utf8 COLLATE utf8_bin,
  `show_in_server_list` tinyint(1) NOT NULL DEFAULT '0',
  `allow_join_command` tinyint(1) NOT NULL DEFAULT '1',
  `allow_spectate_command` int NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `server_type`
--

INSERT INTO `server_type` (`id`, `name`, `display_name`, `soft_player_limit`, `hard_player_limit`, `target_player_count`, `is_minigame`, `return_to_server_type_id`, `server_naming_scheme`, `lore`, `show_in_server_list`, `allow_join_command`, `allow_spectate_command`) VALUES
(1, 'lobby', 'Lobby', 80, 84, 40, 0, NULL, 'lobby', NULL, 0, 1, 0),
(2, 'terrasmp', 'TerraSMP', 25, 25, 25, 0, 1, 'tsmp', '§fSMP server with a map based on the\\n§freal world at a 1:1000 scale', 1, 1, 0),
(3, 'survivalgames', 'Survival Games', 24, 24, 24, 1, 1, 'sg', NULL, 1, 1, 1),
(4, 'skywars', 'Skywars', 12, 12, 12, 1, 1, 'sw', NULL, 1, 1, 1),
(5, 'missilewars', 'Missile Wars', 12, 16, 12, 1, 1, 'mw', NULL, 1, 1, 1),
(6, 'uhc', 'UHC', 24, 26, 24, 1, 1, 'uhc', NULL, 1, 1, 0),
(7, 'deathswap', 'Death Swap', 12, 16, 12, 1, 1, 'ds', NULL, 1, 1, 0),
(8, 'build', 'Build servers', 69, 69, 69, 0, 1, 'build', NULL, 0, 0, 0);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `chat_log`
--
ALTER TABLE `chat_log`
  ADD PRIMARY KEY (`id`),
  ADD KEY `player_id` (`player_id`),
  ADD KEY `player_id_2` (`player_id`),
  ADD KEY `id` (`id`);
ALTER TABLE `chat_log` ADD FULLTEXT KEY `content` (`content`);

--
-- Indexes for table `config`
--
ALTER TABLE `config`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `key` (`data_key`);

--
-- Indexes for table `players`
--
ALTER TABLE `players`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uuid` (`uuid`),
  ADD KEY `server_id` (`server_id`),
  ADD KEY `reconnect_server` (`reconnect_server`),
  ADD KEY `reconnect_server_2` (`reconnect_server`);

--
-- Indexes for table `servers`
--
ALTER TABLE `servers`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`),
  ADD KEY `type_id` (`type_id`);

--
-- Indexes for table `server_type`
--
ALTER TABLE `server_type`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`),
  ADD UNIQUE KEY `server_naming_scheme` (`server_naming_scheme`),
  ADD UNIQUE KEY `id_2` (`id`),
  ADD KEY `id` (`id`),
  ADD KEY `return_to_server_type_id` (`return_to_server_type_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `chat_log`
--
ALTER TABLE `chat_log`
  MODIFY `id` int UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `config`
--
ALTER TABLE `config`
  MODIFY `id` int UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `players`
--
ALTER TABLE `players`
  MODIFY `id` int UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `servers`
--
ALTER TABLE `servers`
  MODIFY `id` int UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `server_type`
--
ALTER TABLE `server_type`
  MODIFY `id` int UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `chat_log`
--
ALTER TABLE `chat_log`
  ADD CONSTRAINT `chat_log_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`);

--
-- Constraints for table `players`
--
ALTER TABLE `players`
  ADD CONSTRAINT `player reconnect server -> server id` FOREIGN KEY (`reconnect_server`) REFERENCES `servers` (`id`) ON DELETE SET NULL ON UPDATE SET NULL,
  ADD CONSTRAINT `player server -> server id` FOREIGN KEY (`server_id`) REFERENCES `servers` (`id`) ON DELETE SET NULL ON UPDATE SET NULL;

--
-- Constraints for table `servers`
--
ALTER TABLE `servers`
  ADD CONSTRAINT `servers_ibfk_1` FOREIGN KEY (`type_id`) REFERENCES `server_type` (`id`);

--
-- Constraints for table `server_type`
--
ALTER TABLE `server_type`
  ADD CONSTRAINT `server_type_ibfk_1` FOREIGN KEY (`return_to_server_type_id`) REFERENCES `server_type` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
