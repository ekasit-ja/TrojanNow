<?php

function getUserId($db) {
    $user_id = null;
    
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
    
    return $user_id;
}