<?php

include_once "vars.php";

if(empty($thing))
	exit;

if ($_SERVER['REQUEST_METHOD'] === 'POST') {


  if ($_FILES["file"]["error"] > 0)
    {
    echo "Return Code: " . $_FILES["file"]["error"] . "<br>";
    }
  else
    {
//    echo "Upload: " . $_FILES["file"]["name"] . "<br>";
//    echo "Type: " . $_FILES["file"]["type"] . "<br>";
//    echo "Size: " . ($_FILES["file"]["size"] / 1024) . " kB<br>";
//    echo "Temp file: " . $_FILES["file"]["tmp_name"] . "<br>";

date_default_timezone_set("Europe/Zurich");
$d = date("YmdHis");
$u = uniqid('');

      if(move_uploaded_file($_FILES["file"]["tmp_name"],   "bin/pocketcampus-server-$team-$d-$u.$thing"))
      echo "Uploaded $thing\n";
else echo "Error moving\n";
    }
exit;
}
?>

<html>
<body>

<h2>Upload <?php echo $thing; ?></h2>


<form action="#" method="post" id="upload_form"
enctype="multipart/form-data">
<p>(upload is triggered automatically after you choose file)</p>
<!--<label for="file">Filename:</label>-->
<input type="file" name="file" id="file"><br>
<!--<input type="submit" name="submit" value="Submit">-->
</form>

<script>
document.getElementById("file").onchange = function() {
    document.getElementById("upload_form").submit();
};
</script>


</body>
</html>


