def application(environ, start_response):
    status = '200 OK'
    output = anotherFunction()

    response_headers = [('Content-type', 'text/plain'), ('Content-Length', str(len(output)))]
    start_response(status, response_headers)

    return [output]


def anotherFunction():
    import psycopg2

    #try:
    connect_str = "dbname='newdb' user='student' host='localhost' password='rollo12.'"
        # use our connection values to establish a connection
    conn = psycopg2.connect(connect_str)
        # create a psycopg2 cursor that can execute queries
    cursor = conn.cursor()
        # create a new table with a single column called "name"
        # cursor.execute("""CREATE TABLE tutorials (name char(40));""")
        # run a SELECT statement - no data in there, but we can try it
    cursor.execute("""SELECT * from new""")
    rows = cursor.fetchall()
    #rows = "hello"
    return str(rows)
    #except Exception as e:
       # return "Uh oh, can't connect. Invalid dbname, user or password?" + e