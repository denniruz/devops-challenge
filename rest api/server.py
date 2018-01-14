#!/usr/bin/python3
from flask import Flask, request, jsonify #import flask, request, and jsonify classes
from flask_restful import Resource, Api

#Create Flask and Api instances.
app = Flask(__name__)
api = Api(app)

#Create an object called WORDCOUNT with the properties word and count with serialization extention method needed for json formatting
class WORDCOUNT(object):
	def __init__(self, word, Count):
		self.word = word
		self.Count = Count
	def serialize(self):
		return {
			self.word: self.Count
		}

#Create empty array to store words from requests
requests = []

#Add route to Flask that accepts PUT and GET requests
@app.route('/words', methods=['PUT','GET'])
def Request():
	if request.method == 'PUT': #Switch on request type
		js = request.json #Extract json content from request
		contentWord = js.get('word') #Get value of word field
		if (len(contentWord.split(" ")) == 1): #Check word count and handle if not eq to one
			requests.append(contentWord) #If request is only one word, add it to the list
			return "Successfully added the word %s.\n" % contentWord #Send response with success message
		else: #Handle if more than one word or an empty string was sent
			return jsonify({ "ERROR": "PUT requests must be one word in length" }), 400 #Send json error response with 400 (Bad Request) http status code
	
	if request.method == 'GET': #Switch on GET request type
		listOfWords = [] #Create empty array to store word count objects
		distinctWords = set(requests) #Get distinct list of words from requests to the api
		for i in distinctWords: #Iterate over distinct word list
			freq = requests.count(i) #determine the number of times that word was sent
			wordObj = WORDCOUNT(i,freq) #Create new object with the word and count property
			listOfWords.append(wordObj) #Add object to list
		return jsonify([e.serialize() for e in listOfWords]) #Send a response with the list of words and their frequency in json format

#Add route that handles GET requests with a route parameter
@app.route('/words/<string:WORDNAME>', methods=['GET'])
def WordRequest(WORDNAME):	
	freq = requests.count(WORDNAME) #Gets the number of times the word passed in the route parameter was sent to the api
	return jsonify({WORDNAME: freq}) #Send a response with a json object containing the word from the route and the number of times it was sent

#Run the program. By default the api can be accessed at http://127.0.0.1:5000/
if __name__ == '__main__':
     app.run()