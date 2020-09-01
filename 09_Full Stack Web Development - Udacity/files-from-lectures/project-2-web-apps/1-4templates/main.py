import os

import jinja2
import webapp2

#to initiliaze jinja2
template_dir = os.path.join(os.path.dirname(__file__), 'templates') #os.path.dirname(__file__) = the current directory I'm in /templates in one URL
jinja_env = jinja2.Environment(loader = jinja2.FileSystemLoader(template_dir),autoescape=True) #initiate jinja environment

# Class including common functions
class Handler(webapp2.RequestHandler):
	def write(self, *a, **kw):
		self.response.out.write(*a, **kw)

	def render_str(self, template, **params): #takes file name and bunch of extra parameters
		t = jinja_env.get_template(template) #using jinja env from above and call get template and create a jinja template
		return t.render(params) #return template

	def render(self, template, **kw):
		self.write(self.render_str(template, **kw)) #send it back to the browser

#Child class of Handler MainPage to create MainPage
class MainPage(Handler):
	def get(self):
		items = self.request.get_all('food') #get_all: get parameters food from URL and store them in var items.
		self.render('shopping_list.html', items = items) #pass in items from above




class FizzBuzzHandler(Handler):
	def get(self):
		n = self.request.get('n', 0) #get n and default it to 0
		n = n and int(n) #same as saying if n
		self.render('fizzbuzz.html', n = n) #rendering fizzbuzz.html and parse in n.

app = webapp2.WSGIApplication([('/', MainPage), ('/fizzbuzz', FizzBuzzHandler),],debug=True)