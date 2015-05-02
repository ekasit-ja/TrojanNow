<?php
	require '../library/dbConnection.php';

	$sql = $db->prepare('select f.user2 as id, a.display_name as name from friends f, account a where f.user1=6 and f.user2=a.id');
        $sql->execute();
        $result = $sql->get_result();

        $friends = array();
        // encode result to json format
        foreach($result as $friend) {
                $friends[] = $friend;
        }

        $response = array(
                'friends' => $friends
        );
        echo json_encode($response);
?>
