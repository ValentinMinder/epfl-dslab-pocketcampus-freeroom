<?php
/*
echo ini_get('ignore_user_abort');
exit;
*/
$reply = array();



if($_SERVER["REQUEST_METHOD"] == "POST"){
	$conn = mysql_connect("localhost", "pocketcampus", "G1lbnVfc2V0dGluZ3MgMHg3ZjA3");
	if(!$conn){
		$reply["error"] = "could not connect to db";
		die(json_encode($reply));
	}
	$select_db = mysql_select_db("pocketcampus", $conn);
	//if(mysql_num_rows($run) == 0){
	if(empty($_POST["action"])){
		$reply["error"] = "no action";
		die(json_encode($reply));
	}
	if($_POST["action"] == "send"){
		if(empty($_POST["myref"]) || empty($_POST["ref"]) || empty($_POST["message"])){
			$reply["error"] = "required fields: myref ref message";
			die(json_encode($reply));
		}
		file_put_contents("messages_log", "{$_POST["myref"]}\t{$_POST["ref"]}\t{$_POST["message"]}\n", FILE_APPEND | LOCK_EX);
		mysql_query("insert into message_passing(`from`, `to`, `message`) values('".mysql_escape_string($_POST["myref"])."', '".mysql_escape_string($_POST["ref"])."', '".mysql_escape_string($_POST["message"])."');");
		$reply["payload"] = array();
		die(json_encode($reply));
	}elseif($_POST["action"] == "recv"){
		if(empty($_POST["myref"])){
			$reply["error"] = "myref is required";
			die(json_encode($reply));
		}
		$useconds = 0;
		while(true){
			set_time_limit(30);
			//if(connection_aborted()) exit;
			$msgs = array();
			$res = mysql_query("select * from message_passing where `to` = '".mysql_escape_string($_POST["myref"])."' order by `id`;");
			while($arr = mysql_fetch_array($res)){
				$msgs[] = array("message" => $arr["message"], "from" => $arr["from"]);
				mysql_query("delete from message_passing where `id` = '".$arr["id"]."';");
			}
			if(count($msgs)) {
				$reply["payload"] = $msgs;
				die(json_encode($reply));
			}
			if($useconds > 10000000){
				$reply["payload"] = array();
				die(json_encode($reply));
			}
			usleep(100000);
			$useconds += 100000;
		}
	}elseif($_POST["action"] == "sendbc"){
		if(empty($_POST["myref"]) || empty($_POST["roomref"]) || empty($_POST["message"])){
			$reply["error"] = "required fields: myref roomref message";
			die(json_encode($reply));
		}
		file_put_contents("messages_log", "{$_POST["myref"]}\t{$_POST["roomref"]}\t{$_POST["message"]}\n", FILE_APPEND | LOCK_EX);
		mysql_query("insert into message_passing(`from`, `to`, `message`) select '".mysql_escape_string($_POST["myref"])."', name, '".mysql_escape_string($_POST["message"])."' from message_rcv where room = '".mysql_escape_string($_POST["roomref"])."' and lastseen > adddate(current_timestamp(), interval -30 second);");
		$reply["payload"] = array();
		die(json_encode($reply));
	}elseif($_POST["action"] == "recvbc"){
		if(empty($_POST["myref"]) || empty($_POST["roomref"])){
			$reply["error"] = "myref and roomref are required";
			die(json_encode($reply));
		}
		mysql_query("replace  into message_rcv (name, room) values ('".mysql_escape_string($_POST["myref"])."', '".mysql_escape_string($_POST["roomref"])."');");
		$useconds = 0;
		while(true){
			set_time_limit(30);
			//if(connection_aborted()) exit;
			$msgs = array();
			$res = mysql_query("select * from message_passing where `to` = '".mysql_escape_string($_POST["myref"])."' order by `id`;");
			while($arr = mysql_fetch_array($res)){
				$msgs[] = array("message" => $arr["message"], "from" => $arr["from"]);
				mysql_query("delete from message_passing where `id` = '".$arr["id"]."';"); // TODO dont delete instead select where id > client_seen_id
			}
			if(count($msgs)) {
				$reply["payload"] = $msgs;
				die(json_encode($reply));
			}
			if($useconds > 10000000){
				$reply["payload"] = array();
				die(json_encode($reply));
			}
			usleep(100000);
			$useconds += 100000;
		}
	} elseif($_POST["action"] == "getactive") {
		if(empty($_POST["vidid"])) {
			$reply["error"] = "vidid is required";
			die(json_encode($reply));
		}
		$rooms = array();
		$res = mysql_query("select room, count(*) as occup from message_rcv where room like '".mysql_escape_string($_POST["vidid"])."%' and lastseen > adddate(current_timestamp(), interval -30 second) group by 1;");
		while($arr = mysql_fetch_array($res)){
			$rooms[] = array("room_name" => $arr["room"], "occupancy" => $arr["occup"]);
		}
		$reply["payload"] = $rooms;
		die(json_encode($reply));
	}
}



?>

<form action='' method='POST' target='results_frame'>
<div>
action <input name='action' value='send'><br>
myref <input name='myref'><br>
ref <input name='ref'><br>
message <input name='message'><br>
</div>
<div>
<input type='submit'>
</div>
</form>

<form action='' method='POST' target='results_frame'>
<div>
action <input name='action' value='recv'><br>
myref <input name='myref'><br>
</div>
<div>
<input type='submit'>
</div>
</form>

<form action='' method='POST' target='results_frame'>
<div>
action <input name='action' value='sendbc'><br>
myref <input name='myref'><br>
roomref <input name='roomref'><br>
message <input name='message'><br>
</div>
<div>
<input type='submit'>
</div>
</form>

<form action='' method='POST' target='results_frame'>
<div>
action <input name='action' value='recvbc'><br>
myref <input name='myref'><br>
roomref <input name='roomref'><br>
</div>
<div>
<input type='submit'>
</div>
</form>

<form action='' method='POST' target='results_frame'>
<div>
action <input name='action' value='getactive'><br>
vidid <input name='vidid'><br>
</div>
<div>
<input type='submit'>
</div>
</form>

<iframe name="results_frame">
</iframe>

