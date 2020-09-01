// Implements a dictionary's functionality

#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include "dictionary.h"

// defining data structure node, to be insterted in hash table
typedef struct node
{
    char word[LENGTH + 1];
    // a pointer for word to point to
    struct node *next;
} node;

// hashtable, i.e. array of 26 for each letter in alphabet.
node *hashtable[26];

// global counter for dictionary size
unsigned int dictionarySizeCounter = 0;
// bool to check whether dictionary has been loaded
bool dictionaryLoaded = false;

// #1 Loads dictionary into memory, returning true if successful else false
bool load(const char *dictionary)
{
    // open dictionary
    FILE *fileDictionary = fopen(dictionary, "r");

    // tjeck if file is OK
    if (fileDictionary == NULL)
    {
        fprintf(stderr, "Could not open %s.\n", dictionary);
        return false;
    }

    // variable to save each string from dictionary. Max 45 characters, last char for "\o" null value, to indicate end of string
    char word[LENGTH+1];

    // scan dictionary word by word
    while(fscanf(fileDictionary,"%s", word) != EOF)
    {
        // make a new word
        node *new_word = malloc(sizeof(node));

        // check if available space in memory
        if(new_word == NULL)
        {
            unload();
            return false;
        }

        // copy word into node
        strcpy(new_word -> word, word);

        // hashing function to find index of hashtable
        int index = word[0] - 97;

        // insert node into beginning of linked list, to save checking where the end of linked list is.
        new_word -> next = hashtable[index];
        hashtable[index] = new_word;
        dictionarySizeCounter++;
    }

    // for count to return size of dictionary
    dictionaryLoaded = true;

    // close fileDictionary, when dictionary has been loaded into memory
    fclose(fileDictionary);

    // return true at succes
    return true;
}

// #2 Returns true if word is in dictionary else false
bool check(const char *word)
{
    // variable to save lowercase word
    char lowercaseWord[LENGTH+1];
    int i = 0;

    // make all characters of string to lower case
    while(word[i])
    {
        lowercaseWord[i] = tolower(word[i]);
        i++;
    }

    // Add null at last position of string
    int len = strlen(word);
    lowercaseWord[len] = '\0';

    // Make cursor point to head of hashtable
    int index = lowercaseWord[0] - 97;
    node *cursor = hashtable[index];

    // Search linked list for word, until NULL, i.e. end of list
    while (cursor != NULL)
    {
        // Compare strings, equal if 0
        if (strcmp(lowercaseWord, cursor -> word) == 0)
        {
            return true;
        }

        // Reassign cursor to what the current node is pointing to
        cursor = cursor -> next;
    }
    // If word is not found in dictionary, return false
    return false;
}

// #3 Returns number of words in dictionary if loaded else 0 if not yet loaded
unsigned int size(void)
{
    if (dictionaryLoaded == true)
    {
        return dictionarySizeCounter;
    }
    return 0;
}

// #4 Unloads dictionary from memory, returning true if successful else false
bool unload(void)
{
    // go through each index of hashtable
    int i = 0;
    for (i = 0; i < 26; i++)
    {
        // make cursor point to head of hashtable
        node *cursor = hashtable[i];

        // go to each node in linked list and free memory
        while (cursor != NULL)
        {
	        node *temp = cursor;
	        cursor = cursor -> next;
	        free(temp);
        }
    }

    // check if for loop was completed
    if (i == 26)
    {
        return true;
    }
    else
    {
        return false;
    }
}
