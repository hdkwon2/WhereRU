<?php
	
$response = array();

	
if(isset($_POST['name']) && isset($_POST['device_id'])){	
	$name = $_POST['name'];
	$device_id = $_POST['device_id'];
	
	//include db_connect class, __DIR__ is an option to specify relative path of the php file
	require_once __DIR__ . '/db_connect.php';
	
	$db = new DB_CONNECT();
	
	//inserting a new entry to the global user info table
	$result = mysql_query("INSERT INTO global_user_info(user_info_name, user_info_device_id) VALUES('$name', '$device_id')");
	$insert_id = mysql_insert_id();
	// create a seperate table for this user holding friends id
	$result2 = mysql_query("CREATE TABLE user_$insert_id(
			friend_id INT NOT NULL AUTO_INCREMENT,
			user_id INT NOT NULL,
			PRIMARY KEY(friend_id),
			INDEX (user_id),
			FOREIGN KEY(user_id) REFERENCES global_user_info(user_info_id))");
	
	if($result && $result2){
		//successufully inserted
		$response["success"] = 1;
		$response["message"] = "User successfully added.";
		$response["insert_id"] = $insert_id;
		//response in json format
		echo json_encode($response);
	}else{
		//an error occured
		$response["success"] = 0;
		$response["message"] = "An error occured while adding the user.";
		
		echo json_encode($response);
	}
} else{
	//required field is missing
	$response["success"] = 0;
	$response["message"] = "Required field is missing";
	
	echo json_encode($response);
}
?>