#!/usr/bin/python3
from flask import Flask, jsonify, abort, request, make_response, url_for
from collections import Counter
import re

app = Flask(__name__)

words = []

@app.route('/')
def index():
    return "Hello, World!"

@app.route('/words', methods=['GET'])
def get_words():
    return jsonify(Counter(words))

@app.route('/words/<string:countword>', methods=['GET'])
def count_words(countword):
    count = str(words.count(countword))
    returnjson = '{' + ' "{}" : {} '.format(countword, count) + '}'
    return returnjson

@app.route('/words', methods=['PUT'])
def create_word():
    if not request.json or not 'word' in request.json:
        abort(400)
    newword = {
        'word': request.json['word']
    }
    if re.match('^\w+$', request.json['word']):
        words.append(request.json['word'])
        return "OK!", 201
    else:
        return jsonify({"error": "PUT requests must be one word in length"}), 400

if __name__ == '__main__':
    app.run(debug=True)

