<?php

    require '../library/dbConnection.php';
    require '../library/user.php';
    
    if(isset($_GET['fromUser']) && isset($_COOKIE['mac_address']) && $_COOKIE['mac_address'] != '') {
        $user_id = getUserId($db);
        $from_user = $_GET['fromUser'];
        
        // Get unread messages and set messages to read:
        $db->begin_transaction();
        $stmt = $db->prepare('select id, message from chat_messages where to_user = ? and from_user = ? and is_read = 0');
        $stmt->bind_param('ii', $user_id, $from_user);
        $stmt->execute();
        $result = $stmt->get_result();
        
        /*$stmt = $db->prepare('update chat_messages set is_read = 1 where to_user = ? and from_user = ? and is_read = 0');
        $stmt->bind_param('ii', $user_id, $from_user);
        $stmt->execute();*/
        
        $db->commit();
        
        $messages = array();
        foreach($result as $message) {
            $messages[] = $message;
        }
		
		$response['messages'] = $messages;
		$response['from_user'] = $from_user;
		$response['to_user'] = $user_id;
        
        echo json_encode($response);
    }
?>