import java.net.*;
import java.io.*;

public class D_Server extends Thread
{

	private ServerSocket serversocket;

	public D_Server(int port) throws IOException
	{
		serversocket = new ServerSocket(port);
		serversocket.setSoTimeout(10000);
	}

	public void run()
	{
		while(true)
		{
			try
			{
				Socket server = serversocket.accept();
				System.out.println("Just connected to " + server.getRemoteSocketAddress());
				int i=0;
				while(i<5)
				{
					DataInputStream in = new DataInputStream(server.getInputStream());
					String line = in.readUTF();
					System.out.println(line);
					DataOutputStream out = new DataOutputStream(server.getOutputStream());
					System.out.println("type: ");
					BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
					int j=0;
					String inputs = "";
					inputs = inputs +  br.readLine();
					String[] splitted = inputs.split(" ");
					if(splitted[0].equals("Sending") && splitted[1].equals("file"))
					{
						out.writeUTF(inputs + "\n");
						File file = new File(splitted[2]);
				        FileInputStream fis = new FileInputStream(file);
				        BufferedInputStream bis = new BufferedInputStream(fis);
				        //OutputStream os = socket.getOutputStream();
				        byte[] contents;
				        long fileLength = file.length(); 
				        long current = 0;
				        long start = System.nanoTime();
				        while(current!=fileLength)
				        { 
				            int size = 10000;
				            if(fileLength - current >= size)
				                current += size;    
				            else{ 
				                size = (int)(fileLength - current); 
				                current = fileLength;
				            } 
				            contents = new byte[size]; 
				            bis.read(contents, 0, size); 
				            out.write(contents);
				            System.out.print("Sending file ... "+(current*100)/fileLength+"% complete!");
				        }
				        System.out.println("Sent");  
				        out.flush(); 
						//out.writeUTF(inputs + "\n" );
					}
					else
					{
						out.writeUTF(inputs + "\n" );
					}
					j++;
					i++;
				}
				server.close();
			}
			catch(SocketTimeoutException s)
			{
            	System.out.println("Socket timed out!");
            	break;
         	}
         	catch(IOException e)
         	{
            	e.printStackTrace();
            	break;
         	}
		}
	}

	public static void main(String [] args)
	{
		try
		{
			Thread t = new D_Server(8080);
			t.start();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}