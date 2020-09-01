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


def render_str(template, **params):
	t = jinja_env.get_template(template)
	return t.render(params)

#Controller class from webapp2 "RequestHandler", with common functionality used in every class
class Handler(webapp2.RequestHandler):
	#Shortcut to write to the browser
	def write(self, *a, **kw):
		self.response.out.write(*a, **kw)
	
	#Takes a template name and returns string
	def render_str(self, template, **params):
		return render_str(template, **params)

	#Combining the former two to generate template from string
	def render(self, template, **kw):
		self.write(self.render_str(template, **kw)) #send it back to the browser

#Save posts in database entity. Create more to create more entities.
class BlogPosts(db.Model):
	subject = db.StringProperty(required = True)
	content = db.TextProperty(required = True)
	created = db.DateTimeProperty(auto_now_add = True)

	def render(self):
		self._render_text = self.content.replace('\n', '<br>')
		return render_str("post.html", p = self)

#Child class of Handler. Create more, to create more sites.
class MainPage(Handler):
	def get(self):
		blogposts = db.GqlQuery("SELECT * FROM BlogPosts ORDER BY created DESC limit 10")
		self.render("front.html", blogposts = blogposts)

#Post page af succesfull submission
class PostPage(Handler):
	def get(self, post_id):
		#make a key of blog post with post_id
		key = db.Key.from_path('BlogPosts', int(post_id))
		#save it in variable
		post = db.get(key)

		if not post:
			self.error(404)
			return

		self.render("permalink.html", post = post)

class NewPost(Handler):
	def get(self):
		self.render("submit.html")

	def post(self):
		subject = self.request.get('subject')
		content = self.request.get('content')

		#error handling
		if subject and content:
			p = BlogPosts(subject = subject, content = content)
			p.put()
			self.redirect('/blog/%s' % str(p.key().id()))
			#go to permalink to post!
		else:
			error = "We need both a title and some artwork!"
			self.render_submit(subject, content, error)




#WSGI Application that maps the URI (e.g. "/") to handler
app = webapp2.WSGIApplication([
	('/blog/newpost', NewPost),
	('/blog', MainPage),
	('/blog/([0-9]+)', PostPage),
	],
	debug=True)