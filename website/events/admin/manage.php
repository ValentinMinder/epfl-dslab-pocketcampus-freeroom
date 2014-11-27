<?php

require_once("functions.php");
require_once("php_headers.php"); // will set $conn to database




if(empty($_GET["parentPool"])) {
	die("No parentPool specified");
}
if(empty($_GET["type"])) {
	die("No type specified");
}

$PARENT_POOL = sql_real_escape_string($_GET["parentPool"]);
$TYPE = ($_GET["type"]);








function get_required_fields(){
	$t=func_get_args();
	global $TYPE;
	return call_user_func_array(__FUNCTION__."_for_$TYPE", $t);
}

function update_eventitem(){
	$t=func_get_args();
	global $TYPE;
	return call_user_func_array(__FUNCTION__."_$TYPE", $t);
}

if(!function_exists("get_required_fields_for_$TYPE") || !function_exists("update_eventitem_$TYPE")){
	die("I don't understand this type");
}




$REQUIRED_FILEDS = get_required_fields();



function delete_pool_contents() {

	global $msg;
	global $conn;
	global $PARENT_POOL;

	sql_query("DELETE FROM eventperms WHERE userToken IN (SELECT userId FROM eventusers WHERE mappedEvent IN (SELECT eventId FROM eventitems WHERE parentPool='$PARENT_POOL'));", $conn);
	$msg .= "<p>Deleted " . mysql_affected_rows($conn) . " rows from eventperms.</p>";

	sql_query("DELETE FROM eventusers WHERE mappedEvent IN (SELECT eventId FROM eventitems WHERE parentPool='$PARENT_POOL');", $conn);
	$msg .= "<p>Deleted " . mysql_affected_rows($conn) . " rows from eventusers.</p>";

	sql_query("DELETE FROM eventitems WHERE parentPool='$PARENT_POOL';", $conn);
	$msg .= "<p>Deleted " . mysql_affected_rows($conn) . " rows from eventitems.</p>";

}


if($_SERVER["REQUEST_METHOD"] == "POST"){
	
	//echo "<pre>";
	//print_r($_FILES);
	$msg = "";
	
	/*if(empty($_POST["eventId"])) {
		die("no eventId");
	}
	if(empty($_POST["userId"])) {
		die("no userId");
	}
	
	$eid = sql_real_escape_string($_POST["eventId"]);
	$uid = sql_real_escape_string($_POST["userId"]);*/
	
	if(!empty($_POST["deleteItems"])) {
	
		delete_pool_contents();

	} else {

		$tmp_csv_file = $_FILES['uploadedfile']['tmp_name'];
		//$ext = pathinfo($_FILES['uploadedfile']['name'], PATHINFO_EXTENSION);
		//$target_path = "/var/www/backend/parti-pics/$uid.$ext";
		////$target_path = $target_path . basename( $_FILES['uploadedfile']['name']);
		if(!empty($_POST["workbookId"])) {
			$tmp_csv_file = "/tmp/" . uniqid(time(0));
//			download_file("https://docs.google.com/spreadsheet/ccc?key={$_POST["workbookId"]}&gid={$_POST["sheetId"]}&output=csv", $tmp_csv_file, "/tmp/" . uniqid(time(0)));
			download_file("https://docs.google.com/spreadsheets/d/{$_POST["workbookId"]}/export?format=csv&id={$_POST["workbookId"]}&gid={$_POST["sheetId"]}", $tmp_csv_file, "/tmp/" . uniqid(time(0)));
			//file_put_contents($tmp_csv_file, file_get_contents("https://docs.google.com/spreadsheet/ccc?key={$_POST["workbookId"]}&gid={$_POST["sheetId"]}&output=csv"));
		}

		$csv_data = csv_to_array($tmp_csv_file);
		if($csv_data) {
		//if(move_uploaded_file($_FILES['uploadedfile']['tmp_name'], $target_path)){
			
			$check = TRUE;
			if(count($csv_data) > 0) {
				foreach($REQUIRED_FILEDS as $field) {
					if(!isset($csv_data[0][$field])) {
						$check = FALSE;
						break;
					}
				}
			}
			
			if($check) {
				if(!empty($_POST["deleteFirst"])) {
					delete_pool_contents();
				}
				$inserted_count = 0;
				foreach($csv_data as $record) {
					//$newid = sql_fetch_array(sql_query("SELECT MAX(eventId)+1 FROM eventitems WHERE parentPool='$PARENT_POOL'", $conn));
					$newid = FALSE;
					if($record["PC_ID"]) $newid = sql_real_escape_string($record["PC_ID"]);
					if($newid) {
						//$newid = $newid[0];
						$res = sql_query("INSERT INTO  eventitems (eventId, parentPool) VALUES ('$newid', '$PARENT_POOL');", $conn);
						//$picurl = sql_real_escape_string("http://pocketcampus.epfl.ch/backend/parti-pics/$uid.$ext"); 
						if($res) {
							$inserted_count++;
							if(!update_eventitem($record, $newid, $conn)) {
								$msg .= "<p>Error while setting some event item details.</p>";
							}
							
						} else {
							$msg .= "<p>Could not insert item in DB (duplicate ID? did you delete first?).</p>";
						}
					} else {
						$msg .= "<p>Could not allocate ID for item / ID is NULL.</p>";
					}
				}
				if($inserted_count != 0) {
					$msg .= "<p>Successfully created $inserted_count new items.</p>";
				}
			} else {
				$msg .= "<p>Missing required fields.</p>";
			}
			
		}else{
			$msg .= "<p>Error uploading file.</p>";
		}
		
		
	}

	
	if(!empty($_POST["silent"])) {
		die(str_replace("<p>", "", str_replace("</p>", "\n", $msg)));
	}

	header("Location: ?parentPool=" . rawurlencode($PARENT_POOL) . "&type=" . rawurlencode($TYPE) . "&msg=" . rawurlencode($msg));
	exit;
}


$POOL_TITLE = "Pool";
$poool = sql_fetch_array(sql_query("SELECT poolTitle FROM eventpools WHERE poolId='$PARENT_POOL'", $conn));
if($poool) $POOL_TITLE = $poool[0];


?><html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Manage <?php echo $POOL_TITLE; ?></title>

</head>






<body style="text-align:center">


<div id=main_div>

	<!--
	<div style='margin-top:50px;'>
		<b>EcoCloud Annual Event 2013</b>
	</div>
	-->

<?php
if(!empty($_GET["msg"])) {
	echo "\t<div>{$_GET["msg"]}</div>\n";
}
?>
	
	<div style='height:600px;width:600px;display:table;margin:auto;'>
		<div style='display:table-cell;vertical-align:middle;'>
			<h1>Manage <?php echo $POOL_TITLE; ?></h1>
			<br>
			<hr>
			<h4>Current items</h4>
			<ul>
<?php 

		$all_users = sql_query("SELECT eventTitle FROM eventitems WHERE parentPool='$PARENT_POOL'", $conn);
		while($user = sql_fetch_array($all_users)) {
			echo "\t\t\t\t<li>{$user["eventTitle"]}</li>\n";
		}

?>
			</ul>
			<hr>
			<h4>Delete all</h4>
				<form enctype="multipart/form-data" action="#" method="POST">
					<input type="hidden" name="deleteItems" value="1" />
					<input type="submit" value="Delete all" />
				</form>
			<hr>
			<h4>Import</h4>
			<p>Required fields</p>
			<pre><?php echo implode(", ", $REQUIRED_FILEDS); ?></pre>
			<!--<div style="height:500px;overflow:scroll;">-->
			<table align=center  style="width:800px;">
				<form enctype="multipart/form-data" action="#" method="POST">
				<input type="hidden" name="MAX_FILE_SIZE" value="512000000" />
				<tr>
					<td>
						From CSV
					</td>
					<td>
						file
					</td>
					<td>
						<input name="uploadedfile" type="file" />
					</td>
					<td>
						<input type="submit" value="import" />
					</td>
				</tr>
				</form>
				<form action="#" method="POST">
				<tr>
					<td>
                                                From Google Spreadsheet
                                        </td>
                                        <td>
                                                key
                                        </td>
                                        <td>
                                                <input name="workbookId" />
                                        </td>
                                        <td>
                                        </td>
				</tr>
				<tr>
                                        <td>
                                        </td>
                                        <td>
                                                gid
                                        </td>
                                        <td>
                                                <input name="sheetId"  />
                                        </td>
                                        <td>
                                                <input type="submit" value="import" />
                                        </td>
                                </tr>
				</form>
			</table>
			<!--</div>-->
		</div>
	</div>
	

</div>


</body>


</html>
