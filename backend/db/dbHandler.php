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
      $sql = "SELECT id, name, hugpoints, lat, lng, wantsHug from mamn01__users;";
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
      $sql = "SELECT id, name, hugpoints, lat, lng, wantsHug from mamn01__users WHERE name=?;";
        $result = $this->db->executeQuery($sql, array($name));
        $this->log("getByName(" . $name . ") -> " . json_encode($result), $device);
      return $result[0];
    }
    public function getById($device, $id){
      $sql = "SELECT id, name, hugpoints, lat, lng, wantsHug from mamn01__users WHERE id=?;";
        $result = $this->db->executeQuery($sql, array($id));
        $this->log("getById(" . $id . ") -> " . json_encode($result), $device);
      return $result[0];
    }
    public function getCoordinate($device, $id){
      $sql = "SELECT lat, lng from mamn01__users WHERE id=?;";
        $result = $this->db->executeQuery($sql, array($id));
        $this->log("getById(" . $id . ") -> " . json_encode($result), $device);
      return $result[0];
    }
    public function createUser($device, $name){
      $sql = "INSERT INTO mamn01__users (name, device) values (?, ?);";
        $result = $this->db->executeUpdate($sql, array($name, $device));
        $this->log("createUser() -> " . json_encode($name), $device);
      return count($result) != 0;
    }
    public function updateName($device, $name){
      $sql = "UPDATE mamn01__users SET name=? WHERE device=?;";
        $result = $this->db->executeUpdate($sql, array($name, $device));
        $this->log("updateName() -> " . json_encode($name), $device);
      return $this->db->getLastId();
    }
    public function wantHug($device){
      $sql = "UPDATE mamn01__users SET wantsHug=1 WHERE device=?;";
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
      $sql = "SELECT id, name, lat, lng, SQRT(POW(69.1 * (lat - ?), 2) +POW(69.1 * (? - lng) * COS(lat / 57.3), 2)) AS distance FROM mamn01__users WHERE wantsHug=1 HAVING distance < 2 ORDER BY distance;";
        $result = $this->db->executeQuery($sql, array($lat, $lng));
        $this->log("getNearbyWanters() -> " . json_encode($result), $device);
      return $result;
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
