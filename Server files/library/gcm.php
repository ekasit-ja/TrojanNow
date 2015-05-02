<?php
	define('API_KEY','AIzaSyBSqK_jToF5ir7MbJE9PqdE9Ixwf_Xt4_8');
	 
	class GCM {
		// constructor
		function __construct() {}
	 
		/**
		 * Sending Push Notification
		 */
		public function send_notification($registatoin_ids, $messages) {
			// Set POST variables
			$url = 'https://android.googleapis.com/gcm/send';
	 
			$fields = array(
				'registration_ids' => $registatoin_ids,
				'data' => $messages,
			);
			
			$headers = array(
				'Authorization: key=' . API_KEY,
				'Content-Type: application/json'
			);
			// Open connection
			$ch = curl_init();
	 
			// Set the url, number of POST vars, POST data
			curl_setopt($ch, CURLOPT_URL, $url);
			curl_setopt($ch, CURLOPT_POST, true);
			curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
			curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
	 
			// Disabling SSL Certificate support temporarly
			curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
	 
			curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
	 
			// Execute post
			$result = curl_exec($ch);
			if ($result === FALSE) {
				die('Curl failed: ' . curl_error($ch));
			}
	 
			// Close connection
			curl_close($ch);
			return $result;
		}
	 
	}
?>
