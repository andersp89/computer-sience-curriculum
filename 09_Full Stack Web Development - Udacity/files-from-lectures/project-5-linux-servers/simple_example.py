## __init__.py app ##

from flask import Flask
app = Flask(__name__)
@app.route("/")
def hello():
    return "Hello, I love Digital Ocean! I did it!!"
if __name__ == "__main__":
    app.run()


## catalog.wsgi ##

#!/usr/bin/python
import sys
import logging
logging.basicConfig(stream=sys.stderr)
sys.path.insert(0,"/var/www/catalog/app/")

from __init__ import app as application
application.secret_key = 'Add your secret key'