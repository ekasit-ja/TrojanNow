<?php
	require '../library/dbConnection.php';
	
	// check if there is parameter
	if( (isset($_POST['post_text']) && $_POST['post_text'] != '') &&
		(isset($_POST['show_name']) && $_POST['show_name'] != '') &&
		(isset($_POST['show_location']) && $_POST['show_location'] != '') &&
		(isset($_POST['show_tempt']) && $_POST['show_tempt'] != '') &&
		(isset($_POST['location']) && $_POST['location'] != '') &&
		(isset($_POST['tempt_in_c'])) &&
		(isset($_COOKIE['mac_address']) && $_COOKIE['mac_address'] != '') ) {
		
		$post_text = $_POST['post_text'];
		$show_name = $_POST['show_name'];
		$show_location = $_POST['show_location'];
		$show_tempt = $_POST['show_tempt'];
		$location = $_POST['location'];
		
		$latitude;
		$longitude;
		if(isset($_POST['latitude']) && isset($_POST['longitude'])) {
			$latitude = $_POST['latitude'];
			$longitude = $_POST['longitude'];
		}
		
		$tempt_in_c;
		if(strlen($_POST['tempt_in_c']) > 0) {
			$tempt_in_c = $_POST['tempt_in_c'];
		}
		else {
			$tempt_in_c = null;
		}
		
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
			$sql = $db->prepare('insert into post(poster_id, post_text, tempt_in_c, location, show_poster_name, show_tempt, show_location, latitude, longitude) values(?, ?, ?, ?, ?, ?, ?, ?, ?)');
			$sql->bind_param('sssssssss', $user_id, $post_text, $tempt_in_c, $location, $show_name, $show_tempt, $show_location, $latitude, $longitude);
		}
		else {
			// save by mac address
			$sql = $db->prepare('insert into post(poster_mac_address, post_text, tempt_in_c, location, show_poster_name, show_tempt, show_location, latitude, longitude) values(?, ?, ?, ?, ?, ?, ?, ?, ?)');
			$sql->bind_param('sssssssss', $mac_address, $post_text, $tempt_in_c, $location, $show_name, $show_tempt, $show_location, $latitude, $longitude);
		}
		
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