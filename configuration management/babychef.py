#!/usr/bin/python2

#Import required libraries for use
import os, sys
from fabric.operations import run, put, sudo
from fabric.api import env, task, run, hide, execute, runs_once

#Set environment variables
env.skip_bad_hosts=True #Allows for the continuation if hosts are bad
env.command_timeout=10  #Specify desired timeout duration
env.shell = "/bin/sh -c"#Which shell you want to use
env.warn_only = True    #Ignore warnings and continue running
use_sudo = True         #Sudo usage
env.user = ''           #Username with sudo permissions
env.password = ''       #Password for abve user

#Creates the directory, places file on specified hosts and changes contents per hostname
@task
def placefile():
    with hide('everything'):
        sudo('mkdir -p /etc/widgetfile/')
        put("./template.file", "/etc/widgetfile/template.file", use_sudo = True)
        sudo('sed -i "s/widget_type X/widget_type `hostname`/" /etc/widgetfile/template.file')

#Wraps the above task and lists failures with message or success with none
@task
@runs_once
def doit():
    collected_output = execute(placefile)
    for host, info in collected_output.iteritems():
        print("On host {0} error was {1}".format(host, info))
