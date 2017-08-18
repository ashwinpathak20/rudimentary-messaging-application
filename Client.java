import java.io.*;
import java.net.*;


@SuppressWarnings("serial")
public class Client extends Thread
{

    private static DataOutputStream dos;
    private static DataInputStream dis;
    private static Socket client;
    private static DatagramSocket udpSocket;
    private static DatagramSocket udpSocket1;
    private static String textin, exitword ="over";
    public String type;
    public static Boolean flag = false;

    public static void main(String[] args)
    {
        try
        {
            System.out.println("Bob wants to chat with Alice");
            client = new Socket("127.0.0.1",8000);
            udpSocket = new DatagramSocket(8080);
            udpSocket1 = new DatagramSocket();
            System.out.println("Alice is online with Address: "+client.getInetAddress()); 
            dos=new DataOutputStream(client.getOutputStream());
            dis=new DataInputStream(client.getInputStream());
            dos.flush();
            flag = true;
        } 
        catch (Exception e)
        {
            System.out.println("It seems Bob is not in a mood to chat (Problem setting up streams and client!)");
        }
        Client sender = new Client("sender");
        Client receiver = new Client("receiver");
        sender.start();
        receiver.start();
    }

    public Client(String t)
    {
        super();
        type = t;
    }

    public String percenti(long percent)
    {
        String x = "";
        int i=0,j;
        for(i=0;i<percent;i++)
        {
            x = x+"=";
        }
        x = x+=">";
        for(j=i;j<10;j++)
        {
            x = x+" ";
        }
        return x;
    }

    public void run() {
        while(flag==true) 
        {
            if(type.equals("sender"))
            {
                System.out.print(">>");
                try 
                {
                    BufferedReader inn = new BufferedReader(new InputStreamReader(System.in));
                    textin = inn.readLine();
                    try
                    {
                        dos.writeUTF("Bob: " + textin);
                        String[] splitted = textin.split(" ");
                        if(splitted.length==3 && splitted[0].equals("Sending"))
                        {
                            File file = new File(splitted[1]);
                            if(!file.exists() && !file.isFile())
                            {
                                long fileLength = -1;
                                dos.writeLong(fileLength);
                                System.out.println("The file does not exist");
                            }
                            else if(!splitted[2].equals("UDP") && !splitted[2].equals("TCP"))
                            {
                                long fileLength = -2;
                                dos.writeLong(fileLength);
                                System.out.println("No correct protocol");
                            }
                            else
                            {
                                FileInputStream fis = new FileInputStream(file);
                                byte[] contents = new byte[10];
                                long fileLength = file.length();
                                dos.writeLong(fileLength);
                                int current = 0;
                                long left = 0, percent, per1;
                                InetAddress host = InetAddress.getByName("localhost");
                                while((current=fis.read(contents))>0)
                                { 
                                    if(splitted[2].equals("UDP"))
                                    {
                                        DatagramPacket dp = new DatagramPacket(contents, contents.length, host, 8081);
                                        udpSocket1.send(dp);
                                    }
                                    else
                                    {
                                        dos.write(contents, 0, current);
                                    }
                                    left += current;
                                    percent = (left*100)/fileLength;
                                    per1 = percent/10;
                                    Thread.sleep(1);
                                    System.out.print("Sending " + splitted[1] + "[" + percenti(per1) + "] " + percent + "%\r");
                                }
                                System.out.print("\n");
                                System.out.println("Sent file");
                                fis.close();
                            }
                        }
                    }
                    catch (IOException ioException)
                    {
                        System.out.println("Error: Bob is having inputs-outputs exceptions");
                    }      
                    if(textin.equals(exitword))
                    {
                        System.out.println("Chat is over now");
                        try
                        {
                            flag=false;
                            textin = "Chat is terminated.  Have a nice day.";
                            dos.close();
                            client.close();
                            break;
                        }
                        catch (Exception One)
                        {
                            System.out.println("Bad termination");
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else if(type.equals("receiver"))
            {
                try 
                {
                    String recv = dis.readUTF();
                    System.out.print("\n");
                    System.out.println(recv);
                    String[] splitted = recv.split(" ");
                    if(splitted.length==2 && splitted[1].equals(exitword))
                    {
                        System.out.println("Chat is over now");
                        try
                        {
                            flag=false;
                            textin = "Chat is terminated.  Have a nice day.";
                            dos.close();
                            client.close();
                            break;
                        }
                        catch (Exception One)
                        {
                            System.out.println("Bad termination");
                        }
                    }
                    if(splitted.length==4 && splitted[1].equals("Sending"))
                    {
                        byte[] contents = new byte[10];
                        byte[] contents1 = new byte[10];
                        FileOutputStream fos = new FileOutputStream(splitted[2]+"x");
                        int bytesRead = 0;
                        long fileSize = dis.readLong();
                        DatagramPacket dp = new DatagramPacket(contents1, contents1.length);
                        if(fileSize==-1)
                        {
                            System.out.println("File name does not exists");
                        }
                        else if(fileSize==-2)
                        {
                            System.out.println("Incorrect protocols are used");
                        }
                        else
                        {
                            long left = 0, percent , per1;
                            while(left <= fileSize)
                            {
                                if(splitted[3].equals("UDP"))
                                {
                                    udpSocket.receive(dp);
                                    contents=dp.getData();
                                    bytesRead=dp.getLength();
                                }
                                else
                                {
                                    bytesRead=dis.read(contents);
                                }
                                if(bytesRead<=0)
                                {
                                    break;
                                }
                                fos.write(contents, 0, bytesRead);
                                left = left + bytesRead;
                                percent = (left*100)/fileSize;
                                per1 = percent/10;
                                System.out.print("Receiving " + splitted[1] + "[" + percenti(per1) + "] " + percent + "%\r");
                                if(left>=fileSize)
                                {
                                    break;
                                }
                            }
                            System.out.print("\n");
                            System.out.println("Received file");  
                            fos.close();
                        }
                    }
                } 
                catch(Exception e)
                {
                    System.out.println("Error: Network is not allowing Bob to intepret the data");
                }
                System.out.print(">>");
            }
            try 
            {
                Thread.sleep(500);
            }
            catch(Exception e)
            {
                System.out.println("Something fishy happened! Both Alice and Bob do not want to talk.");
            }
        }   
    }
}
