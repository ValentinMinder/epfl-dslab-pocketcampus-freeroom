<?php

include_once "vars.php";

?>
<html>
<body>

<h2>Welcome <?php echo $team; ?></h2>


<ul>
<li><a href="upload_jar.php">upload jar</a></li>
<li><a href="upload_config.php">upload config</a></li>
<li><a href="restart_server.php">restart server</a></li>
</ul>
<ul>
<li><a href="server_status.php">server status</a></li>
<li><a href="server_logs.php">server logs</a></li>
<li><a href="tail_log.php">tail -f log</a></li>
</ul>


</body>
</html>
