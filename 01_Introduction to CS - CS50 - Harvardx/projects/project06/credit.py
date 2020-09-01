from cs50 import get_string


def main():
    getNumber()


def getNumber():
    cardNumber = get_string("Number:")

    if cardNumber.isdigit() == False:
        getNumber()

    # Get first and second digit of provided number
    firstDigitCard = int(cardNumber[0])
    secondDigitCard = int(cardNumber[1])

    # (1) Multiply every second digit with 2, starting with second last, and then add those products’ digits together.
    # Get card number's length
    cardNumberLength = len(cardNumber)

    # Position of every second digit
    position = 1
    # List for every second digit
    everySecondDigitList = []
    # Number of multiplications is card number's length divided by 2.
    for i in range(cardNumberLength // 2):
        # while "- 1" of cardNumberLength as string is starting at 0
        everySecondDigitList.append(int(cardNumber[cardNumberLength - 1 - position]) * 2)
        position += 2

    # Add products' digits together
    everySecondDigitSum = 0
    # If 2-digit number, i.e. >= 10, then sum each digit individually
    for i in range(len(everySecondDigitList)):
        if everySecondDigitList[i] >= 10:
            firstDigit = everySecondDigitList[i] % 10
            secondDigit = everySecondDigitList[i] // 10
            everySecondDigitSum += firstDigit + secondDigit
        else:
            everySecondDigitSum += everySecondDigitList[i]

    # (2) Add the sum to the sum of the digits that weren’t multiplied by 2.
    # List for remaining digits and variable for position
    remainingDigits = []
    remainingPosition = 0
    for i in range(cardNumberLength - (cardNumberLength // 2)):
        remainingDigits.append(int(cardNumber[cardNumberLength - 1 - remainingPosition]))
        remainingPosition += 2

    remainingSum = 0
    for i in range(len(remainingDigits)):
        remainingSum += remainingDigits[i]

    sumAll = everySecondDigitSum + remainingSum

    # (3) If the total’s last digit is 0 (or, put more formally, if the total modulo 10 is congruent to 0), the number is valid!
    if sumAll % 10 == 0:
        checkCardType(firstDigitCard, secondDigitCard, cardNumberLength)
    else:
        print("INVALID")
        return


# Check card type
def checkCardType(firstDigit, secondDigit, nDigits):
    # American Express uses 15-digit numbers, starts with 34 or 37
    if firstDigit == 3 and (secondDigit == 4 or secondDigit == 7) and nDigits == 15:
        print("AMEX")
        return
    # MasterCard uses 16-digit numbers, starts with 51, 52, 53, 54, or 55
    elif firstDigit == 5 and (secondDigit == 1 or secondDigit == 2 or secondDigit == 3 or secondDigit == 4 or secondDigit == 5) and (nDigits == 16):
        print("MASTERCARD")
        return
    # Visa uses 13- and 16-digit numbers, starts with 4
    elif firstDigit == 4 and (nDigits == 13 or nDigits == 16):
        print("VISA")
        return
    else:
        print("INVALID")
        return


if __name__ == "__main__":
    main()