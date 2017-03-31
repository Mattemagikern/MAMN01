<?php 
  session_start(); 
  require_once 'db/dbHandler.php';
  $dbHandler = new DatabaseHandler();
  
  $data = '';
  // All endpoints for users (multiple users)
 
  if( $_GET["device"]){
        switch ($_GET["action"]){
            case "getUsers":
                if($dbHandler->isAdmin($_GET["device"])){
                    $data = json_encode($dbHandler->getUsers($_GET["device"]));
                } else {
                    $data = 'Unauthorized';
                }
                break;
            case "getHugboard":
                $data = json_encode($dbHandler->getHugboard($_GET["device"]));
                break;
            case "getLog":
                if($dbHandler->isAdmin($_GET["device"])){
                    $data = json_encode($dbHandler->getLog($_GET["device"]));
                } else {
                    $data = 'Unauthorized';
                }
                break;
            case "wantHug":
                $data = json_encode($dbHandler->wantHug($_GET["device"]));
                break;
            case "getNearbyWanters":
                $data = json_encode($dbHandler->getNearbyWanters($_GET["device"], $_GET["lat"], $_GET["lng"]));
                break;
            case "getCoordinate":
                $data = json_encode($dbHandler->getCoordinate($_GET["device"], $_GET["id"]));
                break;
            case "updateCoordinate":
                $data = json_encode($dbHandler->updateCoordinate($_GET["device"], $_GET["lat"], $_GET["lng"]));
                break;
            case "createUser":
                $data = json_encode($dbHandler->createUser($_GET["device"], $_GET["name"]));
                break;
            case "updateName":
                $data = json_encode($dbHandler->updateName($_GET["device"], $_GET["name"]));
                break;
            case "getById":
                $data = json_encode($dbHandler->getById($_GET["device"], $_GET["id"]));
                break;
            case "getByName":
                $data = json_encode($dbHandler->getByName($_GET["device"], $_GET["name"]));
                break;
        }
  }
  
    if($data == ''){
        echo '{ msg: "Bad request, action not found" }';
    } else {
        echo $data;
    }
  $dbHandler->disconnect();
?>
