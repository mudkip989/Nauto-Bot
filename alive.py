from flask import Flask
from threading import Thread
from os import getenv

app = Flask("")


@app.route("/alive")
def index():
    return "alive"


@app.route("/robots.txt")
def robots():
    return "User-agent: *\nDisallow: /"


Thread(target=app.run, args=("0.0.0.0", getenv('PORT'))).start()
print("running keepalive server")
