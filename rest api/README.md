Requirements
============

* Java 1.8
* Apache Maven 3.5.2

Instructions
============
run:

    mvn clean spring-boot:run

Your server url will be: localhost:8080

Here are a example list of httpie commands for the different endpoints:

    http PUT localhost:8080/word/ignoredword word=beef
    http PUT localhost:8080/word/ignored word="not valid"
    http POST localhost:8080/word/ignored word=beef
    http DELETE localhost:8080/word/beef
    
    http GET localhost:8080/words
    http GET localhost:8080/words/beef


To run the integration tests:

    mvn test

MAC USERS
=========
If you would like homebrew, java, java unlimited cryptography, maven, and httpie
 installed for you in one easy script and you have sudo access, then run:
 
     ./strap.sh
     
If any of those programs are already installed, then the script will just skip over it.