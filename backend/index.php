<?php 
	session_start(); 
	require_once 'db/dbHandler.php';
 	$dbHandler = new DatabaseHandler();
 	
 	$data = '';
 	// All endpoints for users (multiple users)
    switch ($_GET["action"]){
        case "getList":
            $data = json_encode($dbHandler->getUsers());
            break;
        case "getHugboard":
            $data = json_encode($dbHandler->getHugboard());
            break;
    }
    if($data == ''){
        echo '{ msg: "Bad request, action not found" }';
    } else {
        echo $data;
    }
	$dbHandler->disconnect();
	
 ?>