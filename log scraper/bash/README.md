Using Bash (and friends) to parse log data
===============

Basic hammer/anvil approach to beat things out of the data.

Analysis:

* grep -c returns a count of lines matching the pattern.  The URI pattern is pretty unique, no need to worry about fields.


        684-osx:bash ashelley$ grep -c '/production/file\_metadata/modules/ssh/sshd\_config' ../puppet_access_ssl.log 
        6


* Same URI pattern, but a particular field should match 200.  More _awk_ territory.  ALso, ssh\_config is unique enough a pattern match.  The HTTP status is contained in the ninth field.  First, get the count of when status is actually 200.  _Awk_ to filter, dumps the lines, _wc -l_ to generate a count.  

        684-osx:bash ashelley$ awk '/sshd_config/ {if ($9 = 200) print }'  ../puppet_access_ssl.log | wc -l
        6

    Apparently it's the same, so if we exclude any status of 200, it should be 0.
	
		684-osx:bash ashelley$ awk '/sshd_config/ {if ($9 != 200) print }'  ../puppet_access_ssl.log | wc -l
		       0
	
    That's something that awk should be able to return itself.  First, test when status is 200.
		
		684-osx:bash ashelley$ awk '/sshd_config/ {if ($9 = 200) count++ }END{print count}'  ../puppet_access_ssl.log 
		6
	
	  And when it is not 200.
		684-osx:bash ashelley$ awk '/sshd_config/ {if ($9 != 200) count++ }END{print count}'  ../puppet_access_ssl.log 
		
	
	  Note it doesn't return 0, it returns nothing.


* A count of all entries without a status of 200 is an extension of the above _awk_ where we just remove the beginning pattern match.

		684-osx:bash ashelley$ awk '{if ($9 != 200) count++ }END{print count}'  ../puppet_access_ssl.log 
		6
       

* Similar awk to get a sum here, filtering by simplest URI string match "/dev/report" and counting if the 6th field has the PUT string.

		684-osx:bash ashelley$ awk '/\/dev\/report/ {if ($6 ~ PUT) count++ }END{print count}'  ../puppet_access_ssl.log 
		9
       
* The above query, with a breakdown by client IP.  Many will simply resort to _sort_ and _uniq_.

		684-osx:bash ashelley$ awk '/\/dev\/report/ {if ($6 ~ PUT) print $1}'  ../puppet_access_ssl.log  | sort -n | uniq -c
		 1 10.101.3.205
		 1 10.114.199.41
		 1 10.204.150.156
		 1 10.204.211.99
		 1 10.34.89.138
		 1 10.39.111.203
		 1 10.80.146.96
		 1 10.80.174.42
		 1 10.80.58.67
       
    _Awk_ can handle that as well.
	
		684-osx:bash ashelley$ awk '/\/dev\/report/ {if ($6 ~ PUT) a[$1]++ } END { for (n in a) print n, a[n] }'  ../puppet_access_ssl.log 
		10.204.150.156 1
		10.114.199.41 1
		10.39.111.203 1
		10.80.146.96 1
		10.80.58.67 1
		10.34.89.138 1
		10.101.3.205 1
		10.80.174.42 1
		10.204.211.99 1
	
	  Well, that's not sorted.
	
		684-osx:bash ashelley$ awk '/\/dev\/report/ {if ($6 ~ PUT) a[$1]++ } END { for (n in a) print n, a[n]|"sort" }'  ../puppet_access_ssl.log 
		10.101.3.205 1
		10.114.199.41 1
		10.204.150.156 1
		10.204.211.99 1
		10.34.89.138 1
		10.39.111.203 1
		10.80.146.96 1
		10.80.174.42 1
		10.80.58.67 1
	