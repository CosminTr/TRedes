import java.io.*;
import java.net.*;

class server{
	public static void main (String argv[]) throws Exception
	{
		String clientSentence;
		int portNumber = 6784;
		StringBuilder sb = new StringBuilder();
		String resource="";
		int code=200;
		

		//creating welcome socket (the door to knock) -- only used to accept connections
		ServerSocket welcomeSocket = new ServerSocket(portNumber);
		
			while(true){
				
				
					//creating connection socket -- one per TCP connection
			Socket connectionSocket = welcomeSocket.accept();
			
			//creating input and output streams (to receive from/send to client socket)
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

			//capitalizing the received sentence and sending it back to the client
			
			clientSentence = inFromClient.readLine();
			String[] sections = clientSentence.split(" ");
			resource=sections[1];
			while(!clientSentence.isEmpty()) {
				if(!sections[0].equals("GET")) {
					outToClient.writeBytes("HTTP/1.1 501 Method Unimplemented \r\n\r\n");
					break;
				}
				if(clientSentence.contains("If-Modified-Since")) {
					String[] mod=clientSentence.split(":");
					if(new File("cookies.txt").lastModified()<Long.parseLong(mod[1]))
						code=400;
				}
				
				clientSentence = inFromClient.readLine();
			}
			
			if(sections[0].equals("GET"))
				outToClient.writeBytes(makeResponse(resource,code,sb));
			
			//closing the I/O streams and the socket
			connectionSocket.close();
			inFromClient.close();
			outToClient.close();
				
			}
			
		}
	
	private static String makeResponse(String resource, int code, StringBuilder sb) {
		
		if (code==200) {
			sb.append("HTTP/1.1 200 OK\r\n");
			sb.append("Date:" + "12" + "\r\n");
			sb.append("Server:localhost\r\n");
			sb.append("Content-Type: text/html\r\n");
			sb.append("Connection: Closed\r\n");
			sb.append("Set Cookies: "+ resource + "=alfa" + resource + "\r\n");
			sb.append("\r\n");
		}
		else if(code==304) {
			sb.append("HTTP/1.1 304 Not Modified\r\n");
			sb.append("Date:" + "12" + "\r\n");
			sb.append("Server:localhost\r\n");
			sb.append("\r\n");
		}
		else if(code==400) {
			
		}
		else if(code==404) {
			sb.append("HTTP/1.1 404 Not Found\r\n");
			sb.append("Date:" + "12" + "\r\n");
			sb.append("Server:localhost\r\n");
			sb.append("\r\n");
		}
		
		return sb.toString();
	}
}
