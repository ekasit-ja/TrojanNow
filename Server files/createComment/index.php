<?php
	require '../library/dbConnection.php';
	require '../library/gcm.php';
	
	// check if there is parameter
	if( (isset($_POST['post_id']) && $_POST['post_id'] != '') &&
		(isset($_POST['comment_text']) && $_POST['comment_text'] != '') &&
		(isset($_COOKIE['mac_address']) && $_COOKIE['mac_address'] != '') ) {
		
		$post_id = $_POST['post_id'];
		$comment_text = $_POST['comment_text'];
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
		
		$sql;
		if(strlen($user_id) > 0) {
			// save by user id
			$sql = $db->prepare('insert into comment(post_id, commentator_id, comment_text) values(?, ?, ?)');
			$sql->bind_param('sss', $post_id, $user_id, $comment_text);
		}
		else {
			// save by mac address
			$sql = $db->prepare('insert into comment(post_id, commentator_mac_address, comment_text) values(?, ?, ?)');
			$sql->bind_param('sss', $post_id, $mac_address, $comment_text);
		}
		
		$sql->execute();
		
		if($sql) {
			// commit
			mysqli_commit($db);
			
			$last_id = $sql->insert_id;
			$sql = $db->prepare('select c.id, c.post_id, c.comment_text, c.comment_timestamp, c.comment_score,
								ifnull(r.score,0) as user_rating
								from (	select *
										from comment
										where id = ? ) as c
								left join
									(	select comment_id, score
										from comment_rating
										where mac_address = ? or
											  user_id = ? ) as r
								on c.id = r.comment_id
								order by comment_timestamp asc, id asc');
			$sql->bind_param('sss', $last_id, $mac_address, $user_id);
			$sql->execute();
			$result = $sql->get_result();
			
			$row = mysqli_fetch_array($result);
			
			$response['status'] = true;
			$response['comment'] = $row;
			
			
			
			// notification part to inform poster
			$sql = $db->prepare('select android_registration_id
								from account
								where id = (select poster_id
											from post
											where id = ?)
											and id <> ?');
			$sql->bind_param('ss', $post_id, $user_id);
			$sql->execute();
			$result = $sql->get_result();
			
			// prepare registration id to be notified
			$registration_id_array = array();
			foreach($result as $row) {
				$registration_id_array[] = $row['android_registration_id'];
			}
			
			// prepare message
			// prepare message
			$message = array('type' => 'got_comment',
							'post_id' => $post_id,
							'content' => $comment_text,
							'comment' => $response['comment']);
			
			// send notification
			$gcm = new GCM();
			$gcm->send_notification($registration_id_array, $message);
			
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