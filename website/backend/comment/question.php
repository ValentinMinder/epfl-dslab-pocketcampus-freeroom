<?php

define("POST_NAME", "name");

// require codebird
require_once('codebird.php');

$didTweet = false;
$questionError = false;

if(isset($_POST['question'])) {
    $question = $_POST['question'];
    if (strlen($question) > 0) {
        \Codebird\Codebird::setConsumerKey("Nm6VfYfd5i1x68ZcDsVxNc34f", "mDEdN9kHYk1T8eL4du51eqUjJbxoqImTGuofvEnM6lNIM2fu8u");
        $cb = \Codebird\Codebird::getInstance();
        $cb->setToken("2976691042-sr9sWJUZcoQhfusZytvf79PVA1V9OdeOPjCP58N", "ojNmS535XMCyTmWXYusahFafVUIclxekeCnyR11fIz1Ok");
        
        $tweet = $question;
        if (isset($_POST['name'])) {
            $name = $_POST['name'];
            if (strlen($name) > 0) {
                $tweet = $name.": ".$question;
            }
        }
        
        $params = array(
          'status' => $tweet
        );
        $reply = $cb->statuses_update($params);
        $didTweet = true;
    } else {
        $questionError = true;
    }
} 

?>


<html>
	<head>
	<meta charset='utf-8'>
	<meta name='viewport' content='width=device-width; initial-scale=1.0; maximum-scale=1.0;'>
	<style type='text/css'>
		html {
            -webkit-text-size-adjust: none; /* prevent text from being resized in landscape */
            font-family: sans-serif;
        }
        
        .top_message {
            font-size: 12pt;
            border-radius: 5px;
            text-align: center;
            width: 100%;
            padding-top: 5px;
            padding-bottom: 5px;
            margin-bottom: 10px;
        }
        
        #question_successfull {
            background-color: #00b434;
            color: white;
        }
        
        #question_error {
            background-color: #ff4949;
            color: white;
        }
		
		form {
    		display: block;
    		margin: auto;
    		width: 95%;
    		height: 95%;
    		max-width: 800px;
    		border: 0px solid black;
		}
		
		form>input {
    		min-height: 40px;
            font-size: 13pt;
		}
		
		input#name {
    		width: 100%;
		}
		
		textarea#question {
    		font-size: 13pt;
    		width: 100%;
    		height: 50%;
    		margin-top: 20px;
		}
		
		input#submit {
    		display: block;
    		margin: auto;
    		margin-top: 20px;
		}
		
	</style>
	</head>
<body>
    <form action="question.php" method="post">
        <div class="top_message" id="question_successfull" style="display: <?php if ($didTweet) { echo "block"; } else { echo "none"; } ?>">Your question was successfully posted</div>
        <div class="top_message" id="question_error" style="display: <?php if ($questionError) { echo "block"; } else { echo "none"; } ?>">The question cannot be empty</div>
        <input id="name" type="text" name="name" placeholder="Your name (optional)" value="<?php echo $_POST['name']; ?>">
        <br>
        <textarea id="question" name="question" placeholder="Your question"></textarea>
        <br>
        <input id="submit" type="submit" value="Send the question">
    </form>
</body>
</html>
