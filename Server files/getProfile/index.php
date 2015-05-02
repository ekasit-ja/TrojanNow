<?php

	require '../library/dbConnection.php';
	require '../library/user.php';
        
        //if(isset($_GET['userId']) && isset($_COOKIE['mac_address']) && $_COOKIE['mac_address'] != '') {
            $user_id = $_GET['user_id'] == "active" ? getUserId($db) : $_GET['user_id'];

            $sql = $db->prepare('select display_name, age, school, grad_year, about from profile p, account a where p.user_id = ? and p.user_id = a.id');
            $sql->bind_param('i', $user_id);
            $sql->execute();
            $result = $sql->get_result();
            $row = $result->fetch_assoc();

            $response = array(
                'profile' => $row
            );

            echo json_encode($response);            
        //}