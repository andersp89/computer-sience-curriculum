"""Typing test implementation"""

from utils import *
from ucb import main, interact, trace
from datetime import datetime


###########
# Phase 1 #
###########


def choose(paragraphs, select, k):
    """Return the Kth paragraph from PARAGRAPHS for which SELECT called on the
    paragraph returns true. If there are fewer than K such paragraphs, return
    the empty string.
    """
    # BEGIN PROBLEM 1
    "*** YOUR CODE HERE ***"
    acc_para = [para for para in paragraphs if select(para)]

    if k < len(acc_para):
        return acc_para[k]

    return ''
    # END PROBLEM 1


def about(topic):
    """Return a select function that returns whether a paragraph contains one
    of the words in TOPIC.

    >>> about_dogs = about(['dog', 'dogs', 'pup', 'puppy'])
    >>> choose(['Cute Dog!', 'That is a cat.', 'Nice pup!'], about_dogs, 0)
    'Cute Dog!'
    >>> choose(['Cute Dog!', 'That is a cat.', 'Nice pup.'], about_dogs, 1)
    'Nice pup.'
    """
    assert all([lower(x) == x for x in topic]), 'topics should be lowercase.'
    # BEGIN PROBLEM 2
    "*** YOUR CODE HERE ***"
    def helper(para):
        para_as_list = split(lower(remove_punctuation(para)))
        for elem in topic:
            if elem in para_as_list:
                return True
        return False
    return helper
    # END PROBLEM 2


def accuracy(typed, reference):
    """Return the accuracy (percentage of words typed correctly) of TYPED
    when compared to the prefix of REFERENCE that was typed.

    >>> accuracy('Cute Dog!', 'Cute Dog.')
    50.0
    >>> accuracy('A Cute Dog!', 'Cute Dog.')
    0.0
    >>> accuracy('cute Dog.', 'Cute Dog.')
    50.0
    >>> accuracy('Cute Dog. I say!', 'Cute Dog.')
    50.0
    >>> accuracy('Cute', 'Cute Dog.')
    100.0
    >>> accuracy('', 'Cute Dog.')
    0.0
    """
    typed_words = split(typed)
    reference_words = split(reference)
    # BEGIN PROBLEM 3
    "*** YOUR CODE HERE ***"
    if len(typed_words) == 0:
        return 0.0
    
    matched_words = 0
    for i in range(min(len(typed_words), len(reference_words))):
        if typed_words[i] == reference_words[i]:
            matched_words += 1

    """while i < len(typed_words) and i < len(reference_words):
        if typed_words[i] == reference_words[i]:
            matched_words += 1
        i += 1"""

    return matched_words / len(typed_words) * 100
    # END PROBLEM 3


def wpm(typed, elapsed):
    """Return the words-per-minute (WPM) of the TYPED string."""
    assert elapsed > 0, 'Elapsed time must be positive'
    # BEGIN PROBLEM 4
    "*** YOUR CODE HERE ***"
    chars_typed = len(typed)
    words_typed = chars_typed / 5
    sec_to_min = 60 / elapsed

    return words_typed * sec_to_min
    # END PROBLEM 4


def autocorrect(user_word, valid_words, diff_function, limit):
    """Returns the element of VALID_WORDS that has the smallest difference
    from USER_WORD. Instead returns USER_WORD if that difference is greater
    than LIMIT.
    """
    # BEGIN PROBLEM 5
    "*** YOUR CODE HERE ***"
    if user_word in valid_words:
        return user_word
    else:
        word = min(valid_words, key=lambda x: diff_function(user_word, x, limit))
        if diff_function(user_word, word, limit) <= limit:
            return word
        else:
            return user_word
        
        #min_diff, corrected_word = 10000, ""
        """for valid_word in valid_words:    
            diff = diff_function(user_word, valid_word, limit)
            if diff < min_diff and diff <= limit:
                min_diff = diff
                corrected_word = valid_word

        if len(corrected_word) == 0: 
            return user_word
        else:
            return corrected_word"""
    # END PROBLEM 5


def swap_diff(start, goal, limit):
    """A diff function for autocorrect that determines how many letters
    in START need to be substituted to create GOAL, then adds the difference in
    their lengths.
    """
    # BEGIN PROBLEM 6
    if start == goal:
        return 0
    elif start == "" or goal == "": # if all chars checked, return the remaining longest word length
        return max(len(start), len(goal))
    elif limit == 0:
        return limit + 1
    elif start[0] != goal[0]:
        return 1 + swap_diff(start[1:], goal[1:], limit-1) #[1:] removes first char 
    else:
        return swap_diff(start[1:], goal[1:], limit)

    #fjerne 
    """# Made non-recursive:
    chars_to_chg_in_start = 0
    for i in range(min(len(start), len(goal))):
        if start[i] != goal[i]:
            chars_to_chg_in_start += 1
            if chars_to_chg_in_start > limit:
                return limit + 1
    
    diff_in_length = abs(len(start) - len(goal))
    return chars_to_chg_in_start + diff_in_length"""
    # END PROBLEM 6

def edit_diff(start, goal, limit):
    """A diff function that computes the edit distance from START to GOAL."""
    if start == goal: # Fill in the condition
        # BEGIN
        "*** YOUR CODE HERE ***"
        return 0
        # END
    elif start == "" or goal == "": # Feel free to remove or add additional cases
        # BEGIN
        "*** YOUR CODE HERE ***"
        return max(len(start), len(goal))
        # END
    elif limit == 0:
        return limit + 1
    elif start[0] == goal[0]:
        return edit_diff(start[1:], goal[1:], limit)
    else:
        add_diff = 1 + edit_diff(goal[0] + start, goal, limit - 1)  # Fill in these lines
        remove_diff = 1 + edit_diff(start[1:], goal, limit -1)
        substitute_diff = 1 + edit_diff(start[1:], goal[1:], limit - 1)
        # BEGIN
        "*** YOUR CODE HERE ***"
        return min(add_diff, remove_diff, substitute_diff)
        # END


def final_diff(start, goal, limit):
    """A diff function. If you implement this function, it will be used."""
    assert False, 'Remove this line to use your final_diff function'




###########
# Phase 3 #
###########


def report_progress(typed, prompt, id, send):
    """Send a report of your id and progress so far to the multiplayer server."""
    # BEGIN PROBLEM 8

    words_typed_correctly = 0
    for i in range(min(len(typed), len(prompt))):
        if typed[i] == prompt[i]:
            words_typed_correctly += 1
        else:
            break
    
    progress = words_typed_correctly / len(prompt)
    message = {'id': id, 'progress': progress}
    send(message)
    return progress
    # END PROBLEM 8


def fastest_words_report(word_times):
    """Return a text description of the fastest words typed by each player."""
    fastest = fastest_words(word_times)
    report = ''
    for i in range(len(fastest)):
        words = ','.join(fastest[i])
        report += 'Player {} typed these fastest: {}\n'.format(i + 1, words)
    return report


def fastest_words(word_times, margin=1e-5):
    """A list of which words each player typed fastest."""
    n_players = len(word_times)
    n_words = len(word_times[0]) - 1
    assert all(len(times) == n_words + 1 for times in word_times)
    assert margin > 0
    # BEGIN PROBLEM 9
    "*** YOUR CODE HERE ***"
    fastest_words_lists = []
    for i in range(n_players):
        fastest_words_lists.append([])

    for j in range(n_words):
        min_elapsed_time = 1000
        # Find fastest time for each word
        for i in range(n_players):
            time_for_player = elapsed_time(word_times[i][j+1]) - elapsed_time(word_times[i][j])
            if time_for_player < min_elapsed_time:
                min_elapsed_time = time_for_player
        # Append fastest word to player if within margin
        for i in range(n_players):
            if elapsed_time(word_times[i][j+1]) - elapsed_time(word_times[i][j]) - margin < min_elapsed_time:
                fastest_words_lists[i].append(word(word_times[i][j+1]))

    return fastest_words_lists
    # END PROBLEM 9


def word_time(word, elapsed_time):
    """A data abstrction for the elapsed time that a player finished a word."""
    return [word, elapsed_time]


def word(word_time):
    """An accessor function for the word of a word_time."""
    return word_time[0]


def elapsed_time(word_time):
    """An accessor function for the elapsed time of a word_time."""
    return word_time[1]


enable_multiplayer = False  # Change to True when you


##########################
# Command Line Interface #
##########################


def run_typing_test(topics):
    """Measure typing speed and accuracy on the command line."""
    paragraphs = lines_from_file('data/sample_paragraphs.txt')
    select = lambda p: True
    if topics:
        select = about(topics)
    i = 0
    while True:
        reference = choose(paragraphs, select, i)
        if not reference:
            print('No more paragraphs about', topics, 'are available.')
            return
        print('Type the following paragraph and then press enter/return.')
        print('If you only type part of it, you will be scored only on that part.\n')
        print(reference)
        print()

        start = datetime.now()
        typed = input()
        if not typed:
            print('Goodbye.')
            return
        print()

        elapsed = (datetime.now() - start).total_seconds()
        print("Nice work!")
        print('Words per minute:', wpm(typed, elapsed))
        print('Accuracy:        ', accuracy(typed, reference))

        print('\nPress enter/return for the next paragraph or type q to quit.')
        if input().strip() == 'q':
            return
        i += 1


@main
def run(*args):
    """Read in the command-line argument and calls corresponding functions."""
    import argparse
    parser = argparse.ArgumentParser(description="Typing Test")
    parser.add_argument('topic', help="Topic word", nargs='*')
    parser.add_argument('-t', help="Run typing test", action='store_true')

    args = parser.parse_args()
    if args.t:
        run_typing_test(args.topic)