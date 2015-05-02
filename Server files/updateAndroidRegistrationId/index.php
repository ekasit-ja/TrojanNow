<?php
	require '../library/dbConnection.php';
	
	// check if there is parameter
	if( (isset($_POST['android_registration_id']) && $_POST['android_registration_id'] != '') &&
		(isset($_COOKIE['session_id']) && $_COOKIE['session_id'] != '') ) {
		
		$android_registration_id = $_POST['android_registration_id'];		
		$session_id = $_COOKIE['session_id'];
		
		$sql = $db->prepare('update account
							set android_registration_id = ?
							where id = (select user_id
										from session
										where session_id = ?)');
		$sql->bind_param('ss', $android_registration_id, $session_id);
		$sql->execute();
		
		if($sql) {
			// commit
			mysqli_commit($db);
			$response['status'] = true;
		}
		else {
			$response['status'] = false;
			$response['error_msg'] = 'Unable to update android registration id, please try again';
		}
	}
	else {
		$response['status'] = false;
		$response['error_msg'] = 'Incomplete input data';
	}
	
	echo json_encode($response);
?>