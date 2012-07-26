<?php

/*
echo "<pre>";

echo "\$_SERVER = ";
print_r($_SERVER);

//echo "\$_ENV = ";
//print_r($_ENV);

//echo "php://input = ";
//echo file_get_contents('php://input');

exit;
*/




/*
$postdata = http_build_query(
    array(
        'var1' => 'some content',
        'var2' => 'doh'
    )
);
*/
$REPLACE_REDIRECTS = "HTTP/1.1 200 OK";
include "proxy.php";
exit;


$log = "";

$headers = "";
foreach (apache_request_headers() as $header => $value) {
	$log .= "$header: $value\n";
	if($header == "Host") {
		$headers .= "$header: tequila.epfl.ch\r\n";
	} else if($header == "Connection") {
		$headers .= "$header: Close\r\n";
	} else {
		$headers .= "$header: $value\r\n";
	}
}
$log .= "\n";


$opts = array('http' =>
    array(
        'method'             => $_SERVER["REQUEST_METHOD"],
        'header'             => $headers,
        'ignore_errors'      => true,
        'follow_location'    => false,
        'protocol_version'   => 1.1,
        'content'            => file_get_contents('php://input')
    )
);
 
$context  = stream_context_create($opts);

//$trans_uri = $_SERVER["QUERY_STRING"];
$trans_uri = (empty($_SERVER["PATH_INFO"]) ? "/" : ($_SERVER["PATH_INFO"] . "?" . $_SERVER["QUERY_STRING"]));
 
$result = file_get_contents("https://tequila.epfl.ch$trans_uri", false, $context);

foreach($http_response_header as $h) {
	$log .= "$h\n";
	if(stripos($h, "Location") !== false) {
		$h = str_replace("https://tequila.epfl.ch", "http://128.178.77.233{$_SERVER["SCRIPT_NAME"]}", $h);
		$h = str_replace("http://moodle.epfl.ch", "http://128.178.77.233/a/thrift-js/moodle.php", $h);
	}
	header($h);
}
$log .= "\n";

echo $result;

file_put_contents("log.log", file_get_contents("log.log") . $log);

?>