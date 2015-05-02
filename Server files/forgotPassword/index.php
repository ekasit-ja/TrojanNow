<?php
	require '../library/dbConnection.php';
	
	// check if there is parameter
	if(isset($_GET['email'])) {
		// qualify parameter
		$email = strtolower(trim($_GET['email']));
		
		// query database
		$sql = $db->prepare('select password from account where lcase(email)=?');
		$sql->bind_param('s', $email);
		$sql->execute();
		$result = $sql->get_result();
		
		if($result->num_rows == 1) {
			$row = mysqli_fetch_array($result);
			
			// send email
			$to = $email;
			$subject = 'Auto-generated email from TrojanNow';
			$message = '*** Do not reply this email ***
this email has been sent because we receive a reqeust help forgot password with this email address
please ignore it if you have not done such request.

Your password is ' . $row['password'] . '.';
			$headers = 'From: admin@kanitsr.com';
			$server = 'kanitsr.com';
			
			ini_set('SMTP', $server);
			
			mail($to, $subject, $message, $headers);
		}
		
	}
?>