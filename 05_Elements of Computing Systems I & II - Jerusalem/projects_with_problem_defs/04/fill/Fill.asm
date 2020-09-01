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

(LISTENTOKEYB)
	// initialize variables
	@SCREEN
	D=A
	@curraddr
	M=D
	@i
	M=0

	// decide color
	@KBD
	D=M
	@COLORBLACK
	D;JNE // if not 0, i.e. key is pressed
	@COLORWHITE
	D;JEQ // if 0, i.e. no key pressed

(COLORBLACK)
	@i
	D=M
	@8191
	D=D-A
	@LISTENTOKEYB
	D;JGT // i > 8192, i.e. if screen is colored 
	
	@curraddr
	A=M // the address of curraddr and not curraddr itself
	M=-1 // RAM[addr] = 1111111111111111 (black)
	@i
	M=M+1 // i = i + 1
	@curraddr
	M=M+1 // curraddr = curraddr + i
	@COLORBLACK
	0;JMP // always jump, next byte

(COLORWHITE)
	@i
	D=M
	@8191
	D=D-A
	@LISTENTOKEYB
	D;JGT // i > 8192, i.e. if screen is colored 
	
	@curraddr
	A=M // the address of curraddr and not curraddr itself
	M=0 // RAM[curraddr] = white
	@i
	M=M+1 // i = i + 1
	@curraddr
	M=M+1 // curraddr = curraddr + i
	@COLORWHITE
	0;JMP // always jump, next byte
