#define _XOPEN_SOURCE
#include <unistd.h>
#include <stdio.h>
#include <string.h>
#include <crypt.h>
#include <cs50.h>
#include <stdlib.h>

/*
Program to crack password encrypted by DES-based crypt(*key, salt) (*key is password of user), using brute force (trying every possible combination).

Solution is recursive, meaning:
- Base case, i.e. when to stop: checkValidityOfKey
- Work toward base case: bruteByIndex
- Recursive call, i.e. call its own function: bruteForce

The logic of the modules:
- Main: getting hash from command line and salt (first two chars of hash), while executing bruteByIndex
- bruteByIndex: execute brute-force attack per index in array till maxLength is reached, defined by main:
_ \0
_ _ \0
_ _ _ \0
_ _ _ _ \0
_ _ _ _ _ \0
- bruteForce: implement brute-force by trying all possible combinations of chars in alphabet, for each combination check validity of key
- checkValidityOfKey: for each combination in bruteForce, check if it matches the input hash from commandline, if so, then exit with 0
*/

// Alphabet with capital- and lower case letters
static const char alphabet[] =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
static const int alphabetSize = sizeof(alphabet) - 1;

// Check for validity of key in brute-force attack
bool checkValidityOfKey(char *key, char *salt, char *hash)
{
    if (!strcmp(crypt(key, salt), hash))
    {
        printf("Brute-force attack succesful. Password is: %s\n", key);
        return true;
    }
    return false;
}

// Make brute-force attack for keyToCrack, once for each index-length (decided by main) of array.
// I.e. first brute-force for keyToCrack[1], then keyToCrack[2] (by index value more than >1, the function is calling itself till index reaches maxLength!
void bruteForce(char *keyToCrack, int index, int maxLength, char *salt, char *hash)
{
    // Try every possible letter in alphabet for index of array, starting from 0
    for (int i = 0; i < alphabetSize; ++i)
    {
        // Initialize first index value to first value of alphabet
        keyToCrack[index] = alphabet[i];

        // Try alphabet once for each index value.
        if (index == maxLength - 1)
        {
            if (checkValidityOfKey(keyToCrack, salt, hash))
            {
                // Exit, if hash matches hash from command line
                exit(0);
            }
        }
        // Call itself, till index reaches maxLength
        else
        {
            bruteForce(keyToCrack, index + 1, maxLength, salt, hash);
        }
    }
}

// Execute brute-force for each index value of array till maxLength (i.e. "i").
// Remember, always start brute force from 0, hence inject 0 for index when executing bruteForce
void bruteByIndex(int maxLength, char *salt, char *hash)
{
    // Make index size 1 bigger, to accomodate for "\0" char
    char keyToCrack[maxLength + 1];

    for (int i = 1; i <= maxLength; ++i)
    {
        printf("Trying char no.: %i\n", i);
        bruteForce(keyToCrack, 0, i, salt, hash);
    }
}

// Get hash and salt from commandline and execute bruteforce for key of max 5 chars
int main(int argc, char *argv[])
{
    if (argc != 2)
    {
        printf("Provide one argument.\n");
        return 1;
    }

    // Get salt
    char salt[2];
    // Copy first two chars of argv[1]
    strncpy(salt, argv[1], 2);

    // Get hash from commandline
    char *hash = argv[1];


    // Initiate brute-force attack for key of max 5 chars
    bruteByIndex(5, salt, hash);
    return 0;
}