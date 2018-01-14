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


def result_processing(results):
    """
    Results will not be in proper json format if an Error like NetworkError occurs because of timeout, so will
    nned to massage the data into a consumable format and print out results
    :param results: the results to process
    :return: nothing.
    """
    # counters to keep tally.
    num_correct=0
    num_failed=0
    for result in results:
        # print(result)
        if isinstance(results[result], str):
            if "not found" in results[result]:
                # print("NOT FOUND FOUND: " + results[result])
                num_failed += 1
            else:
                # print("SUCCESS: " + results[result])
                num_correct += 1
        else:
            # print("ERROR FOUND! " + result)
            num_failed += 1
    print("")
    print(str(num_correct) + " were correctly templated.")
    print(str(num_failed) + " were failed to template.")


@task
@runs_once
def go():
    env.skip_bad_hosts=True
    env.warn_only=True
    # hide('warnings', 'running', 'stdout', 'stderr')
    with settings(hide('warnings', 'running', 'stdout', 'stderr')):
        try:
            results = execute(upload_template_and_populate)
            result_processing(results)
            # print(results)

        except Exception, e:
            print('IT FAILED ON host %s::%s'%(env.host,str(e)))


