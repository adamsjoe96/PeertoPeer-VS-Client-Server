import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.ConnectException;



public class InitClient implements Runnable
{

	private Client client;
	static int time =0;
	static int close =0;
	InitClient(Client m_client)
	{
		client = m_client;
	}
	@Override
	public void run() {
		   try{
			   if(client.getTransfer()==connectionStatus.TransferStatus.Keep_Alive){
				   while(client.getStatus() == connectionStatus.Status.Activ){
					   Thread.sleep(4000);
					   acceptSocket();
				   }
			  }
			   else{
				   while(client.getStatus() == connectionStatus.Status.Activ){
					   Thread.sleep(4000);
					   acceptSocket();
					   client.setStatus(connectionStatus.Status.Inactiv);
				   }
			   }
			   client.setStatus(connectionStatus.Status.Activ);
		   
		   
		   }catch(ConnectException e)
		   {}
		   catch(IOException e){
			   println("the user that you are trying to reach is not online");
		   }
		   catch (InterruptedException e) {
				print ("Excelkion Client: " );
				//e.printStackTrace();
		   } catch (Exception e)
		   {
			   print("Error");
		   }
		   if (client.getReachable())
		   {
		   
		   try {
			   		Chat.welcome(client);
		   		} catch (IOException e) {
					print ("Excelkion Client: " );

		   				//e.printStackTrace();
		   		}
		   }
		   
		  /* try {
			   		client.toString();
					Chat.exit(client);
				} catch (IOException e) {
						e.printStackTrace();
				}*/	
	}
	
	public void println(String chain)
	{
		System.out.println(chain);
	}
	
	public void print(String chain)
	{
		System.out.print(chain);
	}
	
	
	public void acceptSocket() throws UnknownHostException, IOException, InterruptedException
	{
		   BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		   Socket clientSocket = new Socket(client.getToIp(), client.getToPort());
		   client.setSocket(clientSocket);
		   Runnable writeln = new Runnable(){
			   @Override
			   public void run()
			   {
				try {
						DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
						print("You: ");
						String sentence = inFromUser.readLine();
					   if(sentence.equals("DISCONNECT"))
					   {
						   sentence = "User "+  "[" + client.getName() + " " + client.getIp() + " " + client.getPort()+ "]: " + "DISCONNECT";
						   outToServer.writeBytes(sentence);
						   clientSocket.close();
						   client.setStatus(connectionStatus.Status.Inactiv);
					   }
					   else if(sentence.equals("EXIT"))
					   {
						   client.setStatus(connectionStatus.Status.Inactiv);
						   client.setReachable(false);
						   sentence = "User "+  "[" + client.getName() + " " + client.getIp() + " " + client.getPort()+ "]: " + "Unreachable";
						   outToServer.writeBytes(sentence + '\n');
						   client.getSocket().close();
						   client.getServerSocket().close();
					   }
					   else{
						   sentence = "[" + client.getName() + " " + client.getIp() + " " + client.getPort()+ " to "+ client.getToPort()+ "]: " + sentence;
							outToServer.writeBytes(sentence + '\n');
					   }
					} catch (SocketException e){}
					catch (IOException e) {
						//e.printStackTrace();
					}
			      }
			   };
			Thread write =  new Thread(writeln);
			write.start();
		    write.join();
		    clientSocket.close();
		    
	}

}
