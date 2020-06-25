require 'rails_helper'

describe MoviesController do
  it { should respond_to(:director_movies)}

  describe 'find movies with same director' do
    # Do not work in current versioning
    context 'when a Director value is present' do
      before do
        # stub find method on Movie
        @movie = double('movie', id: '0', director: 'Spielberg')
        allow(Movie).to receive(:find).and_return @movie
        # stub other_movies_by_director method to return director's films
        @spielberg_films = ['E.T.' 'Jaws', 'War Horse']
        allow(Movie).to receive(:other_movies_by_director).and_return @spielberg_films
      end

      it 'calls the model method that performs director search' do
        expect(Movie).to receive(:other_movies_by_director).with('0')
        get :director_movies, id: '0'
      end
      
      it 'makes the search results available to that template' do
        get :director_movies, id: @movie.id
        assigns(:director).should == @movie.director
        assigns(:movies).should == @spielberg_films
      end

      it 'selects the Director Movies template' do
        get :director_movies, id: @movie.id
        response.should render_template :director_movies
      end
    end

    context 'When a "Director" value is not present' do
      before do
        # mock for movie w/o director
        @movie = double("movie", id: '1', title: 'Fake Movie', director: nil)
        allow(Movie).to receive(:find).and_return @movie
        @movie.stub(:other_movies_by_director).and_return(nil)
      end

      it 'should redirect to the home page with a message saying that no movies exist' do
        get :director_movies, id: @movie.id
        flash[:notice].should == "'#{@movie.title}' has no director info"
        response.should redirect_to(root_path)
      end

    end
  end
end