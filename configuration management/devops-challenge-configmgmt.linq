<Query Kind="Program">
  <Reference>&lt;RuntimeDirectory&gt;\System.Collections.Concurrent.dll</Reference>
  <Reference>&lt;RuntimeDirectory&gt;\System.Threading.dll</Reference>
  <Reference>&lt;RuntimeDirectory&gt;\System.Threading.Tasks.dll</Reference>
  <NuGetReference>SSH.NET</NuGetReference>
  <Namespace>Renci.SshNet</Namespace>
  <Namespace>System.Collections.Concurrent</Namespace>
  <Namespace>System.Threading</Namespace>
  <Namespace>System.Threading.Tasks</Namespace>
</Query>

void Main()
{
	List<string> hostList = new List<string>();

	string hostFile = Util.ReadLine<string>("Please specify file containing the hosts.txt file. This should be in the same folder as the template file. If it is the current directory you can leave it blank: ");
	if (hostFile.Length == 0)
	{
		hostFile = String.Format(@"{0}\hosts.txt", Path.GetDirectoryName(Util.CurrentQueryPath));
	}
	string workingDir = Path.GetDirectoryName(hostFile);

	// Read in the hosts from file and store as list.
	var fileText = File.ReadAllLines(hostFile);
	hostList.AddRange(fileText);

	// Get credentials needed to SSH.
	string userName = Util.ReadLine<string>("Please enter username: ");
	string password = Util.ReadLine<string>("Please enter password. Warning: The text will be visable! : ");
	Creds cred = new Creds();
	cred.userNamestring = userName;
	cred.passwordstring = password;

	try{
		var resultSet = runInParallel(hostList, cred, workingDir); // Since others did it in parallel, I figured I would too.
		resultSet.Dump();
	}
	catch (Exception ex){ ex.Dump(); }
}

public ConcurrentQueue<Results> runInParallel(List<string> hostlist, Creds creds, string workingdir)
{
	ConcurrentQueue<Results> list = new ConcurrentQueue<Results>(); // Create a thread-safe way of storing the results for each host.
	Parallel.ForEach(hostlist, currentHost => // Create a new thread for each 'currentHost' in the hostList list.
	{
		try{
			runForHost(currentHost, creds, workingdir); // Actually do seom work here.
			list.Enqueue(new Results { HostName = currentHost, Successful = true, }); // Queue the result to be added to the result collection.
		}
		catch (Exception e){
			list.Enqueue(new Results { HostName = currentHost, Successful = false, ErrorMessage = e.Message }); // Queue the result to be added to the result collection with error message.
		}
	});
	return list;
}

public void runForHost(string hostname, Creds credentials, string workingDir)
{
	// Create a new ConnectionInfo object that contains host, port, username, and password needed to SSH
	ConnectionInfo connectionInfo = new ConnectionInfo(hostname, 22, credentials.userNamestring, new AuthenticationMethod[] { new PasswordAuthenticationMethod(credentials.userNamestring, credentials.passwordstring) });
	using (var sshclient = new SshClient(connectionInfo))
	{
		sshclient.Connect(); // Open connection to host using the connectionInfo
		IDictionary<Renci.SshNet.Common.TerminalModes, uint> modes = new Dictionary<Renci.SshNet.Common.TerminalModes, uint>(); // Create dictionary to store shell properties
		modes.Add(Renci.SshNet.Common.TerminalModes.ECHO, 53); // Option 53 turns on echoing

		ShellStream shellStream = sshclient.CreateShellStream("xterm", 80, 24, 800, 600, 1024, modes); // Create shell stream to host with default sizes and the mode specified earilier.
		var output = shellStream.Expect(new Regex(@"[$>]")); // Wait for terminal prompt.
		shellStream.WriteLine("dzdo mkdir -p /etc/widgetfile"); // Write a command to the stream. This is using dzdo so there will be a prompt for the users password.
		output = shellStream.Expect(new Regex(@"([$#>:])")); // Wait for the dzdo prompt.
		shellStream.WriteLine(credentials.passwordstring); // Write the password to the stream from the credential object.

		shellStream.WriteLine("hostname"); // Run random command that will return results specific to host it is running on.
		var results = shellStream.ReadLine(); // Read the result of the command.

		var populatedFile = populateTemplate(results, "hostname", workingDir); // Returns the text to be written to the template.file on host.
		var command = String.Format("dzdo echo '{0}' > /etc/widgetfile/template.file", populatedFile); // Write file to specified directory.
		shellStream.WriteLine(command); // Run command created above.
		sshclient.Disconnect(); //Disconnect from host.
	}
}

//Reads in template file and and replaces the specified section with appropriate text.
public string populateTemplate(string newText, string hostname, string workingDir)
{
	string outPutText = "";
	var filePath = String.Format(@"{0}\template.file", workingDir);
	var text = File.ReadAllLines(filePath);
	foreach (var line in text)
	{
		var l = line;
		if (line.StartsWith("widget_type"))
		{
			l = String.Format("widget_type {0}", newText);
		}
		outPutText += String.Format("{0}{1}", l, Environment.NewLine);
	}
	return outPutText;
}

public class Creds
{
	public string userNamestring { get; set; }
	public string passwordstring { get; set; }
}

public class Results
{
	public string HostName { get; set; }
	public bool Successful { get; set; }
	public string ErrorMessage { get; set; }
}