echo "Populating data for the test."
printf "\n"

curl -X PUT http://127.0.0.1:5000/words -H "Content-Type: application/json" -d '{"word": "WordOne"}'
curl -X PUT http://127.0.0.1:5000/words -H "Content-Type: application/json" -d '{"word": "WordOne"}'
curl -X PUT http://127.0.0.1:5000/words -H "Content-Type: application/json" -d '{"word": "WordOne"}'
curl -X PUT http://127.0.0.1:5000/words -H "Content-Type: application/json" -d '{"word": "WordOne"}'
curl -X PUT http://127.0.0.1:5000/words -H "Content-Type: application/json" -d '{"word": "WordTwo"}'
curl -X PUT http://127.0.0.1:5000/words -H "Content-Type: application/json" -d '{"word": "WordTwo"}'
curl -X PUT http://127.0.0.1:5000/words -H "Content-Type: application/json" -d '{"word": "WordThree"}'
curl -X PUT http://127.0.0.1:5000/words -H "Content-Type: application/json" -d '{"word": "WordThree"}'
curl -X PUT http://127.0.0.1:5000/words -H "Content-Type: application/json" -d '{"word": "WordThree"}'
curl -X PUT http://127.0.0.1:5000/words -H "Content-Type: application/json" -d '{"word": "WordFour"}'
curl -X PUT http://127.0.0.1:5000/words -H "Content-Type: application/json" -d '{"word": "WordFour"}'
printf "\n"

printf "Sending more than one word.\n"
curl -X PUT http://127.0.0.1:5000/words -H "Content-Type: application/json" -d '{"word": "Word Four"}'
printf "\n"

printf "Getting count of times 'WordOne' was sent.\n"
curl -X GET http://127.0.0.1:5000/words/WordOne
printf "\n"

printf "Getting frequency of all words sent.\n"
curl -X GET http://127.0.0.1:5000/words