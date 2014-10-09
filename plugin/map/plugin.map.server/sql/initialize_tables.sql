SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

INSERT INTO `maplayers` (`nameForQuery`, `nameForQueryAllFloors`, `name_EN`, `name_FR`) VALUES
(1000, 'myprint-{floor}', 'myprintall', 'MyPrint printers', 'Imprimantes MyPrint'),
(2000, 'chargeurs-{floor}', 'chargeursall', 'Camipro chargers', 'Chargeurs Camipro'),
(3000, 'camipro-{floor}', 'camiproall', 'Camipro terminals', 'Bornes Camipro');

