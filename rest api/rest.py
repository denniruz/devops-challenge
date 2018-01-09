#!/usr/bin/python

#Import required libraries for use
import re
from collections import Counter
from flask import Flask, jsonify, abort, make_response, request

app = Flask(__name__)

words = []

#GET a list of words with number of times PUT to API or GET a single word with number of times PUT to API
@app.route('/words', methods=['GET'])
def get_words():
    return jsonify(Counter(words))

@app.route('/words/<string:countword>', methods=['GET'])
def count_words(countword):
    count = str(words.count(countword))
    returnjson = '{' + ' "{}" : {} '.format(countword, count) + '}\n'
    return returnjson

#PUT to words, only accepts single words will error otherwise or if request is not json
@app.route('/words', methods=['PUT'])
def create_words():
    if not request.json or not re.match('^\w+$', request.json['word']):
        abort(400)
    word = {
        'word': request.json['word']
    }
    words.append(request.json['word'])
    return jsonify({'word': word}), 201

#Error handling
@app.errorhandler(400)
def too_many(error):
    return make_response(jsonify({'error': 'PUT requests must be one word in length'}), 400)

@app.errorhandler(404)
def not_found(error):
    return make_response(jsonify({'error': 'Not found'}), 404)

if __name__ == '__main__':
    app.run(debug=True)
