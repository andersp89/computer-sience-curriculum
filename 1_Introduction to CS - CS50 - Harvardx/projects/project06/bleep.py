from cs50 import get_string
from sys import argv


# Words in banned
bannedWords = set()


def main():

    if len(argv) != 2:
        print("Usage: python bleep.py dictionary")
        exit(1)

    userMessage = get_string("Message to protect from cursing-words:")

    load(argv[1])
    check(userMessage)
    exit(0)


def load(dictionary):
    """Load dictionary into memory, returning true if successful else false"""
    file = open(dictionary, "r")
    for line in file:
        bannedWords.add(line.rstrip("\n"))
    file.close()
    return


def check(userMessage):
    """Return true if word is in dictionary else false"""
    userMessageConverted = ""
    for word in userMessage.split():
        if word.lower() in bannedWords:
            newWord = ""
            for char in word:
                newWord += "*"
            userMessageConverted += newWord + " "
        else:
            userMessageConverted += word + " "

    print(userMessageConverted)
    return


if __name__ == "__main__":
    main()
