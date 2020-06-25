from cs50 import get_string
from cs50 import get_int
import sys


def main():
    # Check that user input only 2 arguments
    if len(sys.argv) != 2:
        print("Usage: python caesar.py k")
        exit(1)

    # Get key from argument provided
    k = int(sys.argv[1])

    # Prompt user for plain text
    s = get_string("plaintext: ")
    # And string for cipher
    cipher = ""

    # Convert to Ciphertext!
    for i in range(len(s)):
        if s[i].isupper():
            # Shift plaintext char by key k
            # Subtract 65, to get the position of the uppercase in the English alphabet
            charInAlphabet = ord(s[i]) - 65
            # Shift character k positions (Divided by 26, as number of letter in alphabet), and add 65 to convert to ASCII
            charAsASCII = chr((charInAlphabet + k) % 26 + 65)
            # Build cipher string
            cipher += charAsASCII
        elif s[i].islower():
            # Shift plaintext char by key k
            # Subtract 97, to get the position of the uppercase in the English alphabet
            charInAlphabet = ord(s[i]) - 97
            # Shift character k positions (Divided by 26, as number of letter in alphabet), and add 65 to convert to ASCII
            charAsASCII = chr((charInAlphabet + k) % 26 + 97)
            # Build cipher string
            cipher += charAsASCII
        else:
            cipher += s[i]

    print("ciphertext:", cipher)
    return 0


if __name__ == "__main__":
    main()