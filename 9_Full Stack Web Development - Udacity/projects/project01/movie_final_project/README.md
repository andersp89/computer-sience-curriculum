# Project: Movie Trailer Website

This project creates a website server-side, to display the users favorite movies. The user can click any of the movies, to open a window to view the trailer of the movie.

## Architecture
The page is created server-side, by the following process:
* `media.py`: `Movie` Class, to store movie information in.
* `entertainment_center.py`: Constructors of class `Movie`, which hold the movie information of the favorite movies, i.e. title, description, image-url and youtube link for trailer. Calls function `open_movies_page` of `fresh_tomatoes.py` with a array of the movies, to create the page.
* `fresh_tomatoes.py`: Front-end contents (JS, HTML, CSS) and functions, to generate page.  

## Technology
* Python
* HTML, CSS, Javascript
* Bootstrap

## Deploy code
1. Install [Python](https://www.python.org/).
2. Clone this repository to your local drive.
3. In your terminal, cd into the folder of the cloned repository.
4. In your terminal, run `python entertainment_center.py`, to create the .html file. After which, the browser will open the newly created `fresh_tomatoes.html`-page with the user's favorite movies.
5. That's it - have fun! :-)