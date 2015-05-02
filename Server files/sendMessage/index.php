<?php

	require '../library/dbConnection.php';
	require '../library/gcm.php';
	
	// check if there is parameter
	if( isset($_POST['to_user']) && $_POST['to_user'] != '' &&
            isset($_POST['message']) && $_POST['message'] != '' &&
            isset($_COOKIE['mac_address']) && $_COOKIE['mac_address'] != '') {
            
		$to_user = $_POST['to_user'];
		$message = $_POST['message'];
		$mac_address = $_COOKIE['mac_address'];
		$user_id = '';
		if(isset($_COOKIE['session_id']) && $_COOKIE['session_id'] != '') {
			$session_id = $_COOKIE['session_id'];
			$sql = $db->prepare('select user_id from session where session_id = ?');
			$sql->bind_param('s', $session_id);
			$sql->execute();
			$result = $sql->get_result();
		
			if ($result->num_rows == 1) {
				$row = mysqli_fetch_array($result);
				$user_id = $row['user_id'];
			}
		}
                
		// Find out if the users are friends
		$sql = 'select * from friends where (user1 = '.$user_id.' and user2 = '.$to_user.') or (user2 = '.$user_id.' and user1 = '.$to_user.')';
		if ($db->query($sql)->num_rows > 0) {
			$sql = 'insert into chat_messages (message, from_user, to_user, is_read) values ("'.$message.'", '.$user_id.', '.$to_user.', 0)';

			if (mysqli_query($db, $sql)) {
				echo "New record created successfully";
			} else {
				echo "Error: " . $sql . "<br>" . mysqli_error($db);
			}
			
			// notification part to inform poster
			$sql = $db->prepare('select android_registration_id
								from account
								where id = ?
								union all
								select display_name
								from account
								where id = ?');
			$sql->bind_param('ss', $to_user, $user_id);
			$sql->execute();
			$result = $sql->get_result();
			
			// prepare registration id to be notified
			$registration_id_array = array();
			
			$row = mysqli_fetch_array($result);
			$registration_id_array[] = $row['android_registration_id'];
			
			$row = mysqli_fetch_array($result);
			$display_name = $row['android_registration_id'];
			
			$display_text = $display_name.': '.$message;
			
			// prepare message
			$message = array('type' => 'got_chat_message',
							'from_user' => $user_id,
							'content' => $display_text);
			
			// send notification
			$gcm = new GCM();
			$gcm->send_notification($registration_id_array, $message);
		}
    }
?>