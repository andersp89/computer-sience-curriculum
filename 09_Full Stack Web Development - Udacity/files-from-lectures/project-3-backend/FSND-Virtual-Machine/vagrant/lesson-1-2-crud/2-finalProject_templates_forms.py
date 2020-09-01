from flask import Flask, render_template, redirect, url_for, request, flash, jsonify
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from database_setup import Base, Restaurant, MenuItem
app = Flask(__name__)


#Fake Restaurants
restaurant = {'name': 'The CRUDdy Crab', 'id': '1'}
restaurants = [{'name': 'The CRUDdy Crab', 'id': '1'}, {'name':'Blue Burgers', 'id':'2'},{'name':'Taco Hut', 'id':'3'}]

#Fake Menu Items
items = [ {'name':'Cheese Pizza', 'description':'made with fresh cheese', 'price':'$5.99','course' :'Entree', 'id':'1'}, {'name':'Chocolate Cake','description':'made with Dutch Chocolate', 'price':'$3.99', 'course':'Dessert','id':'2'},{'name':'Caesar Salad', 'description':'with fresh organic vegetables','price':'$5.99', 'course':'Entree','id':'3'},{'name':'Iced Tea', 'description':'with lemon','price':'$.99', 'course':'Beverage','id':'4'},{'name':'Spinach Dip', 'description':'creamy dip with fresh spinach','price':'$1.99', 'course':'Appetizer','id':'5'} ]
item =  {'name':'Cheese Pizza','description':'made with fresh cheese','price':'$5.99','course' :'Entree'}


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
	return render_template('restaurants.html', restaurants=restaurants, items=items)

# Create new restaurant
@app.route('/restaurants/new')
def newRestaurant():
	return render_template('newrestaurant.html')

# Edit a restaurant
@app.route('/restaurants/<int:restaurant_id>/edit')
def editRestaurant(restaurant_id):
	restaurant = restaurant
	return render_template('editrestaurant.html', restaurant=restaurant, restaurant_id=restaurant_id)

# Delete a restaurant
@app.route('/restaurants/<int:restaurant_id>/delete')
def deleteRestaurant(restaurant_id):
	return render_template('deleterestaurant.html', restaurant=restaurant)

### Menu card ###
# All menu items
@app.route('/restaurant/<int:restaurant_id>')
@app.route('/restaurant/<int:restaurant_id>/menu')
def menuCard(restaurant_id):
	return render_template('menu.html', restaurant=restaurant, items=items)

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