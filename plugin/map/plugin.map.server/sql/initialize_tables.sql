SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

INSERT INTO `maplayers` (`layerId`, `nameForQuery`, `nameForQueryAllFloors`, `name_EN`, `name_FR`) VALUES
(1, 'myprint{floor}', 'myprintall', 'Printers (myPrint)', 'Imprimantes (myPrint)'),
(2, 'chargeurs{floor}', 'chargeursall', 'Camipro chargers', 'Chargeurs Camipro'),
(3, 'bornescamipro{floor}', 'bornescamiproall', 'Camipro terminals', 'Bornes Camipro'),
(4, 'parkings_publics{floor}', 'parkings_publicsall', 'Public parking lots', 'Parkings publics'),
(5, 'restauration{floor}', 'restaurationall', 'Restaurants', 'Restauration'),
(6, 'bancomat{floor}', 'bancomatall', 'ATMs', 'Bancomats'),
(7, 'information{floor}', 'informationall', 'Information', 'Information'),
(8, 'douches{floor}', 'douchesall', 'Showers', 'Douches')