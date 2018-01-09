DESCRIPTIOIN:

This will start an api that listens on port 5000 that will do the following:

- Accept PUT requests to /words
- Stores a count of words that have been sent
- Accept GET requests to /words/<word> that reports how many times it has been PUT to the API
- Accept GET request to /words that reports a JSON list of each word with a count of how many times it has been PUT to the API

USAGE:

- To get a list of words with a count of each run the following:
curl -i http://localhost:5000/words

- To return one specific word and a count of how many times it has been entered:
curl -i http://localhost:5000/words/<word>

- To PUT a word into the API run the following:
curl -i -H "Content-Type: application/json" -X PUT -d '{"word":"tina"}' http://localhost:5000/words
