<?php

	require '../library/dbConnection.php';
	require '../library/user.php';
        
        if (isset($_POST['age']) && isset($_POST['school']) && isset($_POST['gradyear']) && isset($_POST['about'])
                && isset($_COOKIE['mac_address']) && $_COOKIE['mac_address'] != '') {
            
            $user_id = getUserId($db);
            
            $age = $_POST['age'];
            $school = $_POST['school'];
            $gradyear = $_POST['gradyear'];
            $about = $_POST['about'];
            
			$sql = $db->prepare('select 1 from profile where user_id = ?');
            $sql->bind_param('i', $user_id);
            $sql->execute();
			$result = $sql->get_result();
			
			if($result->num_rows > 0) {
				$sql = $db->prepare('update profile set age=?, school=?, grad_year=?, about=? where user_id = ?');
				$sql->bind_param('ssssi', $age,$school,$gradyear,$about,$user_id);
				$sql->execute();
				mysqli_commit($db);
			}
			else {
				$sql = $db->prepare('insert into profile(age,school,grad_year,about,user_id) value(?,?,?,?,?)');
				$sql->bind_param('ssssi', $age,$school,$gradyear,$about,$user_id);
				$sql->execute();
				mysqli_commit($db);
			}
			
            
        }
?>