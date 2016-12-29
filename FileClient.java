/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab.pkg11;

import java.io.*;
import java.net.*;

/**
 *
 * @author deser_000
 */
public class FileClient {

    private DatagramSocket socket = null;
    private FileHandler event = null;
    private String sourceFilePath;
    private String destinationPath;
    private String hostName;

    public FileClient() {
        this.hostName = "localHost";
        this.sourceFilePath = "C:\\Users\\Jalib\\Desktop\\abc.txt";
        this.destinationPath = "C:\\Users\\Jalib\\Desktop\\xyz.txt";
    }

    public void createConnection() {
        try {
            //send the file out to the server
            socket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName(hostName);
            byte[] incomingData = new byte[256];
            //The file that is to be sent
            event = getFileHandler();
            
            //send the file
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(outputStream);
            os.writeObject(event);
            byte[] data = outputStream.toByteArray();
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, 9876);
            socket.send(sendPacket);
            System.out.println("File sent from client");
            
            //receive confirmation
            DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
            socket.receive(incomingPacket);
            String response = new String(incomingPacket.getData());
            System.out.println("Response from server:" + response);
            
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileHandler getFileHandler() 
    {
        
        FileHandler  fileHandle = new FileHandler();
        String fileName = sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1, sourceFilePath.length());
        String path = sourceFilePath.substring(0, sourceFilePath.lastIndexOf("/") + 1);
         fileHandle.setDestinationDirectory(destinationPath);
         fileHandle.setFilename(fileName);
         fileHandle.setSourceDirectory(sourceFilePath);
        File file = new File(sourceFilePath);
        InputStream is = null;
        OutputStream os = null;
        int packets=0;
        if (file.isFile()) 
        {
            try 
            {
                
                //Read the file and build the file handle
             
            is = new FileInputStream(sourceFilePath);
            os = new FileOutputStream(destinationPath);

            byte[] buffer = new byte[256];

            int size;
            // copy the file content in bytes
            while ((size = is.read(buffer)) > 0)
            {

                os.write(buffer, 0, size);
                packets++;
                

            }
            fileHandle.setFileData(buffer);
            fileHandle.setFileSize(size);
            fileHandle.setStatus("Success");
            System.out.println("File sent in "+packets);
            is.close();
            os.close();    
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
                 fileHandle.setStatus("Error");
            }
        } else {
            System.out.println("path specified is not pointing to a file");
             fileHandle.setStatus("Error");
        }
        return  fileHandle;
    }

    public static void main(String[] args) {
        FileClient client = new FileClient();
        client.createConnection();
    }

}
