class Movie < ActiveRecord::Base
  def self.all_ratings
    %w(G PG PG-13 NC-17 R)
  end

  def self.other_movies_by_director(id)
    raise ArgumentError if id.empty? || id.nil?

    movie = Movie.find_by_id(id)
    Movie.where(director: movie.director)
  end
end
