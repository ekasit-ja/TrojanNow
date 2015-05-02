<?php
	require '../library/dbConnection.php';
	
	// check if there is parameter
	if( (isset($_POST['email']) && $_POST['email'] != '') &&
		(isset($_POST['password']) && $_POST['password'] != '') &&
		(isset($_COOKIE['mac_address']) && $_COOKIE['mac_address'] != '') ) {
		
		// check if email is existed or not
		$sql = $db->prepare('select * from account where email = ? and password = ?');
		$sql->bind_param('ss', $_POST['email'], $_POST['password']);
		$sql->execute();
		$resultAccount = $sql->get_result();
		
        if ($resultAccount->num_rows == 1) {
            // correct authentication
            // check if session is existed
			$rowAccount = mysqli_fetch_array($resultAccount);
			$display_name = $rowAccount['display_name'];
			$sql = $db->prepare('select * from session where user_id = ? and mac_address = ?');
			$sql->bind_param('ss', $rowAccount['id'], $_COOKIE['mac_address']);
			$sql->execute();
			$resultSession = $sql->get_result();
			
			if($resultSession->num_rows > 0) {
				// update session id
				$session_id = md5($rowAccount['id'] . $_COOKIE['mac_address'] . rand());
				$sql = $db->prepare('update session set session_id = ? where user_id = ? and mac_address = ?');
				$sql->bind_param('sss', $session_id, $rowAccount['id'], $_COOKIE['mac_address']);
				$sql->execute();
				
				if($sql) {
					mysqli_commit($db);
					$response['status'] = true;
					$response['display_name'] = $display_name;
					setcookie('session_id', $session_id);
				}
				else {
					$response['status'] = false;
					$response['error_msg'] = 'Error occured signing in, please try again.';
				}
			}
			else {
				// create new session id
				$session_id = md5($rowAccount['id'] . $_COOKIE['mac_address'] . rand());
				$sql = $db->prepare('insert into session(user_id, session_id, mac_address) values(?, ?, ?)');
				$sql->bind_param('sss', $rowAccount['id'], $session_id, $_COOKIE['mac_address']);
				$sql->execute();
				
				if($sql) {
					// login succeed
					// commit
					mysqli_commit($db);
					$response['status'] = true;
					$response['display_name'] = $display_name;
					setcookie('session_id', $session_id);
				}
				else {
					// insert fail
					$response['status'] = false;
					$response['error_msg'] = 'Error occured signing in, please try again.';
				}
			}
			
        }
		else {
            // incorrect email or password
            $response['status'] = false;
			$response['error_msg'] = 'Incorrect email or password, please try again.';
        }
		
	}
	else {
		$response['status'] = false;
		$response['error_msg'] = 'Incomplete input data';
	}
	
	echo json_encode($response);
?>