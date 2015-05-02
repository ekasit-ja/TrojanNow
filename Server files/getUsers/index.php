<?php
	require '../library/dbConnection.php';
	require '../library/user.php';
        
        if(isset($_GET['users']) && isset($_COOKIE['mac_address']) && $_COOKIE['mac_address'] != '') {
            $user_id = getUserId($db);

            if ($_GET['users'] == 'friends') {
                $only_friends_query = 'select id, display_name from account where id in (select user1 from friends where user2 = ? union select user2 from friends where user1 = ?)';
                $sql = $db->prepare($only_friends_query);
                $sql->bind_param('ii',$user_id,$user_id);

            } else if ($_GET['users'] == 'all') {
                $all_users_query = 'select t.id, t.display_name, case when t.user1 is null then "false" else "true" end as friend from (select id, display_name, user1 from account left join friends on (user1 = id and user2 = ?) or (user1 = ? and user2 = id) where id <> ?) t';
                $sql = $db->prepare($all_users_query);
                $sql->bind_param('iii',$user_id,$user_id,$user_id);
            }

            $sql->execute();
            $result = $sql->get_result();

            $friends = array();
            // encode result to json format
            foreach($result as $friend) {
                    $friends[] = $friend;
            }

            $response = array(
                    'users' => $friends
            );
            echo json_encode($response);
        }