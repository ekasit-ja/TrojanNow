<?php
	require '../library/dbConnection.php';
	
	// check if there is parameter
	if( (isset($_POST['email']) && $_POST['email'] != '') &&
		(isset($_POST['password']) && $_POST['password'] != '') &&
		(isset($_COOKIE['mac_address']) && $_COOKIE['mac_address'] != '') &&
		(isset($_POST['display_name']) && $_POST['display_name'] != '') ) {
		
		// check if email is existed or not		
		$sql = $db->prepare('select email from account where email = ?');
		$sql->bind_param('s', $_POST['email']);
		$sql->execute();
		$result = $sql->get_result();
		
        if ($result->num_rows > 0) {
            // user existed 
            $isUserExisted = true;
        }
		else {
            // user not existed
            $isUserExisted = false;
        }
		
		if($isUserExisted) {
			$response['status'] = false;
			$response['error_msg'] = 'This email is existed in server, please choose another one';
		}
		else {
			
			// qualify parameters
			$email = strtolower(trim($_POST['email']));
			$password = trim($_POST['password']);
			$mac_address = strtolower(trim($_COOKIE['mac_address']));
			$display_name = trim($_POST['display_name']);		
			
			// query database
			$sql = $db->prepare('insert into account(email, password, mac_address, display_name) values(?, ?, ?, ?)');
			$sql->bind_param('ssss', $email, $password, $mac_address, $display_name);
			$sql->execute();			
			
			if($sql) {
				// commit
				mysqli_commit($db);
				
				$response['status'] = true;
				$response['account']['email'] = $email;
				$response['account']['mac_address'] = $mac_address;
				$response['account']['display_name'] = $display_name;
			}
			else {
				$response['status'] = false;
				$response['error_msg'] = 'Error occured in registration';
			}
		}
	}
	else {
		$response['status'] = false;
		$response['error_msg'] = 'Incomplete input data';
	}
	
	echo json_encode($response);
?>