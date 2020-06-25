# Project: Battleship game
## Short description
This game was made as part of Homework assigment 4 of "Software Development Fundamentals" at Penn Engineering.

## Game description
Battleship is usually a two-player game, where each player has a fleet and an ocean (hidden from the other player), and tries to be the first to sink the other player’s fleet. We will do just a solo version, where the computer places the ships, and the human attempts to sink them.

We’ll play this game on a 20x20 ocean. This is larger than the ocean in the traditional battleship game.  In this game we will have one 8-square Battleship, one 7-square Battlecruiser, two 6-square Cruisers, two 5-square Light Cruisers, three 4-square Destroyers and four 3-square Submarines. Finally, unlike the traditional game, a player can shoot 5 times in each turn.

The computer places these 13 ships on the ocean in such a way that no ships are immediately adjacent to each other, either horizontally, vertically, or diagonally.

The human player does not know where the ships are. The initial display of the ocean shows a 20 by 20 array of locations, all the same.

The human player tries to hit the ships, by calling out a row and column number. The computer responds with one bit of information saying ”hit” or ”miss.” When a ship is hit but not sunk, the program does not provide any information about what kind of a ship was hit. However, when a ship is hit and sinks, the program prints out a message ”You just sank a ship-type.” After each shot, the computer redisplays the ocean with the new information.

A ship is ”sunk” when every square of the ship has been hit. Thus, it takes 8 hits to sink a battleship but only 6 to sink a cruiser. The objective is to sink the fleet with as few shots as possible. When all ships have been sunk, the program prints out a message that the game is over, and tells the user how many shots were required.
  
## Architecture
The program consists of the following classes and methods:
* **BattleshipGame**: class with main 
	* void **main**: initiate game and control gaming logic
* **Ocean**: class holding instances of type Ship in 20x20 grid
	* void **placeAllShipsRandomly**: place all ships randomly on empty ocean 
	* boolean **isOccupied**: checks if location in ocean has ship
	* boolean **shootAt**: shoots at position and updates related variables
	* void **print**: prints the ocean
* **Ship**: abstract Class to build ships
	* String **getShipType**: to return ship type of instance
	* boolean **okToPlaceShipAt**: checks if ok to place ship at position
	* boolean **placeShipAt**: update grid with ship and set instance variables
	* boolean **shootAt**: update the hit array of the instance of type Ship
	* boolean **isSunk**: checks if instance of type Ship is sunk 

Class **Ship** has the following subclasses: **BattleCruiser**, **BattleShip**, **Cruiser**, **Destroyer**, **LightCruiser**, **Submarine**, and **EmptySea**. Each subclass initiates instance variables and overwrites abstract method **String getShipType((** to return the corresponding ship type. Further, the class **EmptySea** overwrites additional methods.

Moreover, it has the following test class:
* OceanTest: unit tests for methods of class Ocean

## System dependencies
* JavaSE-1.8
* Eclipse v. 4.13.0