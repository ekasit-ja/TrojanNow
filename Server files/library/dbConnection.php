<?php
	// connect to local database 'trojannow'
	$db = new mysqli('localhost','root', '123456', 'trojannow');
	
	// inform user in case of error
	if($db->connect_errno) {
		echo 'Error occurs while attempting to connect database';
		echo nl2br("\n\n");
		echo $db->connect_error;
		
		// immediately exit this script and its consecutive
		die();
	}
?>
