# Questions

## What is pneumonoultramicroscopicsilicovolcanoconiosis?

Pneumonoultramicroscopicsilicovolcanoconiosis is with its 45 characters the longest word in the English language. It is the word of a lung disease. 

## According to its man page, what does `getrusage` do?

getrusage get's the resource usage for who, which can be the calling process, all children of the calling process, or the calling process only.

## Per that same man page, how many members are in a variable of type `struct rusage`?

16.

## Why do you think we pass `before` and `after` by reference (instead of by value) to `calculate`, even though we're not changing their contents?

Because, before and after are structs of type "struct rusage", that have the values inside them. 

## Explain as precisely as possible, in a paragraph or more, how `main` goes about reading words from a file. In other words, convince us that you indeed understand how that function's `for` loop works.

The outer for loop initilizes the counter variable 'c' with the first unsigned char of text file, by using the library function "fgetc". The for loop ends, when the "fgetc" function returns EOF, i.e. when 
all text have been read. Lastly, the c is updated with the next unsigned char at every cicle through the for-loop. 
The first if, checks that the character is alpabetical or apostrophes. If not, it is a digit and can be ignored. If it is a character, initiate creation of a word by appending character to word and increment 
variable index with 1, to check length of word.
The next if, check whether word is longer than 45 chars, if so, word can be ignored, that is, not checked.
The for loop continues to append characters to word array till it reaches NULL, i.e. end of word (\0). It then terminates the current word and update the counter for number of words found. While, measuring 
the resources used to check word in dictionary with function "check". It passes the time to "calculate" function, that keeps track of the total time used to execute program.

## Why do you think we used `fgetc` to read each word's characters one at a time rather than use `fscanf` with a format string like `"%s"` to read whole words at a time? Put another way, what problems might arise by relying on `fscanf` alone?

Using fscanf() may cause memory overflow, if reading word longer than 45 chars, as it does not read character by chacter, as fgetc does.

## Why do you think we declared the parameters for `check` and `load` as `const` (which means "constant")?

Constants refer to fixed values that the program may not alter during its execution. This makes sense to utilize in speller.c, as their values should not be modified, when returned from dictionary.c.