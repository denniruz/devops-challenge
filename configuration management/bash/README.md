Devops Challenge, Configuration Management
============

This is an attempt at an all-bash script solution.  It emplements 2 scripts to manage the test environment in AWS.  It implements one script that handles execution across N numberof hosts built by the environment management.  Lastly, there is a final script to install the desired file contents, but this solution is not limited to that.  

Known Issues

* Environment creation script could use arguments to be more generic 
* Automation run script executes serially, so as N approaches infinity, so does total execution time.
* Still a little noiser than it could be.

**Environment**

* Requires you to have a working aws cli environment. 

To create environment:

*./aws_start.sh*

There are a handful of settings in the aws_start.sh script:

* aws profile to use from aws cli environment
* count of number of hosts to build.  
* AMI to use

(those could probably be arguments in a later revision)

To delete environment:

*./aws_delete.sh*

Also cleans up temporary files.

**Automation**

The script run.sh takes a number of arguments.  The hosts file and the ssh key are created as a result of the AWS environment start script.  The default user for the AMI that we are using here is 'ubuntu'.  
 
	./run.sh [-h] -k <key file> -t <hosts file> -u <remote user> -s <script file>
 
	options:
	-h                show brief help
	-u username       remote username
	-k keyfile        which ssh key to use
	-t hostfile       file with list of hosts
	-s script         script to send and execute


For the challange. the script *template_install.sh* manages the installation of /etc/widgetfile.
