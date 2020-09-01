#!/usr/bin/python

import webbrowser
import os
import re

# Header of page, including styles and javascript for the page
main_page_head = open("html/main_page.html").read()

# The body layout and title bar
main_page_content = open("html/main_page_content.html").read()

# HTML for single movie entry
movie_tile_content = open('html/movie_tile_content.html').read()


# Function to create a tile for each movie in movies
def create_movie_tiles_content(movies):
    content = ''
    for movie in movies:
        # Extract the youtube ID from the url, either after v= or be/ of URL
        youtube_id_match = re.search(
            r'(?<=v=)[^&#]+', movie.trailer_youtube_url)
        youtube_id_match = youtube_id_match or re.search(
            r'(?<=be/)[^&#]+', movie.trailer_youtube_url)
        trailer_youtube_id = (youtube_id_match.group(0) if youtube_id_match
                              else None)

        # Append the tile for the movie with its content filled in
        content += movie_tile_content.format(
            movie_title=movie.title,
            poster_image_url=movie.poster_image_url,
            trailer_youtube_id=trailer_youtube_id
        )
    return content


# Function to build html page and open it
def open_movies_page(movies):
    # Create or overwrite the output file
    output_file = open('html/fresh_tomatoes.html', 'w')

    # Replace the movie tiles placeholder generated content
    rendered_content = main_page_content.format(
        movie_tiles=create_movie_tiles_content(movies))

    # Output the file
    output_file.write(main_page_head + rendered_content)
    output_file.close()

    # open the output file in the browser (in a new tab, if possible)
    url = os.path.abspath(output_file.name)
    webbrowser.open('file://' + url, new=2)
