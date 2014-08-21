<?php

if(empty($_GET["id"]))
	exit;


header("Location: pocketcampus://eventsurvey.plugin.pocketcampus.org/showForm?formId={$_GET["id"]}");


?>
