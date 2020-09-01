#
#
#This is a standard GAE app using jinja2
#
#

#Miscellaneous operating system interfaces. E.g. "os.path"
import os
#Jinja2 is template framework separating HTML and server code
import jinja2
#Webapp framework handling routing: get, post etc. Built on WSGI framework and compatible with GAE
import webapp2 
#GAEs database
from google.appengine.ext import db 

#Using os.path.join to join urls. "dirname(__file__)"" = path of root
template_dir = os.path.join(os.path.dirname(__file__), 'templates') 
#Initiate jinja environment. Autoescape to escape HTML
jinja_env = jinja2.Environment(loader = jinja2.FileSystemLoader(template_dir),autoescape=True) 

#Controller class from webapp2 "RequestHandler", with common functionality used in every class
class Handler(webapp2.RequestHandler):
	#Shortcut to write to the browser
	def write(self, *a, **kw):
		self.response.out.write(*a, **kw)
	
	#Takes a template name and returns string
	def render_str(self, template, **params):
		t = jinja_env.get_template(template) 
		return t.render(params)

	#Combining the former two to generate template from string
	def render(self, template, **kw):
		self.write(self.render_str(template, **kw)) #send it back to the browser

#Child class of Handler, called MainPage to create MainPage. Create more, to create more sites.
class MainPage(Handler):
	def get(self):
		self.render('front.html')

#WSGI Application that maps the URI (e.g. "/") to handler
app = webapp2.WSGIApplication([
	('/', MainPage),],
	debug=True)