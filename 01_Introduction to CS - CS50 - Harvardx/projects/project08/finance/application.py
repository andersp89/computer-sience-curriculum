import os

from cs50 import SQL
from flask import Flask, flash, jsonify, redirect, render_template, request, session
from flask_session import Session
from tempfile import mkdtemp
from werkzeug.exceptions import default_exceptions, HTTPException, InternalServerError
from werkzeug.security import check_password_hash, generate_password_hash

from helpers import apology, login_required, lookup, usd

# Configure application
app = Flask(__name__)

# Ensure templates are auto-reloaded
app.config["TEMPLATES_AUTO_RELOAD"] = True

# Ensure responses aren't cached


@app.after_request
def after_request(response):
    response.headers["Cache-Control"] = "no-cache, no-store, must-revalidate"
    response.headers["Expires"] = 0
    response.headers["Pragma"] = "no-cache"
    return response


# Custom filter
app.jinja_env.filters["usd"] = usd

# Configure session to use filesystem (instead of signed cookies)
app.config["SESSION_FILE_DIR"] = mkdtemp()
app.config["SESSION_PERMANENT"] = False
app.config["SESSION_TYPE"] = "filesystem"
Session(app)

# Configure CS50 Library to use SQLite database
db = SQL("sqlite:///finance.db")


@app.route("/")
@login_required
def index():
    """Show portfolio of stocks"""
    # Find all users stocks
    userStocks = db.execute("SELECT * FROM stockOwnership WHERE userId=:userId", userId=session["user_id"])

    # Create list with stock name, Find their current price, total value
    listWithCurrentStockPrices = []
    grandTotal = 0
    for stock in range(len(userStocks)):
        quote = lookup(userStocks[stock].get("symbol"))
        totalValue = userStocks[stock].get("stockNumber") * quote.get("price")
        grandTotal += totalValue
        listWithCurrentStockPrices.append([userStocks[stock].get("stock"), usd(quote.get("price")), usd(totalValue)])

    # Users cash balance
    cashUser = db.execute("SELECT cash FROM users WHERE id=:user_id", user_id=session["user_id"])

    # Grand total
    grandTotal += float(cashUser[0].get("cash"))

    # Return list of: Stock name, Current stock price, Total value
    return render_template("/index.html", stocks=listWithCurrentStockPrices, cash=usd(cashUser[0].get("cash")), grandTotal=usd(grandTotal))


@app.route("/buy", methods=["GET", "POST"])
@login_required
def buy():
    """Buy shares of stock"""
    # User reached route via POST (as by submitting a form via POST)
    if request.method == "POST":
        # Check if "symbol" exist
        if not request.form.get("symbol"):
            return apology("must provide stock symbol", 400)

        # Check if "shares" exist
        if not request.form.get("shares"):
            return apology("must provide shares parameter", 400)

        # Check if "shares" is int and positive
        try:
            if int(request.form.get("shares")) < 0:
                return apology("must provide positive integer", 400)
        except ValueError:
            return apology("must provide integer", 400)

        # Quote stock symbol
        quote = lookup(request.form.get("symbol"))

        if quote == None:
            return apology("No stock found")

        # Check user's cash
        cashUser = db.execute("SELECT cash FROM users WHERE id=:user_id", user_id=session["user_id"])
        newCash = 0
        total = (quote.get("price") * float(request.form.get("shares")))

        # Check if cash is sufficient to buy stocks, if then substract from users cash
        if total > float(cashUser[0].get("cash")):
            return apology("you don't have enough cash, bro", 400)
        else:
            newCash = float(cashUser[0].get("cash")) - (quote.get("price") * float(request.form.get("shares")))

        # Save purchase in new table
        db.execute("INSERT INTO stockOwnership (stock, stockPrice, stockNumber, totalValue, time, userId, symbol) VALUES (:stock, :stockPrice, :stockNumber, :totalValue, CURRENT_TIMESTAMP, :userId, :symbol)",
                   stock=quote.get("name"), stockPrice=quote.get("price"), stockNumber=int(request.form.get("shares")), totalValue=total, userId=session["user_id"], symbol=quote.get("symbol"))

        # Update history with purchase
        db.execute("INSERT INTO history (activity, stock, symbol, stockPrice, totalValue, time, userId) VALUES (:activity, :stock, :symbol, :stockPrice, :totalValue, CURRENT_TIMESTAMP, :userId)",
                   activity="Buy", stock=quote.get("name"), symbol=quote.get("symbol"), stockPrice=quote.get("price"), totalValue=total, userId=session["user_id"])

        # Set cash for user, after purchase
        db.execute("UPDATE users SET cash=:cash WHERE id=:user_id", cash=newCash, user_id=session["user_id"])

        return redirect("/")

    # User reached route via GET (as by clicking a link or via redirect)
    else:
        return render_template("buy.html")


@app.route("/check", methods=["GET"])
def check():
    """Return true if username available, else false, in JSON format"""
    if request.args.get("username"):
        checkUsername = db.execute("SELECT * FROM users WHERE userName=:userName", userName=request.args.get("username"))
        if len(checkUsername) >= 1 or len(request.args.get("username")) < 1:
            return jsonify(False)
        else:
            return jsonify(True)

    return apology("No username parameter provided", 400)


@app.route("/history")
@login_required
def history():
    """Show history of transactions"""
    stocks = db.execute("SELECT * FROM history WHERE userId=:userId", userId=session["user_id"])
    return render_template("history.html", stocks=stocks)


@app.route("/login", methods=["GET", "POST"])
def login():
    """Log user in"""

    # Forget any user_id
    session.clear()

    # User reached route via POST (as by submitting a form via POST)
    if request.method == "POST":

        # Ensure username was submitted
        if not request.form.get("username"):
            return apology("must provide username", 400)

        # Ensure password was submitted
        elif not request.form.get("password"):
            return apology("must provide password", 400)

        # Query database for username
        rows = db.execute("SELECT * FROM users WHERE username = :username",
                          username=request.form.get("username"))

        # Ensure username exists and password is correct
        if len(rows) != 1 or not check_password_hash(rows[0]["hash"], request.form.get("password")):
            return apology("invalid username and/or password", 400)

        # Remember which user has logged in
        session["user_id"] = rows[0]["id"]

        # Redirect user to home page
        return redirect("/")

    # User reached route via GET (as by clicking a link or via redirect)
    else:
        return render_template("login.html")


@app.route("/logout")
def logout():
    """Log user out"""

    # Forget any user_id
    session.clear()

    # Redirect user to login form
    return redirect("/")


@app.route("/quote", methods=["GET", "POST"])
@login_required
def quote():
    """Get stock quote."""
    # User reached route via POST (as by submitting a form via POST)
    if request.method == "POST":
        if not request.form.get("symbol"):
            return apology("must provide stock symbol", 400)

        quote = lookup(request.form.get("symbol"))

        # Lookup function from helper returns "None" if error
        if quote == None:
            return apology("must provide valid stock symbol", 400)

        quote["price"] = usd(quote.get("price"))

        return render_template("quoted.html", quote=quote)

    # User reached route via GET (as by clicking a link or via redirect)
    else:
        return render_template("quote.html")


@app.route("/register", methods=["GET", "POST"])
def register():
    """Register user"""

    # User reached route via POST (as by submitting a form via POST)
    if request.method == "POST":

        # Ensure username was submitted
        if not request.form.get("username"):
            return apology("must provide username", 400)

        # Ensure password was submitted
        elif not request.form.get("password"):
            return apology("must provide password", 400)

        # Ensure password was confirmed correctly
        elif not request.form.get("confirmation"):
            return apology("must provide password twice", 400)

        # Ensure passwords match
        elif not request.form.get("password") == request.form.get("confirmation"):
            return apology("passwords do not match", 400)

        # Query database for username
        rows = db.execute("SELECT * FROM users WHERE username=:username", username=request.form.get("username"))

        # Ensure username is not taken
        if len(rows) >= 1:
            return apology("sorry username is taken, please select another", 400)

        # Hash the user's password
        hashPassword = generate_password_hash(request.form.get("password"))

        # Insert new user into database
        db.execute("INSERT INTO users (username, hash) VALUES (:username, :hashPassword)",
                   username=request.form.get("username"), hashPassword=hashPassword)

        # Redirect user to home page
        return redirect("/login")

    # User reached route via GET (as by clicking a link or via redirect)
    else:
        return render_template("register.html")


@app.route("/sell", methods=["GET", "POST"])
@login_required
def sell():
    """Sell shares of stock"""

    # User reached route via POST (as by submitting a form via POST)
    if request.method == "POST":
        # Check if symbol is provided (i.e. stockid)
        if not request.form.get("symbol"):
            return apology("must provide available stock for user", 400)

        # Check if symbol exist
        quote = lookup(request.form.get("symbol"))
        if quote == None:
            return apology("No stock found")

        # Check if stock is in user's portfolio
        if len(db.execute("SELECT * FROM stockOwnership WHERE symbol=:symbol", symbol=request.form.get("symbol"))) < 1:
            return apology("You do not own such stock!")

        # Check if "shares" exist
        if not request.form.get("shares"):
            return apology("must provide shares parameter", 400)

        # Check if "shares" is int and positive
        try:
            if int(request.form.get("shares")) <= 0:
                return apology("must provide positive integer", 400)
        except ValueError:
            return apology("must provide integer", 400)

        # Check if user has sufficient shares to sell
        currentStock = db.execute("SELECT stockNumber FROM stockOwnership WHERE symbol=:symbol", symbol=request.form.get("symbol"))
        if int(request.form.get("shares")) > currentStock[0].get("stockNumber"):
            return apology("You cannot sell more, than what you own", 400)

        # Calculate new stock number
        newStock = currentStock[0].get("stockNumber") - int(request.form.get("shares"))

        # Check if quantity exceed stock in ownership
        db.execute("UPDATE stockOwnership SET stockNumber=:stockNumber WHERE id=:stockId",
                   stockNumber=newStock, stockId=request.form.get("symbol"))

        # Delete stock from stockownership, if quantity is 0
        if newStock == 0:
            db.execute("DELETE FROM stockOwnership WHERE symbol=:symbol", symbol=request.form.get("symbol"))

        # Update users cash balance
        # Get current cash
        cashUser = db.execute("SELECT cash FROM users WHERE id=:user_id", user_id=session["user_id"])

        # Calculate new cash
        cashBySell = (quote.get("price") * int(request.form.get("shares")))
        newCash = cashUser[0].get("cash") + cashBySell

        # Update users cash
        db.execute("UPDATE users SET cash=:cash WHERE id=:user_id", cash=newCash, user_id=session["user_id"])

        # Update history with purchase
        db.execute("INSERT INTO history (activity, stock, symbol, stockPrice, totalValue, time, userId) VALUES (:activity, :stock, :symbol, :stockPrice, :totalValue, CURRENT_TIMESTAMP, :userId)",
                   activity="Sell", stock=quote.get("name"), symbol=quote.get("symbol"), stockPrice=quote.get("price"), totalValue=cashBySell, userId=session["user_id"])

        # Direct to start page
        return redirect("/")

    else:
        # Find users stock in db
        stocks = db.execute(
            "SELECT id, stock, stockprice, stockNumber, totalValue, symbol FROM stockOwnership WHERE userId=:user_id", user_id=session["user_id"])
        return render_template("/sell.html", stocks=stocks)


@app.route("/insert", methods=["POST"])
@login_required
def insert():
    """Insert cash into user's account."""
    # User reached route via POST (as by submitting a form via POST)
    if request.method == "POST":
        # Check if "cash" parameter exist
        if not request.form.get("cash"):
            return apology("must provide cash", 400)

        cash = request.form.get("cash")

        # Check if "cash" parameter has value
        if cash == None or cash.isdigit() == "False":
            return apology("must provide number", 400)

        # Check if cash is positive
        if float(request.form.get("cash")) < 0:
            return apology("number must be positive", 400)

        # Update users cash balance
        # Get current cash
        cashUser = db.execute("SELECT cash FROM users WHERE id=:user_id", user_id=session["user_id"])

        # Calculate new cash
        newCash = cashUser[0].get("cash") + float(cash)

        # Update users cash
        db.execute("UPDATE users SET cash=:cash WHERE id=:user_id", cash=newCash, user_id=session["user_id"])

        return redirect("/")

    # User reached route via GET (as by clicking a link or via redirect)
    else:
        return apology("must provide cash via POST", 400)


def errorhandler(e):
    """Handle error"""
    if not isinstance(e, HTTPException):
        e = InternalServerError()
    return apology(e.name, e.code)


# Listen for errors
for code in default_exceptions:
    app.errorhandler(code)(errorhandler)
