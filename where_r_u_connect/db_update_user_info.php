
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
		
		
		$count = mysql_query("SELECT COUNT(*) FROM locations WHERE user_id = $user_id");
		
		if($count < DB_MAX_NUM_LOC){
			// room for additional locations, add new location to locations table
			$result = mysql_query("INSERT INTO locations(user_id, location, time_stamp) VALUES($user_id, GeomFromText('POINT($lat $long)'), $time_stamp)");
		} else{
			// need to replace one of the stored point
			// replace the one with the smallest time stamp (earliest location)
			$result = mysql_query("UPDATE locations SET location=GeomFromText('POINT($lat $long)'), time_stamp=$time_stamp ORDER BY time_stamp LIMIT 1");
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