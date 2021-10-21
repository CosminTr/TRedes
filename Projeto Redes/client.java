import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

class client{
	public static void main (String argv[]) throws Exception
	{
		String sentence;
		String modifiedSentence;
		int serverPort = 6784;	
		StringBuilder sc = new StringBuilder();
		
		try {
			//creating client socket
		Socket clientSocket = new Socket("localhost" , serverPort);

		//creating streams for stdin, to output characters to the server, and to receive from the socket
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

		//reading sentence from stdin and sending it to the server
		Path path = Paths.get("cookies.txt");
		sentence = inFromUser.readLine();
		if (sentence.split(" ").length==1) {
				outToServer.writeBytes(makeGet(sentence.split(" "), Files.exists(path))+ "\n");
		}
		
		modifiedSentence = inFromServer.readLine();
		while(!modifiedSentence.isEmpty()) {
			if(modifiedSentence.contains("Set Cookies"))
				saveCookie(modifiedSentence);
			sc.append(modifiedSentence + "\n");
			modifiedSentence = inFromServer.readLine();
		}
		System.out.print(sc.toString());
		
		//closing I/O streams and socket
		inFromUser.close();
		inFromServer.close();
		outToServer.close();
		clientSocket.close();
		}
		catch( Exception e )
        {
			
            e.printStackTrace();
        }
	}
	
	private static String makeGet(String[] words, boolean fileExists) throws FileNotFoundException {
		StringBuilder request = new StringBuilder(	"GET " + words[0] + " HTTP/1.1\r\n" 
													+ " Host:localhost_\r\n"
													+ " Connection: closed\r\n"
													+ " User-Agent: Mozilla/5.0\r\n");
		
		
		Scanner fileReader;
		if(fileExists) {
			File cookies =new File("cookies.txt");
			fileReader = new Scanner(cookies);	
			request.append("Cookies: ");
			while (fileReader.hasNextLine()) {
				request.append(fileReader.nextLine()+";");
			}	
			request.append("\r\n");
			fileReader.close();
		}
			
		if (words.length == 2) {
			request.append("If-Modified-Since:" + words[1] + "\r\n\r\n");
		}
		
		return request.toString();
	}
	
	private static void saveCookie(String line) {
		String[] cookie=line.split("=");
		FileWriter mw;
		try {
			mw = new FileWriter("cookies.txt");
			mw.write(cookie[1] + "; \r\n");
			mw.close();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		
	}
}
