SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

INSERT INTO `maplayers` (`nameForQuery`, `nameForQueryAllFloors`, `name_EN`, `name_FR`) VALUES
('myprint-{floor}', 'myprintall', 'MyPrint printers', 'Imprimantes MyPrint'),
('chargeurs-{floor}', 'chargeursall', 'Camipro chargers', 'Chargeurs Camipro'),
('camipro-{floor}', 'camiproall', 'Camipro terminals', 'Bornes Camipro');

