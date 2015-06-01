<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>EcoCloud :: Posters</title>

</head>






<body style="text-align:center">


<div id=main_div>
<form action="?" method="post">

	<div style='margin-top:50px;'>
		<b>EcoCloud event 2015 :: Poster QR code download page</b>
	</div>
	
	<div style='height:400px;display:table;margin:auto;'>
		<div style='display:table-cell;vertical-align:middle;'>






                        Choose your poster from the dropdown below, in order to display the corresponding QR code.<br>
                        Copy the image and embed it in your poster, in the provided placeholder.<br>
                        Alternatively you can right-click the link, and select "save as".<br>
                        <br>
                        <br>
                        <table align=center>
                                <tr>
                                        <td>Poster</td>
                                        <td style="text-align:center;">
                                                <select style="width:400px;" onchange="set_image(this.value)" name="poster">
                                                        <option value="" selected disabled> - - </option>

<?php


                                $posters = file_get_contents("EcoCloudPosters.txt");

                                $posters = explode("\n", trim($posters));

                                foreach($posters as $p) {
                                    $p = explode("\t", $p);
                                    echo "<option value=\"{$p[0]}\">{$p[1]}</option>\n";
                                }

?>

                                        </td>
                                </tr>
                                <tr>
                                        <td>QR code</td>
                                        <td style="text-align:center;"><img style="width:200px;height:200px;border:1px solid;" id="qr_image"></td>
                                </tr>
                                <tr>
                                        <td>Link</td>
                                        <td style="text-align:center;"><span id="qr_link" ></span></td>
                                </tr>
                        </table>









		</div>
	</div>
	
	<div style='margin-bottom:50px;'>
		
	</div>

</form>
</div>

<p>For your poster, you must use <a href="poster.ai">this Adobe Illustrator EcoCloud poster template</a>, <a href="poster.pptx">this PowerPoint poster template</a>, or  
<a href="poster.png">this PNG file that you can use as background (300 dpi)</a>.<br/> These files show you where to place the QR code.<br/>Make sure that the QR code completely covers the red square!</p>
<script>
    function set_image(iid) {
        url = "http://chart.apis.google.com/chart?cht=qr&chs=200x200&chl=pocketcampus://events.plugin.pocketcampus.org/showEventPool?eventPoolId=23000070%26markFavorite=" + iid;
        document.getElementById("qr_image").src=url;
        document.getElementById("qr_link").innerHTML='<a href="' + url + '" target="_blank">download</a>';
    }
//document.getElementById("qr_image").focus();
</script>

</body>


</html>
