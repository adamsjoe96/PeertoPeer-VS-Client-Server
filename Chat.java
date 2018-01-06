import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Chat {

	static ArrayList<Client> list = new ArrayList<Client>(); // 
	static long end, tStart = System.currentTimeMillis();
	private static final int MAX_AVAILABLE = 100;
	public static final Semaphore available = new Semaphore(MAX_AVAILABLE, true);
	static int trying =0;
	public static void main(String[] args) {
		
		try{
		Client client1 = new Client(args[0], args[1], Integer.parseInt(args[2]));
		Runnable ServerRunnable = new InitServer(client1); // der Server hat dieselbe ip und denselben Portnummer mit seinem Client
		Thread connectServer = new Thread(ServerRunnable);
		connectServer.start();
		
		Runnable timeCheck = new Runnable(){

			@Override
			public void run() {
				while(client1.getReachable())
				{
					end = System.currentTimeMillis();
					if(end - tStart == 10000)
					{
						try {
							
						for(Client l : list){
							//println(l.toString());
							String str = l.getIp() + " " + l.getPort();
							try{
								poke(client1, str);
							}catch (IOException e) {
									println("[" + l.getName() + " " + l.getIp() + " " + l.getPort() + " " + l.getStatus() + " " + l.getReachable() + "] is not online");
							  }
								l.setStatus(connectionStatus.Status.Inactiv);
								l.setReachable(false);
								if (!l.getReachable())
								{
									list.removeIf(x -> x.getName().equals(l.getName()) && x.getIp().equals(l.getIp()) && x.getPort() == l.getPort());
								}
							}
						  }
						  catch (ConcurrentModificationException e){}
						  //catch (InterruptedException e1) {}
						tStart =  System.currentTimeMillis();
					}
					
				}
				
			}
			
		};
		Thread checkTime = new Thread(timeCheck);
		checkTime.start();
		welcome(client1);
		checkTime.join();
		connectServer.join();
		} catch (IOException e) {
			println("Socket is closed");
		} catch (InterruptedException e1) {}
		catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e){
			println("Usage: Peers.jar <Name> <IP> <PORT>");
		}
		
		
	}
	
	public static void welcome(Client client) throws UnknownHostException, IOException
	{
		println("Welcome to our chat simulation, we hope you will like it\n");
		println("please follow the instruction");
		println("choose between connect MX, EXIT, M, CONNECT or DISCONNECT");
		Scanner sc = new Scanner(System.in);
		String str = sc.nextLine();
		boolean run = true;
		while(run)
		{
			if (str.equals("MX") || str.equals("EXIT") || str.equals("M") || str.equals("DISCONNECT") || str.equals("CONNECT") || str.equals("LIST") )
				{
					run=false;
					
				}
			else
				{
					println("choose between MX, EXIT, M, CONNECT or DISCONNECT");
					str = sc.nextLine();
				}
				
		}
		if(str.equals("MX"))
		{
			//Client client = new Client();
			print("please give the IP and the Port separate with one Space  : ");
			str = sc.nextLine();
			print("\nDo you want HTTP 0.0 or HTTP 1.1. choose 0 for 0.0 or 1 for 1.1: ");
			int type = sc.nextInt();
			if(type==0)
			{
				client.setTransfer(connectionStatus.TransferStatus.Keep_Close);
			}
			else{
				client.setTransfer(connectionStatus.TransferStatus.Keep_Alive);

			}
			messx(client, str);
		}
		else if(str.equals("LIST"))
		{
			for(Client l: list)
			{
				println(l.toString());
			}
			welcome(client);
		}
		else if(str.equals("EXIT"))
		{
			disco(client);
			exit(client);
			client.setReachable(false);
		}
		else if(str.equals("M"))
		{
			print("please give the name: ");
			String name = sc.nextLine();
			print("please give the text");
			str = sc.nextLine();
			mess(client, name, str);
			welcome(client);
		}
		else if (str.equals("CONNECT"))
		{
			print("please give the IP and the Port separate with one Space  : ");
			str = sc.nextLine();
			poke(client, str);
			welcome(client);
		}
		else if(str.equals("DISCONNECT"))
		{
			disco(client);
			welcome(client);
		}
	}
	
	public static void println(String x)
	{
		System.out.println(x);
	}
	public static void print(String x)
	{
		System.out.print(x);
	}
	
	public static void exit(Client client) throws UnknownHostException, IOException
	{
		 client.setToIp(client.getIp());
		 client.setToPort(client.getPort());
		 Socket clientSocket = new Socket(client.getIp(), client.getPort());
		 DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		 outToServer.writeBytes("EXIT");
		 client.setStatus(connectionStatus.Status.Inactiv);
		 clientSocket.close();
	}
	
	public static void messx(Client client, String str)
	{
		String[] table = str.split(" ");
		client.setToIp(table[0]);
		client.setToPort(Integer.parseInt(table[1]));
		Runnable clientRunnable = new InitClient(client);
		Thread connectClient = new Thread(clientRunnable);
		connectClient.start();
	}
    public static void mess (Client client, String name, String text) throws UnknownHostException, IOException
    {
    	try {
			available.acquire();
		
    	for(Client l : list)
    	{
    		connectionStatus.TransferStatus buf = l.getTransfer();
    		if(l.getName().equals(name))
    		{
            	l.setTransfer(connectionStatus.TransferStatus.Keep_Close);
    			Socket clientSocket = new Socket(l.getIp(), l.getPort());
    			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
    			outToServer.writeBytes("[" + client.getName() + " " + client.getIp() + " " + client.getPort()+ " to "+ client.getToPort()+ "]: " + text);
    			clientSocket.close();
    	    	l.setTransfer(buf);

    		}
    	}
    	available.release();
    	} catch (InterruptedException e) {}
    }
    public static void disco (Client client) throws UnknownHostException, IOException
    {
    	try {
			available.acquire();
		
    	for(Client l : list)
    	{
    		connectionStatus.TransferStatus buf = l.getTransfer();
        	l.setTransfer(connectionStatus.TransferStatus.Keep_Close);
			Socket clientSocket = new Socket(l.getIp(), l.getPort());
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			outToServer.writeBytes("DISCONNECT " + client.getName() + " " + client.getIp() + " " + client.getPort());
			clientSocket.close();
	    	l.setTransfer(buf);
    	}
    	available.release();
    	} catch (InterruptedException e) {}
    }
    
    public static void poke (Client client, String ipp) throws UnknownHostException, IOException
    {
    			String[] table = ipp.split(" ");
    			connectionStatus.TransferStatus buf = client.getTransfer();
            	client.setTransfer(connectionStatus.TransferStatus.Keep_Close);
       			Socket clientSocket = new Socket(table[0], Integer.parseInt(table[1]));
    			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
    			outToServer.writeBytes("POKE " + client.getName() + " " + client.getIp() + " " + client.getPort());
    			clientSocket.close();
    	    	client.setTransfer(buf);

    }
}
