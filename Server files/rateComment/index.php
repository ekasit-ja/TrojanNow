<?php
	require '../library/dbConnection.php';
	
	// check if there is parameter
	if( (isset($_POST['comment_id']) && $_POST['comment_id'] != '') &&
		(isset($_POST['rating_score']) && $_POST['rating_score'] != '') &&
		(isset($_POST['new_score']) && $_POST['new_score'] != '') &&
		(isset($_COOKIE['mac_address']) && $_COOKIE['mac_address'] != '') ) {
		
		$comment_id = $_POST['comment_id'];
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
		$sql = $db->prepare('select 1 from comment_rating where comment_id = ? and (user_id = ? or mac_address = ?)');
		$sql->bind_param('sss', $comment_id, $user_id, $mac_address);
		$sql->execute();
		$result = $sql->get_result();
		if($result->num_rows > 0) {
			// if existed, update
			$sql = $db->prepare('update comment_rating set score = ? where comment_id = ? and (user_id = ? or mac_address = ?)');
			$sql->bind_param('ssss', $rating_score, $comment_id, $user_id, $mac_address);
		}
		else {
			// if not existed, create
			if(strlen($user_id) > 0) {
				// save by user id
				$sql = $db->prepare('insert into comment_rating(user_id, comment_id, score) values(?, ?, ?)');
				$sql->bind_param('sss', $user_id, $comment_id, $rating_score);
			}
			else {
				// save by mac address
				$sql = $db->prepare('insert into comment_rating(mac_address, comment_id, score) values(?, ?, ?)');
				$sql->bind_param('sss', $mac_address, $comment_id, $rating_score);
			}
		}
		
		$sql->execute();
		
		$sql = $db->prepare('update comment set comment_score = ? where id = ?');
		$sql->bind_param('ss', $new_score, $comment_id);
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