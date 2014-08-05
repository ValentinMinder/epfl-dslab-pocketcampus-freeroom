<?php

require_once("functions.php");
require_once("php_headers.php"); // will set $conn to database




?><html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Manage EcoCloud</title>

</head>






<body style="text-align:center">


<div id=main_div>


	
	<div style='height:600px;width:600px;display:table;margin:auto;'>
		<div style='display:table-cell;vertical-align:middle;'>
			<h1>EcoCloud Admin</h1>
			<br>
			<hr>
			<h4>Sections</h4>
			<ul>
				<li><a href="manage.php?parentPool=15000001&type=schedule">manage schedule</a></li>
				<li><a href="manage.php?parentPool=15000004&type=posters">manage posters</a></li>
				<li><a href="manage.php?parentPool=15000005&type=participants">manage participants</a></li>
				<li><a href="manage.php?parentPool=15000006&type=labs">manage labs</a></li>
				<li><a href="manage.php?parentPool=15000007&type=affiliates">manage affiliates</a></li>
				<li><a href="manage.php?parentPool=15000008&type=venue">manage venue</a></li>
			</ul>
			<hr>
		</div>
	</div>
	

</div>


</body>


</html>
