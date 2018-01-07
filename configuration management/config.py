#!/usr/bin/python2

import sys
from fabric.api import env, settings, runs_once, execute, run, task, hide
from fabric.operations import run, put, prompt, sudo

# Don't die on bad hosts
env.skip_bad_hosts=True

# Timeout for commands. Probably a little long but harmless
env.command_timeout=160

# Issa shell!
env.shell = "/bin/sh -c"

# By default, it dies when warnings happen. Ignore them so things keep thinging.
env.warn_only = True

# Yay sudo
use_sudo = True

# This task does the actual copying. Creates the directory, copies the template file, updates it per the ouptut
@task
def copytemplate():
    with hide('everything'):
        output = sudo('mkdir -p /etc/widgetfile').succeeded
        #print "My X was ", output
        if not output:
            return output
        output = put ('template.file' ,'/etc/widgetfile/', use_sudo=True).succeeded
        if not output:
            return output
        output = sudo('sed -i "s/widget_type X/widget_type `facter -p osfamily`/" /etc/widgetfile/template.file').succeeded
        if not output:
            return output
        else:
            return output

# The wrapper task that handles running each the main task for each host and counts the overall success/failure
@task
@runs_once
def go():
     results = execute(copytemplate)
     #print results
     succeeded = 0
     failed = 0
     for host, results in results.iteritems():
         #print results
         if (results == True):
           succeeded += 1
           #print {1}
         else:
           failed += 1
     print "There were", succeeded, "successful and", failed, "failed deployments"
