<?php
	require '../library/dbConnection.php';
	
	// check if there is parameter
	if( (isset($_POST['post_id']) && $_POST['post_id'] != '') &&
		(isset($_POST['rating_score']) && $_POST['rating_score'] != '') &&
		(isset($_POST['new_score']) && $_POST['new_score'] != '') &&
		(isset($_COOKIE['mac_address']) && $_COOKIE['mac_address'] != '') ) {
		
		$post_id = $_POST['post_id'];
		$rating_score = $_POST['rating_score'];
		$new_score = $_POST['new_score'];
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
		
		// check if already rate or not
		$sql = $db->prepare('select 1 from post_rating where post_id = ? and (user_id = ? or mac_address = ?)');
		$sql->bind_param('sss', $post_id, $user_id, $mac_address);
		$sql->execute();
		$result = $sql->get_result();
		if($result->num_rows > 0) {
			// if existed, update
			$sql = $db->prepare('update post_rating set score = ? where post_id = ? and (user_id = ? or mac_address = ?)');
			$sql->bind_param('ssss', $rating_score, $post_id, $user_id, $mac_address);
		}
		else {
			// if not existed, create
			if(strlen($user_id) > 0) {
				// save by user id
				$sql = $db->prepare('insert into post_rating(user_id, post_id, score) values(?, ?, ?)');
				$sql->bind_param('sss', $user_id, $post_id, $rating_score);
			}
			else {
				// save by mac address
				$sql = $db->prepare('insert into post_rating(mac_address, post_id, score) values(?, ?, ?)');
				$sql->bind_param('sss', $mac_address, $post_id, $rating_score);
			}
		}
		
		$sql->execute();
		
		$sql = $db->prepare('update post set post_score = ? where id = ?');
		$sql->bind_param('ss', $new_score, $post_id);
		$sql->execute();
		
		if($sql) {
			// commit
			mysqli_commit($db);
			$response['status'] = true;
		}
		else {
			$response['status'] = false;
			$response['error_msg'] = 'Unable to add comment, please try again';
		}
	}
	else {
		$response['status'] = false;
		$response['error_msg'] = 'Incomplete input data';
	}
	
	echo json_encode($response);
?>