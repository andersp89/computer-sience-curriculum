class HangpersonGame

  # add the necessary class methods, attributes, etc. here
  # to make the tests in spec/hangperson_game_spec.rb pass.

  # Get a word from remote "random word" service

  # def initialize()
  # end

  def initialize(word)
    @word = word
    @guesses = ''
    @wrong_guesses = ''
  end

  attr_reader :word, :guesses, :wrong_guesses

  # updates @guesses if match,
  # updates wrong_guesses if no match, else
  # false
  # if letter already guessed or non-alphabet
  # character, then invalid guess.
  def guess(guess_word)
    if guess_word.nil? || guess_word.empty? || guess_word.match(/[^a-zA-Z]/)
      raise ArgumentError
    end

    guess_word.downcase.chars do |char|
      return false if @guesses.match(char) || @wrong_guesses.match(char)

      if word.match(char)
        @guesses << char
      else
        @wrong_guesses << char
      end
    end
  end

  # returns word with guesses and '-' if not guessed yet
  # 'bn' =>  'b-n-n-'
  def word_with_guesses
    word_w_guessed = ''
    @word.chars do |char|
      if @guesses.match(char)
        word_w_guessed << char
      else
        word_w_guessed << '-'
      end
    end
    word_w_guessed
  end

  # returns :win if all chars guessed,
  # :lose after 7 incorrect guesses,
  # :play if still
  def check_win_or_lose
    if !word_with_guesses.include?('-')
      :win
    elsif @wrong_guesses.length == 7
      :lose
    else
      :play
    end
  end

  # You can test it by running $ bundle exec irb -I. -r app.rb
  # And then in the irb: irb(main):001:0> HangpersonGame.get_random_word
  #  => "cooking"   <-- some random word
  def self.get_random_word
    require 'uri'
    require 'net/http'
    uri = URI('http://watchout4snakes.com/wo4snakes/Random/RandomWord')
    Net::HTTP.new('watchout4snakes.com').start { |http|
      return http.post(uri, '').body
    }
  end

end
