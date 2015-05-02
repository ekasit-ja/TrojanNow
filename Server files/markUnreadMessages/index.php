<?php

    require '../library/dbConnection.php';
    require '../library/user.php';
    
    if( isset($_GET['from_user']) && $_GET['from_user'] != '' &&
		isset($_GET['to_user']) && $_GET['to_user'] != '' &&
		isset($_GET['max_id']) && $_GET['max_id'] != '' &&
		isset($_GET['min_id']) && $_GET['min_id'] != '' ) {
        
		$from_user = $_GET['from_user'];
		$to_user = $_GET['to_user'];
        $max_id = $_GET['max_id'];
		$min_id = $_GET['min_id'];
        
        // Get unread messages and set messages to read:
		$stmt = $db->prepare(	'update chat_messages
								set is_read = 1
								where to_user = ? and from_user = ? and
								(id >= ? and id <= ?)' );
        $stmt->bind_param('ssss', $to_user, $from_user, $min_id, $max_id);
        $stmt->execute();
        $db->commit();
    }
?>