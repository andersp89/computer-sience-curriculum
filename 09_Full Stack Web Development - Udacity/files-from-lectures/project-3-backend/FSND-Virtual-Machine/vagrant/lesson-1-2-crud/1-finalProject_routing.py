from flask import Flask, render_template, redirect, url_for, request, flash, jsonify
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from database_setup import Base, Restaurant, MenuItem
app = Flask(__name__)

# Instantiate server and create session
def connect():
	try:
		engine = create_engine('sqlite:///restaurantmenu.db')
		Base.metadata.bind = engine
		DBSession = sessionmaker(bind=engine)
		session = DBSession()
	except IOError:
		print "No server connection"
		exit(1)

### Restaurants ###
# All restaurants
@app.route('/')
@app.route('/restaurants/')
def allRestaurants():
	return "Showing all restaurants"

# Create new restaurant
@app.route('/restaurants/new')
def newRestaurant():
	return "Create a new restaurant"

# Edit a restaurant
@app.route('/restaurants/<int:restaurant_id>/edit')
def editRestaurant(restaurant_id):
	return "Here to edit an existing restaurant"

# Delete a restaurant
@app.route('/restaurants/<int:restaurant_id>/delete')
def deleteRestaurant(restaurant_id):
	return "Here to delete an existing restaurant"


### Menu card ###
# All menu items
@app.route('/restaurant/<int:restaurant_id>')
@app.route('/restaurant/<int:restaurant_id>/menu')
def menuCard(restaurant_id):
	return "Menu for a restaurant"

# Create menu item
@app.route('/restaurant/<int:restaurant_id>/menu/create')
def createMenuItem(restaurant_id):
	return "Create new menu item"

# Edit menu item
@app.route('/restaurant/<int:restaurant_id>/menu/<int:menuitem_id>/edit')
def editMenuItem(restaurant_id, menuitem_id):
	return "Edit a menu item"

# Delete menu item
@app.route('/restaurant/<int:restaurant_id>/menu/<int:menuitem_id>/delete')
def deleteMenuItem(restaurant_id, menuitem_id):
	return "Delete a menu item"


if __name__ == '__main__':
    connect()
    app.secret_key = 'supersecretkey' # SECRET IN PRODUCTION!
    app.debug = True
    app.run(host='0.0.0.0', port=5000)