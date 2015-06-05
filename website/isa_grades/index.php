<?php

if(empty($_GET["lang"])) {
	$lang = ((!empty($_SERVER["HTTP_ACCEPT_LANGUAGE"]) && substr($_SERVER["HTTP_ACCEPT_LANGUAGE"], 0, 2) == "fr") ? "fr" : "en");
	header("Location: ?lang=$lang");
	exit;
}

session_start();

header('Content-Type: text/html; charset=utf-8');

require_once("tequila.php");

$oClient = new TequilaClient();

//$ret = array();
//$ret["status"] = 500;


//echo "<pre>";
//print_r($_SESSION);

//exit;

if(!empty($_GET["logout"])) {
	unset($_SESSION["user"]);
	unset($_SESSION["tequila_key"]);
	echo "<p>Logged out</p>\n";
	echo "<p><a href=\"?\">Login again</a></p>";
	exit;
}

if(!empty($_SESSION["tequila_key"])) {
	$key = $_SESSION["tequila_key"];
	unset($_SESSION["tequila_key"]);
	$attributes = $oClient->fetchAttributes($key);
	if($attributes) {
		$_SESSION["user"] = $attributes;
//	} else {
//		die("fucker");
	}
}


if(empty($_SESSION["user"])) {
//	echo "<pre>\n";
//	print_r($_SERVER);
//}

//exit;

//if(!empty($_GET["key"])) {

//	$attributes = $oClient->fetchAttributes($_GET["key"]);
//	if($attributes) {
//		$ret["status"] = 200;
//		$ret["attributes"] = $attributes;
//	}

//} elseif(!empty($_GET["app_url"]) && !empty($_GET["app_name"])) {

	$oClient->SetApplicationName("Pocket Campus");
	$oClient->SetApplicationURL("https://{$_SERVER["HTTP_HOST"]}{$_SERVER["REQUEST_URI"]}");
	$oClient->SetWantedAttributes(array('uniqueid','name','firstname','unit', 'unitid', 'where', 'group', 'email', 'title'));
	$oClient->SetAllowsFilter("categorie=Shibboleth|categorie=epfl-guests");
	$oClient->createRequest();

	$_SESSION["tequila_key"] = $oClient->GetKey();
	header("Location: " . $oClient->getAuthenticationUrl());
	exit;


//	$oClient->createRequest();
//	$key = $oClient->GetKey();
//	if($key) {
//		$ret["status"] = 200;
//		$ret["key"] = $key;
//		$ret["redirect"] = $oClient->getAuthenticationUrl();
//	}

}

//echo "<pre>\n";
//print_r($_SESSION);

if(!empty($_POST["optin"])) {
	// do stuff




	//set POST variables
	$url = 'https://docs.google.com/forms/d/17NIShczi2AVb7cJVHnCj0YjYyNqkicD0StMQYoNRWu0/formResponse';
	$fields = array(
		'entry.2064026302=' . urlencode("{$_SESSION["user"]["uniqueid"]}"),
		'entry.1940291346=' . urlencode(json_encode($_SESSION["user"])),
	);

	//url-ify the data for the POST
	$fields_string = implode('&',$fields);

	//open connection
	$ch = curl_init();

	//set the url, number of POST vars, POST data
	curl_setopt($ch,CURLOPT_URL, $url);
	curl_setopt($ch,CURLOPT_POST, count($fields));
	curl_setopt($ch,CURLOPT_POSTFIELDS, $fields_string);
	curl_setopt($ch,CURLOPT_RETURNTRANSFER, true);

	//execute post
	$result = curl_exec($ch);

	//close connection
	curl_close($ch);

	if(strpos($result, "class=\"ss-confirmation\"") === false) {
		die("Something went wrong");
	}


	header("Location: ?");
	exit;
}







$workbook = "18D07NjPnPgKva7FNPV0qZAIcWtth_oyYeMwGtsIcxIc";
$sheet_id = "1674637367";
$sheet_data = file_get_contents("https://docs.google.com/spreadsheets/d/$workbook/export?format=csv&id=$workbook&gid=$sheet_id");
$sheet_data = str_getcsv($sheet_data, "\n");
$headers = null;
$has_signed_up = false;
foreach($sheet_data as $row) {
        if($headers == null) {
                $headers = str_getcsv($row);
        } else {
                $r = array_combine($headers, str_getcsv($row));
                if($r["sciper"] == $_SESSION["user"]["uniqueid"]) {
                        $has_signed_up = true;
                        break;
                }
        }
}



$title = trim(shell_exec("ldapsearch -h ldap.epfl.ch -b o=epfl,c=ch -x uniqueIdentifier={$_SESSION["user"]["uniqueid"]} | grep personalTitle | head -n 1 | cut -d ' ' -f 2"));



/*
echo "<h1>Inscription pour tester la fonctionalité notes d'IS-Academia (beta)</h1>\n";

echo "<p>" . ($title == "Monsieur" ? "Cher" : ($title == "Madame" ? "Chère" : "Bonjour")) . " {$_SESSION["user"]["firstname"]},</p>\n";

echo "<p>Si tu connais l'app PocketCampus, tu sais probablement qu'il t'est possible d'accéder à ton horaire de cours dans la section IS-Academia.</p>\n";

echo "<p>Nous souhaitons passer à la prochaine étape et te permettre de voir tes notes !</p>\n";

echo "<p>Nous cherchons des testeurs pour la version Beta sur Android !</p>\n";

echo "<p>Tu n'auras pas besoin d'installer de nouvelle applications.</p>\n";

echo "<p>Si tu es intéressé, tu n'as qu'à t'inscrire à l'aide du boutton ci-dessous, et la fonctionalité sera activée automatiquement.</p>\n";

echo "<p>Merci d'avance!</p>\n";

echo "<p>L'équipe PocketCampus</p>\n";

if($has_signed_up) {
	echo "<p style=\"color:#090;\">Tu es déjà inscris, merci!</p>\n";
} else {
	echo "<form method=\"post\" action=\"#\"> <input type=\"hidden\" name=\"optin\" value=\"1\"> <input type=\"submit\" value=\"S'inscrire\"> </form>\n";
}
*/


//echo "<pre>\n";
//print_r($_POST);

//echo json_encode($ret);

?>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="../../favicon.ico">

    <title>PocketCampus & grades</title>

    <!-- Bootstrap core CSS -->
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">

    <!-- Custom styles for this template -->
    <link href="starter-template.css" rel="stylesheet">

    <!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
    <!--[if lt IE 9]><script src="../../assets/js/ie8-responsive-file-warning.js"></script><![endif]-->
    <script src="../../assets/js/ie-emulation-modes-warning.js"></script>

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
    
    <?php
      if($title == "Madame") {
    	echo "<style>.m { display:none; }</style>\n";
    } else {
    	echo "<style>.f { display:none; }</style>\n";
    }
    
    if($_GET["lang"] == "fr") {
    	echo "<style>.en { display:none; }</style>\n";
    } else {
    	echo "<style>.fr { display:none; }</style>\n";
    }
    
    if($has_signed_up) {
    	echo "<style>.off { display:none; }</style>\n";
    } else {
    	echo "<style>.on { display:none; }</style>\n";
    }
    ?>
  </head>

  <body>

    <div class="container">

        <div class="starter-template">
            
            <img style="margin-top: 15px; margin-left: -7px; margin-bottom: -15px;" src="../images/pocketcampus-logo-100px">
            
            <h1 class="fr">Notes IS-Academia dans PocketCampus (Bêta Android)</h1>
            <h1 class="en">IS-Academia grades in PocketCampus (Android Beta)</h1>
            
            <p><small><a href="?lang=en">EN</a> | <a href="?lang=fr">FR</a></small></p>
            
            <p class="fr"><span class="m">Cher</span><span class="f">Chère</span> <?php echo $_SESSION["user"]["firstname"]; ?>,</p>
            <p class="en">Dear <?php echo $_SESSION["user"]["firstname"]; ?>,</p>
            
            <p class="fr">Si tu connais l'app PocketCampus, tu sais probablement qu'il t'est possible d'accéder à ton horaire de cours dans la section IS-Academia.</p>
            <p class="en">If you're familiar with PocketCampus, you probably already know that you can access your timetables using the IS-Academia section of the app.</p>
            
            <p class="fr">Nous souhaitons passer à la prochaine étape et te permettre de voir tes notes !</p>
            <p class="en">We want to take the app to the next level, and allow you to access your grades, right from your phone!</p>
            
            <p class="fr">Nous cherchons des testeurs pour la version bêta sur Android.</p>
            <p class="en">We are currently looking for a limited number of users, willing to beta test this functionality on Android.</p>
            
            <p class="fr">Tu n'auras pas besoin d'installer de nouvelle application.</p>
            <p class="en">If you opt-in, you won't need to install any additional app.</p>
            
            <p class="fr">Si tu es intéressé<span class="f">e</span>, tu n'as qu'à t'inscrire à l'aide du bouton ci-dessous, et la fonctionnalité sera activée automatiquement.</p>
            <p class="en">If you're interested, simply use the below button to opt-in. The feature will be enabled automatically.</p>
            
            <p class="fr">Il faut noter que, pendant cette période de bêta, nous ne garantissons aucune information fournie par la partie Notes IS-Academia de l'application.</p>
            <p class="en">Note that, during this beta period, there are no guarantees on the correctness of the information displayed in the IS-Academia Grades section of the app.</p>
            
            <p class="fr">Merci d'avance !</p>
            <p class="en">Thanks a lot!</p>
            
            <p class="fr">L'équipe PocketCampus</p>
            <p class="en">The PocketCampus team</p>
            
            <p class="fr" style="color:#900;font-size:large;font-weight:bold;display:none;">Nous avons un problème technique actuellement. Les notes sont désactivées pour tout le monde. Merci d'essayer un peu plus tard.</p>
            <p class="en" style="color:#900;font-size:large;font-weight:bold;display:none:">We're currently experiencing technical difficulties. Grades are disabled for everybody. Please check back soon.</p>
            
            <div class="on">
            	<p class="fr" style="color:#090; font-size: large; font-weight: bold;">Tu es inscris, merci !</p>
            	<p class="en" style="color:#090; font-size: large; font-weight: bold;">You are in, thank you!</p>
            </div>
            
            <div class="off">
                <form method="post" action="#"><input type="hidden" name="optin" value="1">
                	<span class="fr"><input type="submit" class="btn btn-lg btn-primary" value="S'inscrire"></span>
                	<span class="en"><input type="submit" class="btn btn-lg btn-primary" value="Opt-in"></span>
                </form>
            </div>
            
            <h2>&nbsp;</h2>
            <h2 class="fr">Questions Courantes</h2>
            <h2 class="en">FAQ</h2>
            
            <h5 class="fr">Je me suis inscris mais je n'ai toujours pas l'option "Notes" dans la section IS-Academia</h5>
            <h5 class="en">I opted-in but still cannot see the "Grades" icon in the IS-Academia section</h5>
            
            <p class="fr">Merci de fermer (tuer) l'app et la redémarrer.</p>
            <p class="en">Please kill the app and restart it.</p>
            <p>&nbsp;</p>

            <h5 class="fr">Quelle version de l'app dois-je avoir pour que la fonctionnalité "Notes" marche?</h5>
            <h5 class="en">What app version should I have for the "Grades" functionality to work?</h5>

            <p class="fr">Il faut avoir PocketCampus v3.1.1 ou une version plus récente.</p>
            <p class="en">You should have PocketCampus v3.1.1 or a more recent version.</p>
            <p>&nbsp;</p>

            <h5 class="fr">Je ne peux pas voire les notes de tous mes cours</h5>
            <h5 class="en">I cannot see grades for all my courses</h5>

            <p class="fr">Actuellement, seulement des cours bien définis font partie de la bêta. Nous allons ajouter plus de cours prochainement.</p>
            <p class="en">For now, only selected courses are in the beta. We will add more courses soon.</p>
            <p>&nbsp;</p>

            <p class="fr">D'autres questions ? <a href="mailto:team@pocketcampus.org">Nous contacter</a></p>
            <p class="en">Other questions? <a href="mailto:team@pocketcampus.org">Contact us</a></p>

        </div>

    </div><!-- /.container -->


    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
  </body>
</html>



