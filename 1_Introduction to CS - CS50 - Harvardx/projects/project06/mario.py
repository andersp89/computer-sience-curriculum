from cs50 import get_int


def main():
    getInt()


def getInt():
    i = get_int("Height: ")

    # check if value a positive integer between 1 and 8, both included
    if i < 1 or i > 8:
        getInt()

    printPyramid(i)


def printPyramid(height):
    actualLevel = height - 1
    for i in range(height):
        # Print spaces before #
        for j in range(actualLevel):
            print(" ", end='')
        # Print left #s
        for k in range(height-actualLevel):
            print("#", end='')
        # Print spaces between #-coloumns
        print("  ", end='')
        # Print right #s
        for l in range(height-actualLevel):
            print("#", end='')
        # New line
        print("")
        actualLevel -= 1


if __name__ == "__main__":
    main()