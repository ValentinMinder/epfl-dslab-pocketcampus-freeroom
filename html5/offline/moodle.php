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



/*if(!empty($_SERVER["PATH_INFO"]) && strpos($_SERVER["PATH_INFO"], "auth/tequila/teq_return.php") !== false) {
	die("AUTH SUCC");
}*/

$REPLACE_REDIRECTS = "HTTP/1.1 500 Upstream Server Redirected";
$STRIP_CACHECONTROL = 1;
include "proxy.php";
exit;

?>