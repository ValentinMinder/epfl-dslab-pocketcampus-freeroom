<?php
$allowedExts = array("gif", "jpeg", "jpg", "png");
$temp = explode(".", $_FILES["file"]["name"]);
$extension = end($temp);
if ((($_FILES["file"]["type"] == "image/gif")
|| ($_FILES["file"]["type"] == "image/jpeg")
|| ($_FILES["file"]["type"] == "image/jpg")
|| ($_FILES["file"]["type"] == "image/pjpeg")
|| ($_FILES["file"]["type"] == "image/x-png")
|| ($_FILES["file"]["type"] == "image/png"))
&& ($_FILES["file"]["size"] < 2000000)
&& in_array($extension, $allowedExts))
  {
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

//    if (file_exists("upload/" . $_FILES["file"]["name"]))
//      {
//      echo $_FILES["file"]["name"] . " already exists. ";
//      }
//    else
//      {
      $new_file = basename($_FILES["file"]["tmp_name"]) . ".$extension";
      move_uploaded_file($_FILES["file"]["tmp_name"],
      "images/$new_file");// . $_FILES["file"]["name"]);
      echo "<h2>Uploaded to (copy the URL below to PictureURL_IF_NOT_FROM_EPFL column in spreadsheet)</h2> <p>https://test-pocketcampus.epfl.ch/events/openhouse2015/images/$new_file</p>";// . $_FILES["file"]["name"];
      echo "<h2>Preview</h2> <p><iframe src=\"https://test-pocketcampus.epfl.ch/events/openhouse2015/images/$new_file\" style=\"width:300px;height:300px;\"></iframe></p>";
//      }
    }
  }
else
  {
  echo "Invalid file";
  }
?>
