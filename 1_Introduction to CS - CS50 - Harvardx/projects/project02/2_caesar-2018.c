#include <stdio.h>
#include <cs50.h>
#include <string.h>
#include <ctype.h>

int main(int argc, string argv[])
{

    if (argc == 2)
    {
        // Turn key into integer
        int k = atoi(argv[1]);
        // Promt user for plain text:
        string s = get_string("plaintext: ");

        // For each char in string
        for (int i = 0; i < strlen(s); i++)
        {
            // Check if char is alphabetic (not a space etc.)
            if (isalpha(s[i]) != 0)
            {
                // Preserve case upper
                if (isupper(s[i]))
                {
                    // Shift plaintext char by key k
                    // Subtract 65, to get the position of the uppercase in the alphabet
                    int mem = s[i] - 65;
                    // Shift character k positions
                    mem = (mem + k) % 26;
                    // Alphabetical to ASCII
                    s[i] = mem + 65;
                }
                // Preserve case lower
                if (islower(s[i]))
                {
                    // Shift plaintext char by key k
                    // Subtract 97, to get the position of the lowercase char in the alphabet
                    int mem = s[i] - 97;
                    // Shift character k positions
                    mem = (mem + k) % 26;
                    // Alphabetical to ASCII
                    s[i] = mem + 97;
                }
            }
        }
        // Print ciphertext
        printf("ciphertext: %s\n", s);
        return 0;
    }
    // If more than two arguments provided
    else if (argc > 2)
    {
        printf("Please provide only one argument\n");
        return 1;
    }
    // If no arguments provided
    else
    {
        printf("Please provide one argument\n");
        return 1;
    }
}