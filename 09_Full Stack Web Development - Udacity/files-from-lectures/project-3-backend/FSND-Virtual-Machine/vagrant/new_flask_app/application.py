from flask import Flask, render_template, redirect, url_for, request, flash, jsonify
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from database_setup import Base, User, Category, Item
app = Flask(__name__)

# Instantiate server and create session
def connect():
	try:
		engine = create_engine('sqlite:///catalog.db')
		Base.metadata.bind = engine
		DBSession = sessionmaker(bind=engine)
		session = DBSession()
	except IOError:
		print "No server connection"
		exit(1)

# Categories
@app.route('/')
def allCategories():
	return render_template('catalog.html')
	#return "Showing all categories"

@app.route('/new')
def newCategory():
	return "Create a new category"

@app.route('/<category_name>/edit')
def editCategory(category_name):
	return "Here to edit an existing category"

@app.route('/<category_name>/delete')
def deleteCategory(category_name):
	return "Here to delete an existing category"

# Items
@app.route('/<category_name>')
def allItems(category_name):
	return "All items in category"

@app.route('/<category_name>/<item_name>')
def singleItem(category_name, item_name):
	return "Show single item"

@app.route('/<category_name>/new')
def newItem(category_name):
	return "New item"

@app.route('/<category_name>/<item_name>/edit')
def editItem(category_name, item_name):
	return "Edit a menu item"

@app.route('/<category_name>/<item_name>/delete')
def deleteItem(category_name, item_name):
	return "Delete a menu item"

if __name__ == '__main__':
    connect()
    app.debug = True
    app.run(host='0.0.0.0', port=8000)