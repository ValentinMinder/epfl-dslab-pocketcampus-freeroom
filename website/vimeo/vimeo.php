<?php

function before($s, $d) {
	return reset(explode($d, $s, 2));
}
function after($s, $d) {
	return end(explode($d, $s, 2));
}
function getpage($url, $ref) {
	global $useragent;
	$ch = curl_init();
	curl_setopt($ch, CURLOPT_URL, $url);
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
	curl_setopt($ch, CURLOPT_REFERER, $ref);
	curl_setopt($ch, CURLOPT_USERAGENT, $useragent);
	return curl_exec($ch);
}
function getredirect($url) {
	global $useragent;
	$ch = curl_init();
	curl_setopt($ch, CURLOPT_URL, $url);
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
	curl_setopt($ch, CURLOPT_HEADER, true);
	curl_setopt($ch, CURLOPT_FOLLOWLOCATION, false);
	curl_setopt($ch, CURLOPT_USERAGENT, $useragent);
	return curl_exec($ch);
}

if(empty($_GET["vid"]))
	exit;
$vid = $_GET["vid"];
$referer = "https://myedu.epfl.ch/courses";
$useragent = "User-Agent=Mozilla/5.0 (X11; Linux x86_64; rv:16.0) Gecko/20100101 Firefox/16.0";


$vidpage = getpage("https://player.vimeo.com/video/$vid", $referer);

$vidpage = after($vidpage, "<script>");
$vidpage = after($vidpage, "config:");
$vidpage = before($vidpage, ",assets");

$vidpage = json_decode($vidpage);

print_r($vidpage);
exit;

$sig = $vidpage->request->signature;
$stamp = $vidpage->request->timestamp;

$redir = getredirect("https://player.vimeo.com/play_redirect?clip_id=$vid&sig=$sig&time=$stamp&quality=sd&codecs=H264,VP8,VP6&type=moogaloop&embed_location=$referer&seek=0");

$redir = after($redir, "Location: ");
$redir = before($redir, "\n");

header("Location: ".$redir);

?>
