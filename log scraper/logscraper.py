#!/usr/bin/env python

"""
USAGE:
logscraper.py puppet_access_ssl.log
This script takes apache log file as an argument and then parses the file to create different stats as needed.

If apache log file is split by any whitespace string, than an example output would be:
['10.101.3.205', '-', '-', '[25/Nov/2013:16:51:19', '+0000]', '"GET', '/validate_cmd.rb', 'HTTP/1.1"', '200', '1621', '"-"', '"-"']

index 0 contains ip address
index 3 contains date
index 4 contains time zone
index 5 contains http method
index 6 contains endpoint
index 7 contains protocal
index 8 contains http status code
index 9 contains bytes transferred
"""

import sys
from collections import defaultdict

from flask import json


def get_endpoint_calls_from_log(logfile_name, endpoint):
    found_lines = []

    with open(logfile_name, 'r') as logfile:
        for line in logfile:
            split_line = line.split()
            if split_line[6].__contains__(endpoint):
                found_lines.append(split_line)

    return found_lines


def get_num_of_not_200s(log_endpoints_to_check):
    counter = 0
    for line in log_endpoints_to_check:
        if int(line[8]) != 200:
            counter += 1
    return counter


def get_log_of_puts(log_of_endpoints_to_check):
    to_return = []
    for line in log_of_endpoints_to_check:
        if str(line[5]).__contains__("PUT"):
            to_return.append(line)
    return to_return


def print_breakdown_of_puts(log_of_puts_to_print):
    freq = defaultdict(int)
    for line in log_of_puts_to_print:
        freq[line[0]] += 1
    print(json.dumps(freq, indent=1))


if __name__ == "__main__":
    if not len(sys.argv) > 1:
        print(__doc__)
        sys.exit(1)
    infile_name = sys.argv[1]
    try:
        infile = open(infile_name, 'r')
        infile.close()
    except IOError:
        print("You must specify a valid file to parse")
        print(__doc__)
        sys.exit(1)

    # ****  First Question
    print(f"How many times was the URL '/production/file_metadata/modules/ssh/sshd_config' was fetched???")
    endpoint = "/production/file_metadata/modules/ssh/sshd_config"
    log_of_endpoints = get_endpoint_calls_from_log(infile_name, endpoint)
    print(len(log_of_endpoints))
    print()
    print("Of those requests, how many times the return code from Apache was not 200?")
    print(get_num_of_not_200s(log_of_endpoints))

    print()
    print()
    # ****  Second Question
    print("The total number of times Apache returned any code other than 200")
    log_of_endpoints = get_endpoint_calls_from_log(infile_name, "/")
    print(get_num_of_not_200s(log_of_endpoints))

    print()
    print()
    # ****  Third Question
    print("The total number of times that any IP address sent a PUT request to a path under '/dev/report/'")
    log_of_endpoints = get_endpoint_calls_from_log(infile_name, "/dev/report/")
    log_of_puts = get_log_of_puts(log_of_endpoints)
    print(len(log_of_puts))
    print("A breakdown of how many times such requests were made by IP address")
    print_breakdown_of_puts(log_of_puts)
