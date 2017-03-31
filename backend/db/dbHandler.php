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


		public function getUsers(){
			$sql = "SELECT * from mamn01__users;";
    		$result = $this->db->executeQuery($sql, array());
			return $result;
		}
		
		public function getHugboard(){
			$sql = "SELECT name, hugpoints from mamn01__users ORDER BY hugpoints DESC LIMIT 10;";
    		$result = $this->db->executeQuery($sql, array());
			return $result;
		}
		
		
			// Log
		// ======================================================
		public function log($logMessage, $user){
			$sql = "INSERT INTO mamn01__log (message, user, logdate) VALUES (?,?,NOW());";
    		$result = $this->db->executeUpdate($sql, array($logMessage, $user));
			//return $this->db->getLastId();
		}





		// User
		// ======================================================
		/*
		public function getUsers(){
			$sql = "SELECT * from connectfour_user;";
    		$result = $this->db->executeQuery($sql, array());
			return $result;
		}

		public function getUserName($id){
			$sql = "SELECT * from connectfour_user WHERE id=?;";
    		$result = $this->db->executeQuery($sql, array($id));
			return $result[0][1];
		}

		public function createUser($name){
			$sql = "INSERT INTO connectfour_user (name) values (?);";
    		$result = $this->db->executeUpdate($sql, array($name));
			return count($result) != 0;
		}

		// Game
		// ======================================================
		public function getHighscore($limit){
			$sql = "SELECT u.name, count(*) as win FROM connectfour_game as g join connectfour_user as u on u.id=g.winner group by g.winner ORDER BY win DESC LIMIT 5;";
    		$result = $this->db->executeQuery($sql, array());
			return $result;
		}

		public function addNewGame($row, $col){
			$sql = "INSERT INTO connectfour_game (row, col) VALUES (?,?);";
    		$result = $this->db->executeUpdate($sql, array($row, $col));
    		$gameId = $this->db->getLastId();
    		$this->log("Game created", "Someone");
			return $gameId;
		}

		public function getLastCreatedGame(){
			$sql = "SELECT * FROM connectfour_game ORDER BY id DESC LIMIT 1;";
    		$result = $this->db->executeQuery($sql, array());
			return $result[0];
		}

		public function saveBoard($boardString, $gameId){
			$sql = "UPDATE connectfour_game SET gameData=? WHERE id=?;";
    		$result = $this->db->executeUpdate($sql, array($boardString, $gameId));
			return $this->db->getLastId();
		}

		public function setWinner($playerId, $gameId){
			$sql = "UPDATE connectfour_game SET winner=? WHERE id=?;";
    		$result = $this->db->executeUpdate($sql, array($playerId, $gameId));

    		$usrName = $this->getUserName($playerId);
    		$this->log("User won game: $gameId", "$usrName");
			return $this->db->getLastId();
		}
		
		public function getWinner($game){
			$sql = "SELECT winner FROM connectfour_game WHERE id=?;";
    		$result = $this->db->executeQuery($sql, array($game));
			return $result[0][0];
		}

		// GameUser
		// ======================================================
		public function addGameUser($userId, $gameId, $order, $ch, $color, $rgba){
			$sql = "INSERT INTO connectfour_gameuser (user, game, playorder, playerChar, color, rgba) VALUES (?,?,?,?,?,?);";
    		$result = $this->db->executeUpdate($sql, array($userId, $gameId, $order, $ch, $color, $rgba));

    		$usrName = $this->getUserName($userId);
    		$this->log("User added to game: $gameId", "$usrName");
			return $this->db->getLastId();
		}

		public function incrementMove($game, $user){
			$sql = "UPDATE connectfour_gameuser set moves=moves+1 WHERE game=? AND user=?;";
    		$result = $this->db->executeUpdate($sql, array($game, $user));

    		$usrName = $this->getUserName($user);
    		$this->log("User moved", "$usrName");
			return $result[0];
		}

		public function getCurrentPlayer($gameId){
			$sql = "SELECT * FROM connectfour_gameuser WHERE game=? ORDER BY moves ASC, playorder LIMIT 1;";
    		$result = $this->db->executeQuery($sql, array($gameId));
			return $result;
		}

		public function getGameTotalMoves($gameId){
			$sql = "SELECT SUM(moves) FROM connectfour_gameuser WHERE game=?;";
    		$result = $this->db->executeQuery($sql, array($gameId));
			return $result[0][0];
		}

		public function getGameUsers($gameId){
			$sql = "SELECT * FROM connectfour_gameuser JOIN connectfour_user WHERE connectfour_gameuser.game=? AND connectfour_gameuser.user=connectfour_user.id;";
    		$result = $this->db->executeQuery($sql, array($gameId));
			return $result;
		}

		// Log
		// ======================================================
		public function log($logMessage, $user){
			$sql = "INSERT INTO connectfour_log (message, user, logdate) VALUES (?,?,NOW());";
    		$result = $this->db->executeUpdate($sql, array($logMessage, $user));
			//return $this->db->getLastId();
		}
		*/	
	}
?>