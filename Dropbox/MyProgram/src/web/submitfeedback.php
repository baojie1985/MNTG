<?php
$name = $_GET["name"];
$name = str_replace("'","\'",$name);

$email = $_GET["email"];
$email = str_replace("'","\'",$email);

$msg = $_GET["msg"];
$msg = str_replace("'","\'",$msg);

$con = mysql_connect("localhost", "traffic", "thetrafficgenerator");
if (!$con)
{ 
}
else {
	mysql_select_db("traffic", $con);
}
$query = "INSERT INTO feedback (name, email, feedback) VALUES ('".$name."','".$email."','".$msg."')";

if (!mysql_query($query,$con)) {
	//die(mysql_error());
}
else
	mysql_query('commit',$con);

$headers = 'From: '.$email."\r\n".
'Reply-To: '.$email."\r\n" .
'X-Mailer: PHP/' . phpversion();
if(mail('amr@cs.umn.edu', 'MinnesotaTG Feedback', $msg, $headers))
	echo "Email Sent<br/>";

echo $name." FROM ".$email." SAID ".$msg."<br/>";

error_reporting(E_ALL|E_STRICT);
ini_set('display_errors', 1);
echo 'I am : ' . `whoami`;
$result = mail('amr@cs.umn.edu','Testing 1 2 3','This is a test.', $headers);
echo '<hr>Result was: ' . ( $result === FALSE ? 'FALSE' : 'TRUE') . $result;
echo '<hr>';
echo phpinfo();

?>