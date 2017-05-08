<?php 
  session_start(); 
  require_once 'db/dbHandler.php';
  $dbHandler = new DatabaseHandler();
  $data = '';
  // All endpoints for users (multiple users)

  // Check if phone is in testmode
  $testMode = $_GET['testmode'] != 'false';
 
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
            case "giveHugpoint":
                $data = json_encode($dbHandler->giveHugPoint($_GET["device"], $_GET["id"]));
                break;
            case "hugcancelled":
                $dbHandler->setWantNoHug($_GET["device"]);
                $data = "Hug cancelled";
                break;
            case "hugfailed":
                $me = $dbHandler->getByDevice($_GET["device"]);
                if($_GET["other"] != "1"){
                  $dbHandler->setHugfailed($_GET["device"], $_GET['other']);
                }
                $dbHandler->setHugfailed($_GET["device"], $me['id']);
                $data = "HugFailed is a fact";
                break;
            case "hugccess":
                $me = $dbHandler->getByDevice($_GET["device"]);
                if($_GET["other"] != "1"){
                  $dbHandler->giveHugPoint($_GET["device"], $_GET["other"]);
                  $dbHandler->setHugccessById($_GET["device"], $_GET['other']);
                }
                $dbHandler->giveHugPoint($_GET["device"], $me["id"]);
                $dbHandler->setHugccessById($_GET["device"], $me['id']);
                $dbHandler->hugAdded($_GET["device"], $me['id'], $_GET['other']);
                $data = "Hugccess is a fact";
                break;
            case "createUser":
                $data = json_encode($dbHandler->createUser($_GET["device"], $_GET["name"]));
                break;
            case "updateName":
                if($dbHandler->deviceExist($_GET["device"])){
                  $data = json_encode($dbHandler->updateName($_GET["device"], $_GET["name"], $_GET["range"]));
                } else {
                  $data = json_encode($dbHandler->createUser($_GET["device"], $_GET["name"], $_GET["range"]));
                }
                break;
            case "getByDevice":
                $data = json_encode($dbHandler->getByDevice($_GET["device"]));
                break;
            case "getById":
                $data = json_encode($dbHandler->getById($_GET["device"], $_GET["id"]));
                break;
            case "getByName":
                $data = json_encode($dbHandler->getByName($_GET["device"], $_GET["name"]));
                break;
            case "matchMeUp":
                // Set i want a hug.
                $data = json_encode($dbHandler->wantHug($_GET["device"]));
                
                // Get my user object
                $me = $dbHandler->getByDevice($_GET["device"]);
                // Check if i am busy with someone
                if($me['isBusy'] > 0){
                    $data =$dbHandler->getById($_GET["device"], $me['isBusy']);
                } else {
                    // Else try to match me up.
                    if($testMode){
                      $other = $dbHandler->getById($_GET["device"], "1");
                    }else{
                      $other = $dbHandler->matchMeUp($_GET["device"], $me['id'], $me['lat'], $me['lng'], $me['hugrange']);
                    }
                    if(count($other) > 0){
                        // If match found, set us as busy.
                        $data = $other[0];
                        $dbHandler->setBusy($_GET['device'], $data['id']);
                        $dbHandler->setBusy($data['device'], $me['id']);
                    } else {
                        $data = '"No match"';
                    }
                }
                break;
        }
    }
  
    if($data == ''){
        echo '{ err: "Bad request, action not found", data: "" }';
    } else {
        if($data =='null'){
            $data = '""';
        }
        echo '{ err: "", data: ' . json_encode($data) . ' }';
    }
  $dbHandler->disconnect();
?>
