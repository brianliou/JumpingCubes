JumpingCubes
============

Rules of JumpingCubes
- A game board consists of N x N squares (1 to N)
- At anytime a square can be either red, white, or blue AND have some number of spots
- NEIGHBORS of a square are the horizontally and vertically adjacent squares
- A square is OVERFULL when the number of spots it contains is more than the square has NEIGHBORS
- A MOVE consists of adding a spot to a square which does not have the opponents color
- SPILLING occurs when a move makes a square OVERFULL and a spot is added to each of its NEIGHBORS and the COLOR is transformed for each square
- A player wins when all the squares are the player's color

Running JumpingCubes
- See README document for additional details
- Compile java files using "make" command
- Then type "java jump61.Main" to begin the game
