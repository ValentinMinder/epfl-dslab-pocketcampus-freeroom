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

$headers = "";
foreach (apache_request_headers() as $header => $value) {
	if($header == "Host") {
		$headers .= "$header: dslabpc33.epfl.ch:9090\r\n";
	} else if($header == "Connection") {
		$headers .= "$header: Close\r\n";
	} else {
		$headers .= "$header: $value\r\n";
	}
}


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
 
$result = file_get_contents("http://dslabpc33.epfl.ch:9090{$_SERVER["QUERY_STRING"]}", false, $context);

foreach($http_response_header as $h) {
	if(stripos($h, "Location") !== false) {
		header(str_replace("http://dslabpc33.epfl.ch:9090/", "http://128.178.77.233/a/thrift-js/?/", $h));
	} else {
		header($h);
	}
}

echo $result;

?>