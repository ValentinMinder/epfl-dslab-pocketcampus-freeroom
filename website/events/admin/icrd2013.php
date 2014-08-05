<?php

require_once("functions.php");
require_once("php_headers.php"); // will set $conn to database




?><html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Manage IC Research Day 2013</title>

</head>






<body style="text-align:center">


<div id=main_div>


	
	<div style='height:600px;width:600px;display:table;margin:auto;'>
		<div style='display:table-cell;vertical-align:middle;'>
			<h1>Manage IC Research Day 2013</h1>
			<br>
			<hr>
			<h4>Sections</h4>
			<ul>
				<li><a href="manage.php?parentPool=16000001&type=schedule">manage schedule</a></li>
				<li><a href="manage.php?parentPool=16000003&type=posters">manage posters</a></li>
				<li><a href="manage.php?parentPool=16000004&type=participants">manage participants</a></li>
				<li><a href="manage.php?parentPool=16000005&type=labs">manage labs</a></li>
				<li><a href="manage.php?parentPool=16000009&type=research">manage research areas</a></li>
				<li><a href="manage.php?parentPool=16000007&type=info">manage useful info</a></li>
			</ul>
			<hr>
			<h4>Export</h4>
			<ul>
				<li><a href="export.php?parentPool=16000003&type=posters">export posters</a></li>
			</ul>
			<hr>
		</div>
	</div>
	

</div>


</body>


</html>
