<?php

	require '../library/dbConnection.php';
	require '../library/user.php';
        
        if(isset($_POST['action']) && isset($_POST['user_id']) && isset($_COOKIE['mac_address']) && $_COOKIE['mac_address'] != '') {
            $userID = getUserId($db);
            $friendId = $_POST['user_id'];
            
            if ($_POST['action'] == 'add') {
                   $sql = $db->prepare('insert into friends (user1, user2) values (?,?)');
                   $sql->bind_param('ii', $userID, $friendId);
                   $sql->execute();
            } else if ($_POST['action'] == 'remove') { 
                   $sql = $db->prepare('delete from friends where (user1 = ? and user2 = ?) or (user1 = ? and user2 = ?)');
                   $sql->bind_param('iiii', $userID, $friendId, $friendId, $userID);
                   $sql->execute();
           }   
        }