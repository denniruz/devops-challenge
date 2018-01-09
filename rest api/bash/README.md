Devops Challenge, Rest API, Bash version
============

Using build and deployment script from the configuration management exercise.  One slight change, the run.sh also copies a few more things up.

* README.md - this file
* aws_start.sh - to build environment.
* aws_delete.sh - to destroy environment when done
* run.sh - to handle host management
* http - xinetd config for service
* rest.sh - the service
* install.sh - service installer.  debconf might make noise.

        ./aws_start.sh
        ./run.sh -u ubuntu -k asdevchal.pem -t hosts -s install.sh


Test:

    SRV=`cat hosts`
		curl -v -X PUT -H "Content-Type: application/json" http://$SRV/word/bar -d '
		[{"word":"bar"}]
		'

Do a few more to seed the data store with various words.  Test some failure conditions.

		curl -v -X PUT -H "Content-Type: application/json" http://$SRV/word/bar -d '
		[{"word":"foo"}]
		'
Which will throw a 503 because the URI and the JSON do not match.

		curl -v -X PUT -H "Content-Type: application/json" http://$SRV/word/bar -d '
		[{"word":"foo bar"}]
		'
Which also throws 503, with an appropriate error message for two words, as will:

		curl -v -X PUT -H "Content-Type: application/json" "http://$SRV/word/foo bar" -d '
		[{"word":"foo bar"}]
		'
 
The accumulated counts are stored locally in a JSON file.  You can ssh in and see, or just request it:

    curl -v http://$SRV/words

Or for specific term:

    curl -v http://$SRV/words/foo

When done, clean up after yourself.

    ./aws_delete.sh