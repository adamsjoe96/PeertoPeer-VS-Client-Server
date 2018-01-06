import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class InitServer implements Runnable
{

	private Client client;
	InitServer(Client m_client)
	{
		client = m_client;
	}
	@Override
	public void run() {
		
			try{
				ServerSocket sock = new ServerSocket();
				client.setServerSocket(sock);
				sock.bind(new InetSocketAddress(client.getIp(), client.getPort()));
				client.setStatus(connectionStatus.Status.Activ);
				while (client.getStatus()==connectionStatus.Status.Activ) {
					   acceptSocket(sock);					   
					if (client.getStatus()==connectionStatus.Status.Inactiv)
					{
						sock.close();
					}
					if (client.getReachable()){
						 Runnable serverRunnable = new InitServer(client);
						 Thread acco = new Thread(serverRunnable);
						 acco.start();
					}
				}
				 
			}
			catch (UnknownHostException e) {
				println("[-] The Host couldn't be resolved");
			}
			catch(IOException e){ /*en("must imperatively be treated otherwise an error appears)  fr(doit imperativement etre traité sinon une erreur apparait)*/
				//println("[-] the Socket ist Closed");
			} catch (InterruptedException e) {
				//e.printStackTrace();
			}
		
	}

	public static void print(String chain)
	{
		System.out.println(chain);
	}
	public static void println(String chain)
	{
		System.out.println(chain);
	}
	private void acceptSocket(ServerSocket sock) throws IOException, InterruptedException
	{
		  //print("server listening...");
		  Socket connectionSocket = sock.accept();
		   BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		   Runnable writeln = new Runnable(){
			   @Override
			   public void run()
			   {
				try {
						String clientSentence = inFromClient.readLine();
						try {
							String[] table = clientSentence.split(" ");						
						if (table[table.length-1].equals("DISCONNECT") && table.length == 1)
						{
							System.out.println("\n" + clientSentence);
							client.setStatus(connectionStatus.Status.Inactiv);						
						}
						else if(table[0].equals("DISCONNECT") && table.length == 4)
						{
							System.out.println("\n" + clientSentence);
							Chat.list.removeIf(x -> x.getName().equals(table[1]) && x.getIp().equals(table[2]) && x.getPort() == Integer.parseInt(table[3]));
							Chat.disco(new Client(table[1], table[2], Integer.parseInt(table[3]) ));
							
						}
						else if (table[0].equals("POKE") && table.length == 4)
						{
							int h = Integer.parseInt(table[3]);
							if(Chat.list.contains(new Client(table[1], table[2], Integer.parseInt(table[3]))))
							{
								println("POKE FROM: " + table[1] + " " + table[2] + " " + h);
								for(Client l : Chat.list)
								{
									Chat.poke(new Client(table[1], table[2], h), (l.getIp() + " " + l.getPort()));
									
								}
							}
							else{
								Chat.list.add(new Client(table[1], table[2], h));
							}
						}
						else if(table[table.length-1].equals("EXIT"))
						{
							if(client.getIp().equals(client.getToIp()) && client.getPort()==client.getToPort())
							{
							 	client.setReachable(false);
							 	client.setStatus(connectionStatus.Status.Inactiv);
							}
						}
						else if (table[table.length-1].equals("Unreachable"))
						{
							client.getSocket().close();
							//connectionSocket.close();
						}
						else{
							System.out.println("\n" + clientSentence);
						}
					 }catch (NullPointerException e){}
					} catch (IOException e) {
						//e.printStackTrace();
					}catch (Exception e){
						println("Error");
					}
			   }
				   
		   };
		   Thread write = new Thread(writeln);
		   write.start();
		   write.join();
		   connectionSocket.close();
		   client.setStatus(connectionStatus.Status.Inactiv);	
		   
	}
}
