// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)

// Put your code here.
	@i
	M=1 // i = 1
	@sum
	M=0 // sum = 0

(LOOP)
	@i
	D=M
	@R1
	D=D-M
	@STOP
	D;JGT // if i > R1 goto STOP, i.e. multiplication done

	@sum
	D=M
	@R0
	D=D+M
	@sum
	M=D // sum = sum + R0
	@i
	M=M+1 // i = i + 1
	@LOOP
	0;JMP // always jump to loop, above

(STOP)
	@sum
	D=M
	@R2
	M=D // save result to RAM[2]

(END)
	@END
	0;JMP // infinite loop to exit program