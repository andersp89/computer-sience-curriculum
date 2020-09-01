#Miscellaneous operating system interfaces. E.g. "os.path"
import os

#For hashing:
import hashlib
#RE: Regular expresseions to search and modify strings
import re
from string import letters

#Jinja2 is template framework separating HTML and server code
import jinja2
#Webapp framework handling routing: get, post etc. Built on WSGI framework and compatible with GAE
import webapp2 

### Create new hash ###
def hash_str(s):
    return hashlib.md5(s).hexdigest()

### Return string and hashed value ###
def make_secure_val(s):
    return "%s, %s" % (s, hash_str(s))

### Check secure val ###
def check_secure_val(h):
    val = h.split('|')[0]
    if h == make_secure_val(val):
        return val 






#Using os.path.join to join urls. "dirname(__file__)"" = path of root
template_dir = os.path.join(os.path.dirname(__file__), 'templates') 
#Initiate jinja environment. Autoescape to escape HTML
jinja_env = jinja2.Environment(loader = jinja2.FileSystemLoader(template_dir),autoescape=True)

#Controller class from webapp2 "RequestHandler", with common functionality used in every class
class Handler(webapp2.RequestHandler):
	def render_str(self, template, **params): #takes file name and bunch of extra parameters
		t = jinja_env.get_template(template) #using jinja env from above and call get template and create a jinja template
		return t.render(params) #return template

    #Shorthand for self.write instead of self.response.out...
	def write(self, *a, **kw):
		self.response.out.write(*a, **kw)

	def render(self, template, **kw):
		self.write(self.render_str(template, **kw)) #send it back to the browser


### Set and check regular expression (special string) for username, password and email
USER_RE = re.compile(r"^[a-zA-Z0-9_-]{3,20}$")
def valid_username(username):
    return username and USER_RE.match(username)

PASS_RE = re.compile(r"^.{3,20}$")
def valid_password(password):
    return password and PASS_RE.match(password)

EMAIL_RE  = re.compile(r'^[\S]+@[\S]+\.[\S]+$')
def valid_email(email):
    return not email or EMAIL_RE.match(email)


### Create sign-up page ###
class Signup(Handler):
    def get(self):
        self.render("signup-form.html")

    #Get values from user via POST
    def post(self):
        have_error = False
        username = self.request.get('username')
        password = self.request.get('password')
        verify = self.request.get('verify')
        email = self.request.get('email')

        #Store username and email in dictionary
        params = dict(username = username,
                      email = email)

        #Checks if values are valid. If, then go to welcome page.
        if not valid_username(username):
            print valid_username
            params['error_username'] = "That's not a valid username."
            have_error = True

        if not valid_password(password):
            params['error_password'] = "That wasn't a valid password."
            have_error = True
        elif password != verify:
            params['error_verify'] = "Your passwords didn't match."
            have_error = True

        if not valid_email(email):
            params['error_email'] = "That's not a valid email."
            have_error = True

        if have_error:
            self.render('signup-form.html', **params)
        else:
            self.redirect('/welcome?username=' + username)


        #Set cookie




### Render welome page with username ###
class Welcome(Handler):
    def get(self): ##Should not be get!!!
        username = self.request.get('username')
        if valid_username(username):
            self.render('welcome.html', username = username)
        else:
            self.redirect('/signup')

#Routing
app = webapp2.WSGIApplication([('/signup', Signup),
                               ('/welcome', Welcome)],
                              debug=True)
