<?php

include_once "vars.php";


function get_latest($team, $thing) {
        $files = glob("bin/pocketcampus-server-$team-*-*.$thing");
        sort($files);
        return basename(array_pop($files));
}


$logfile = get_latest($team, "log");

$logfile or die("No log file");

require 'PHPTail.php';
/**
 * Initilize a new instance of PHPTail
 * @var PHPTail
 */
$tail = new PHPTail("bin/$logfile");

/**
 * We're getting an AJAX call
 */
if(isset($_GET['ajax']))  {
        echo $tail->getNewLines($_GET['lastsize'], $_GET['grep'], $_GET['invert']);
        die();
}
/**
 * Regular GET/POST call, print out the GUI
 */
$tail->generateGUI();

