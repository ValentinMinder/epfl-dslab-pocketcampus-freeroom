-- phpMyAdmin SQL Dump
-- version 4.2.10.1deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Nov 10, 2014 at 10:16 AM
-- Server version: 5.5.40-0+wheezy1
-- PHP Version: 5.6.2-1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `pocketcampus`
--

-- --------------------------------------------------------

--
-- Table structure for table `fr-roomslist`
--

CREATE TABLE IF NOT EXISTS `fr-roomslist` (
  `uid` char(255) NOT NULL,
  `doorCode` char(255) NOT NULL,
  `doorCodeWithoutSpace` char(255) NOT NULL,
  `alias` char(255) DEFAULT NULL,
  `capacity` int(11) DEFAULT NULL,
  `site_label` char(255) DEFAULT NULL,
  `surface` double DEFAULT NULL,
  `building_name` char(255) DEFAULT NULL,
  `zone` char(255) DEFAULT NULL,
  `unitlabel` char(255) DEFAULT NULL,
  `site_id` int(11) DEFAULT NULL,
  `floor` int(11) DEFAULT NULL,
  `unitname` char(255) DEFAULT NULL,
  `site_name` char(255) DEFAULT NULL,
  `unitid` int(11) DEFAULT NULL,
  `building_label` char(255) DEFAULT NULL,
  `cf` char(255) DEFAULT NULL,
  `adminuse` char(255) DEFAULT NULL,
  `EWAid` char(255) DEFAULT NULL,
  `typeFR` char(255) DEFAULT NULL,
  `typeEN` char(255) DEFAULT NULL,
  `dincat` char(255) DEFAULT NULL,
  `enabled` tinyint(1) DEFAULT '1',
  `groupAccess` int(11) DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `fr-roomslist`
--

INSERT INTO `fr-roomslist` (`uid`, `doorCode`, `doorCodeWithoutSpace`, `alias`, `capacity`, `site_label`, `surface`, `building_name`, `zone`, `unitlabel`, `site_id`, `floor`, `unitname`, `site_name`, `unitid`, `building_label`, `cf`, `adminuse`, `EWAid`, `typeFR`, `typeEN`, `dincat`, `enabled`, `groupAccess`) VALUES
('10698', 'CO 6', 'CO6', 'CO6', NULL, 'ECUBLENS', 137.23, 'CO', 'Z', 'Domaine de la formation', 3, 2, 'DAF', 'E', 10026, 'Coupole', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', NULL, 'SALLE TP', 'PW LAB', '5.3.IT', 1, 20),
('1070', 'CH A3 30', 'CHA330', 'CHA30', 16, 'ECUBLENS', 37.01, 'CH', 'A', 'Domaine de la formation', 3, 3, 'DAF', 'E', 10026, 'Bâtiment CH', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('1084', 'CH B0 427', 'CHB0427', 'CH B0 427', NULL, 'ECUBLENS', 109.75, 'CH', 'B', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Bâtiment CH', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', NULL, 'SALLE TP', 'PW LAB', '5.3.D', 1, 20),
('10965', 'ODY -1 0020', 'ODY-10020', 'ODY-1 0020', 38, 'ECUBLENS', 125.23, 'ODY', 'Z', 'Domaine de la formation', 3, -1, 'DAF', 'E', 10026, 'Odyssea', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('10966', 'ODY -1 0021', 'ODY-10021', 'ODY-1 0021', 32, 'ECUBLENS', 125.21, 'ODY', 'Z', 'Domaine de la formation', 3, -1, 'DAF', 'E', 10026, 'Odyssea', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('11092', 'ODY 0 16', 'ODY016', 'ODY16', 72, 'ECUBLENS', 104.7, 'ODY', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Odyssea', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('11184', 'BCH 1103', 'BCH1103', 'Bch 1103', NULL, 'ECUBLENS', 171.88, 'BCH', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Batochime UNIL', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', NULL, 'SALLE TP', 'PW LAB', '5.3.A', 1, 20),
('11186', 'BCH 1113', 'BCH1113', 'Bch 1113', 30, 'ECUBLENS', 73.44, 'BCH', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Batochime UNIL', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', NULL, 'SALLE TP', 'PW LAB', '5.3.IT', 1, 20),
('1135', 'CH B2 392', 'CHB2392', 'CH B2 392', NULL, 'ECUBLENS', 37.5, 'CH', 'B', 'Domaine de la formation', 3, 2, 'DAF', 'E', 10026, 'Bâtiment CH', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', NULL, 'SALLE TP', 'PW LAB', '5.3.IT', 1, 20),
('11416', 'BSP 626', 'BSP626', 'Cub 626', 36, 'ECUBLENS', 68.02, 'BSP', 'Z', 'Domaine de la formation', 3, 6, 'DAF', 'E', 10026, 'Cubotron UNIL', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('1195', 'CH B3 30', 'CHB330', 'CH B330', 68, 'ECUBLENS', 94.61, 'CH', 'B', 'Domaine de la formation', 3, 3, 'DAF', 'E', 10026, 'Bâtiment CH', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('1196', 'CH B3 31', 'CHB331', 'CH B331', 50, 'ECUBLENS', 78.59, 'CH', 'B', 'Domaine de la formation', 3, 3, 'DAF', 'E', 10026, 'Bâtiment CH', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('12205', 'BC 01', 'BC01', 'BC 01', 84, 'ECUBLENS', 122.09, 'BC', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Bâtiment BC', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', 'bc01@intranet.epfl.ch', 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('12206', 'BC 02', 'BC02', 'BC 02', 40, 'ECUBLENS', 79.52, 'BC', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Bâtiment BC', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', 'bc02@intranet.epfl.ch', 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('12207', 'BC 03', 'BC03', 'BC 03', 40, 'ECUBLENS', 79.54, 'BC', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Bâtiment BC', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', 'bc03@intranet.epfl.ch', 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('12208', 'BC 04', 'BC04', 'BC 04', 40, 'ECUBLENS', 78.97, 'BC', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Bâtiment BC', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', 'bc04@intranet.epfl.ch', 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('12211', 'BC 07', 'BC07', 'BC 07-08', 35, 'ECUBLENS', 116.07, 'BC', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Bâtiment BC', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', 'bc07-08@intranet.epfl.ch', 'SALLE TP', 'PW LAB', '5.3.IT', 1, 20),
('12212', 'BC 08', 'BC08', 'BC 08', 39, 'ECUBLENS', 139.35, 'BC', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Bâtiment BC', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', 'bc07-08@intranet.epfl.ch', 'SALLE TP', 'PW LAB', '5.3.IT', 1, 20),
('12214', 'BC 010', 'BC010', 'BC 010', NULL, 'ECUBLENS', 51.89, 'BC', 'Z', 'IC - Services généraux Infrastructures', 3, NULL, 'IC-IN', 'E', 10395, 'Bâtiment BC', '1195', 'LOCAUX DE CONFERENCES', 'bc010@intranet.epfl.ch', 'CONFERENCES', 'CONFERENCE ROOM', '2.3.E', 1, 20),
('14868', 'BSP 231', 'BSP231', 'BSP1', 96, 'ECUBLENS', 146.48, 'BSP', 'Z', 'Domaine de la formation', 3, 2, 'DAF', 'E', 10026, 'Cubotron UNIL', '0021', 'LOCAUX D''ENSEIGNEMENT AVEC MOBILIER FIXE', NULL, 'AUDITOIRE', 'AUDIENCE ROOM', '5.1.A', 1, 20),
('14869', 'BSP 233', 'BSP233', 'BSP3', 36, 'ECUBLENS', 66.32, 'BSP', 'Z', 'Domaine de la formation', 3, 2, 'DAF', 'E', 10026, 'Cubotron UNIL', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('14870', 'BSP 234', 'BSP234', 'BSP2', 46, 'ECUBLENS', 78.43, 'BSP', 'Z', 'Domaine de la formation', 3, 2, 'DAF', 'E', 10026, 'Cubotron UNIL', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('15340', 'BSP 727', 'BSP727', 'Cub 727', 41, 'ECUBLENS', 56.52, 'BSP', 'Z', 'Domaine de la formation', 3, 7, 'DAF', 'E', 10026, 'Cubotron UNIL', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('16', 'AU -1 7005', 'AU-17005', 'AU - 1 7005', 20, 'LAUSANNE', 80.39, 'AU', 'Z', 'Domaine de la formation', 1, -1, 'DAF', 'L', 10026, 'Aula des Cèdres - sous-sol', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('1831', 'CM 1 1', 'CM11', 'CM1', 168, 'ECUBLENS', 247.66, 'CM', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Midi', '0021', 'LOCAUX D''ENSEIGNEMENT AVEC MOBILIER FIXE', NULL, 'AUDITOIRE', 'AUDIENCE ROOM', '5.1.A', 1, 20),
('1835', 'CM 1 120', 'CM1120', 'CM120', 100, 'ECUBLENS', 204.13, 'CM', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Midi', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('1837', 'CM 1 221', 'CM1221', 'CM 1 221', 64, 'ECUBLENS', 98.87, 'CM', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Midi', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('1883', 'CM 0 10', 'CM010', 'CM10', 48, 'ECUBLENS', 100.85, 'CM', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Centre Midi', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'TUTORAT', 'EDUCATION', '5.2.C', 1, 20),
('1884', 'CM 1 100', 'CM1100', 'CM100', 50, 'ECUBLENS', 69.58, 'CM', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Midi', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('1885', 'CM 1 103', 'CM1103', 'CM103', NULL, 'ECUBLENS', 217.42, 'CM', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Midi', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', NULL, 'SALLE TP', 'PW LAB', '5.3.IT', 1, 20),
('1886', 'CM 1 104', 'CM1104', 'CM104', 50, 'ECUBLENS', 75.97, 'CM', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Midi', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('1887', 'CM 1 105', 'CM1105', 'CM105', 112, 'ECUBLENS', 204.49, 'CM', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Midi', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('1888', 'CM 1 106', 'CM1106', 'CM106', 64, 'ECUBLENS', 99.4, 'CM', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Midi', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('1889', 'CM 1 107', 'CM1107', 'CM107', NULL, 'ECUBLENS', 45.83, 'CM', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Midi', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', NULL, 'SALLE TP', 'PW LAB', '5.3.IT', 1, 20),
('1890', 'CM 1 108', 'CM1108', 'CM108', NULL, 'ECUBLENS', 37.5, 'CM', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Midi', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', NULL, 'SALLE TP', 'PW LAB', '5.3.A', 1, 20),
('1891', 'CM 0 11', 'CM011', 'CM11', 56, 'ECUBLENS', 101.24, 'CM', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Centre Midi', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('1892', 'CM 1 110', 'CM1110', 'CM110', NULL, 'ECUBLENS', 68.25, 'CM', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Midi', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', NULL, 'SALLE TP', 'PW LAB', '5.3.IT', 1, 20),
('1893', 'CM 1 111', 'CM1111', 'CM111', 24, 'ECUBLENS', 75.97, 'CM', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Midi', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', NULL, 'SALLE TP', 'PW LAB', '5.3.IT', 1, 20),
('1894', 'CM 1 112', 'CM1112', 'CM112', 36, 'ECUBLENS', 101.35, 'CM', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Midi', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', NULL, 'SALLE TP', 'PW LAB', '5.3.IT', 1, 20),
('1895', 'CM 1 113', 'CM1113', 'CM113', 22, 'ECUBLENS', 50.23, 'CM', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Midi', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('1896', 'CM 0 12', 'CM012', 'CM12', 50, 'ECUBLENS', 75.31, 'CM', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Centre Midi', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('1898', 'CM 1 121', 'CM1121', 'CM121', 100, 'ECUBLENS', 203.56, 'CM', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Midi', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('1899', 'CM 0 13', 'CM013', 'CM13', 60, 'ECUBLENS', 86.82, 'CM', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Centre Midi', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('1928', 'CM 1 2', 'CM12', 'CM2', 168, 'ECUBLENS', 247.66, 'CM', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Midi', '0021', 'LOCAUX D''ENSEIGNEMENT AVEC MOBILIER FIXE', NULL, 'AUDITOIRE', 'AUDIENCE ROOM', '5.1.A', 1, 20),
('2021', 'CM 1 3', 'CM13', 'CM3', 168, 'ECUBLENS', 247.66, 'CM', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Midi', '0021', 'LOCAUX D''ENSEIGNEMENT AVEC MOBILIER FIXE', NULL, 'AUDITOIRE', 'AUDIENCE ROOM', '5.1.A', 1, 20),
('2023', 'CM 1 4', 'CM14', 'CM4', 88, 'ECUBLENS', 150.15, 'CM', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Midi', '0021', 'LOCAUX D''ENSEIGNEMENT AVEC MOBILIER FIXE', NULL, 'AUDITOIRE', 'AUDIENCE ROOM', '5.1.A', 1, 20),
('2024', 'CM 1 5', 'CM15', 'CM5', 88, 'ECUBLENS', 150.15, 'CM', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Midi', '0021', 'LOCAUX D''ENSEIGNEMENT AVEC MOBILIER FIXE', NULL, 'AUDITOIRE', 'AUDIENCE ROOM', '5.1.A', 1, 20),
('2042', 'CM 0 9', 'CM09', 'CM9', 40, 'ECUBLENS', 100.85, 'CM', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Centre Midi', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'TUTORAT', 'EDUCATION', '5.2.C', 1, 20),
('2043', 'CO 010', 'CO010', 'CO10', 40, 'ECUBLENS', 109.4, 'CO', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Coupole', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'TUTORAT', 'EDUCATION', '5.2.C', 1, 1),
('2044', 'CO 011', 'CO011', 'CO11', 40, 'ECUBLENS', 108.89, 'CO', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Coupole', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'TUTORAT', 'EDUCATION', '5.2.C', 1, 1),
('2045', 'CO 015', 'CO015', 'CO15', 40, 'ECUBLENS', 96.14, 'CO', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Coupole', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'TUTORAT', 'EDUCATION', '5.2.C', 1, 1),
('2046', 'CO 016', 'CO016', 'CO16', 40, 'ECUBLENS', 96.44, 'CO', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Coupole', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'TUTORAT', 'EDUCATION', '5.2.C', 1, 1),
('2047', 'CO 017', 'CO017', 'CO17', 40, 'ECUBLENS', 99.22, 'CO', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Coupole', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'TUTORAT', 'EDUCATION', '5.2.C', 1, 1),
('2048', 'CO 020', 'CO020', 'CO20', NULL, 'ECUBLENS', 137.82, 'CO', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Coupole', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', 'co020@intranet.epfl.ch', 'SALLE TP', 'PW LAB', '5.3.IT', 1, 20),
('2049', 'CO 021', 'CO021', 'CO21', NULL, 'ECUBLENS', 209.21, 'CO', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Coupole', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', 'co021@intranet.epfl.ch', 'SALLE TP', 'PW LAB', '5.3.IT', 1, 20),
('2051', 'CO 023', 'CO023', 'CO23', NULL, 'ECUBLENS', 137.49, 'CO', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Coupole', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', 'co023@intranet.epfl.ch', 'SALLE TP', 'PW LAB', '5.3.IT', 1, 20),
('20869', 'BSP 407', 'BSP407', 'Cub 407', 38, 'ECUBLENS', 56.52, 'BSP', 'Z', 'Domaine de la formation', 3, 4, 'DAF', 'E', 10026, 'Cubotron UNIL', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('2119', 'CO 2', 'CO2', 'CO2', 216, 'ECUBLENS', 309.08, 'CO', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Coupole', '0021', 'LOCAUX D''ENSEIGNEMENT AVEC MOBILIER FIXE', NULL, 'AUDITOIRE', 'AUDIENCE ROOM', '5.1.A', 1, 20),
('2123', 'CO 120', 'CO120', 'CO 120', 40, 'ECUBLENS', 86.94, 'CO', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Coupole', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('2124', 'CO 121', 'CO121', 'CO 121', 30, 'ECUBLENS', 69.16, 'CO', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Coupole', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('2125', 'CO 122', 'CO122', 'CO 122', 40, 'ECUBLENS', 103.43, 'CO', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Coupole', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'TUTORAT', 'EDUCATION', '5.2.C', 1, 1),
('2126', 'CO 123', 'CO123', 'CO 123', 40, 'ECUBLENS', 103.43, 'CO', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Coupole', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'TUTORAT', 'EDUCATION', '5.2.C', 1, 1),
('2127', 'CO 124', 'CO124', 'CO 124', 40, 'ECUBLENS', 102.96, 'CO', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Coupole', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'TUTORAT', 'EDUCATION', '5.2.C', 1, 1),
('2184', 'CO 1', 'CO1', 'CO1', 326, 'ECUBLENS', 450.51, 'CO', 'Z', 'Domaine de la formation', 3, 2, 'DAF', 'E', 10026, 'Coupole', '0021', 'LOCAUX D''ENSEIGNEMENT AVEC MOBILIER FIXE', NULL, 'AUDITOIRE', 'AUDIENCE ROOM', '5.1.A', 1, 20),
('2185', 'CO 216', 'CO216', 'CO 216', NULL, 'ECUBLENS', 55.41, 'CO', 'Z', 'Médiacom', 3, 2, 'MEDIACOM', 'E', 10007, 'Coupole', '0090', 'LOCAUX DE CONFERENCES', NULL, 'CONFERENCES', 'CONFERENCE ROOM', '2.3.A', 1, 20),
('2228', 'CO 3', 'CO3', 'CO3', 218, 'ECUBLENS', 312.4, 'CO', 'Z', 'Domaine de la formation', 3, 3, 'DAF', 'E', 10026, 'Coupole', '0021', 'LOCAUX D''ENSEIGNEMENT AVEC MOBILIER FIXE', NULL, 'AUDITOIRE', 'AUDIENCE ROOM', '5.1.A', 1, 20),
('2236', 'CO 4', 'CO4', 'CO4', NULL, 'ECUBLENS', 137.3, 'CO', 'Z', 'Domaine de la formation', 3, 2, 'DAF', 'E', 10026, 'Coupole', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', NULL, 'SALLE TP', 'PW LAB', '5.3.IT', 1, 20),
('2237', 'CO 5', 'CO5', 'CO5', NULL, 'ECUBLENS', 137.79, 'CO', 'Z', 'Domaine de la formation', 3, 2, 'DAF', 'E', 10026, 'Coupole', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', NULL, 'SALLE TP', 'PW LAB', '5.3.IT', 1, 20),
('2271', 'DIA 003', 'DIA003', 'DIA 003', 50, 'ECUBLENS', 96.77, 'DIA', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Diagonale', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('2273', 'DIA 005', 'DIA005', 'DIA 005', 50, 'ECUBLENS', 96.77, 'DIA', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Diagonale', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('2309', 'ELA 1', 'ELA1', 'ELA1', 122, 'ECUBLENS', 190.31, 'ELA', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Bâtiment ELA', '0021', 'LOCAUX D''ENSEIGNEMENT AVEC MOBILIER FIXE', NULL, 'AUDITOIRE', 'AUDIENCE ROOM', '5.1.A', 1, 20),
('2319', 'ELA 2', 'ELA2', 'ELA2', 88, 'ECUBLENS', 144.76, 'ELA', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Bâtiment ELA', '0021', 'LOCAUX D''ENSEIGNEMENT AVEC MOBILIER FIXE', NULL, 'AUDITOIRE', 'AUDIENCE ROOM', '5.1.A', 1, 20),
('23227', 'ODY -1 0019.1', 'ODY-10019.1', 'ODY-1 0019.1', 12, 'ECUBLENS', 32.68, 'ODY', 'Z', 'Domaine de la formation', 3, -1, 'DAF', 'E', 10026, 'Odyssea', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('23228', 'ODY -1 0019.2', 'ODY-10019.2', 'ODY -1 0019.2', 16, 'ECUBLENS', 41.17, 'ODY', 'Z', 'Domaine de la formation', 3, -1, 'DAF', 'E', 10026, 'Odyssea', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('2412', 'ELB 217', 'ELB217', 'ELB 217', NULL, 'ECUBLENS', 230.49, 'ELB', 'Z', 'Domaine de la formation', 3, 2, 'DAF', 'E', 10026, 'Bâtiment ELB', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', NULL, 'SALLE TP', 'PW LAB', '5.3.H', 1, 20),
('2481', 'ELD 020', 'ELD020', 'ELD 020', NULL, 'ECUBLENS', 126.2, 'ELD', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Bâtiment ELD', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', NULL, 'SALLE TP', 'PW LAB', '5.3.IT', 1, 20),
('2484', 'ELD 040', 'ELD040', 'ELD 040', NULL, 'ECUBLENS', 377.31, 'ELD', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Bâtiment ELD', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', NULL, 'SALLE TP', 'PW LAB', '5.3.C', 1, 20),
('2507', 'ELD 120', 'ELD120', 'ELD 120', 26, 'ECUBLENS', 59.15, 'ELD', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Bâtiment ELD', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('2647', 'ELE 111', 'ELE111', 'ELE 111', 22, 'ECUBLENS', 48.21, 'ELE', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Bâtiment ELE', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('2744', 'ELG 116', 'ELG116', 'ELG 116', 24, 'ECUBLENS', 48.82, 'ELG', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Bâtiment ELG', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('2745', 'ELG 120', 'ELG120', 'ELG 120', 50, 'ECUBLENS', 96.97, 'ELG', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Bâtiment ELG', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('2746', 'ELG 124', 'ELG124', 'ELG 124', NULL, 'ECUBLENS', 230.43, 'ELG', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Bâtiment ELG', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', NULL, 'SALLE TP', 'PW LAB', '5.3.C', 1, 20),
('2919', 'GC A1 416', 'GCA1416', 'GC A1 416', 24, 'ECUBLENS', 54.37, 'GC', 'A', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Bâtiment GC', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('3014', 'GC A3 30', 'GCA330', 'GCA30', 70, 'ECUBLENS', 94.61, 'GC', 'A', 'Domaine de la formation', 3, 3, 'DAF', 'E', 10026, 'Bâtiment GC', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('3015', 'GC A3 31', 'GCA331', 'GCA31', 60, 'ECUBLENS', 78.74, 'GC', 'A', 'Domaine de la formation', 3, 3, 'DAF', 'E', 10026, 'Bâtiment GC', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('3137', 'GC B3 30', 'GCB330', 'GCB30', 70, 'ECUBLENS', 94.61, 'GC', 'B', 'Domaine de la formation', 3, 3, 'DAF', 'E', 10026, 'Bâtiment GC', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('3138', 'GC B3 31', 'GCB331', 'GCB31', 70, 'ECUBLENS', 113.62, 'GC', 'B', 'Domaine de la formation', 3, 3, 'DAF', 'E', 10026, 'Bâtiment GC', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('3208', 'GC C3 30', 'GCC330', 'GCC30', 90, 'ECUBLENS', 155.63, 'GC', 'C', 'Domaine de la formation', 3, 3, 'DAF', 'E', 10026, 'Bâtiment GC', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('3211', 'GC D0 383', 'GCD0383', 'GC D0 383', NULL, 'ECUBLENS', 66.06, 'GC', 'D', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Bâtiment GC', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', NULL, 'SALLE TP', 'PW LAB', '5.3.A', 1, 20),
('3213', 'GC D0 386', 'GCD0386', 'GC D0 386', 42, 'ECUBLENS', 66.06, 'GC', 'D', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Bâtiment GC', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('3623', 'GR A3 30', 'GRA330', 'GRA30', 58, 'ECUBLENS', 78.59, 'GR', 'A', 'Domaine de la formation', 3, 3, 'DAF', 'E', 10026, 'Bâtiment GR', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('3624', 'GR A3 31', 'GRA331', 'GRA31', 48, 'ECUBLENS', 88.44, 'GR', 'A', 'Domaine de la formation', 3, 3, 'DAF', 'E', 10026, 'Bâtiment GR', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('3625', 'GR A3 32', 'GRA332', 'GRA32', 58, 'ECUBLENS', 78.59, 'GR', 'A', 'Domaine de la formation', 3, 3, 'DAF', 'E', 10026, 'Bâtiment GR', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('3652', 'GR B0 01', 'GRB001', 'GRB01', NULL, 'ECUBLENS', 171.07, 'GR', 'B', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Bâtiment GR', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', NULL, 'SALLE TP', 'PW LAB', '5.3.IT', 1, 20),
('3702', 'GR B3 30', 'GRB330', 'GRB30', 84, 'ECUBLENS', 111.2, 'GR', 'B', 'Domaine de la formation', 3, 3, 'DAF', 'E', 10026, 'Bâtiment GR', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('3703', 'GR B3 31', 'GRB331', 'GRB31', NULL, 'ECUBLENS', 156.12, 'GR', 'B', 'Domaine de la formation', 3, 3, 'DAF', 'E', 10026, 'Bâtiment GR', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', NULL, 'SALLE TP', 'PW LAB', '5.3.B', 1, 20),
('3738', 'GR C0 01', 'GRC001', 'GRC01', 64, 'ECUBLENS', 100.39, 'GR', 'C', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Bâtiment GR', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('3739', 'GR C0 02', 'GRC002', 'GRC02', NULL, 'ECUBLENS', 100.39, 'GR', 'C', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Bâtiment GR', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', NULL, 'SALLE TP', 'PW LAB', '5.3.IT', 1, 20),
('3848', 'INF 1', 'INF1', 'INF 1', NULL, 'ECUBLENS', 267.16, 'INF', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Bâtiment INF', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', NULL, 'SALLE TP', 'PW LAB', '5.3.IT', 1, 20),
('3854', 'INF 119', 'INF119', 'INF 119', 50, 'ECUBLENS', 132.6, 'INF', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Bâtiment INF', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', 'inf119@intranet.epfl.ch', 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('3886', 'INF 2', 'INF2', 'INF 2', NULL, 'ECUBLENS', 257.49, 'INF', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Bâtiment INF', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', NULL, 'SALLE TP', 'PW LAB', '5.3.IT', 1, 20),
('3888', 'INF 213', 'INF213', 'INF 213', 59, 'ECUBLENS', 94.2, 'INF', 'Z', 'Domaine de la formation', 3, 2, 'DAF', 'E', 10026, 'Bâtiment INF', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', 'inf213@intranet.epfl.ch', 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('3921', 'INF 3', 'INF3', 'INF 3', NULL, 'ECUBLENS', 257.04, 'INF', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Bâtiment INF', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', NULL, 'SALLE TP', 'PW LAB', '5.3.IT', 1, 20),
('4033', 'INJ 218', 'INJ218', 'INJ 218', 96, 'ECUBLENS', 177.41, 'INJ', 'Z', 'Domaine de la formation', 3, 2, 'DAF', 'E', 10026, 'Bâtiment INJ', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', 'inj218@intranet.epfl.ch', 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('4137', 'INM 10', 'INM10', 'INM 10', 58, 'ECUBLENS', 119.95, 'INM', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Bâtiment INM', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', 'inm10@intranet.epfl.ch', 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('4138', 'INM 11', 'INM11', 'INM 11', 42, 'ECUBLENS', 81.16, 'INM', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Bâtiment INM', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', 'inm11@intranet.epfl.ch', 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('4157', 'INM 200', 'INM200', 'INM 200', 86, 'ECUBLENS', 153.6, 'INM', 'Z', 'Domaine de la formation', 3, 2, 'DAF', 'E', 10026, 'Bâtiment INM', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', 'inm200@intranet.epfl.ch', 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('4158', 'INM 201', 'INM201', 'INM 201', 36, 'ECUBLENS', 85.74, 'INM', 'Z', 'Domaine de la formation', 3, 2, 'DAF', 'E', 10026, 'Bâtiment INM', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', 'inm201@intranet.epfl.ch', 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('4159', 'INM 202', 'INM202', 'INM 202', 88, 'ECUBLENS', 153.6, 'INM', 'Z', 'Domaine de la formation', 3, 2, 'DAF', 'E', 10026, 'Bâtiment INM', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', 'inm202@intranet.epfl.ch', 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('4160', 'INM 203', 'INM203', 'INM 203', 40, 'ECUBLENS', 82.21, 'INM', 'Z', 'Domaine de la formation', 3, 2, 'DAF', 'E', 10026, 'Bâtiment INM', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', 'inm203@intranet.epfl.ch', 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('4255', 'INN 218', 'INN218', 'INN 218', NULL, 'ECUBLENS', 222.66, 'INN', 'Z', 'Domaine de la formation', 3, 2, 'DAF', 'E', 10026, 'Bâtiment INN', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', NULL, 'SALLE TP', 'PW LAB', '5.3.A', 1, 20),
('4344', 'INR 113', 'INR113', 'INR 113', 50, 'ECUBLENS', 94.39, 'INR', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Bâtiment INR', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', 'inr113@intranet.epfl.ch', 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('4380', 'INR 219', 'INR219', 'INR 219', 78, 'ECUBLENS', 132.94, 'INR', 'Z', 'Domaine de la formation', 3, 2, 'DAF', 'E', 10026, 'Bâtiment INR', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', 'inr219@intranet.epfl.ch', 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('4654', 'MA B1 486', 'MAB1486', 'MA B1 486', NULL, 'ECUBLENS', 101.53, 'MA', 'B', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Bâtiment MA', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', NULL, 'SALLE TP', 'PW LAB', '5.3.IT', 1, 20),
('4911', 'MA A1 10', 'MAA110', 'MA10', 48, 'ECUBLENS', 69.21, 'MA', 'A', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Bâtiment MA', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('4912', 'MA B1 11', 'MAB111', 'MA11', 84, 'ECUBLENS', 133.48, 'MA', 'B', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Bâtiment MA', '0021', 'LOCAUX D''ENSEIGNEMENT AVEC MOBILIER FIXE', NULL, 'AUDITOIRE', 'AUDIENCE ROOM', '5.1.A', 1, 20),
('4913', 'MA A1 12', 'MAA112', 'MA12', 48, 'ECUBLENS', 69.21, 'MA', 'A', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Bâtiment MA', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('4914', 'MA A3 30', 'MAA330', 'MA30', 60, 'ECUBLENS', 95.18, 'MA', 'A', 'Domaine de la formation', 3, 3, 'DAF', 'E', 10026, 'Bâtiment MA', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('4915', 'MA A3 31', 'MAA331', 'MA31', 60, 'ECUBLENS', 79.1, 'MA', 'A', 'Domaine de la formation', 3, 3, 'DAF', 'E', 10026, 'Bâtiment MA', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('4920', 'ME A0 392', 'MEA0392', 'ME A0 392', NULL, 'ECUBLENS', 151.8, 'ME', 'A', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Bâtiment ME', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', NULL, 'SALLE TP', 'PW LAB', '5.3.C', 1, 20),
('4926', 'ME A0 407', 'MEA0407', 'ME A0 407', 22, 'ECUBLENS', 46.66, 'ME', 'A', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Bâtiment ME', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('500', 'BS 150', 'BS150', 'BS 150', NULL, 'ECUBLENS', 72.27, 'BS', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Bâtiment BS', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', NULL, 'SALLE TP', 'PW LAB', '5.3.IT', 1, 20),
('501', 'BS 160', 'BS160', 'BS 160', 100, 'ECUBLENS', 151.86, 'BS', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Bâtiment BS', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('503', 'BS 170', 'BS170', 'BS 170', 100, 'ECUBLENS', 151.9, 'BS', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Bâtiment BS', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('5041', 'ME B3 31', 'MEB331', 'MEB31', 94, 'ECUBLENS', 94.29, 'ME', 'B', 'Domaine de la formation', 3, 3, 'DAF', 'E', 10026, 'Bâtiment ME', '0021', 'LOCAUX D''ENSEIGNEMENT AVEC MOBILIER FIXE', NULL, 'AUDITOIRE', 'AUDIENCE ROOM', '5.1.A', 1, 20),
('5386', 'MXC 136', 'MXC136', 'MXC 136', NULL, 'ECUBLENS', 37.17, 'MXC', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Bâtiment MXC', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', NULL, 'SALLE TP', 'PW LAB', '5.3.H', 1, 20),
('567', 'BS 260', 'BS260', 'BS 260', 72, 'ECUBLENS', 95.08, 'BS', 'Z', 'Domaine de la formation', 3, 2, 'DAF', 'E', 10026, 'Bâtiment BS', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('568', 'BS 270', 'BS270', 'BS 270', 72, 'ECUBLENS', 95.23, 'BS', 'Z', 'Domaine de la formation', 3, 2, 'DAF', 'E', 10026, 'Bâtiment BS', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('569', 'BS 280', 'BS280', 'BS 280', 72, 'ECUBLENS', 95.23, 'BS', 'Z', 'Domaine de la formation', 3, 2, 'DAF', 'E', 10026, 'Bâtiment BS', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('5814', 'MXF 014', 'MXF014', 'MXF 014', NULL, 'ECUBLENS', 150.95, 'MXF', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Bâtiment MXF', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', NULL, 'SALLE TP', 'PW LAB', '5.3.IT', 1, 20),
('5876', 'MXF 1', 'MXF1', 'MXF1', 87, 'ECUBLENS', 142.83, 'MXF', 'Z', 'Domaine de la formation', 3, 2, 'DAF', 'E', 10026, 'Bâtiment MXF', '0021', 'LOCAUX D''ENSEIGNEMENT AVEC MOBILIER FIXE', NULL, 'AUDITOIRE', 'AUDIENCE ROOM', '5.1.A', 1, 20),
('5922', 'MXG 110', 'MXG110', 'MXG 110', 50, 'ECUBLENS', 68.85, 'MXG', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Bâtiment MXG', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('6519', 'PH C1 425', 'PHC1425', 'PH C1 425', NULL, 'ECUBLENS', 100.35, 'PH', 'C', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Bâtiment PH', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', NULL, 'SALLE TP', 'PW LAB', '5.3.B', 1, 20),
('6847', 'PH H3 31', 'PHH331', 'PH31', 29, 'ECUBLENS', 47.39, 'PH', 'H', 'Domaine de la formation', 3, 3, 'DAF', 'E', 10026, 'Bâtiment PH', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('6849', 'PH H3 33', 'PHH333', 'PH33', 24, 'ECUBLENS', 47.32, 'PH', 'H', 'Domaine de la formation', 3, 3, 'DAF', 'E', 10026, 'Bâtiment PH', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('7047', 'PO 01', 'PO01', 'POLYDôME', 180, 'ECUBLENS', 610.64, 'PO', 'Z', 'Médiacom', 3, NULL, 'MEDIACOM', 'E', 10007, 'Polydôme', '0090', 'LOCAUX DE REUNION', NULL, 'REUNION', 'MEETING', '5.6.A', 1, 20),
('7226', 'PPH 275', 'PPH275', 'PPH 275', 20, 'ECUBLENS', 61.75, 'PPH', 'Z', 'Domaine de la formation', 3, 2, 'DAF', 'E', 10026, 'Bâtiment PPH', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('793', 'CE 1 1', 'CE11', 'CE1', 214, 'ECUBLENS', 233.57, 'CE', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Est', '0021', 'LOCAUX D''ENSEIGNEMENT AVEC MOBILIER FIXE', NULL, 'AUDITOIRE', 'AUDIENCE ROOM', '5.1.A', 1, 20),
('8393', 'BM 5202', 'BM5202', 'BM 5202', 80, 'ECUBLENS', 109.32, 'BM', 'Z', 'Domaine de la formation', 3, 5, 'DAF', 'E', 10026, 'Bâtiment BM', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('875', 'CE 1 100', 'CE1100', 'CE100', 64, 'ECUBLENS', 104.65, 'CE', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Est', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('876', 'CE 1 101', 'CE1101', 'CE101', 64, 'ECUBLENS', 99.4, 'CE', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Est', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('877', 'CE 1 103', 'CE1103', 'CE103', 64, 'ECUBLENS', 101, 'CE', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Est', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('878', 'CE 1 104', 'CE1104', 'CE104', 72, 'ECUBLENS', 99.82, 'CE', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Est', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('880', 'CE 1 105', 'CE1105', 'CE105', 72, 'ECUBLENS', 99.82, 'CE', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Est', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('882', 'CE 1 2', 'CE12', 'CE2', 178, 'ECUBLENS', 200.54, 'CE', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Est', '0021', 'LOCAUX D''ENSEIGNEMENT AVEC MOBILIER FIXE', NULL, 'AUDITOIRE', 'AUDIENCE ROOM', '5.1.A', 1, 20),
('9001', 'CE 1 102', 'CE1102', 'CE102', 24, 'ECUBLENS', 45.66, 'CE', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Est', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('9002', 'CE 1 102A', 'CE1102A', 'CE102A', 24, 'ECUBLENS', 46.43, 'CE', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Est', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('9054', 'SG 0211', 'SG0211', 'SG 0211', 112, 'ECUBLENS', 139.05, 'SG', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Bâtiment SG', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('9055', 'SG 0213', 'SG0213', 'SG 0213', 80, 'ECUBLENS', 110.13, 'SG', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Bâtiment SG', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('9111', 'SG 1138', 'SG1138', 'SG1', 348, 'ECUBLENS', 482.8, 'SG', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Bâtiment SG', '0021', 'LOCAUX D''ENSEIGNEMENT AVEC MOBILIER FIXE', NULL, 'AUDITOIRE', 'AUDIENCE ROOM', '5.1.A', 1, 20),
('9208', 'AAC 0 08', 'AAC008', 'AAC 0 08', 60, 'ECUBLENS', 79.92, 'AAC', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Bâtiment AAC', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('9209', 'AAC 0 14', 'AAC014', 'AAC 0 14', 60, 'ECUBLENS', 79.92, 'AAC', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Bâtiment AAC', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('9210', 'AAC 0 20', 'AAC020', 'AAC 0 20', 60, 'ECUBLENS', 79.92, 'AAC', 'Z', 'Domaine de la formation', 3, NULL, 'DAF', 'E', 10026, 'Bâtiment AAC', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('926', 'CE 1 3', 'CE13', 'CE3', 214, 'ECUBLENS', 233.57, 'CE', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Est', '0021', 'LOCAUX D''ENSEIGNEMENT AVEC MOBILIER FIXE', NULL, 'AUDITOIRE', 'AUDIENCE ROOM', '5.1.A', 1, 20),
('9275', 'AAC 1 06', 'AAC106', 'AAC 1 06', 24, 'ECUBLENS', 53.04, 'AAC', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Bâtiment AAC', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('9276', 'AAC 1 08', 'AAC108', 'AAC 1 08', 60, 'ECUBLENS', 80.04, 'AAC', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Bâtiment AAC', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('9277', 'AAC 1 14', 'AAC114', 'AAC 1 14', 60, 'ECUBLENS', 80.04, 'AAC', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Bâtiment AAC', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('9278', 'AAC 1 20', 'AAC120', 'AAC 1 20', 60, 'ECUBLENS', 80.04, 'AAC', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Bâtiment AAC', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('9280', 'AAC 1 32', 'AAC132', 'AAC 1 32', 60, 'ECUBLENS', 80.5, 'AAC', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Bâtiment AAC', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('9281', 'AAC 1 37', 'AAC137', 'AAC 1 37', 84, 'ECUBLENS', 115.96, 'AAC', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Bâtiment AAC', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('9312', 'AAC 2 01', 'AAC201', 'AAC 2 01', NULL, 'ECUBLENS', 505.44, 'AAC', 'Z', 'Domaine de la formation', 3, 2, 'DAF', 'E', 10026, 'Bâtiment AAC', '0021', 'LOCAUX D''ENS.ET D''EX.SPEC.SANS MOB.FIXE', NULL, 'SALLE TP', 'PW LAB', '5.3.K', 1, 20),
('9313', 'AAC 2 31', 'AAC231', 'AAC 2 31', 144, 'ECUBLENS', 233.28, 'AAC', 'Z', 'Domaine de la formation', 3, 2, 'DAF', 'E', 10026, 'Bâtiment AAC', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 1),
('9394', 'CE 1 106', 'CE1106', 'CE106', 99, 'ECUBLENS', 152.51, 'CE', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Est', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('9546', 'BCH 3118', 'BCH3118', 'Bch 3118', 30, 'ECUBLENS', 74.6, 'BCH', 'Z', 'Domaine de la formation', 3, 3, 'DAF', 'E', 10026, 'Batochime UNIL', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('958', 'CE 1 4', 'CE14', 'CE4', 233, 'ECUBLENS', 246.77, 'CE', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Est', '0021', 'LOCAUX D''ENSEIGNEMENT AVEC MOBILIER FIXE', NULL, 'AUDITOIRE', 'AUDIENCE ROOM', '5.1.A', 1, 20),
('959', 'CE 1 5', 'CE15', 'CE5', 98, 'ECUBLENS', 152.22, 'CE', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Est', '0021', 'LOCAUX D''ENSEIGNEMENT AVEC MOBILIER FIXE', NULL, 'AUDITOIRE', 'AUDIENCE ROOM', '5.1.A', 1, 20),
('9595', 'BCH 4119', 'BCH4119', 'Bch 4119', 18, 'ECUBLENS', 49.15, 'BCH', 'Z', 'Laboratoire de chimie et biochimie computationnelles', 3, 4, 'LCBC', 'E', 2, 'Batochime UNIL', '0669', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('960', 'CE 1 6', 'CE16', 'CE6', 308, 'ECUBLENS', 425.16, 'CE', 'Z', 'Domaine de la formation', 3, 1, 'DAF', 'E', 10026, 'Centre Est', '0021', 'LOCAUX D''ENSEIGNEMENT AVEC MOBILIER FIXE', NULL, 'AUDITOIRE', 'AUDIENCE ROOM', '5.1.A', 1, 20),
('9620', 'BCH 4310', 'BCH4310', 'Bch 4310', 18, 'ECUBLENS', 44.73, 'BCH', 'Z', 'Domaine de la formation', 3, 4, 'DAF', 'E', 10026, 'Batochime UNIL', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20),
('9666', 'BCH 5310', 'BCH5310', 'Bch 5310', 23, 'ECUBLENS', 56.31, 'BCH', 'Z', 'Domaine de la formation', 3, 5, 'DAF', 'E', 10026, 'Batochime UNIL', '0021', 'LOCAUX D''ENS.ET D''EX.GEN.SANS MOB.FIXE', NULL, 'SALLE DE COURS', 'EDUCATION', '5.2.A', 1, 20);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `fr-roomslist`
--
ALTER TABLE `fr-roomslist`
 ADD PRIMARY KEY (`uid`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
