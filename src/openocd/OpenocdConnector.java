package openocd;

import java.net.*;
import java.io.*;

public class OpenocdConnector implements Closeable{

	public static final String DEFAULT_HOST = "127.0.0.1";
	
	private Socket socket;
	
	private InputStream input;
	
	private BufferedReader reader;
	
	private PrintWriter writer;
	
	public OpenocdConnector(int port) throws UnknownHostException, IOException {
		this.socket = new Socket(DEFAULT_HOST, port);
		this.input = socket.getInputStream();
		this.reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
		skipLines(2);
		this.writer = new PrintWriter(socket.getOutputStream(), true);
		System.out.println("Connected to " + DEFAULT_HOST + ":" + port);
	}
	
	public int getRegValue(String r) throws IOException {
		send("reg " + r);
		skipLines(1);
		String rsp = recvLine();
		skipLines(1);
		String valueString = rsp.split(" ")[2];
		int value = Integer.parseUnsignedInt(valueString.substring(2), 16);
		return value;
	}
	
	public void setRegValue(String r, int value) throws IOException {
		send("reg " + r + " " + value);
		skipLines(3);
	}
	
	public void resetHalt() throws IOException {
		send("reset halt");
		skipLines(5);
	}
	
	public void step() throws IOException {
		send("step");
		skipLines(5);
	}
	
	public void skipLines(int lines) throws IOException {
		for (int i = 0; i < lines; i++) {
			reader.readLine();
		}
	}
	
	public String recvLine() throws IOException {
		String line = reader.readLine();
		//System.out.println("Received: " + line);
		return line;
	}
	
	public void send(String str) throws IOException {
		//System.out.println("Send: " + str);
		writer.println(str);
	}
	
	public void close() throws IOException {
		send("exit");
		input.close();
		reader.close();
		writer.close();
		socket.close();
	}
	
}
