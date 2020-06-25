import cs50
import csv

from flask import Flask, jsonify, redirect, render_template, request, url_for

# Configure application
app = Flask(__name__)

# Reload templates when they are changed
app.config["TEMPLATES_AUTO_RELOAD"] = True


@app.after_request
def after_request(response):
    """Disable caching"""
    response.headers["Cache-Control"] = "no-cache, no-store, must-revalidate"
    response.headers["Expires"] = 0
    response.headers["Pragma"] = "no-cache"
    return response


@app.route("/", methods=["GET"])
def get_index():
    return redirect("/form")


@app.route("/form", methods=["GET"])
def get_form():
    return render_template("form.html")


@app.route("/form", methods=["POST"])
def post_form():
    courseName = request.form.get("courseName")
    schoolName = request.form.get("schoolName")
    completed = request.form.get("completed")

    if not courseName or not schoolName or not completed:
        return render_template("error.html", message="You did not fill in form correctly.")

    file = open("survey.csv", "a")
    writer = csv.writer(file)
    writer.writerow((courseName, schoolName, completed))
    file.close()
    return redirect(url_for('get_sheet'))


@app.route("/sheet", methods=["GET"])
def get_sheet():
    file = open("survey.csv", "r")
    reader = csv.reader(file)
    courses = list(reader)
    return render_template("sheet.html", courses=courses)
