# Configuration
import sys
import os

from sqlalchemy import Column, ForeignKey, Integer, String
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import relationship
from sqlalchemy import create_engine

# Make my classes sqlalchemy classes, representing tables in db
Base = declarative_base()

class Restaurant(Base):
	__tablename__ = 'restaurant'
	id = Column(Integer, primary_key = True)
	name = Column(String(250), nullable = False)

class MenuItem(Base):
	__tablename__ = 'menu_item'
	id = Column(Integer, primary_key = True)
	name = Column(String(80), nullable = False)
	description = Column(String(250))
	price = Column(String(8))
	course = Column(String(250))
	restaurant_id = Column(Integer, ForeignKey('restaurant.id'))
	restaurant = relationship(Restaurant) # Needed when creating ForeignKey relationship

	@property
	def serialize(self):
		# Returns object data in JSON format
		return {
			'name' : self.name,
			'description' : self.description,
			'id' : self.id,
			'price' : self.price,
			'course' : self.course,
		}

# Insert at end of file
engine = create_engine('sqlite:///restaurantmenu.db')
# Goes into database and creates tables
Base.metadata.create_all(engine)