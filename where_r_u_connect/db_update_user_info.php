Progress:

need to figure out how to add location point when user has more than 10
need to figure out how to parse the data sent back as the result of user info get
<?php

$response = array();


if(isset($_POST["user_id"])){
	$user_id = $_POST["user_id"];

	require_once __DIR__ . '/db_connect.php';
	$db = new DB_CONNECT();

	if(isset($_POST["latitude"]) && isset($_POST["longitude"]) && isset($_POST["time_stamp"])){

		require_once __DIR__ . '/db_config.php';
		// Location update
		$lat = $_POST["latitude"];
		$long = $_POST["longitude"];
		$time_stamp = $_POST["time_stamp"];
		
		// Get number of locations currently stored for this user
		$num_loc = mysql_query("SELECT user_info_num_loc FROM global_user_info WHERE user_info_id=$user_id");

		if($num_loc < DB_MAX_NUM_LOC){
			// room for additional locations, add new location to locations table
			$result = mysql_query("INSERT INTO locations(user_id, location, time_stamp) VALUES($user_id, GeomFromText('POINT($lat $long)'), $time_stamp)");
		} else{
			
			$result = mysql_query("UPDATE  SET user_info_loc1='$user_loc' WHERE user_info_id='$user_id'");
		}
		
		if($result){
			$response["success"] = 1;
			$response["message"] = "User location successfully updated";
			echo json_encode($response);
		}else{
			$response["success"] = 0;
			$response["message"] = "Failed to update user location";
			echo json_encode($response);
		}
	} else if(isset($_POST["user_name"])) {
		// Other attributes update
		$user_name = $_POST["user_name"];

		$result = mysql_query("UPDATE user_info SET user_info_name='$user_name' WHERE user_info_id='$user_id'");

		if($result){
			$response["success"] = 1;
			$response["message"] = "User info successfully updated";
			echo json_encode($response);
		} else{
			$response["success"] = 0;
			$response["message"] = "Failed to update user info";
			echo json_encode($response);
		}
	} else{
		$response["success"] = 0;
		$response["message"] = "Missing an attribute to update";
		echo json_encode($response);
	}

} else{
	$response["success"] = 0;
	$response["message"] = "Missing unique user id";
	echo json_encode($response);
}