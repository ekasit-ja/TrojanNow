<?php
	require '../library/dbConnection.php';
	
	// check if there is parameter 'location' or not
	if(isset($_GET['latitude']) && isset($_GET['longitude'])) {
		// trim blank space and apply lower case for querying database
		$latitude = $_GET['latitude'];
		$longitude = $_GET['longitude'];
		$radius = 5; // in kilometers
		$ratio = 1/111.32;
		
		$diff = $radius/2 * $ratio;
		
		$top = $latitude + $diff;
		$bottom = $latitude - $diff;
		$left = $longitude - $diff;
		$right = $longitude + $diff;
		
		$mac_address = '';
		if(isset($_COOKIE['mac_address'])) {
			$mac_address = $_COOKIE['mac_address'];
		}
		
		$user_id = '';
		if(isset($_COOKIE['session_id'])) {
			$session_id = $_COOKIE['session_id'];
			$sql = $db->prepare('select user_id from session where session_id = ?');
			$sql->bind_param('s', $session_id);
			$sql->execute();
			
			if($sql) {
				$resultUserId = $sql->get_result();
				$rowUserId = mysqli_fetch_array($resultUserId);
				$user_id = $rowUserId['user_id'];
			}
		}
		
		// query database		
		$sql = $db->prepare(
			'select	p.id, p.post_text, p.post_timestamp, p.post_score,
					sum(ifnull(c.1,0)) as reply_count,
					ifnull(r.score,0) as user_rating,
					case p.show_poster_name
						when true then a.display_name
						else ""
					end as display_name,
					case p.show_tempt
						when true then p.tempt_in_c
						else ""
					end as tempt_in_c,
					case p.show_location
						when true then p.location
						else ""
					end as location
			from (	select *
					from post
					where latitude <= ? and latitude >= ? and
							longitude >= ? and longitude <= ?) as p
			left join (
					select display_name, id
					from account ) as a
			on a.id = p.poster_id
			left join (
					select 1,post_id
					from comment) as c
            on p.id = c.post_id
            left join (
                	select post_id, score
                	from post_rating
                	where mac_address = ? or
                		  user_id = ? ) as r
			on p.id = r.post_id
			group by p.id
			order by post_timestamp desc, id desc'
		);
		
		$sql->bind_param('ssssss', $top, $bottom, $left, $right, $mac_address, $user_id);
		$sql->execute();
		$result = $sql->get_result();
		
		$posts = array();
		// encode result to json format
		foreach($result as $post) {
			$posts[] = $post;
		}
		echo json_encode($posts);

	}
	else {
		// return null as array
		echo '[]';
	}
?>