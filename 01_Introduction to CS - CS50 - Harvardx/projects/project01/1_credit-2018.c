#include <stdio.h>
#include <cs50.h>
#include <math.h>

int main(void)
{

    // Credit card number
    long cc = get_long_long("Please provide me your credit card numer: ");

    // Get number of digits in number
    int nDigits = floor(log10(cc)) + 1;

    // Variables for 2 first digits
    int firstDigit;
    int secondDigit;

    // Go to the first decimal, pow increases 10, 100, 1000 etc.
    firstDigit = cc / pow(10, nDigits - 1);
    // Get decimal, as it is the rest when divided by 10
    firstDigit = firstDigit % 10;
    // Go to the second decimal
    secondDigit = cc / pow(10, nDigits - 2);
    // Get decimal, as it is the rest when divided by 10
    secondDigit = secondDigit % 10;

    // Checking whether numbers match
    // (1) Multiply every second digit with two, starting with second last
    // 1.1 saves input in an array
    long everySecond;
    int arrayNo[nDigits];
    for (int i = 0; i < nDigits; i++)
    {
        // Go through all decimals
        everySecond = cc / pow(10, i);
        // Get decimal, i.e. the rest
        arrayNo[i] = everySecond % 10;
    };

    // 1.2 takes every second, starting from second last, and multiply by 2
    for (int i = 1; i < nDigits; i += 2)
    {
        arrayNo[i] *= 2;
    };

    // (2) Add those products' digits to a sum
    int sum;
    // Loop through every second number
    for (int i = 1; i < nDigits; i += 2)
    {
        // If number is greater than 10, add each decimal of number seperately
        if ((arrayNo[i] / 10) >= 1)
        {
            // 1st decimal, starting from last
            int dec1;
            dec1 = arrayNo[i] % 10; // Get decimal, i.e. rest

            // 2nd decimal, i.e. the remainder
            int dec2;
            dec2 = arrayNo[i] / 10;
            dec2 = dec2 % 10;

            // Calculate sum of both decimals
            sum = sum + dec1 + dec2;
        }
        // If number is less than 10, add digit to sum
        else
        {
            sum += arrayNo[i];
        };
    };

    // (3) Add that sum to the sum of the digits that weren’t multiplied by 2
    // Loop through array, starting with zero and then every second (untouched numbers)
    for (int i = 0; i < nDigits; i += 2)
    {
        sum += arrayNo[i];
    };

    // American Express uses 15-digit numbers, starts with 34 or 37
    // MasterCard uses 16-digit numbers, starts with 51, 52, 53, 54, or 55
    // Visa uses 13- and 16-digit numbers, starts with 4

    // American Express
    if ((sum % 10 == 0) && (firstDigit == 3 && (secondDigit == 4 || secondDigit == 7)) && (nDigits == 15))
    {
        printf("AMEX\n");
        return 0;
    }
    // MasterCard
    else if ((sum % 10 == 0) && (firstDigit == 5 && (secondDigit == 1 || secondDigit == 2 || secondDigit == 3 || secondDigit == 4
                                 || secondDigit == 5)) && (nDigits == 16))
    {
        printf("MASTERCARD\n");
        return 0;
    }
    // Visa
    else if ((sum % 10 == 0) && (firstDigit == 4) && (nDigits == 13 || nDigits == 16))
    {
        printf("VISA\n");
        return 0;
    }
    else
    {
        printf("INVALID\n");
        return 0;
    };
    return 0;
};