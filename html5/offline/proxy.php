<?php

require_once "common.php";

$self_basename = basename($_SERVER["SCRIPT_NAME"]);

$log = "===========================" . basename($_SERVER["SCRIPT_NAME"]) . "\n";

$headers = "";
foreach (apache_request_headers() as $header => $value) {
	$log .= "$header: $value\n";
	if($header == "Host") {
		$headers .= "$header: " . $PC_PROXY_CONFIG[$self_basename]["url"] . "\r\n";
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
        'follow_location'    => 0,
        'max_redirects'      => 1,
        'protocol_version'   => 1.1,
        'content'            => file_get_contents('php://input')
    )
);
 
$context  = stream_context_create($opts);

//$trans_uri = $_SERVER["QUERY_STRING"];
$trans_uri = (empty($_SERVER["PATH_INFO"]) ? "/" : ($_SERVER["PATH_INFO"] . "?" . $_SERVER["QUERY_STRING"]));
 
$result = file_get_contents($PC_PROXY_CONFIG[$self_basename]["protto"] . $PC_PROXY_CONFIG[$self_basename]["url"] . $trans_uri, false, $context);

foreach($http_response_header as $h) {
	$log .= "$h\n";
	if(empty($REPLACE_REDIRECTS)) { // translate redirects
		if(stripos($h, "Location: ") === 0) {
			foreach($PC_PROXY_CONFIG as $k => $v) {
				$h = str_replace($PC_PROXY_CONFIG[$k]["protto"] . $PC_PROXY_CONFIG[$k]["url"], "{$HTML5_APP_URL_PROTO}{$HTML5_APP_URL_HOST}" . dirname($_SERVER["SCRIPT_NAME"]) . "/$k", $h);
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

file_put_contents("log.log", $log, FILE_APPEND);

?>
