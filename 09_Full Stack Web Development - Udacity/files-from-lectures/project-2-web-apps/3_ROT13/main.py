import os
import jinja2
import webapp2 #webapp framework handling routing etc. get, post, Build on WSGI framework

template_dir = os.path.join(os.path.dirname(__file__), 'templates') #using os to join urls: os.path.dirname(__file__) = the current directory I'm in /templates in one URL
jinja_env = jinja2.Environment(loader = jinja2.FileSystemLoader(template_dir),autoescape=True) #initiate jinja environment

# Class including common functions
class Handler(webapp2.RequestHandler):
	def render_str(self, template, **params): #takes file name and bunch of extra parameters
		t = jinja_env.get_template(template) #using jinja env from above and call get template and create a jinja template
		return t.render(params) #return template

	def write(self, *a, **kw):
		self.response.out.write(*a, **kw)

	def render(self, template, **kw):
		self.write(self.render_str(template, **kw)) #send it back to the browser

#Child class of Handler MainPage to create MainPage
class Rot13(Handler):
	def get(self):
		self.render('rot13.html') #using function() render to create the page for the user to see via get

	def post(self):
		rot13 = '' #empty string rot13
		text = self.request.get('text') #get user input with name text
		if text:
			rot13 = text.encode('rot13') 
		self.render('rot13.html', text = rot13)

#A list of routes is registered in the WSGI application. When the application receives a request, it tries to match each one in order until one matches, and then call the corresponding handler.
app = webapp2.WSGIApplication([
	('/', Rot13),],
	debug=True)