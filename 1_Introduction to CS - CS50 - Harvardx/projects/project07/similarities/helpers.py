from nltk.tokenize import sent_tokenize


def lines(a, b):
    """Return lines in both a and b"""
    """
    * Given two strings, a and b, it returns a list of the lines that are, identically, in both a and b.
    * List should not contain any duplicates.
    * Assume that lines in a and b will be separated by \n
    * Strings in returned list should not end in \n
    * If both a and b contain one or more blank lines (i.e., a \n immediately preceded by no other characters), the returned list should include an empty string (I.e. “”)
    """
    linesInCommon = []

    for line in a.split("\n"):
        if line in b.split("\n"):
            if line not in linesInCommon:
                linesInCommon.append(line)

    return linesInCommon


def sentences(a, b):
    """Return sentences in both a and b"""
    """
    * given two strings a and b, it returns a list of the unique English sentences that are, identically, present in in both a and b.
    * The list should not contain any duplicates.
    * Use “sent_tokenize” from the Natural Language Toolkit to tokenize (i.e. separate) each string into a list of sentences. It is imported by:
    * from nltk.tokenize import sent_tokenize
    * Per its documentation,sent_tokenize, given a str as input, returns a list of English sentences therein. It assumes that its input is indeed English text (and not, e.g., code, which might coincidentally have periods too).
    """

    sentencesInCommon = []

    englishA = sent_tokenize(a)
    englishB = sent_tokenize(b)

    for sentence in englishA:
        if sentence in englishB:
            if sentence not in sentencesInCommon:
                sentencesInCommon.append(sentence)

    return sentencesInCommon


def substrings(a, b, n):
    """Return substrings of length n in both a and b"""
    """
    * given two strings, a and b, and an integer, n, it returns a list of all substrings of length n that are, identically, present in both a and b.
    * The list should not contain any duplicates.
    * Recall that a substring of length n of some string is just a sequence of n characters from that string. For instance, if n is 2 and the string is Yale, there are three possible substrings of length 2: Ya, al, and le. Meanwhile if n is 1 and the string is Harvard, there are seven possible substrings of length 1: H a r v a r d. But once we eliminate duplicates, there are only five unique substrings: H, a, r, v, d
    """

    # Create substrings a[i:j] gives substring from i to j, j not included
    substringA = [a[i:i+n] for i in range(0, len(a), 1) if len(a[i:i+n]) >= n]
    substringB = [b[i:i+n] for i in range(0, len(b), 1) if len(b[i:i+n]) >= n]

    # Eliminate duplicates
    uniqueSubstringA = []
    for substring in substringA:
        if substring not in uniqueSubstringA:
            uniqueSubstringA.append(substring)

    uniqueSubstringB = []
    for substring in substringB:
        if substring not in uniqueSubstringB:
            uniqueSubstringB.append(substring)

    # Check for identicals, between a and b
    commonSubstrings = [substring for substring in uniqueSubstringA if substring in uniqueSubstringB]

    return commonSubstrings
