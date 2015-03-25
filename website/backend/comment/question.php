<?php

define("POST_NAME", "name");
define("POST_QUESTION", "question");
define("POST_TAG", "tag");
define("TWEET_MAX_LENGTH", 140);

// require codebird
require_once('codebird.php');

$didTweet = false;
$questionError = false;

if(isset($_POST[POST_QUESTION])) {
    $question = $_POST[POST_QUESTION];
    $question = str_replace("\r\n", "\n", $question);
    if (mb_strlen($question, 'UTF-8') > 0) {
        \Codebird\Codebird::setConsumerKey("Nm6VfYfd5i1x68ZcDsVxNc34f", "mDEdN9kHYk1T8eL4du51eqUjJbxoqImTGuofvEnM6lNIM2fu8u");
        $cb = \Codebird\Codebird::getInstance();
        $cb->setToken("2976691042-sr9sWJUZcoQhfusZytvf79PVA1V9OdeOPjCP58N", "ojNmS535XMCyTmWXYusahFafVUIclxekeCnyR11fIz1Ok");
        
        $tweet = $question;
        if (isset($_POST[POST_NAME])) {
            $name = $_POST[POST_NAME];
            if (strlen($name) > 0) {
                $tweet = $name.": ".$question;
            }
        }
        
        if (mb_strlen($tweet, 'UTF-8') <= TWEET_MAX_LENGTH) {
            $params = array(
                'status' => $tweet
            );
            $reply = $cb->statuses_update($params);
            $didTweet = true;
        } else {
            $questionError = true;
        }
    } else {
        $questionError = true;
    }
} 

?>


<html>
    <head>
    <meta charset='utf-8'>
    <meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0'>
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
            height: 30%;
            margin-top: 20px;
        }
        
        #nb_chars_left {
            width: 100%;
            text-align: center;
            color: gray;
            margin-top: 5px;
        }
        
        input#submit {
            display: block;
            margin: auto;
            margin-top: 0px;
        }
        
    </style>
    <script type="text/javascript">
        
        window.onload = function(){
            
            var STR_MAX_LENGTH = 140;
            
            var nameInput = document.getElementById("name");
            var questionInput = document.getElementById("question");
            var nbCharsLeftDiv = document.getElementById("nb_chars_left");
            var submitInput = document.getElementById("submit");
            
            var validateInput = function () {
                var fullString = nameInput.value+questionInput.value;
                if (fullString.length <= STR_MAX_LENGTH && fullString.length > 0 && questionInput.value.length > 0) {
                    submit.removeAttribute("disabled");
                } else {
                    submit.setAttribute("disabled", "1");
                }
                var charsLeft = (STR_MAX_LENGTH - fullString.length);
                if (nameInput.value.length > 0) {
                    charsLeft -= 2;
                }
                nbCharsLeftDiv.innerHTML = charsLeft + " caractère(s) restant(s)";
            }
            
            nameInput.oninput = validateInput;
            questionInput.oninput = validateInput;
            
            validateInput();
        };
        
    </script>
    </head>
<body>
    <form action="question.php" method="post">
        <div class="top_message" id="question_successfull" style="display: <?php if ($didTweet) { echo "block"; } else { echo "none"; } ?>">Question envoyée avec succès</div>
        <div class="top_message" id="question_error" style="display: <?php if ($questionError) { echo "block"; } else { echo "none"; } ?>">Erreur: la question est trop longue ou vide.</div>
        <input id="name" type="text" name="name" placeholder="Votre nom (optionnel)" value="<?php echo $_POST['name']; ?>">
        <br>
        <textarea id="question" name="question" placeholder="Votre question"></textarea>
        <br>
        <div id="nb_chars_left"></div>
        <br>
        <input id="submit" type="submit" value="Envoyer la question" disabled>
    </form>
</body>
</html>
