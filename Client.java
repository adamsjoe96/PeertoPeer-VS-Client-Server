
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client extends connectionStatus 
{
	private String name;
	private String ip;
	private int port;
	private String toname;
	private String toip;
	private int toport;
	private boolean reachable;
	private List<String> ipList;
	private Status status = Status.Activ;
	private TransferStatus transfer = TransferStatus.Keep_Close;
	private Socket socket;
	private ServerSocket server;
	
	// Constructor 
	
	public Client (String m_name, String m_ip, int m_port) throws IOException
	{
		name = m_name;
		ip = m_ip;
		port = m_port;
		toname ="";
		toip = m_ip;
		toport = m_port;
		ipList = new ArrayList<String>();
		reachable = true;
		socket = new Socket();
		server = new ServerSocket();
	}
	public Client() throws IOException
	{
		name = "";
		ip = "";
		port = 0;
		ipList = new ArrayList<String>();
		reachable = true;
		socket = new Socket();
		server = new ServerSocket();

	}
	// intialising of Getter and Setter
	

	public String getName(){return name;}
	public void setName(String newName) {name = newName;}
	
	public Socket getSocket(){return socket;}
	public void setSocket(Socket newSocket){socket = newSocket;}
	
	public ServerSocket getServerSocket(){return server;}
	public void setServerSocket(ServerSocket newServer){server = newServer;}
	
	public boolean getReachable(){return reachable;}
	public void setReachable(boolean newReachable) {reachable = newReachable;}
	
	public String getIp() {return ip;}
	public void setIp(String newIp) {ip = newIp;}
	
	public int getPort() {return port;}
	public void setPort(int newPort) {port = newPort;}
	
	public String geTotName(){return toname;}
	public void setToName(String newName) {toname = newName;}
	
	public String getToIp() {return toip;}
	public void setToIp(String newIp) {toip = newIp;}
	
	public int getToPort() {return toport;}
	public void setToPort(int newPort) {toport = newPort;}
	
	public List<String> getIpList(){return ipList;}
	public void set(List<String> newIpList) {ipList = newIpList;}
	
	public Status getStatus() {return status;}
	public void setStatus(Status newTyp) {status = newTyp;}
	
	public TransferStatus getTransfer() {return transfer;}
	public void setTransfer(TransferStatus newTyp) {transfer = newTyp;}
	
	@Override
	public String toString()
	{
		String status;
		if (getStatus() == connectionStatus.Status.Activ)
			status = "actif";
		else
			status = "inactif";
		return "Name: " + name + " ip: " + ip + " port: " + port+ " Status: "+ status + " To ip: " + toip + " to port: " + toport;
	}
	
	@Override
	public boolean equals(Object p)
	{
		Client e = (Client)p;
		return ip.equals(e.getIp()) && name.equals(e.getName()) && port == e.getPort();
	}
	
}
