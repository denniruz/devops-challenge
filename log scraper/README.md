DESCRIPTION:

Scrapes the provided log file for the following:

- How many times the URL "/production/file_metadata/modules/ssh/sshd_config" was fetched
- Of those requests, how many times the return code from Apache was not 200
- The total number of times Apache returned any code other than 200
- The total number of times that any IP address sent a PUT request to a path under "/dev/report/"
- A breakdown of how many times such requests were made by IP address

USAGE:

Place the logscrape file on the desired machine and configure the logfile variable to point to the logfile you wish to scrape.
Run ./logscrape.sh to retrieve the results
This could be expanded to accept another file via the addition of an argument.
