#!/bin/bash
#
# could be split into a config file
# sufficient for demo
DIR=/home/ubuntu
DEBUG=$DIR/debug.out
DATASTORE=$DIR/wordlist.json

declare -A data

function fail {
   # fail out with message back to client
   echo "HTTP/1.0 503 Internal Server Error"
   echo "Content-Type: application/json"
   echo $1
   echo "Connection: close"
   exit 1
}

function read_array {
   # reads in datastore json into bash associative array
   if [ -e $DATASTORE ];then
      while IFS=":" read -r key value
      do
         data[$key]=$value
      done < <(jq -rc .[] $DATASTORE | sed -e 's/[\{\}]//g' -e 's/"//g')
   fi
}

function write_array {
   # write our bash associative array into the data store as json
   for key in "${!data[@]}"; do
      temp+=`printf '{"%s":"%s"},' "$key" "${data[$key]}"`
   done
   echo "  write_array: writing $temp" >> $DEBUG
   echo "[${temp::-1}]" > $DATASTORE
}

function put_word {
   # PUT requests
   # read array, increment counter, write array
   echo " put_word: Incrementing  $1" >> $DEBUG
   read_array
   echo " put_word: Incrementing  data[$1]:${data[$1]}" >> $DEBUG
   (( data[$1]++ ))
   echo " put_word: new values  data[$1]:${data[$1]}" >> $DEBUG
   write_array 
   end_it
}

function get_word {
   # GET requests
   # -> read json datastore into assoc array
   # -> if specific word
   # -> -> return array[word]
   # -> else
   # -> -> return all word counts
   echo "  get_word: reading array for $1" >> $DEBUG
   read_array
   echo "  get_word: count for $1 is ${data[$1]}" >> $DEBUG
   end_it "{ \"$1\": \"${data[$1]}\" }"
}

function dump_it {
   # dump the datastore
   echo "---> dumping datastore" >> $DEBUG
   end_it `cat $DATASTORE`
}

function end_it {
   # end it all
   LEN=`echo $1 | wc -c`
   echo "HTTP/1.0 200 OK"
   echo "Content-Length: $LEN"
   echo "Content-Type: application/json"
   echo "Connection: close"
   echo 
   echo "$1"
   echo 
   exit
}

# _main_
while read -t 10 line;
do
   echo "INPUT:$line" >> $DEBUG
   # PUT request line
   if [[ $line =~ (PUT)( /word/)([[:alnum:][:space:]_]+)( HTTP) ]]; then 
      echo "-> REQUEST: $line" >> $DEBUG
      URI_WORD=${BASH_REMATCH[3]} 
      METHOD="PUT"
   fi
   
   # parse message body json, if we got a PUT
   if [[ $line =~ (\{\"word\":\")([[:alnum:][:space:]_]+)\"  ]] && [ $METHOD = "PUT" ]; then
      # JSON sent via PUT, one word only, vasily
      echo "-> BODY: $line" >> $DEBUG
      echo ${BASH_REMATCH[2]} | grep " " > /dev/null
      if [ $? -eq 0  ]; then
         fail '{ "error": "PUT requests must be one word in length" }'
      
      elif [ $URI_WORD = ${BASH_REMATCH[2]} ]; then
         # URI matches body, get busy
         put_word  ${BASH_REMATCH[2]}

      else
         # nothing else matches
         fail '{"error":"URI - json mismatch"}'
      fi 
   fi

   # Handle the GET /words/*
   if [[ $line =~ (GET)( /words/)([[:alnum:][:space:]_]+)( HTTP) ]]; then 
      echo " -> Return count for ${BASH_REMATCH[3]}" >> $DEBUG
      get_word ${BASH_REMATCH[3]}
   elif [[ $line =~ (GET)( /words)( HTTP) ]]; then 
      echo " -> Dumping word list" >> $DEBUG
      dump_it
   fi

done

