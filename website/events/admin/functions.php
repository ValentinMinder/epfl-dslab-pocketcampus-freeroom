<?php


function download_file($url, $path, $cookie) {
	$fp = fopen ($path, "w+");
	$ch = curl_init($url);
	curl_setopt($ch, CURLOPT_TIMEOUT, 30);
	curl_setopt($ch, CURLOPT_FILE, $fp);
	curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);
	curl_setopt($ch, CURLOPT_COOKIEFILE, $cookie);
	curl_setopt($ch, CURLOPT_COOKIEJAR, $cookie);
	curl_exec($ch);
	curl_close($ch);
	fclose($fp);
}

function make_li($str) {
	return "&bull; $str<br>";
}

function csv_to_array($filename)
{
	if(!file_exists($filename) || !is_readable($filename))
		return FALSE;

	$header = NULL;
	$data = array();
	if (($handle = fopen($filename, 'r')) !== FALSE)
	{
		while (($row = fgetcsv($handle)) !== FALSE)
		{
			if(!$header) {
				$header = $row;
			} else {
				$record = @array_combine($header, $row);
				if($record) $data[] = $record;
			}
		}
		fclose($handle);
	}
	return $data;
}

function htmlify($str) {
	return str_replace("\n", "<br>", $str);
}









function export_csv_stats_pageviews_l3($eventItemId, $conn) {
	$eventItemId = sql_real_escape_string($eventItemId);
$query = <<<EOS

SELECT  ei1.eventId AS id1, ei1.eventTitle AS title1, count1.accessCount AS count1,
ep2.poolId AS id2, ep2.poolTitle AS title2, count2.accessCount AS count2, 
ei3.eventId AS id3, ei3.eventTitle AS title3, count3.accessCount AS count3

FROM  eventitems ei1
LEFT JOIN  eventpools ep2 ON ep2.parentEvent = ei1.eventId
LEFT JOIN  eventitems ei3 ON ei3.parentPool = ep2.poolId

LEFT JOIN (
SELECT  ei1.eventId AS eventId, sum(pv1.viewCount) AS accessCount
FROM  eventitems ei1
LEFT JOIN  eventpageviews pv1 ON pv1.nodeId = ei1.eventId AND pv1.pageType = 'eventitem' AND pv1.userTicket IN (SELECT userToken FROM eventperms WHERE eventItemId = ei1.eventId)
group by  ei1.eventId
) count1 ON ei1.eventId = count1.eventId

LEFT JOIN (
SELECT    ep2.poolId AS poolId, sum(pv2.viewCount) AS accessCount
FROM   eventpools ep2 
LEFT JOIN  eventpageviews pv2 ON pv2.nodeId = ep2.poolId AND pv2.pageType = 'eventpool'  AND pv2.userTicket IN (SELECT userToken FROM eventperms WHERE eventItemId = ep2.parentEvent)
group by ep2.poolId
) count2 ON ep2.poolId = count2.poolId

LEFT JOIN (
SELECT  ei1.eventId AS eventId, sum(pv1.viewCount) AS accessCount
FROM  eventitems ei1
LEFT JOIN  eventpageviews pv1 ON pv1.nodeId = ei1.eventId AND pv1.pageType = 'eventitem' AND pv1.userTicket IN (SELECT userToken FROM eventperms INNER JOIN eventpools ON parentEvent = eventItemId WHERE poolId = ei1.parentPool)
group by  ei1.eventId
) count3 ON ei3.eventId = count3.eventId

WHERE ei1.eventId like '$eventItemId'

EOS;
	return query_to_array($query, $conn);
}

function export_csv_stats_pageviews_l2($eventItemId, $conn) {
	$eventItemId = sql_real_escape_string($eventItemId);
$query = <<<EOS
SELECT  ei1.eventId AS id1, ei1.eventTitle AS title1, count1.accessCount AS count1,
ep2.poolId AS id2, ep2.poolTitle AS title2, count2.accessCount AS count2

FROM  eventitems ei1
LEFT JOIN  eventpools ep2 ON ep2.parentEvent = ei1.eventId

LEFT JOIN (
SELECT  ei1.eventId AS eventId, sum(pv1.viewCount) AS accessCount
FROM  eventitems ei1
LEFT JOIN  eventpageviews pv1 ON pv1.nodeId = ei1.eventId AND pv1.pageType = 'eventitem' AND pv1.userTicket IN (SELECT userToken FROM eventperms WHERE eventItemId = ei1.eventId)
group by  ei1.eventId
) count1 ON ei1.eventId = count1.eventId

LEFT JOIN (
SELECT    ep2.poolId AS poolId, sum(pv2.viewCount) AS accessCount
FROM   eventpools ep2
LEFT JOIN  eventpageviews pv2 ON pv2.nodeId = ep2.poolId AND pv2.pageType = 'eventpool'  AND pv2.userTicket IN (SELECT userToken FROM eventperms WHERE eventItemId = ep2.parentEvent)
group by ep2.poolId
) count2 ON ep2.poolId = count2.poolId

WHERE ei1.eventId like '$eventItemId'

EOS;
	return query_to_array($query, $conn);
}

function export_csv_stats_pageviews_l1($eventItemId, $conn) {
	$eventItemId = sql_real_escape_string($eventItemId);
$query = <<<EOS
SELECT  ei1.eventId AS id1, ei1.eventTitle AS title1, count1.accessCount AS count1

FROM  eventitems ei1

LEFT JOIN (
SELECT  ei1.eventId AS eventId, sum(pv1.viewCount) AS accessCount
FROM  eventitems ei1
LEFT JOIN  eventpageviews pv1 ON pv1.nodeId = ei1.eventId AND pv1.pageType = 'eventitem' AND pv1.userTicket IN (SELECT userToken FROM eventperms WHERE eventItemId = ei1.eventId)
group by  ei1.eventId
) count1 ON ei1.eventId = count1.eventId

WHERE ei1.eventId like '$eventItemId'
EOS;
	return query_to_array($query, $conn);
}

function export_csv_stats_users($eventItemId, $conn) {
	$eventItemId = sql_real_escape_string($eventItemId);
$query = <<<EOS
SELECT ei100.eventId, ei100.eventTitle, engagedUsers.engagedUsers, allUsers.allUsers
FROM eventitems ei100
INNER JOIN (

SELECT hp.eventItemId, COUNT(*) AS engagedUsers
FROM eventperms hp
WHERE hp.userToken IN (




SELECT DISTINCT pv1.userTicket
FROM  eventitems ei1
INNER JOIN  eventpageviews pv1 ON pv1.nodeId = ei1.eventId AND pv1.pageType = 'eventitem' AND pv1.userTicket IN (SELECT userToken FROM eventperms WHERE eventItemId = ei1.eventId)
where ei1.eventId = hp.eventItemId


UNION

SELECT DISTINCT pv2.userTicket
FROM   eventpools ep2 
INNER JOIN  eventpageviews pv2 ON pv2.nodeId = ep2.poolId AND pv2.pageType = 'eventpool'  AND pv2.userTicket IN (SELECT userToken FROM eventperms WHERE eventItemId = ep2.parentEvent)
WHERE ep2.poolId IN (
SELECT  ep10.poolId 
FROM  eventpools ep10 
WHERE ep10.parentEvent = hp.eventItemId
)


UNION



SELECT DISTINCT pv1.userTicket
FROM  eventitems ei1
INNER JOIN  eventpageviews pv1 ON pv1.nodeId = ei1.eventId AND pv1.pageType = 'eventitem' AND pv1.userTicket IN (SELECT userToken FROM eventperms INNER JOIN eventpools ON parentEvent = eventItemId WHERE poolId = ei1.parentPool)
WHERE ei1.eventId IN (
SELECT  ei20.eventId 
FROM  eventpools ep10 
INNER JOIN  eventitems ei20 ON ei20.parentPool = ep10.poolId
WHERE ep10.parentEvent = hp.eventItemId
)



) GROUP BY hp.eventItemId 

) engagedUsers ON ei100.eventId = engagedUsers.eventItemId
INNER JOIN (

SELECT hp.eventItemId, COUNT(*) AS allUsers
FROM eventperms hp
GROUP BY hp.eventItemId 

) allUsers ON ei100.eventId = allUsers.eventItemId

WHERE ei100.eventId like '$eventItemId'

EOS;
	return query_to_array($query, $conn);
}









function query_to_array($query, $conn) {
	$csv = array();
	$resource = sql_query($query, $conn);
	if($resource) {
		$row = array();
		for($i=0;$i<sql_num_fields($resource);$i++) {
			$row[] = sql_field_name($resource, $i);
		}
		$csv[] = $row;
	}
	while($item = sql_fetch_array($resource)) {
		$row = array();
		for($i=0;$i<sql_num_fields($resource);$i++) {
			$row[] = $item[$i];
		}
		$csv[] = $row;
	}
	return $csv;
}









function export_event_items_posters($parent, $conn) {
	$COLS = 4;
	$resource = sql_query("SELECT * FROM eventitems WHERE parentPool='$parent' ORDER BY secondLine,eventTitle", $conn);
	$ret = "<table style=\"margin:auto;\">";
	$table = array();
	$row = array();
	while($item1 = sql_fetch_array($resource)) {
		if(count($row) == $COLS) {
			$table[] = $row;
			$row = array();
		}
		$row[] = $item1;
	}
	if(count($row) > 0) {
		$table[] = $row;
		$row = array();
	}
	foreach($table as $row) {
		$ret .= "<tr>";
		foreach($row as $i) {
			$ret .= "<td style=\"text-align:center;width:300px;\">";
			$ret .= "<img style=\"\" src=\"http://pocketcampus.epfl.ch/events/qr_code.php?s=190x190&id=" . rawurlencode($parent) . "&mf=" . rawurlencode($i["eventId"]) . "\">";
			$ret .= "<div style=\"width:300px;height:40px;overflow:hidden;margin:auto;text-align:center\">" . htmlentities(($i["eventTitle"])) . "<br>";
			$ret .= "<small><b>" . htmlentities(($i["secondLine"])) . "&nbsp;</b></small></div>";
			$ret .= "</td>";
		}
		$ret .= "</tr>";
	}
//	while($item1 = sql_fetch_array($resource)) {
//		$ret .= "<tr>";
//		$ret .= "<td style=\"text-align:center;width:300px;\">";
//		$ret .= "<img style=\"margin-bottom:-20px;\" src=\"http://pocketcampus.epfl.ch/events/qr_code.php?s=190x190&id=" . rawurlencode($parent) . "&mf=" . rawurlencode($item1["eventId"]) . "\">";
//		$ret .= "<div style=\"width:300px;height:40px;overflow:hidden;margin:auto;text-align:center\"><b>" . htmlentities(($item1["eventTitle"])) . "</b></div>";
//		$ret .= "<div style=\"margin:auto;text-align:center\"><i>" . htmlentities(($item1["secondLine"])) . "&nbsp;</i></div>";
//		$ret .= "</td>";
//		$ret .= "<td>&nbsp;&nbsp;&nbsp;</td>";
//		$ret .= "<td style=\"text-align:center;width:300px;\">";
//		if($item2 = sql_fetch_array($resource)) {
//			$ret .= "<img style=\"margin-bottom:-20px;\" src=\"http://pocketcampus.epfl.ch/events/qr_code.php?s=190x190&id=" . rawurlencode($parent) . "&mf=" . rawurlencode($item2["eventId"]) . "\">";
//			$ret .= "<div style=\"width:300px;height:40px;overflow:hidden;margin:auto;text-align:center\"><b>" . htmlentities(($item2["eventTitle"])) . "</b></div>";
//			$ret .= "<div style=\"margin:auto;text-align:center\"><i>" . htmlentities(($item2["secondLine"])) . "&nbsp;</i></div>";
//		}
//		$ret .= "</td>";
//		$ret .= "</tr>";
//	}
	$ret .= "</table>";
	return $ret;
}







function get_required_fields_for_schedule(){
	return array("StartDate", "EndDate", "Title", "Abstract", "Venue", "VenueURL", "Speaker", "Biography", "SpeakerPicture", "HomepageLink", "PC_ID", "PC_CATEG", "SPEAKER_PC_ID", "Speakers", "SPEAKER_PC_IDS");
}

function update_eventitem_schedule($record, $newid, $conn) {
        $succ = TRUE;
        if($record["PC_CATEG"]) $succ = $succ && sql_query("UPDATE eventitems SET eventCateg = '" . sql_real_escape_string($record["PC_CATEG"]) . "'  WHERE eventId = '$newid'", $conn);
        if($record["StartDate"]) $succ = $succ && sql_query("UPDATE eventitems SET startDate = '" . sql_real_escape_string($record["StartDate"]) . "'  WHERE eventId = '$newid'", $conn);
        if($record["EndDate"]) $succ = $succ && sql_query("UPDATE eventitems SET endDate = '" . sql_real_escape_string($record["EndDate"]) . "'  WHERE eventId = '$newid'", $conn);
        if($record["Title"]) $succ = $succ && sql_query("UPDATE eventitems SET eventTitle = '" . sql_real_escape_string($record["Title"]) . "'  WHERE eventId = '$newid'", $conn);
        if($record["Venue"]) $succ = $succ && sql_query("UPDATE eventitems SET eventPlace = '" . sql_real_escape_string($record["Venue"]) . "'  WHERE eventId = '$newid'", $conn);
        if($record["VenueURL"]) $succ = $succ && sql_query("UPDATE eventitems SET locationHref = '" . sql_real_escape_string($record["VenueURL"]) . "'  WHERE eventId = '$newid'", $conn);

	// NEW
        if($record["Speaker"]) $succ = $succ && sql_query("UPDATE eventitems SET secondLine = '" . sql_real_escape_string($record["Speaker"]) . "'  WHERE eventId = '$newid'", $conn);
	//if($record["Speaker"]) $succ = $succ && sql_query("UPDATE eventitems SET eventSpeaker = '" . sql_real_escape_string($record["Speaker"]) . "'  WHERE eventId = '$newid'", $conn);

        if($record["SpeakerPicture"]) $succ = $succ && sql_query("UPDATE eventitems SET eventThumbnail = '" . sql_real_escape_string($record["SpeakerPicture"]) . "'  WHERE eventId = '$newid'", $conn);
        $eventDetails = "";
	if($record["Speakers"]) {
		$speakers = array_map("trim", explode(",", $record["Speakers"]));
		if($record["SPEAKER_PC_IDS"]) {
			$speaker_map = array_map("rawurlencode", array_map("trim", explode(",", $record["SPEAKER_PC_IDS"])));
			if(count($speakers) == count($speaker_map)) {
				for($i = 0; $i < count($speaker_map); $i++) if($speaker_map[$i]) $speakers[$i] = "<a href=\"pocketcampus://events.plugin.pocketcampus.org/showEventItem?eventItemId={$speaker_map[$i]}\">{$speakers[$i]}</a>";
			}
		}
		$speakers = array_map("make_li", $speakers);
		if(count($speakers)) $eventDetails .= "<h2>Speakers</h2><p>" . implode("", $speakers) . "</p>";
	}
        if($record["Abstract"]) $eventDetails .= "<h2>Abstract</h2><p>" . htmlify($record["Abstract"]) . "</p>";
        if($record["Biography"]) $eventDetails .= "<h2>Biography</h2><p>" . htmlify($record["Biography"]) . "</p>";
        $speakerDetails = array();
        if($record["SPEAKER_PC_ID"]) $speakerDetails[] = "<a href=\"pocketcampus://events.plugin.pocketcampus.org/showEventItem?eventItemId=" . rawurlencode($record["SPEAKER_PC_ID"]) . "\">Contact details</a>";
        if($record["HomepageLink"]) $speakerDetails[] = "<a href=\"{$record["HomepageLink"]}\">Homepage</a>";
        if(count($speakerDetails)) $eventDetails .= "<h2>Speaker</h2><p>" . implode("<br>", $speakerDetails) . "</p>";
        if($eventDetails) $succ = $succ && sql_query("UPDATE eventitems SET eventDetails = '" . sql_real_escape_string($eventDetails) . "'  WHERE eventId = '$newid'", $conn);
        return $succ;
}




function get_required_fields_for_posters(){
	return array("Affiliation", "PosterTitle", "Author", "ThumbnailLink", "PreviewLink", "PdfLink", "PC_ID", "PC_CATEG", "AUTHOR_PC_ID", "LAB_PC_ID");
}

function update_eventitem_posters($record, $newid, $conn) {
	$succ = TRUE;
	if($record["PC_CATEG"]) $succ = $succ && sql_query("UPDATE eventitems SET eventCateg = '" . sql_real_escape_string($record["PC_CATEG"]) . "'  WHERE eventId = '$newid'", $conn);
	
	if($record["PosterTitle"]) $succ = $succ && sql_query("UPDATE eventitems SET eventTitle = '" . sql_real_escape_string($record["PosterTitle"]) . "'  WHERE eventId = '$newid'", $conn);
	if($record["Affiliation"]) $succ = $succ && sql_query("UPDATE eventitems SET secondLine = '" . sql_real_escape_string($record["Affiliation"]) . "'  WHERE eventId = '$newid'", $conn);
	if($record["ThumbnailLink"]) $succ = $succ && sql_query("UPDATE eventitems SET eventThumbnail = '" . sql_real_escape_string($record["ThumbnailLink"]) . "'  WHERE eventId = '$newid'", $conn);
	if($record["PreviewLink"]) {
		$succ = $succ && sql_query("UPDATE eventitems SET eventPicture = '" . sql_real_escape_string($record["PreviewLink"]) . "'  WHERE eventId = '$newid'", $conn);
		$succ = $succ && sql_query("UPDATE eventitems SET hideThumbnail = '1'  WHERE eventId = '$newid'", $conn);
	}
	if($record["Author"] || $record["PdfLink"]) {
		$eventDetails = "";
		if($record["Author"]) {
			$authors = array_map("trim", explode(",", $record["Author"]));
			if($record["AUTHOR_PC_ID"]) {
				$author_map = array_map("rawurlencode", array_map("trim", explode(",", $record["AUTHOR_PC_ID"])));
				if(count($authors) == count($author_map)) {
					for($i = 0; $i < count($author_map); $i++) if($author_map[$i]) $authors[$i] = "<a href=\"pocketcampus://events.plugin.pocketcampus.org/showEventItem?eventItemId={$author_map[$i]}\">{$authors[$i]}</a>";
				}
			}
			if(count($authors) == 1) $eventDetails .= "<h2>Author</h2><p>{$authors[0]}</p>";
			else $eventDetails .= "<h2>Authors</h2><p>" . implode("<br>", $authors) . "</p>";
		}
		if($record["PdfLink"]) $eventDetails .= "<h2>PDF version</h2><p><a href=\"{$record["PdfLink"]}\">Link</a></p>";
		if($record["LAB_PC_ID"]) $eventDetails .= "<h2>Lab</h2><p><a href=\"pocketcampus://events.plugin.pocketcampus.org/showEventItem?eventItemId=" . rawurlencode($record["LAB_PC_ID"]) . "\">Link</a></p>";
		$succ = $succ && sql_query("UPDATE eventitems SET eventDetails = '" . sql_real_escape_string($eventDetails) . "'  WHERE eventId = '$newid'", $conn);
	}
	return $succ;
}














function get_required_fields_for_venue(){
	return array("VenueName", "MapPictureURL", "VenueThumbnail", "VenueAddress", "VenueEmail", "VenuePhoneNumber", "VenueNote", "PC_ID", "PC_CATEG");
}

function update_eventitem_venue($record, $newid, $conn) {
	$succ = TRUE;
	if($record["PC_CATEG"]) $succ = $succ && sql_query("UPDATE eventitems SET eventCateg = '" . sql_real_escape_string($record["PC_CATEG"]) . "'  WHERE eventId = '$newid'", $conn);
	
	if($record["VenueName"]) $succ = $succ && sql_query("UPDATE eventitems SET eventTitle = '" . sql_real_escape_string($record["VenueName"]) . "'  WHERE eventId = '$newid'", $conn);
	if($record["MapPictureURL"]) $succ = $succ && sql_query("UPDATE eventitems SET eventPicture = '" . sql_real_escape_string($record["MapPictureURL"]) . "'  WHERE eventId = '$newid'", $conn);
	if($record["VenueThumbnail"]) $succ = $succ && sql_query("UPDATE eventitems SET eventThumbnail = '" . sql_real_escape_string($record["VenueThumbnail"]) . "'  WHERE eventId = '$newid'", $conn);
	$eventDetails = "";
	if($record["VenueEmail"]) $eventDetails .= "<h2>Email address</h2><p><a href=\"mailto:{$record["VenueEmail"]}\">{$record["VenueEmail"]}</a></p>";
	if($record["VenuePhoneNumber"]) $eventDetails .= "<h2>Phone number</h2><p><a href=\"tel:{$record["VenuePhoneNumber"]}\">{$record["VenuePhoneNumber"]}</a></p>";

	// NEW
	//if($record["VenueAddress"]) $eventDetails .= "<h2>Address</h2><p><a href=\"pocketcampus://map.plugin.pocketcampus.org/search?q=" . rawurlencode(strip_tags($record["VenueAddress"])) . "\">" . htmlify($record["VenueAddress"]) . "</a></p>";
	if($record["VenueAddress"]) $eventDetails .= "<h2>Address</h2><p><a href=\"http://maps.google.com/maps?q=" . rawurlencode(strip_tags($record["VenueAddress"])) . "\">" . htmlify($record["VenueAddress"]) . "</a></p>";

	if($record["VenueNote"]) $eventDetails .= "<h2>Note</h2><p>" . htmlify($record["VenueNote"]) . "</p>";
	if($eventDetails) $succ = $succ && sql_query("UPDATE eventitems SET eventDetails = '" . sql_real_escape_string($eventDetails) . "'  WHERE eventId = '$newid'", $conn);
	return $succ;
}










function get_required_fields_for_info(){
	return array("InformationTitle", "InformationText", "PC_ID", "PC_CATEG");
}

function update_eventitem_info($record, $newid, $conn) {
	$succ = TRUE;
	if($record["PC_CATEG"]) $succ = $succ && sql_query("UPDATE eventitems SET eventCateg = '" . sql_real_escape_string($record["PC_CATEG"]) . "'  WHERE eventId = '$newid'", $conn);
	
	if($record["InformationTitle"]) $succ = $succ && sql_query("UPDATE eventitems SET eventTitle = '" . sql_real_escape_string($record["InformationTitle"]) . "'  WHERE eventId = '$newid'", $conn);
	if($record["InformationText"]) $succ = $succ && sql_query("UPDATE eventitems SET eventDetails = '" . sql_real_escape_string($record["InformationText"]) . "'  WHERE eventId = '$newid'", $conn);
	return $succ;
}












function get_required_fields_for_labs(){
	return array("LabAcronym", "LabName", "LabLogoURL", "LabDescription", "LabHomepageURL", "LabDirector", "AOIKeys", "PC_ID", "PC_CATEG", "DIRECTOR_PC_ID","ATTENDING_MEMBERS_IDS","ATTENDING_MEMBERS_NAMES");
}

function update_eventitem_labs($record, $newid, $conn) {
	$succ = TRUE;
	if($record["PC_CATEG"]) $succ = $succ && sql_query("UPDATE eventitems SET eventCateg = '" . sql_real_escape_string($record["PC_CATEG"]) . "'  WHERE eventId = '$newid'", $conn);
	
	if($record["LabName"]) $succ = $succ && sql_query("UPDATE eventitems SET eventTitle = '" . sql_real_escape_string($record["LabName"]) . "'  WHERE eventId = '$newid'", $conn);
	if($record["LabLogoURL"]) $succ = $succ && sql_query("UPDATE eventitems SET eventThumbnail = '" . sql_real_escape_string($record["LabLogoURL"]) . "'  WHERE eventId = '$newid'", $conn);
	if($record["LabAcronym"]) $succ = $succ && sql_query("UPDATE eventitems SET secondLine = '" . sql_real_escape_string($record["LabAcronym"]) . "'  WHERE eventId = '$newid'", $conn);
	if($record["AOIKeys"]) $succ = $succ && sql_query("UPDATE eventitems SET broadcastInFeeds = '" . sql_real_escape_string($record["AOIKeys"]) . "'  WHERE eventId = '$newid'", $conn);
	$eventDetails = "";
	if($record["LabDescription"]) $eventDetails .= "<h2>Description</h2><p>" . htmlify($record["LabDescription"]) . "</p>";
	$labDirector = "";
	if($record["LabDirector"]) {
		$labDirector = $record["LabDirector"];
		if($record["DIRECTOR_PC_ID"]) $labDirector = "<a href=\"pocketcampus://events.plugin.pocketcampus.org/showEventItem?eventItemId=" . rawurlencode($record["DIRECTOR_PC_ID"]) . "\">" . $labDirector . "</a>";
		$eventDetails .= "<h2>Director</h2><p>$labDirector</p>";
	}
	if($record["ATTENDING_MEMBERS_NAMES"]) {
		$authors = array_map("trim", explode(",", $record["ATTENDING_MEMBERS_NAMES"]));
		if($record["ATTENDING_MEMBERS_IDS"]) {
			$author_map = array_map("rawurlencode", array_map("trim", explode(",", $record["ATTENDING_MEMBERS_IDS"])));
			if(count($authors) == count($author_map)) {
				for($i = 0; $i < count($author_map); $i++) if($author_map[$i]) $authors[$i] = "<a href=\"pocketcampus://events.plugin.pocketcampus.org/showEventItem?eventItemId={$author_map[$i]}\">{$authors[$i]}</a>";
			}
		}
		if($labDirector) $authors = array_diff($authors, array($labDirector));
		$authors = array_map("make_li", $authors);
		if(count($authors)) $eventDetails .= "<h2>Attending Members</h2><p>" . implode("", $authors) . "</p>";
	}
	if($record["LabHomepageURL"]) $eventDetails .= "<h2>Homepage</h2><p><a href=\"{$record["LabHomepageURL"]}\">Link</a></p>";
	if($eventDetails) $succ = $succ && sql_query("UPDATE eventitems SET eventDetails = '" . sql_real_escape_string($eventDetails) . "'  WHERE eventId = '$newid'", $conn);
	return $succ;
}








function get_required_fields_for_research(){
	return array("Area", "PC_ID", "PC_CATEG", "MEMBER_LABS", "MEMBER_LABS_IDS");
}

function update_eventitem_research($record, $newid, $conn) {
	$succ = TRUE;
	if($record["PC_CATEG"]) $succ = $succ && sql_query("UPDATE eventitems SET eventCateg = '" . sql_real_escape_string($record["PC_CATEG"]) . "'  WHERE eventId = '$newid'", $conn);
	
	if($record["Area"]) $succ = $succ && sql_query("UPDATE eventitems SET eventTitle = '" . sql_real_escape_string($record["Area"]) . "'  WHERE eventId = '$newid'", $conn);
	$eventDetails = "";
	if($record["MEMBER_LABS"]) {
		$authors = array_map("trim", explode(",", $record["MEMBER_LABS"]));
		if($record["MEMBER_LABS_IDS"]) {
			$author_map = array_map("rawurlencode", array_map("trim", explode(",", $record["MEMBER_LABS_IDS"])));
			if(count($authors) == count($author_map)) {
				for($i = 0; $i < count($author_map); $i++) if($author_map[$i]) $authors[$i] = "<a href=\"pocketcampus://events.plugin.pocketcampus.org/showEventItem?eventItemId={$author_map[$i]}\">{$authors[$i]}</a>";
			}
		}
		$authors = array_map("make_li", $authors);
		if(count($authors)) $eventDetails .= "<h2>Labs in this area</h2><p>" . implode("", $authors) . "</p>";
	}
	if($eventDetails) $succ = $succ && sql_query("UPDATE eventitems SET eventDetails = '" . sql_real_escape_string($eventDetails) . "'  WHERE eventId = '$newid'", $conn);
	return $succ;
}




function get_required_fields_for_visits(){
	return array("VisitName", "Venue", "VenueURL", "OrganizingProfs", "ORG_PROF_PC_IDS", "PC_ID", "PC_CATEG");
}

function update_eventitem_visits($record, $newid, $conn) {
	$succ = true;
	if($record["PC_CATEG"]) $succ = $succ && sql_query("UPDATE eventitems SET eventCateg = '" . sql_real_escape_string($record["PC_CATEG"]) . "'  WHERE eventId = '$newid'", $conn);

	if($record["VisitName"]) $succ = $succ && sql_query("UPDATE eventitems SET eventTitle = '" . sql_real_escape_string($record["VisitName"]) . "'  WHERE eventId = '$newid'", $conn);

	$eventDetails = "";
	if($record["Venue"]) {
		$profs = array_map("trim", explode(",", $record["Venue"]));
		if($record["VenueURL"]) {
			$prof_map = array_map("trim", explode(",", $record["VenueURL"]));
			if(count($profs) == count($prof_map)) {
				for($i = 0; $i < count($prof_map); $i++) if($prof_map[$i]) $profs[$i] = "<a href=\"{$prof_map[$i]}\">{$profs[$i]}</a>";
			}
		}
		$profs = array_map("make_li", $profs);
		if(count($profs)) $eventDetails .= "<h2>Venue</h2><p>" . implode("", $profs) . "</p>";
	}
	if($record["OrganizingProfs"]) {
		$profs = array_map("trim", explode(",", $record["OrganizingProfs"]));
		if($record["ORG_PROF_PC_IDS"]) {
			$prof_map = array_map("rawurlencode", array_map("trim", explode(",", $record["ORG_PROF_PC_IDS"])));
			if(count($profs) == count($prof_map)) {
				for($i = 0; $i < count($prof_map); $i++) if($prof_map[$i]) $profs[$i] = "<a href=\"pocketcampus://events.plugin.pocketcampus.org/showEventItem?eventItemId={$prof_map[$i]}\">{$profs[$i]}</a>";
			}
		}
		$profs = array_map("make_li", $profs);
		if(count($profs)) $eventDetails .= "<h2>Organizing Professors</h2><p>" . implode("", $profs) . "</p>";
	}
	if($eventDetails) $succ = $succ && sql_query("UPDATE eventitems SET eventDetails = '" . sql_real_escape_string($eventDetails) . "'  WHERE eventId = '$newid'", $conn);
	return $succ;
}








function get_required_fields_for_affiliates(){
	return array("AffiliateName", "AffiliateHomepageURL", "AffiliateLogoURL", "Representatives_Names", "PC_ID", "PC_CATEG", "Representatives_IDs");
}

function update_eventitem_affiliates($record, $newid, $conn) {
	$succ = TRUE;
	if($record["PC_CATEG"]) $succ = $succ && sql_query("UPDATE eventitems SET eventCateg = '" . sql_real_escape_string($record["PC_CATEG"]) . "'  WHERE eventId = '$newid'", $conn);
	
	if($record["AffiliateName"]) $succ = $succ && sql_query("UPDATE eventitems SET eventTitle = '" . sql_real_escape_string($record["AffiliateName"]) . "'  WHERE eventId = '$newid'", $conn);
	if($record["AffiliateLogoURL"]) $succ = $succ && sql_query("UPDATE eventitems SET eventThumbnail = '" . sql_real_escape_string($record["AffiliateLogoURL"]) . "'  WHERE eventId = '$newid'", $conn);
	if($record["Representatives_Names"] || $record["AffiliateHomepageURL"]) {
		$eventDetails = "";
		if($record["Representatives_Names"]) {
			$authors = array_map("trim", explode(",", $record["Representatives_Names"]));
			if($record["Representatives_IDs"]) {
				$author_map = array_map("rawurlencode", array_map("trim", explode(",", $record["Representatives_IDs"])));
				if(count($authors) == count($author_map)) {
					for($i = 0; $i < count($author_map); $i++) if($author_map[$i]) $authors[$i] = "<a href=\"pocketcampus://events.plugin.pocketcampus.org/showEventItem?eventItemId={$author_map[$i]}\">{$authors[$i]}</a>";
				}
			}
			if(count($authors) == 1) $eventDetails .= "<h2>Representative</h2><p>{$authors[0]}</p>";
			else $eventDetails .= "<h2>Representatives</h2><p>" . implode("<br>", $authors) . "</p>";
		}
		if($record["AffiliateHomepageURL"]) $eventDetails .= "<h2>Homepage</h2><p><a href=\"{$record["AffiliateHomepageURL"]}\">Link</a></p>";
		$succ = $succ && sql_query("UPDATE eventitems SET eventDetails = '" . sql_real_escape_string($eventDetails) . "'  WHERE eventId = '$newid'", $conn);
	}
	return $succ;
}








function get_required_fields_for_participants(){
	// NEW
	return array("FirstName", "FullName", "Affiliation", "EmailAddress", "PictureURL", "HomepageURL", "PC_ID", "PC_CATEG", "EPFL_SCIPER", "userId", "exchangeToken", "accessTo", "IsPrivate", "AOIKeys", "LabName", "LAB_PC_ID");
	
	//return array("FirstName", "FullName", "Affiliation", "EmailAddress", "PictureURL", "HomepageURL", "PC_ID", "PC_CATEG", "EPFL_SCIPER", "userId", "exchangeToken", "accessTo");
}

function update_eventitem_participants($record, $newid, $conn) {
	$succ = TRUE;
	if($record["PC_CATEG"]) $succ = $succ && sql_query("UPDATE eventitems SET eventCateg = '" . sql_real_escape_string($record["PC_CATEG"]) . "'  WHERE eventId = '$newid'", $conn);
	
	// NEW
	if($record["IsPrivate"]) $succ = $succ && sql_query("UPDATE eventitems SET isProtected = 1  WHERE eventId = '$newid'", $conn);
	if($record["AOIKeys"]) $succ = $succ && sql_query("UPDATE eventitems SET broadcastInFeeds = '" . sql_real_escape_string($record["AOIKeys"]) . "'  WHERE eventId = '$newid'", $conn);
	
	if($record["FullName"]) $succ = $succ && sql_query("UPDATE eventitems SET eventTitle = '" . sql_real_escape_string($record["FullName"]) . "'  WHERE eventId = '$newid'", $conn);
	if($record["Affiliation"]) $succ = $succ && sql_query("UPDATE eventitems SET secondLine = '" . sql_real_escape_string($record["Affiliation"]) . "'  WHERE eventId = '$newid'", $conn);
	if($record["PictureURL"]) $succ = $succ && sql_query("UPDATE eventitems SET eventThumbnail = '" . sql_real_escape_string($record["PictureURL"]) . "'  WHERE eventId = '$newid'", $conn);
	$eventDetails = "";
	if($record["EmailAddress"]) $eventDetails .= "<h2>Email address</h2><p><a href=\"mailto:{$record["EmailAddress"]}\">{$record["EmailAddress"]}</a></p>";
	if($record["HomepageURL"]) $eventDetails .= "<h2>Homepage</h2><p><a href=\"{$record["HomepageURL"]}\">Link</a></p>";
	if($record["LabName"]) {
		$LabName = $record["LabName"];
		if($record["LAB_PC_ID"]) $LabName = "<a href=\"pocketcampus://events.plugin.pocketcampus.org/showEventItem?eventItemId=" . rawurlencode($record["LAB_PC_ID"]) . "\">" . $LabName . "</a>";
		$eventDetails .= "<h2>Lab</h2><p>$LabName</p>";
	}
	$contactCardLink = "";
	if($record["FullName"] && $record["EmailAddress"]) {
		$contactCardLink = "/view?firstName=" . rawurlencode($record["FullName"]) . "&email=" . rawurlencode($record["EmailAddress"]);
	}
	if($record["EPFL_SCIPER"]) {
		$contactCardLink = "/search?q=" . rawurlencode($record["EPFL_SCIPER"]);
	}
	if($contactCardLink) $eventDetails .= "<h2>Contact details</h2><p><a href=\"pocketcampus://directory.plugin.pocketcampus.org$contactCardLink\">View contact card</a></p>";
	if($eventDetails) $succ = $succ && sql_query("UPDATE eventitems SET eventDetails = '" . sql_real_escape_string($eventDetails) . "'  WHERE eventId = '$newid'", $conn);
	
	
	if($succ && $record["userId"] && $record["exchangeToken"]) {
		$succ = $succ && sql_query("INSERT INTO  eventusers (userId, mappedEvent, exchangeToken, addressingName, emailAddress) VALUES ('" . sql_real_escape_string($record["userId"]) . "', '$newid', '" . sql_real_escape_string($record["exchangeToken"]) . "', " . ($record["FirstName"] ? ("'" . sql_real_escape_string($record["FirstName"]) . "'") : "NULL") . ", " . ($record["EmailAddress"] ? ("'" . sql_real_escape_string($record["EmailAddress"]) . "'") : "NULL") . ");", $conn);
		if($succ && $record["accessTo"]) {
			$succ = $succ && sql_query("INSERT INTO  eventperms (userToken, eventItemId, permLevel) VALUES ('" . sql_real_escape_string($record["userId"]) . "', '" . sql_real_escape_string($record["accessTo"]) . "', '100');", $conn);
		}
	}
	
	
	return $succ;
}











// ECHO HTML FUNCTIONS


function echo_html_box($title, $body, $bars=1, $footer=0){
				
	echo "					<table class=box cellspacing=0 cellpadding=0><tr>\n";
	echo "						<td class=tl_corner>";
	if($bars)
		echo "<div class=bars1><div class=bars2><img class=bars_img src=\"img/bars.png\" /></div></div>\n						";
	echo "<img class=corner_img src=\"img/corner2.gif\" /></td>\n";
	echo "						<td".($footer?" id=footer":"")." class=t_border".($bars||$footer?"_wt":"")." rowspan=2>".($footer?"":"<div><span>").($bars||$footer?$title:"&nbsp;").($footer?"":"</span></div>")."</td>\n";
	echo "						<td class=tr_corner><img class=corner_img src=\"img/corner3.gif\" /></td>\n";
	echo "					</tr><tr>\n";
	echo "						<td class=l_border>&nbsp;</td>\n";
	echo "						<td class=r_border>&nbsp;</td>\n";
	echo "					</tr><tr>\n";
	echo "						<td class=l_border>&nbsp;</td>\n";
	echo "						<td".($footer?" id=footer":"")." class=box_center".($bars?"_wi":"").">".$body."</td>\n";
	echo "						<td class=r_border>&nbsp;</td>\n";
	echo "					</tr><tr>\n";
	echo "						<td class=bl_corner><img class=corner_img src=\"img/corner4.gif\" /></td>\n";
	echo "						<td class=b_border>&nbsp;</td>\n";
	echo "						<td class=br_corner><img class=corner_img src=\"img/corner5.gif\" /></td>\n";
	echo "					</tr></table>\n";

}





// GENERATE HTML FUNCTIONS


function generate_html_tree($conn, $top_id=null, $f=999999){
	if($f<1) $f=1;
	$gen="";
	$top_text="Home";
	$tree=Array();
	$trs=sql_query(query_select_all_menu_items(),$conn);
	while($tr=sql_fetch_array($trs)) {
		if($top_id==$tr["id"])
			$top_text=$tr["name"];
		$tree[count($tree)]=$tr;
	}
	$gen="<table class=map_tbl cellspacing=0 cellpadding=0><tr><td width=16><img class=icon_img src=\"img/".($top_id===null?"home":"folder").".png\"></td><td>".generate_html_node_link(Array("name"=>$top_text,"id"=>$top_id))."</td></tr></table>".generate_html_tree_sub($tree,$top_id,$f);
	return $gen;
}
function generate_html_tree_sub($tree,$parent,$f){
	$gen="<table id=tb_".$parent." class=map_ch cellspacing=0 cellpadding=0".($f>0?"":" style=\"display:none;\"").">";
	for($i=0;$i<count($tree);$i++){
		if($tree[$i]["parent"]==$parent){
			$temp1=generate_html_tree_sub($tree,$tree[$i]["id"],$f-1);
			if(!$temp1)
				$temp2="";
			else
				$temp2="<a onclick='if(tb_".$tree[$i]["id"].".style.display==\"none\"){tb_".$tree[$i]["id"].".style.display=\"inline\";this.getElementsByTagName(\"img\")[0].src=\"img/minus.png\"}else{tb_".$tree[$i]["id"].".style.display=\"none\";this.getElementsByTagName(\"img\")[0].src=\"img/plus.png\"}'><img class=icon_img src=\"img/".($f>1?"minus":"plus").".png\"></a>";
			$gen.="<tr><td width=16>".$temp2."</td><td><table class=map_tbl cellspacing=0 cellpadding=0><tr><td width=16><img class=icon_img src=\"img/".($tree[$i]["file_id"]===null?($tree[$i]["page_id"]===null?"folder":"page"):"file").".png\"></td><td>".generate_html_node_link($tree[$i])."</td></tr></table>".$temp1."</td></tr>";
		}
	}
	$gen.="</table>";
	if(strpos($gen,"<tr>")===false) // operator === is needed coz == will change the 2 operands to the same type before comparison and then 0 would be like false
		return "";
	return $gen;
}
function generate_html_node_link($arr,$sub="",$more=""){
	//return "<a href=\"".($arr[$sub."href"]?$arr[$sub."href"]:("topic.php?categ=".$arr[$sub."id"]))."\"".($arr[$sub."target"]?(" target=\"".$arr[$sub."target"]."\""):"").($more===""?"":(" ".$more)).">".$arr[$sub."name"]."</a>";
	return "<a href=\"topic.php?categ=".$arr[$sub."id"]."\" ".$more.">".$arr[$sub."name"]."</a>";
}


function generate_html_tree_manage($conn, $top_id=null, $f=999999){
	if($f<1) $f=1;
	$gen="";
	$top_text="Home";
	$tree=Array();
	$trs=sql_query(query_select_all_menu_items(),$conn);
	while($tr=sql_fetch_array($trs)) {
		if($top_id==$tr["id"])
			$top_text=$tr["name"];
		$tree[count($tree)]=$tr;
	}
	$gen="<table class=map_tbl cellspacing=0 cellpadding=0><tr><td width=16><img class=icon_img src=\"img/".($top_id===null?"home":"folder").".png\"></td><td>".generate_html_node_link(Array("name"=>$top_text,"id"=>$top_id))."</td><td class=r_btn_td>".($top_id===null?"":gen_btn($top_id,1))."</td></tr></table>".generate_html_tree_manage_sub($tree,$top_id,$f);
	return $gen;
}
function generate_html_tree_manage_sub($tree,$parent,$f){
	$gen="<table id=tb_".$parent." class=map_ch cellspacing=0 cellpadding=0".($f>0?"":" style=\"display:none;\"").">";
	for($i=0;$i<count($tree);$i++){
		if($tree[$i]["parent"]==$parent){
			$temp1=generate_html_tree_manage_sub($tree,$tree[$i]["id"],$f-1);
			if(!$temp1)
				$temp2="";
			else
				$temp2="<a onclick='if(tb_".$tree[$i]["id"].".style.display==\"none\"){tb_".$tree[$i]["id"].".style.display=\"inline\";this.getElementsByTagName(\"img\")[0].src=\"img/minus.png\"}else{tb_".$tree[$i]["id"].".style.display=\"none\";this.getElementsByTagName(\"img\")[0].src=\"img/plus.png\"}'><img class=icon_img src=\"img/".($f>1?"minus":"plus").".png\"></a>";
			$gen.="<tr><td width=16>".$temp2."</td><td><table class=map_tbl cellspacing=0 cellpadding=0><tr><td width=16><img class=icon_img src=\"img/".($tree[$i]["file_id"]===null?($tree[$i]["page_id"]===null?"folder":"page"):"file").".png\"></td><td>".generate_html_node_link($tree[$i])."</td><td class=r_btn_td>".($tree[$i]["page_id"]===null?($tree[$i]["file_id"]===null?(gen_btn($tree[$i]["id"],1).($temp2?"":(" ".gen_btn($tree[$i]["id"],2)." ".gen_btn($tree[$i]["id"],3)))):gen_btn($tree[$i]["id"],4)):"")."</td></tr></table>".$temp1."</td></tr>";
		}
	}
	$gen.="</table>";
	if(strpos($gen,"<tr>")===false) // operator === is needed coz == will change the 2 operands to the same type before comparison and then 0 would be like false
		return "";
	return $gen;
}
function gen_btn($id,$num){
	if($num==1)
		return "<a href=\"javascript: mng_create_child($id)\"><img class=icon_img alt=\"create child\" src=\"img/folder_add.png\"></a>";
	elseif($num==2)
		return "<a href=\"javascript: mng_attach_file($id)\"><img class=icon_img alt=\"attach file\" src=\"img/file_add.png\"></a>";
	elseif($num==3)
		return "<a href=\"javascript: mng_delete_node($id)\"><img class=icon_img alt=\"delete node\" src=\"img/folder_delete.png\"></a>";
	elseif($num==4)
		return "<a href=\"javascript: mng_delete_file($id)\"><img class=icon_img alt=\"delete attached file\" src=\"img/file_delete.png\"></a>";

	elseif($num==11)
		return "<a href=\"javascript: frm_delete_field($id)\"><img class=icon_img alt=\"delete field\" src=\"img/file_delete.png\"></a>";
	elseif($num==12)
		return "<a href=\"javascript: frm_delete_form($id)\"><img class=icon_img alt=\"delete form\" src=\"img/folder_delete.png\"></a>";
	elseif($num==13)
		return "<a href=\"javascript: frm_add_field($id)\"><img class=icon_img alt=\"add field\" src=\"img/file_add.png\"></a>";
	elseif($num==14)
		return "<a href=\"javascript: frm_add_form()\"><img class=icon_img alt=\"add form\" src=\"img/folder_add.png\"></a>";
}






// DATABSE FUNCTIONS

$DB_FUNC_PREFIX="my";

function sql_connect(){
	$t=func_get_args();
	global $DB_FUNC_PREFIX;
	return call_user_func_array($DB_FUNC_PREFIX.__FUNCTION__, $t);
}
function sql_data_seek(){
	$t=func_get_args();
	global $DB_FUNC_PREFIX;
	return call_user_func_array($DB_FUNC_PREFIX.__FUNCTION__, $t);
}
function sql_fetch_array(){
	$t=func_get_args();
	global $DB_FUNC_PREFIX;
	return call_user_func_array($DB_FUNC_PREFIX.__FUNCTION__, $t);
}
function sql_fetch_field(){
	$t=func_get_args();
	global $DB_FUNC_PREFIX;
	return call_user_func_array($DB_FUNC_PREFIX.__FUNCTION__, $t);
}
function sql_insert_id(){
	$t=func_get_args();
	global $DB_FUNC_PREFIX;
	if($DB_FUNC_PREFIX=="ms"){
		$ins_id=sql_fetch_array(sql_query("SELECT SCOPE_IDENTITY() AS id;",func_get_arg(0)));
		return $ins_id["id"];
	}
	return call_user_func_array($DB_FUNC_PREFIX.__FUNCTION__, $t);
}
function sql_num_fields(){
	$t=func_get_args();
	global $DB_FUNC_PREFIX;
	return call_user_func_array($DB_FUNC_PREFIX.__FUNCTION__, $t);
}
function sql_field_name(){
	$t=func_get_args();
	global $DB_FUNC_PREFIX;
	return call_user_func_array($DB_FUNC_PREFIX.__FUNCTION__, $t);
}
function sql_num_rows(){
	$t=func_get_args();
	global $DB_FUNC_PREFIX;
	return call_user_func_array($DB_FUNC_PREFIX.__FUNCTION__, $t);
}
function sql_query(){
	$t=func_get_args();
	global $DB_FUNC_PREFIX;
	return call_user_func_array($DB_FUNC_PREFIX.__FUNCTION__, $t);
}
function sql_real_escape_string(){
	$t=func_get_args();
	global $DB_FUNC_PREFIX;
	if($DB_FUNC_PREFIX=="ms"){
		return str_replace("'", "''", func_get_arg(0));
	}
	return call_user_func_array($DB_FUNC_PREFIX.__FUNCTION__, $t);
}
function sql_select_db(){
	$t=func_get_args();
	global $DB_FUNC_PREFIX;
	return call_user_func_array($DB_FUNC_PREFIX.__FUNCTION__, $t);
}


// DATABSE QUERIES

//TODO make them injection proof
//     use sql_real_escape_string
//TODO make them cross database compatible
//     quoting for mssql: []
//     quoting for mysql: ``




function query_insert_new_lab($s, $a, $e, $o) {
	$s = sql_real_escape_string($s);
	$a = sql_real_escape_string($a);
	$e = sql_real_escape_string($e);
	$o = sql_real_escape_string($o);
	return "INSERT INTO  `eventitems` ( `sex` , `age` , `education` , `ocountry` , `rcountry` ) VALUES ( '$s' ,  '$a' ,  '$e' ,  '$o' ,  '$r' );";
}




function query_insert_image($p, $t, $f) {
	$p = sql_real_escape_string($p);
	$t = sql_real_escape_string($t);
	$f = sql_real_escape_string($f);
	return "INSERT INTO  `images` ( `path` , `t` , `f` ) VALUES ( '$p',  '$t',  '$f' );";
}
function query_insert_user_epfl($f, $l, $e, $o, $g, $s) {
	$f = sql_real_escape_string($f);
	$l = sql_real_escape_string($l);
	$e = sql_real_escape_string($e);
	$o = sql_real_escape_string($o);
	$g = sql_real_escape_string($g);
	$s = sql_real_escape_string($s);
	return "INSERT INTO  `users` (`firstname` ,`lastname` ,`email` ,`org` ,`gaspar` ,`sciper` ) VALUES ( '$f',  '$l',  '$e',  '$o',  '$g',  '$s');";
}
function query_insert_answer($u, $i, $r, $t) {
	$u = sql_real_escape_string($u);
	$i = sql_real_escape_string($i);
	$r = sql_real_escape_string($r);
	$t = sql_real_escape_string($t);
	return "INSERT INTO  `answers` ( `userid` ,`imageid` ,`result` ,`reaction` ) VALUES ( '$u',  '$i',  '$r',  '$t' );";
}
function query_select_all_images() {
	return "select * from (select id, path, rand() as r from images where id in (select id from images group by f, t) union select id, path, rand() as r from images) as b order by r";
	//return "SELECT id, path, RAND() AS R FROM `images`, (SELECT 1 UNION SELECT 2) AS T ORDER BY R;";
	//return "SELECT id, path, RAND() AS R FROM `images` ORDER BY R;";
}
function query_compute_stats($userid) {
	$userid = sql_real_escape_string($userid);
	return "SELECT discr, AVG( reaction ) /1000 AS reaction, SUM( CASE WHEN result =  'f' THEN 1 ELSE 0 END ) AS f , SUM( CASE WHEN result =  't' THEN 1 ELSE 0 END ) AS t FROM answers, images WHERE imageid = id AND userid = '$userid' GROUP BY discr;";
}
function query_select_all_stats() {
	return "select id, progress, consistency, sex, age, education, ocountry, rcountry, timestamp  from (SELECT userid as userid1, round(count(*) / 95 * 100) as progress  FROM answers group by userid) a, (SELECT userid as userid2, round(sum(CASE WHEN min = max THEN 1 ELSE 0 END) / count(*) * 100) as consistency FROM (SELECT userid, min(result) as min,   max(result) as max  FROM `answers` group by imageid, userid) as T GROUP BY userid) b, users Where userid1 = id and  userid2 = id";
}
function query_select_all_stats_with_filter_arr($arr) {
	$query = "select id, progress, consistency, sex, age, education, ocountry, rcountry, timestamp  from " .
			"(SELECT userid as userid1, round(count(*) / 95 * 100) as progress  FROM answers group by userid) a, " .
			"(SELECT userid as userid2, round(sum(CASE WHEN min = max THEN 1 ELSE 0 END) / count(*) * 100) as consistency FROM (SELECT userid, min(result) as min,   max(result) as max  FROM `answers` group by imageid, userid) as T GROUP BY userid) b, " .
			"users " .
			"where userid1 = id and  userid2 = id ";
	if(!empty($arr["p"])) {
		$query .= "and (" . str_replace("$", " `progress` ", $arr["p"]) . ")";
	}
	if(!empty($arr["c"])) {
		$query .= "and (" . str_replace("$", " `consistency` ", $arr["c"]) . ")";
	}
	if(!empty($arr["s"])) {
		$query .= "and (" . str_replace("$", " `sex` ", $arr["s"]) . ")";
	}
	if(!empty($arr["a"])) {
		$query .= "and (" . str_replace("$", " `age` ", $arr["a"]) . ")";
	}
	if(!empty($arr["e"])) {
		$query .= "and (" . str_replace("$", " `education` ", $arr["e"]) . ")";
	}
	if(!empty($arr["o"])) {
		$query .= "and (" . str_replace("$", " `ocountry` ", $arr["o"]) . ")";
	}
	if(!empty($arr["r"])) {
		$query .= "and (" . str_replace("$", " `rcountry` ", $arr["r"]) . ")";
	}
	if(!empty($arr["t"])) {
		$query .= "and (" . str_replace("$", " `timestamp` ", $arr["t"]) . ")";
	}
	return $query;
}
function query_select_stats_by_image($order) {
	$query = "select lo.cue as cue,  lo.path as lo_path, lo.lying as lo_lying , lo.nlying as lo_nlying , hi.nlying as hi_nlying , hi.lying as hi_lying , hi.path as hi_path     , lo.lying/lo.nlying as olo , hi.lying/hi.nlying as ohi " .
			"from " .
			"(select imageid,path,cue, SUM(CASE WHEN result =  'f' THEN 1 ELSE 0 END) as lying , SUM(CASE WHEN result =  't' THEN 1 ELSE 0 END) as nlying  from answers, images where id = imageid and discr = 50  group by imageid) as lo, " .
			"(select imageid,path,cue, SUM(CASE WHEN result =  'f' THEN 1 ELSE 0 END) as lying , SUM(CASE WHEN result =  't' THEN 1 ELSE 0 END) as nlying  from answers, images where id = imageid and discr = 100 group by imageid) as hi " .
			"where lo.cue = hi.cue ";
	if(!empty($order)) {
		$query .= "order by $order ";
	}
	return $query;
}
function query_select_stats_by_image_filtered($uids, $order) {
	$query = "select lo.cue as cue,  lo.path as lo_path, lo.lying as lo_lying , lo.nlying as lo_nlying , hi.nlying as hi_nlying , hi.lying as hi_lying , hi.path as hi_path     , lo.lying/lo.nlying as olo , hi.lying/hi.nlying as ohi " .
			"from " .
			"(select imageid,path,cue, SUM(CASE WHEN result =  'f' THEN 1 ELSE 0 END) as lying , SUM(CASE WHEN result =  't' THEN 1 ELSE 0 END) as nlying  from answers, images where id = imageid and userid IN ($uids) and discr = 50  group by imageid) as lo, " .
			"(select imageid,path,cue, SUM(CASE WHEN result =  'f' THEN 1 ELSE 0 END) as lying , SUM(CASE WHEN result =  't' THEN 1 ELSE 0 END) as nlying  from answers, images where id = imageid and userid IN ($uids) and discr = 100 group by imageid) as hi " .
			"where lo.cue = hi.cue ";
	if(!empty($order)) {
		$query .= "order by $order ";
	}
	return $query;
}
function query_select_stats_by_image_category_filtered($uids) {
	//$query = "select t,f, SUM(CASE WHEN result =  'f' THEN 1 ELSE 0 END) as lying , SUM(CASE WHEN result =  't' THEN 1 ELSE 0 END) as nlying , avg(reaction) as reaction from answers, images where id = imageid " . (empty($uids) ? "" : " and userid IN ($uids) ")." group by t,f order by t,f;";
	$query = "select t,f, round(avg((reaction - min ) / (max-min))*100,0) as reaction, sum(lying) as lying, sum(nlying) as nlying " .
			"from  " .
			"(select userid,t,f, avg(reaction) as reaction,  SUM(CASE WHEN result =  'f' THEN 1 ELSE 0 END) as lying , SUM(CASE WHEN result =  't' THEN 1 ELSE 0 END) as nlying from answers, images where id = imageid " . (empty($uids) ? "" : " and userid IN ($uids) ")." group by userid,t,f) as a, " .
			"( " .
			"    select userid, min(reaction) as min, max(reaction) as max " .
			"    from " .
			"    (select userid, avg(reaction) as reaction from answers, images where id = imageid " . (empty($uids) ? "" : " and userid IN ($uids) ")." group by userid,t,f) as a " .
			"    group by userid " .
			") as b " .
			"where a.userid = b.userid " .
			"group by t,f ";
	return $query;
}
function query_delete_participant_answers($userid) {
	$userid = sql_real_escape_string($userid);
	return "DELETE FROM `answers` where userid = '$userid';";
}
function query_delete_participant($userid) {
	$userid = sql_real_escape_string($userid);
	return "DELETE FROM `users` where id = '$userid';";
}




#######
# mysql_ affected_ rows
# mysql_ change_ user
# mysql_ client_ encoding
# mysql_ close
# mysql_ connect
# mysql_ create_ db
# mysql_ data_ seek
# mysql_ db_ name
# mysql_ db_ query
# mysql_ drop_ db
# mysql_ errno
# mysql_ error
# mysql_ escape_ string
# mysql_ fetch_ array
# mysql_ fetch_ assoc
# mysql_ fetch_ field
# mysql_ fetch_ lengths
# mysql_ fetch_ object
# mysql_ fetch_ row
# mysql_ field_ flags
# mysql_ field_ len
# mysql_ field_ name
# mysql_ field_ seek
# mysql_ field_ table
# mysql_ field_ type
# mysql_ free_ result
# mysql_ get_ client_ info
# mysql_ get_ host_ info
# mysql_ get_ proto_ info
# mysql_ get_ server_ info
# mysql_ info
# mysql_ insert_ id
# mysql_ list_ dbs
# mysql_ list_ fields
# mysql_ list_ processes
# mysql_ list_ tables
# mysql_ num_ fields
# mysql_ num_ rows
# mysql_ pconnect
# mysql_ ping
# mysql_ query
# mysql_ real_ escape_ string
# mysql_ result
# mysql_ select_ db
# mysql_ set_ charset
# mysql_ stat
# mysql_ tablename
# mysql_ thread_ id
# mysql_ unbuffered_ query
#######
# mssql_ bind
# mssql_ close
# mssql_ connect
# mssql_ data_ seek
# mssql_ execute
# mssql_ fetch_ array
# mssql_ fetch_ assoc
# mssql_ fetch_ batch
# mssql_ fetch_ field
# mssql_ fetch_ object
# mssql_ fetch_ row
# mssql_ field_ length
# mssql_ field_ name
# mssql_ field_ seek
# mssql_ field_ type
# mssql_ free_ result
# mssql_ free_ statement
# mssql_ get_ last_ message
# mssql_ guid_ string
# mssql_ init
# mssql_ min_ error_ severity
# mssql_ min_ message_ severity
# mssql_ next_ result
# mssql_ num_ fields
# mssql_ num_ rows
# mssql_ pconnect
# mssql_ query
# mssql_ result
# mssql_ rows_ affected
# mssql_ select_ db
#######



function connect_to_db($db_name){
	global $DB_FUNC_PREFIX;
	if($_SERVER["SERVER_ADDR"]=="127.0.0.1"){ // IF MY PC
		if($DB_FUNC_PREFIX=="ms"){ // IF SQL
			//$Connection = sql_connect("DANA_PC","sa","");
			$Connection = sql_connect("SATELLITE-A75","sa","");
			sql_select_db($db_name,$Connection) or die("ERROR: NO SUCH DATABASE");
		}else{ // IF MYSQL
			$Connection = sql_connect("pocketcampus.epfl.ch","pocketcampus","XXXXXXX");
			sql_select_db($db_name,$Connection) or die("ERROR: NO SUCH DATABASE");
		}
	}else{ // IF LIVE SERVER
		if($DB_FUNC_PREFIX=="ms"){ // IF SQL
			$Connection = sql_connect("ASB00D11","sa","");
			sql_select_db($db_name,$Connection) or die("ERROR: NO SUCH DATABASE");
		}else{ // IF MYSQL
			$config = parse_ini_file("/var/www/vhosts/pocketcampus/private/pocketcampus-server.config");
			$config or die("CANNOT FIND OR OPEN CONFIG FILE");
			$db_url = parse_url(str_replace("jdbc:", "", stripslashes("{$config["DB_URL"]}")));
			$Connection = sql_connect("{$db_url["host"]}:{$db_url["port"]}","{$config["DB_USERNAME"]}","{$config["DB_PASSWORD"]}");
			$db_name = array_pop(explode("/", "{$db_url["path"]}"));
			sql_select_db("{$db_name}",$Connection) or die("ERROR: NO SUCH DATABASE");
		}
	}
	return $Connection;
}








// BUSINESS LOGIC FUNCTIONS

// permissions functions WITHOUT recursion
function check_file_permission($conn,$user_hrid,$file_id){
	$t_perms=sql_query(query_check_file_permission($user_hrid,$file_id),$conn);
	$t_perm=sql_fetch_array($t_perms);
	if($t_perm)
		return true;
	return false;
}
function check_page_permission($conn,$user_hrid,$page_id){
	$t_perms=sql_query(query_check_page_permission($user_hrid,$page_id),$conn);
	$t_perm=sql_fetch_array($t_perms);
	if($t_perm)
		return true;
	return false;
}
// permissions functions WITH recursion
function check_file_permission_rec($conn,$user_hrid,$file_id){
	$t_perms=sql_query(query_check_file_permission_rec($user_hrid,$file_id),$conn);
	$t_perm=sql_fetch_array($t_perms);
	$parent_id=$t_perm["parent_id"];
	$t_perm=sql_fetch_array($t_perms);
	if($t_perm)
		return true;
	if($parent_id)
		return check_page_permission_rec($conn,$user_hrid,$parent_id); // check parent page (user must make sure there is no circular references)
	return false;
}
function check_page_permission_rec($conn,$user_hrid,$page_id){
	$t_perms=sql_query(query_check_page_permission_rec($user_hrid,$page_id),$conn);
	$t_perm=sql_fetch_array($t_perms);
	$parent_id=$t_perm["parent"];
	$t_perm=sql_fetch_array($t_perms);
	if($t_perm)
		return true;
	if($parent_id)
		return check_page_permission_rec($conn,$user_hrid,$parent_id); // check parent page (user must make sure there is no circular references)
	return false;
}





// LOGIN ENCRYPTION FUNCTIONS

function login_decrypt($val,$key=null){
	if($key===null)
		$key=md5((empty($_SERVER["REMOTE_ADDR"])?"":$_SERVER["REMOTE_ADDR"]).":".(empty($_SERVER["HTTP_USER_AGENT"])?"":$_SERVER["HTTP_USER_AGENT"]));
	$retval="";
	for($i=0;2*$i+2<=strlen($val);$i++)
		$retval.=chr(hexdec(substr($val,2*$i,2))^ord($key[$i%strlen($key)]));
	return $retval;
}
function encrypt_for_db($val,$key){
	$retval="";
	for($i=0;$i<strlen($val);$i++){
		$temp=dechex(ord($val[$i])^ord($key[$i%strlen($key)]));
		$retval.=(strlen($temp)==1?"0":"").$temp;
	}
	return $retval;
}









// ACTIVE DIRECTORY INTEGRATION

function ad_authenticate($username,$userpass){

	//FOR REMOTE DEBUGGING PURPOSES : I MUST BE ABLE TO LOGIN WITHOUT ACTIVE DIRECTORY
	if($username=="D001") return array(1,array(array("givenname"=>array("Amer"),"sn"=>array("Chamseddine"),"displayname"=>array("Amer Chamseddine"))),"You have successfully logged in to Active Directory");

	$ldap = @ldap_connect("10.10.201.211");
	if(!$ldap)
		return array(11,"","Cannot connect to the ldap server");
	$auth=false;
	if(!($res=@ldap_bind($ldap,"cn=Power User, cn=users, dc=almawarid, dc=com","XXXX")))
		return array(12,ldap_error($ldap),"Could not bind to $dn2");
	else{
		$sr=@ldap_search($ldap,"dc=almawarid, dc=com","samaccountname=$username");
		if(!$sr)
			return array(13,"","Search failed");
		else{
			$info=ldap_get_entries($ldap,$sr);
			if($info["count"]){
				$auth=true;
				$user_cn=$info[0]["distinguishedname"][0];
			}
			ldap_unbind($ldap);
		}
	}
	if(!$auth)
		return array(0,"","Could not authenticate you to the Active Directory Server"); // invalid username

	$ldap = @ldap_connect("10.10.201.211");
	if(!$ldap)
		return array(15,"","Cannot connect to AD server");
	if($res=@ldap_bind($ldap,$user_cn,$userpass))
		return array(1,$info,"You have successfully logged in to Active Directory");
	else
		return array(0,"","Invalid Active Directory User Password");

}























// OTHER
function mkdir_rec($pathname){
	is_dir(dirname($pathname)) || mkdir_rec(dirname($pathname));
	return is_dir($pathname) || @mkdir($pathname);
}









// TEST

function test1(){
	$t=Array();
	$t["amer"]="amer";
	$t["dana"]="dana";
	echo implode(" ",$t);
}





?>
