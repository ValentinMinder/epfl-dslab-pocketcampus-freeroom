<?php


$mapping = array (
	"pc-server.php" => array("protto" => "http://", "url" => "dslabpc36.epfl.ch:9090"),
	"tequila.php" => array("protto" => "https://", "url" => "tequila.epfl.ch"),
	"moodle.php" => array("protto" => "http://", "url" => "moodle.epfl.ch"),
);

$self_basename = basename($_SERVER["SCRIPT_NAME"]);

$log = "";

$headers = "";
foreach (apache_request_headers() as $header => $value) {
	$log .= "$header: $value\n";
	if($header == "Host") {
		$headers .= "$header: " . $mapping[$self_basename]["url"] . "\r\n";
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
 
$result = file_get_contents($mapping[$self_basename]["protto"] . $mapping[$self_basename]["url"] . $trans_uri, false, $context);

foreach($http_response_header as $h) {
	$log .= "$h\n";
	if(empty($REPLACE_REDIRECTS)) { // translate redirects
		if(stripos($h, "Location: ") === 0) {
			foreach($mapping as $k => $v) {
				$h = str_replace($mapping[$k]["protto"] . $mapping[$k]["url"], "http://128.178.77.233" . dirname($_SERVER["SCRIPT_NAME"]) . "/$k", $h);
			}
		}
	} else { // replace redirects
		if(stripos($h, "HTTP/1.1 302") === 0 || stripos($h, "HTTP/1.1 303") === 0) {
			$h = $REPLACE_REDIRECTS;
		} else if(stripos($h, "Location: ") === 0) {
			$result = $h;
			continue;
		}
	}
	if(!empty($STRIP_CACHECONTROL)) {
		if(stripos($h, "Expires: ") === 0 || stripos($h, "Cache-Control: ") === 0 || stripos($h, "Pragma: ") === 0) {
			continue;
		}
	}
	header($h);
}
$log .= "\n";

echo $result;

file_put_contents("log.log", file_get_contents("log.log") . $log);

?>