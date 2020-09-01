class Movie < ActiveRecord::Base
  def self.all_ratings
    %I[G PG PG-13 R]
  end
end
