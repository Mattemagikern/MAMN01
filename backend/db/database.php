<?php
/*
 ** Class Database: interface to the database from PHP.
 **/
class Database {
  private $host;
  private $userName;
  private $password;
  private $database;
  private $conn;

  /**
   ** Constructs a database object for the specified user.
   **/
  public function __construct($host, $userName, $password, $database) {
    $this->host = $host;
    $this->userName = $userName;
    $this->password = $password;
    $this->database = $database;
  }

  /** 
   ** Opens a connection to the database, using the earlier specified user
   ** name and password.
   **
   ** @return true if the connection succeeded, false if the connection 
   ** couldn't be opened or the supplied user name and password were not 
   ** recognized.
   **/
  public function openConnection() {
    try {
      $this->conn = new PDO("mysql:host=$this->host;dbname=$this->database", 
        $this->userName,  $this->password,
        array(PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8"));
      $this->conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    } catch (PDOException $e) {
      $error = "Connection error: " . $e->getMessage();
      print $error . "<p>";
      unset($this->conn);
      return false;
    }
    return true;
  }

  /**
   ** Closes the connection to the database.
   **/
  public function closeConnection() {
    $this->conn = null;
    unset($this->conn);
  }

  /**
   ** Checks if the connection to the database has been established.
   **
   ** @return true if the connection has been established
   **/
  public function isConnected() {
    return isset($this->conn);
  }

  /**
   ** Execute a database query (select).
   **
   ** @param $query The query string (SQL), with ? placeholders for parameters
   ** @param $param Array with parameters 
   ** @return The result set
   **/
  public function executeQuery($query, $param = null) {
    try {
      $stmt = $this->conn->prepare($query);
      $stmt->execute($param);
      $result = $stmt->fetchAll();
    } catch (PDOException $e) {
      $error = "*** Internal error: " . $e->getMessage() . "<p>" . $query;
      die($error);
    }
    return $result;
  }

  /**
   ** Execute a database update (insert/delete/update).
   **
   ** @param $query The query string (SQL), with ? placeholders for parameters
   ** @param $param Array with parameters 
   ** @return The number of affected rows
   **/
  public function executeUpdate($query, $param = null) {
    try{
      $stmt = $this->conn->prepare($query);
      $stmt->execute($param);
      return $stmt->rowCount();
    } catch(PDOException $e) {
      $error = "*** Internal error: " . $e->getMessage() . "<p>" . $query;
      die($error);
    }
  }

  /**
   ** Get the last auto_incremented id that was inserted to the database.
   **/
  public function getLastId(){
    return $this->conn->lastInsertId();
  }
}
?>