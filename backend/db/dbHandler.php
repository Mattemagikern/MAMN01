<?php
  require_once 'dbconfig.php'; 
  require_once 'database.php';
  class DatabaseHandler {
    private $db;
    public function __construct() {
      $this->db = new Database(DB_SERVER, DB_USERNAME, DB_PASSWORD, DB_DATABASE);
      $this->connect();
    }
    public function connect(){
      $this->db->openConnection();
      if(!$this->db->isConnected()) {
        header("Location: cannotConnect.php");
        exit();
      }
    }
    public function disconnect(){
      $this->db->closeConnection();
    }

    // Here are all the methods for retrieving and updating data.
    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    public function isAdmin($device){
      $sql = "SELECT * FROM mamn01__users WHERE device=?";
      $result = $this->db->executeQuery($sql, array($device));
      return count($result) == 1;
    }
    public function deviceExist($device){
      $sql = "SELECT * from mamn01__users WHERE device=?;";
      $result = $this->db->executeQuery($sql, array($device));
      return count($result) == 1;
    }
    public function getUsers($device){
      $sql = "SELECT id, name, hugrange, hugpoints, lat, lng, wantsHug from mamn01__users;";
      $result = $this->db->executeQuery($sql, array());
      $this->log("getHugboard() -> " . json_encode($result), $device);
      return $result;
    }
    public function getHugboard($device){
      $sql = "SELECT name, hugpoints from mamn01__users ORDER BY hugpoints DESC LIMIT 10;";
      $result = $this->db->executeQuery($sql, array());
      $this->log("getHugboard() -> " . json_encode($result), $device);
      return $result;
    }
    public function getByName($device, $name){
      $sql = "SELECT id, name, hugrange, hugpoints, lat, lng, wantsHug from mamn01__users WHERE name=?;";
      $result = $this->db->executeQuery($sql, array($name));
      $this->log("getByName(" . $name . ") -> " . json_encode($result), $device);
      return $result[0];
    }
    public function getByDevice($device){
      $sql = "SELECT id, name, hugrange, hugpoints, lat, lng, wantsHug, isBusy from mamn01__users WHERE device=?;";
      $result = $this->db->executeQuery($sql, array($device));
      $this->log("getByDevice() -> " . json_encode($result), $device);
      return $result[0];
    }
    public function getById($device, $id){
      $sql = "SELECT id, device, name, hugrange, lat, lng, hugfailed, hugccess, lost from mamn01__users WHERE id=?;";
      $result = $this->db->executeQuery($sql, array($id));
      $this->log("getById(" . $id . ") -> " . json_encode($result), $device);
      return $result[0];
    }
    public function getCoordinate($device, $id){
      $sql = "SELECT lat, lng, hugfailed, hugccess, lost from mamn01__users WHERE id=?;";
      $result = $this->db->executeQuery($sql, array($id));
      $this->log("getCoordinate(" . $id . ") -> " . json_encode($result), $device);
      return $result[0];
    }
    public function createUser($device, $name, $range){
      $sql = "INSERT INTO mamn01__users (name, device, hugrange) values (?, ?, ?);";
      $result = $this->db->executeUpdate($sql, array($name, $device, $range));
      $this->log('createUser(' . $name . ', ' . $range . ')', $device);
      return count($result) != 0;
    }
    public function updateName($device, $name, $range){
      $sql = "UPDATE mamn01__users SET name=?, hugrange=? WHERE device=?;";
      $result = $this->db->executeUpdate($sql, array($name, $range, $device));
      $this->log('updateName(' . $name . ', ' . $range . ')', $device);
      return $this->db->getLastId();
    }
    public function wantHug($device){
      $sql = "UPDATE mamn01__users SET wantsHug=1, hugccess=0, hugfailed=0, lost=0 WHERE device=?;";
      $result = $this->db->executeUpdate($sql, array($device));
      $this->log("wantHug() -> ", $device);
      return $this->db->getLastId();
    }
    public function updateCoordinate($device, $lat, $lng){
      $sql = "UPDATE mamn01__users SET lat=?, lng=? WHERE device=?;";
      $result = $this->db->executeUpdate($sql, array($lat, $lng, $device));
      $this->log("updateCoordinate(" . $lat . ", " . $lng . ") -> ", $device);
      return $this->db->getLastId();
    }
    public function getNearbyWanters($device, $lat, $lng){
      $sql = <<<'EOT'
        SELECT 
          id, 
          device, 
          name, 
          hugrange, 
          lat, 
          lng, 
          SQRT(POW(69.1 * (lat - ?), 2) +POW(69.1 * (? - lng) * COS(lat / 57.3), 2)) AS distance,
          hugfailed,
          hugccess,
          lost
        FROM 
          mamn01__users 
        WHERE 
          wantsHug=1 
        ORDER BY 
          distance;
EOT;
      $result = $this->db->executeQuery($sql, array($lat, $lng));
      $this->log("getNearbyWanters() -> " . json_encode($result), $device);
      return $result;
    }

    public function matchMeUp($device, $myid, $lat, $lng, $myrange){
      $sql = <<<'EOT'
      SELECT  id, 
              device, 
              name, 
              hugrange, 
              lat, 
              lng, 
              SQRT(POW(69.1 * (lat - ?), 2) +POW(69.1 * (? - lng) * COS(lat / 57.3), 2)) AS distance 
        FROM mamn01__users as u 
        WHERE 
          wantsHug=1 
        AND 
          isBusy=0 
        AND 
          device!=? 
        HAVING distance < (hugrange / 1000) AND distance < (? / 1000) 
        ORDER BY distance LIMIT 1;
EOT;
/* Removed for testing
        AND 
          NOT EXISTS (
            SELECT  * FROM mamn01__hugs 
              WHERE 
                hugger=u.id AND hugged=? AND DATE(hug_date) = CURDATE()
              OR 
                hugger=? AND hugged=u.id AND DATE(hug_date) = CURDATE()
          ) 
*/
      $result = $this->db->executeQuery($sql, array($lat, $lng, $device, $myid, $myid, $myrange));
      $this->log("matchMeUp(" . $lat . ", " . $lng .  ", " . $myrange . ") -> " . json_encode($result), $device);
      return $result;
    }
    public function hugAdded($device, $myid, $otherid){
      $sql = "INSERT INTO mamn01__hugs (hugger, hugged, hug_date) values (?,?,NOW());";
      $result = $this->db->executeUpdate($sql, array($myid, $otherid));
      $this->log('hugAdded(' . $myid . ', ' . $otherid . ')', $device);
      return count($result) != 0;
    }
    public function setBusy($device, $id){
      $sql = "UPDATE mamn01__users SET isBusy=? WHERE device=?;";
      $result = $this->db->executeUpdate($sql, array($id, $device));
      $this->log("setBusy(" . $id . ") -> ", $device);
      return $this->db->getLastId();
    }
    public function setHugccessById($device, $id){
      $sql = "UPDATE mamn01__users SET isBusy=0, wantsHug=0, hugccess=1 WHERE id=?;";
      $result = $this->db->executeUpdate($sql, array($id));
      $this->log("setHugccessById(" . $id . ") -> ", $device);
      return $this->db->getLastId();
    }
    public function setHugfailed($device, $id){
      $sql = "UPDATE mamn01__users SET isBusy=0, wantsHug=0, hugfailed=1 WHERE id=?;";
      $result = $this->db->executeUpdate($sql, array($id));
      $this->log("setHugfailed(" . $id . ") -> ", $device);
      return $this->db->getLastId();
    }
    public function giveHugpoint($device, $id){
      $sql = "UPDATE mamn01__users SET hugpoints=hugpoints+1 WHERE id=?;";
      $result = $this->db->executeUpdate($sql, array($id));
      $this->log("giveHugPoint(" . $id . ") -> ", $device);
      return $this->db->getLastId();
    }
    
  
    // Log
    // ======================================================
    public function log($logMessage, $device){
      $sql = "INSERT INTO mamn01__log (message, device, logdate) VALUES (?,?,NOW());";
        $result = $this->db->executeUpdate($sql, array($logMessage, $device));
    }
    public function getLog(){
      $sql = "SELECT * from mamn01__log limit 100;";
        $result = $this->db->executeQuery($sql, array());
      return $result;
    }

  }
?>
