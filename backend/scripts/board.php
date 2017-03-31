<?php


	class GameBoard{

		public $board = array();
		public $gamePlayers = array();

		// Create a board without any moves.
		public function init_empty_board($row, $col){ 
			$this->board = array_fill(0, $row, array_fill(0, $col, null));	
		}
		// Creates a board from a string with data.
		public function init_board_from_string($arrStr){
			$rowArr = explode(":", $arrStr);
			$arr = array();
			foreach ($rowArr as $key => $a) {
				$arr[] = explode(".",$a);
			}
			$this->board = $arr;
		}
		// Turns the board to a string.
		public function board_to_string(){
			$arrStr = array();
			foreach ($this->board as $key => $a) {
				$arrStr[] = implode(".",$a);
			}
			return implode(":", $arrStr);
		}

		public function setGamePlayers($gu){
			$this->gamePlayers = $gu;
		}

		// Generates the HTML for the gameboard.
		public function generateHTML(){
			$k = 0;
			$result = "";
			foreach ($this->board as $key => $r){
				$result .= '<div class="row">';
				foreach ($r as $key => $c) {
					$k++;
					$color= "transparent";

					// Decide color.
					$color = "transparent";
					foreach ($this->gamePlayers as $key => $gu) {
						if($gu[4] == $c){
							$color = $gu[6];
						}
						
					}
					//echo count($this->gamePlayers);
					/*
					if($c == "X"){
						$color = "rgba(242,134,125,0.65)";
					} else if ($c == "O"){
						$color = "rgba(0,255,0,0.2)";
					} 
					*/
					$result .= "<div class='cell' id='$k' style='background-color: $color;'>$c</div>";
				}
				$result .= '</div>';
			}
			return $result;
		}

		// Add a move in the board.
		public function addMove($column, $ch){
			$row = count($this->board) - 1;
			$col = ($column-1) % count($this->board[0]);

			// Specialcase: if the value sent in is 0 then it actually is
			// the column to the furthest right. Ergo number of columns - 1.
			if($column == 0){
				$col = count($this->board[0]) - 1;
			}

			// Loop the specified column from the last row to the first,
			// adding the character on the first non-null cell that is found.
			$largest = 0;
			for($i = $row; $i >= 0; $i--){
				$cell = $this->board[$i][$col];
				if($cell == null){
					$this->board[$i][$col] = $ch;
					$largest = $this->largestConnection($col,$i);
					break;
				}
			}	
			return $largest;
		}

		// Retrieves the largest connection-chain this cell is a part of.
		private function largestConnection($x, $y){
			// Make sure the coordinates are within the board
			if(!$this->withinBoard($x,$y)){
				return 0; 
			}
			// Check that the given cell actually has a value.
			$playerChar = $this->board[$y][$x];
			if($playerChar == ""){
				return 0;
			}
			// Get amount of equal cells in given direction
			$top = $this->connectionDirection($x, $y, 0, -1);
			$bottom = $this->connectionDirection($x, $y, 0, 1);

			$left = $this->connectionDirection($x, $y, -1, 0);
			$right = $this->connectionDirection($x, $y, 1, 0);

			$topright = $this->connectionDirection($x, $y, 1, -1);
			$bottomleft = $this->connectionDirection($x, $y, -1, 1);

			$topleft = $this->connectionDirection($x, $y, -1, -1);
			$bottomright = $this->connectionDirection($x, $y, 1, 1);

			// Sum the opposite directions
			$direction1 = $left + $right;
			$direction2 = $top + $bottom;
			$direction3 = $topright + $bottomleft;
			$direction4 = $topleft + $bottomright;

			// Returns the maximun amount of equal cells in the opposite-directions + itself.
			return max($direction1, $direction2, $direction3, $direction4) + 1;
		}

		// A recursive method that checks how many of the cells in a given direction
		// are equal to the one in origin. 
		private function connectionDirection($x, $y, $deltaX, $deltaY){
			$newX = $x + $deltaX;
			$newY = $y + $deltaY;
			$cell = $this->board[$y][$x];
			$newCell = $this->board[$newY][$newX];
			
			if($cell == $newCell && $newCell != "" && $newCell != null){
				return $this->connectionDirection($newX, $newY, $deltaX, $deltaY) + 1;
			} else {
				return 0;
			}
		}

		// Checks if the coordinate is within the board.
		private function withinBoard($x, $y){
			$xWithin = $x < count($this->board[0]) && $x >= 0;
			$yWithin = $y < count($this->board) && $y >= 0;
			return $xWithin && $yWithin;
		}
	}
?>