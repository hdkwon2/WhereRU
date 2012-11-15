<?php

$response = array();



if(isset($_GET["user_id"])){
	$user_id = $_GET["user_id"];

	require_once __DIR__ . '/db_connect.php';
	$db = new DB_CONNECT();	
	
	
	//get the user info
	$result = mysql_query("SELECT locations.user_id, locations.location, locations.time_stamp, global_user_info.user_info_name 
							FROM user_$user_id 
							LEFT JOIN locations 
								ON user_$user_id.user_id = locations.user_id 
							LEFT JOIN global_user_info 
								ON user_$user_id.user_id = global_user_info.user_info_id");
	
	// if no error
	if($result){
		// if at least one user is found
		if(mysql_num_rows($result) > 0){
			$result = mysql_fetch_array($result);
			
			$user_info = array();
			$user_info["friend_id"] = $result["user_id"];
			$user_info["location"] = $result["location"];
			$user_info["time_stamp"] = $result["time_stamp"];
			$user_info["user_name"] = $result["user_info_name"];
			$response["success"] = 1;
			$response["friends_info"] = array();
			
			array_push($response["friends_info"], $user_info);
			echo json_encode($response);
		} else{
			$response["success"] = 0;
			$response["message"] = "No matching user found";
			echo json_encode($response);
		}
	} else{
		$response["success"] = 0;
		$response["message"] = "No matching user found";
		echo json_encode($response);
	}
} else{
	$response["success"] = 0;
	$response["message"] = "Mssing unique user id";
	echo json_encode($response);
}
?>