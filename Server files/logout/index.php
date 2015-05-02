<?php
	require '../library/dbConnection.php';
	
	// check if there is parameter
	if( (isset($_COOKIE['session_id']) && $_COOKIE['session_id'] != '') ) {
		
		$session_id = $_COOKIE['session_id'];
		
		// get user id
		$sql = $db->prepare('select user_id from session where session_id = ?');
		$sql->bind_param('s', $session_id);
		$sql->execute();
		$result = $sql->get_result();
		
		$user_id;
		if($result->num_rows == 1) {
			$row = mysqli_fetch_array($result);
			$user_id = $row['user_id'];
		}
		
		// delete session
		$sql = $db->prepare('delete from session where session_id = ?');
		$sql->bind_param('s', $session_id);
		$sql->execute();
		
		// delete registration id value
		$sql = $db->prepare('update account set android_registration_id = "" where id = ?');
		$sql->bind_param('s', $user_id);
		$sql->execute();
				
		if($sql) {
			// logout succeed
			// commit
			mysqli_commit($db);
			$response['status'] = true;
		}
		else {
			// insert fail
			$response['status'] = false;
			$response['error_msg'] = 'Error occured signing out, please try again.';
		}
		
	}
	else {
		$response['status'] = true;
	}
	
	echo json_encode($response);
?>