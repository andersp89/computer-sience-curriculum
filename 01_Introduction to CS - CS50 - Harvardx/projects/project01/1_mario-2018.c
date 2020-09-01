#include <stdio.h>
#include <cs50.h>

int main(void)
{
    // Prompt user for height of pyramid
    int pyramidHeight = get_int("Pyramid height: ");

    // If pyramid height is 0, then end program
    if (pyramidHeight == 0)
    {
        EOF;
    }
    // Check for non-negative integer between 1 and 23
    else if (pyramidHeight > 0 && pyramidHeight < 24)
    {
        // Print pyramid with appropriate height
        for (int i = 1; i <= pyramidHeight; i++)
        {
            // Start each row at the right position
            printf("%.*s", pyramidHeight - i, "                         ");
            // Print left side
            printf("%.*s", i, "#######################");
            // Space inbetween left and right rows
            printf("  ");
            // Print right side
            printf("%.*s", i, "#######################");
            // New line for next row
            printf("\n");
        }
    }
    else
    {
        // If number provided is not between 0 and 23, quote user again
        printf("Ups, please provide a number between 0 and 23! You typed: %i. ", pyramidHeight);
        main();
    }
}