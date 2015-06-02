<?php
ini_set('display_errors', 'On');
error_reporting(E_ALL | E_STRICT);

$posterName = "template128020001.png";
$croppedPosterName = "cropped_{$posterName}";
$cwd = getcwd();
$systemCommand = "/usr/local/Cellar/imagemagick/6.8.9-7/bin/convert {$cwd}/{$posterName} -gravity NorthEast -crop 6.2%\! {$cwd}/decodedIds/{$croppedPosterName}";

echo '<pre>';
echo "Outcome of running system({$systemCommand})";

$last_line = system($systemCommand, $retval);
// Printing additional info
echo '
</pre>
<hr />Last line of the output: ' . $last_line . '
<hr />Return value: ' . $retval . '<hr/>';

$url = "https://api.qrserver.com/v1/read-qr-code/";

$file_name_with_full_path="{$cwd}/decodedIds/{$croppedPosterName}";

$post = array('extra_info' => '123456','file'=>'@'.$file_name_with_full_path);

$ch = curl_init();

curl_setopt($ch, CURLOPT_URL,"http://api.qrserver.com/v1/read-qr-code/");

curl_setopt($ch, CURLOPT_POST,1);

curl_setopt($ch, CURLOPT_POSTFIELDS, $post);

curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);

$result=curl_exec ($ch);

curl_close ($ch);

$json = json_decode($result, true);

$link = $json[0]["symbol"][0]["data"];

$query = parse_url($link)["query"];

parse_str($query);

print_r($markFavorite);

$systemCommand = "cp {$cwd}/{$posterName} {$cwd}/decodedIds/{$markFavorite}.png";

echo '<pre>';
echo "Outcome of running system({$systemCommand})";

$last_line = system($systemCommand, $retval);
// Printing additional info
echo '
</pre>
<hr />Last line of the output: ' . $last_line . '
<hr />Return value: ' . $retval . '<hr/>';

$systemCommand = "rm {$cwd}/decodedIds/{$croppedPosterName}";

echo '<pre>';
echo "Outcome of running system({$systemCommand})";

$last_line = system($systemCommand, $retval);
// Printing additional info
echo '
</pre>
<hr />Last line of the output: ' . $last_line . '
<hr />Return value: ' . $retval . '<hr/>';
?>