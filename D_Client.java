import java.net.*;
import java.io.*;

public class D_Client
{
	public static void main(String [] args)
	{
		try 
		{
			Socket client = new Socket("127.0.0.1",8080);
			System.out.println("Just connected to " + client.getRemoteSocketAddress());
			int i=0;
			while(i<5)
			{
				OutputStream toserver = client.getOutputStream();
				DataOutputStream out = new DataOutputStream(toserver);
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				System.out.println("type: ");
				String inputs = br.readLine();
				out.writeUTF( inputs + "\n" );
				InputStream fromserver = client.getInputStream();
	        	DataInputStream in = new DataInputStream(fromserver);
	        	String got = in.readUTF();
	        	System.out.println("Client sent : " + got);
	        	String[] splitted = got.split(" ");
	        	System.out.println(splitted[1]);
	        	if(splitted[0].equals("Sending") && splitted[1].equals("file"))
	        	{
	        		byte[] contents = new byte[10000];
			        FileOutputStream fos = new FileOutputStream("A.java");
			        BufferedOutputStream bos = new BufferedOutputStream(fos);
			        InputStream is = client.getInputStream();
			        int bytesRead = 0;
			        while((bytesRead=is.read(contents))!=-1)
			        {
			        	System.out.println(contents);
			            bos.write(contents, 0, bytesRead);
			            System.out.println("Ashwin2");
			        }
			        System.out.println("Ashwin");
			        bos.flush(); 
	        	}
	        	else
	        	{
	        		toserver = client.getOutputStream();
	        	}
	        	i++;
	        }
        	client.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}