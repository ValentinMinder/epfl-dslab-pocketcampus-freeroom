<?php



if(false){
	echo "<pre>";
	echo "This site is not available at the moment\n";
	echo "We are performing some upgrade\n";
	echo "We apologize for the inconvenience\n";
	echo "Please try again in a few minutes\n";
	echo "</pre>";
	exit;
}







header("Cache-Control: no-cache, must-revalidate");
header("Expires: Mon, 26 Jul 1997 05:00:00 GMT");

$conn=connect_to_db("pocketcampus");
mysql_set_charset("utf8",$conn);
//sql_query("set time_zone = '+01:00';", $conn);
sql_query("set time_zone = 'Europe/Zurich';", $conn);
//die(mysql_client_encoding($conn));


session_start();


$config = parse_ini_file("/var/www/vhosts/pocketcampus/private/pocketcampus-access.config");
$config or die("CANNOT FIND OR OPEN CONFIG FILE");

$ADMIN_KEY = "{$config["EVENT_ADMIN_ACCESS"]}";


if(!empty($_GET[$ADMIN_KEY])) {
        $_SESSION[$ADMIN_KEY] = $_GET[$ADMIN_KEY];
        header("Location: ?");
        exit;
}

if(empty($_SESSION[$ADMIN_KEY])) {
        header('HTTP/1.0 403 Forbidden');
        exit;
}




?>
