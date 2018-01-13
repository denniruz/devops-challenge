from __future__ import with_statement
from fabric.api import task, execute, env, local, settings, run, runs_once, hide, put
import StringIO


file_str = ""
with open('template.file', 'r') as f:
    file_str = f.read()


@task
def upload_template_and_populate():
    # result = run('whoami')
    result = run('facter -p widget')
    # print(result)
    file_text = file_str.replace("widget_type X", "widget_type " + result)
    # print(file_text)
    put(StringIO.StringIO(file_text), "/etc/widgetfile", use_sudo=True)
    return result

@task
@runs_once
def go():
    env.skip_bad_hosts=True
    env.warn_only=True
    # hide('warnings', 'running', 'stdout', 'stderr')
    with settings(hide('warnings')):
        try:
            results = execute(upload_template_and_populate)
            print(results)

        except Exception, e:
            print('IT FAILED ON host %s::%s'%(env.host,str(e)))


