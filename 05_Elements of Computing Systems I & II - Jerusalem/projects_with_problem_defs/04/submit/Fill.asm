// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// Put your code here.
	// initialize keyboard to 0, if program run prior
	@KBD
	M=0
	// initialize variables
	@SCREEN
	D=A
	@Position
	M=D // initial position to screen

	// decide color
(CHECK)
	@KBD
	D=M
	@COLORBLACK
	D;JNE // if not 0, i.e. key is pressed
	@COLORWHITE
	D;JEQ // if 0, i.e. no key pressed

(COLORBLACK)
	@24576 // if at end of screen, i.e. start pos. (16384) + complete screen mem (8191) + 1 (start 0)
	D=A
	@Position
	D=D-M
	@CHECK
	D;JEQ // position at end, i.e. if screen is colored
	
	@Position
	A=M // address of position
	M=-1 // RAM[addr] = 1111111111111111 (black)
	@Position
	M=M+1 // // position = position + 1
	@CHECK
	0;JMP // always jump, check if user holds key

(COLORWHITE)
	@SCREEN // if position at left top of screen, do nothing
	D=A 
	@Position
	D=D-M
	@CHECK
	D;JGT
	
	@Position
	A=M // address of position
	M=0 // RAM[curraddr] = white
	@Position
	M=M-1 // position = position + 1
	@CHECK
	0;JMP // always jump, check if user holds key
