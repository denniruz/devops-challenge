#!/usr/bin/python
from flask import Flask, jsonify, abort, request, make_response, url_for

app = Flask(__name__)

words = []
     #{
    # 	'word': 'boat'
    # }
#]

@app.route('/')
def index():
    return "Hello, World!"

@app.route('/words', methods=['GET'])
def get_tasks():
    return jsonify({'words': words})

@app.route('/words', methods=['PUT'])
def create_word():
    if not request.json or not 'word' in request.json:
        abort(400)
    word = {
        'word': request.json['word']
    }
    words.append(word)
    return jsonify({'words': word}), 201

if __name__ == '__main__':
    app.run(debug=True)

