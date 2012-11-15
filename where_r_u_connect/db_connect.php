
<?php
//Referenced from a tutorial:
//http://www.androidhive.info/2012/05/how-to-connect-android-with-php-mysql/
//An interface class to set up connection to dabase server

	class DB_CONNECT {
		
		function __construct(){
			$this->connect();
		}
		
		function __destruct() {
			$this->close();
		}
		
		function connect() {
			// import database connection variables
			require_once __DIR__ . '/db_config.php';
			
			// connecting to the db server
			$con = mysql_connect(DB_SERVER, DB_USER, DB_PASSWORD) or die(mysql_error());
			
			
			// selecting db
			$db = mysql_select_db(DB_DATABASE) or die(mysql_error());
			
			return $con;
		}
		
		function close() {
			mysql_close();
		}
	}
?>