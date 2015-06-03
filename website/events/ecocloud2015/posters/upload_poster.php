<?php

$size_limit = 20;


if($_SERVER["REQUEST_METHOD"] != "POST") {

	echo "<h2>EcoCloud 2015 :: Upload Poster</h2>";
	echo "<p>Use the form below to choose and upload your poster (PDF only, {$size_limit}MB max)</p>";
	echo '<form action="?" method="post" id="upload_form" enctype="multipart/form-data">';
	echo '<input type="file" name="file" id="file_input">';
	echo '<input type="submit" id="submit_button" value="Upload" onclick="show_please_wait();">';
	echo '</form>';
	echo '<div id="status_div"></div>';
	echo '<script>function show_please_wait() { document.getElementById("status_div").innerHTML = "Please wait while we upload and analyse your poster" }</script>';
	echo '<script>document.getElementById("file_input").onchange = function() { show_please_wait(); document.getElementById("upload_form").submit(); };</script>';
	echo '<script>document.getElementById("submit_button").style.display = "none";</script>';
	exit;
}


if(empty($_FILES["file"])) {
	die("No file");
}

if(!isset($_FILES["file"]["error"]) || !isset($_FILES["file"]["name"]) || !isset($_FILES["file"]["type"]) || !isset($_FILES["file"]["size"]) || !isset($_FILES["file"]["tmp_name"])) {
	die("WTF");
}

if($_FILES["file"]["error"]) {
	die("Error {$_FILES["file"]["error"]}");
}

if($_FILES["file"]["type"] != "application/pdf" || strtolower(end(explode(".", $_FILES["file"]["name"]))) != "pdf") {
	die("PDF please");
}

if($_FILES["file"]["size"] > $size_limit * 1024 * 1024) {
	die("Sorry, the file size exceeds {$size_limit}MB");
}

$new_file = basename($_FILES["file"]["tmp_name"]);
if(!move_uploaded_file($_FILES["file"]["tmp_name"], "tmp/$new_file.pdf")) {
	die("Move failed");
}

system("convert tmp/{$new_file}.pdf -gravity NorthEast -crop 6.2% tmp/{$new_file}_cropped.png", $status);
if($status) {
	die("Error while running convert");
}

$ch = curl_init();
curl_setopt($ch, CURLOPT_URL,"http://api.qrserver.com/v1/read-qr-code/");
curl_setopt($ch, CURLOPT_POST,1);
curl_setopt($ch, CURLOPT_POSTFIELDS, array("file" => "@tmp/{$new_file}_cropped.png"));
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
$result=curl_exec($ch);
curl_close ($ch);

$json = json_decode($result, true);
if(empty($json) || empty($json[0]) || empty($json[0]["symbol"]) || empty($json[0]["symbol"][0]) || empty($json[0]["symbol"][0]["data"])) {
	die("Failed to decode QR code, did you put your QR code in the proper placeholder?");
}

$parsed_url = parse_url($json[0]["symbol"][0]["data"]);
if(empty($parsed_url) || empty($parsed_url["scheme"]) || $parsed_url["scheme"] != "pocketcampus" || empty($parsed_url["query"])) {
	die("Unexpected encoded URL");
}

parse_str($parsed_url["query"], $parsed_query_string);
if(empty($parsed_query_string) || empty($parsed_query_string["markFavorite"])) {
	die("Unexpected encoded query string");
}

system("mv tmp/{$new_file}.pdf  tmp/{$new_file}_{$parsed_query_string["markFavorite"]}.pdf", $status);
if($status) {
	die("Rename failed");
}

$all_posters = explode("\n", trim(file_get_contents("EcoCloudPosters.txt")));
$posters_map = array();
foreach($all_posters as $p) {
	$p = explode("\t", $p, 2);
	$posters_map[$p[0]] = $p[1];
}

if(!isset($posters_map[$parsed_query_string["markFavorite"]])) {
	die("Failed to find your poster, did you register it?");
}

echo "<p>Upload succeeded, detected poster:</p>\n";
echo "<p><b>{$posters_map[$parsed_query_string["markFavorite"]]}</b></p>\n";
echo "<p>If it's not your poster, please notify the organizers</p>\n";

?>
