DESCRIPTION:

Running this will take the provided template file and perform the following on the specified hosts:

- Create /etc/widgetfile location
- Place the template.file on the remote hosts in /etc/widgetfile
- Outputs the successful machines with an error of None
- Outputs the failed machine with the error

USAGE:

Place babychef.py and template.file into the same directory
You will need fabric api installed on the host via one of the following commands:
  pip install fabric
  sudo apt-get install fabric
  sudo apt-get install python-fabric

Once fabric is loaded you can run the following command:
fab -f ./chefthing.py doit -H hostname,hostname,hostname,...

This will create a babychef.pyc in the same directory.
