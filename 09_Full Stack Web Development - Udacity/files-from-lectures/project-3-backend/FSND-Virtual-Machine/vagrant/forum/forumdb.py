# "Database code" for the DB Forum.

import psycopg2, bleach

#POSTS = [("This is the first post.", datetime.datetime.now())]

def get_posts():
  """Return all posts from the 'database', most recent first."""
  pg_db = psycopg2.connect("dbname=forum")
  c = pg_db.cursor()
  c.execute("delete from posts where content like '%spam%'") # To delete spam
  #c.execute("update posts set content = 'cheese' where content like '%spam%'") # To update spam in content away
  c.execute("select content, time from posts order by time desc")
  #posts = ({'content': str(row[1]), 'time': str(row[0])} for row in c.fetchall()) # Fetch all and format output, as expected
  posts = c.fetchall()  
  pg_db.close()
  return posts

def add_post(content):
  """Add a post to the 'database' with the current timestamp."""
  pg_db = psycopg2.connect("dbname=forum")
  c = pg_db.cursor()
  c.execute("insert into posts (content) values (%s)", (bleach.clean(content),)) # Use tuple query to escape output from user + bleach
  pg_db.commit()
  pg_db.close()