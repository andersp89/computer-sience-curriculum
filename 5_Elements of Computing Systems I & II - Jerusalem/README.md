<h1>Elements of Computing Systems</h1>

Completed course "*Nand to Tetris - Building a Modern Computer From First Principles*" by the University of Israel. The course is focused on building a general purpose computer from first principles, based on the **"Von Neumann Architecture"**, including both hardware and software. It is divided into two separate parts:

* Part 1 - Hardware platform: https://www.coursera.org/learn/build-a-computer
* Part 2 - Software hierarchy: https://www.coursera.org/learn/nand2tetris2

**Part 1**, the hardware platform, is focused on building all hardware components in HDL from a single **NAND gate**, and includes the projects 1 to 6. Please find them below.

**Part 2**, the software hierarchy, is focused on building all software layers to allow for compiling and running programs written in a simple object-oriented programming language called "Hack". Program compilation of "Hack" is following a **two-tier compilation process** (similar to Java). First, high-level **Hack code** is compiled to **VM code** (In Java called "byte code") that can be run on a **Virtual Machine**. Second, the **VM code** is translated into **Asssembly code** by a **VM Translator** (In Java called "JVM Implementation"), and subsequently translated to **Machine code** by an **Assembler**, that can be processed by the CPU of the hardware platform, developed in part 1. Remark, running Hack programs relies on several utility functions of an Operating System, which is also developed, such as: assigning space in memory to Arrays and Strings, handling multiplication and division, and communicating with screen and keyboard. Projects 7 to 12 make up part 2, please find them below.

**Projects of part 1 and 2:**
* [Project 1: Logic Gates](/projects/project01)
* [Project 2: Artithmetic Logic Unit (ALU)](/projects/project02)
* [Project 3: Registers & Memory](/projects/project03)
* [Project 4: Machine Language](/projects/project04)
* [Project 5: Computer Architecture](/projects/project05)
* [Project 6: Assembler](/projects/project06)
* [Project 7: Virtual Machine I](/projects/project07)
* [Project 8: Virtual Machine II](/projects/project08)
* [Project 9: Programming Language "Hack"](/projects/project09)
* [Project 10: Compiler I](/projects/project10)
* [Project 11: Compiler II](/projects/project11)
* [Project 12: Operating System](/projects/project12)