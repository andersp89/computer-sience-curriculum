# When done, submit this entire file to the autograder.

# Part 1
 
# takes an array of integers as
# an argument and returns the 
# sum of its elements. For an 
# empty array it should return zero
def sum arr
  arr.inject(0,:+)
end

# which takes an array of integers as
# an argument and returns the sum of 
# its two largest elements. For an
# empty array it should return zero.
# For an array with just one element, 
# it should return that element.
def max_2_sum arr
  arr.max(2).inject(0,:+)
end

# that takes an array of integers and 
# an additional integer, n, as arguments 
# and returns true if any two elements 
# in the array of integers sum to n.

def sum_to_n? arr, n
  match = Set.new()
  arr.each do | num |
    delta = n - num
    if match.include?(num)
      return true
    else
      match.add(delta)
    end
  end
  return false
end

# Part 2

# that takes a string representing a name
# and returns the string "Hello, "
# concatenated with the name
def hello(name)
  "Hello, #{name}"
end

# that takes a string and returns true if
# it starts with a consonant and false otherwise.
# (For our purposes, a consonant is any letter
# other than A, E, I, O, U.) NOTE: be sure it
# works for both upper and lower case and for non-letters!
def starts_with_consonant? s
  s =~ /\A(?=[^aeiou])(?=[a-z])/i
end

# takes a string and returns true if the string
# represents a binary number that is a multiple of 4.
# NOTE: be sure it returns false if the string is
# not a valid binary number!
def binary_multiple_of_4? s
  if s.match(/[^0-9]/) or s.empty?
    return false
  end

  if s.to_i(2)%4 == 0
    true
  else
    false
  end
end

# Part 3

# Define a class BookInStock which represents a
# book with an ISBN number, isbn, and price of
# the book as a floating-point number, price,
# as attributes.
#
# Include a method price_as_string that returns
# the price of the book formatted with a leading
# dollar sign and two decimal places
class BookInStock
  def initialize(isbn, price)
    if isbn.empty? or price <= 0
      raise ArgumentError.new("Price cannot be <= 0, and/or isbn cannot not be empty")
    end
    @isbn = isbn
    @price = price
  end

  # getters and setters
  #def isbn
  #  @isbn
  #end
  #def isbn=(isbn)
  #  @isbn = isbn
  #end
  #def price
  #  @price
  #end
  #def price=(new_price)
  #  @price=new_price
  #end
  # short-hand
  attr_acessor :price, :isbn

  def price_as_string
    "$%2.2f" % @price
  end
end