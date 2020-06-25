// Helper functions for music

#include <cs50.h>
#include <math.h>
#include <string.h>
#include <stdio.h>
#include "helpers.h"

// Converts a fraction formatted as X/Y to eighths
int duration(string fraction)
{
    int x = atoi(&fraction[0]);
    int y = atoi(&fraction[2]);
    // If duration is either 1/8, 3/8, or 8/8
    if (y / 2 == 4)
    {
        if (x == 1)
        {
            return 1;
        }
        else if (x == 3)
        {
            return 3;
        }
        else if (x == 8)
        {
            return 8;
        }
    }

    // If duration is 1/4
    if (y / 2 == 2)
    {
        return 2;
    }

    // If duration is 1/2
    if (y / 2 == 1)
    {
        return 4;
    }

    return 1;
}

// Calculates frequency (in Hz) of a note
int frequency(string note)
{
    // Letter (A-G), Accidental, and Octave (0-8)
    char letter;
    char accidental;
    float octave;

    // Define distance in semitones from A4
    float semiToneDistance;

    // If NO sharp (b or #) in node
    if (strlen(note) == 2)
    {
        // Define Herz based on A4
        if (note[1] == '0')
        {
            octave = 27.5;
        }
        else if (note[1] == '1')
        {
            octave = 55;
        }
        else if (note[1] == '2')
        {
            octave = 110;
        }
        else if (note[1] == '3')
        {
            octave = 220;
        }
        else if (note[1] == '4')
        {
            octave = 440;
        }
        else if (note[1] == '5')
        {
            octave = 880;
        }
        else if (note[1] == '6')
        {
            octave = 1760;
        }
        else if (note[1] == '7')
        {
            octave = 3520;
        }
        else if (note[1] == '8')
        {
            octave = 7040;
        }

        letter = note[0];
        // Define distance to A for letter
        if (letter == 'C')
        {
            semiToneDistance = -9;
            float frequenceHz = pow(2, semiToneDistance / 12) * octave;
            return round(frequenceHz);
        }
        else if (letter == 'D')
        {
            semiToneDistance = -7;
            float frequenceHz = pow(2, semiToneDistance / 12) * octave;
            return round(frequenceHz);
        }
        else if (letter == 'E')
        {
            semiToneDistance = -5;
            float frequenceHz = pow(2, semiToneDistance / 12) * octave;
            return round(frequenceHz);
        }
        else if (letter == 'F')
        {
            semiToneDistance = -4;
            float frequenceHz = pow(2, semiToneDistance / 12) * octave;
            return round(frequenceHz);
        }
        else if (letter == 'G')
        {
            semiToneDistance = -2;
            float frequenceHz = pow(2, semiToneDistance / 12) * octave;
            return round(frequenceHz);
        }
        else if (letter == 'A')
        {
            semiToneDistance = 0;
            float frequenceHz = pow(2, semiToneDistance / 12) * octave;
            return round(frequenceHz);
        }
        else if (letter == 'B')
        {
            semiToneDistance = 2;
            float frequenceHz = pow(2, semiToneDistance / 12) * octave;
            return round(frequenceHz);
        }
    }

    // If a sharp (b or #)
    if (strlen(note) == 3)
    {
        // Define Herz based on A4
        if (note[2] == '0')
        {
            octave = 27.5;
        }
        else if (note[2] == '1')
        {
            octave = 55;
        }
        else if (note[2] == '2')
        {
            octave = 110;
        }
        else if (note[2] == '3')
        {
            octave = 220;
        }
        else if (note[2] == '4')
        {
            octave = 440;
        }
        else if (note[2] == '5')
        {
            octave = 880;
        }
        else if (note[2] == '6')
        {
            octave = 1760;
        }
        else if (note[2] == '7')
        {
            octave = 3520;
        }
        else if (note[2] == '8')
        {
            octave = 7040;
        }

        letter = note[0];
        accidental = note[1];
        // Define distance to A for letter
        if (letter == 'C')
        {
            semiToneDistance = -8;
            float frequenceHz = pow(2, semiToneDistance / 12) * octave;
            return round(frequenceHz);
        }
        else if (letter == 'D' && accidental == 'b')
        {
            semiToneDistance = -8;
            float frequenceHz = pow(2, semiToneDistance / 12) * octave;
            return round(frequenceHz);
        }
        else if (letter == 'D' && accidental == '#')
        {
            semiToneDistance = -6;
            float frequenceHz = pow(2, semiToneDistance / 12) * octave;
            return round(frequenceHz);
        }
        else if (letter == 'E')
        {
            semiToneDistance = -6;
            float frequenceHz = pow(2, semiToneDistance / 12) * octave;
            return round(frequenceHz);
        }
        else if (letter == 'F')
        {
            semiToneDistance = -3;
            float frequenceHz = pow(2, semiToneDistance / 12) * octave;
            return round(frequenceHz);
        }
        else if (letter == 'G' && accidental == 'b')
        {
            semiToneDistance = -3;
            float frequenceHz = pow(2, semiToneDistance / 12) * octave;
            return round(frequenceHz);
        }
        else if (letter == 'G' && accidental == '#')
        {
            semiToneDistance = -1;
            float frequenceHz = pow(2, semiToneDistance / 12) * octave;
            return round(frequenceHz);
        }
        else if (letter == 'A' && accidental == 'b')
        {
            semiToneDistance = -1;
            float frequenceHz = pow(2, semiToneDistance / 12) * octave;
            return round(frequenceHz);
        }
        else if (letter == 'A' && accidental == '#')
        {
            semiToneDistance = 1;
            float frequenceHz = pow(2, semiToneDistance / 12) * octave;
            return round(frequenceHz);
        }
        else if (letter == 'B')
        {
            semiToneDistance = 1;
            float frequenceHz = pow(2, semiToneDistance / 12) * octave;
            return round(frequenceHz);
        }
    }
    return 1;
}

// Determines whether a string represents a rest
bool is_rest(string s)
{
    if (strcmp(s, ""))
    {
        return false;
    }
    else
    {
        return true;
    }
}