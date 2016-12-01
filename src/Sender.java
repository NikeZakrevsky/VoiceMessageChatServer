import java.io.IOException;
import java.lang.reflect.Type;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Sender extends Thread {
	private static List<Client> IPList = new ArrayList<Client>();
	private static DatagramPacket packet;
	private static String address;
	private static byte[] sourceData = new byte[8500];
	private static byte[] dataWithIP = new byte[8520];
	private static int serverPort = 3005;
	private static int clientPort = 3001;

	static class Client {
		public String IP;
		public String NickName;

		public Client(String IP, String NickName) {
			this.IP = IP;
			this.NickName = NickName;
		}

	}

	public static boolean inArray(String address) {
		boolean flag = true;
		String addr = address.substring(1, address.length());
		for (Client IPForSend : IPList) {
			if (IPForSend.IP.equals(addr)) {
				flag = false;
			}
		}
		return flag;
	}

	public static void main(String args[]) throws IOException {
		@SuppressWarnings("resource")
		DatagramSocket socket = new DatagramSocket(serverPort);
		DatagramPacket dataPacket = new DatagramPacket(sourceData,
				sourceData.length);
		while (true) {
			socket.receive(dataPacket);
			address = dataPacket.getAddress().toString();
			if (inArray(address)) {
				address = address.substring(1, address.length());
				Client client = new Client(address, new String(sourceData));
				IPList.add(client);
				Client client2 = new Client("192.168.1.2", "nike001");
				IPList.add(client2);
				Type listOfTestObject = new TypeToken<List<Client>>() {
				}.getType();
				String s = new Gson().toJson(IPList, listOfTestObject);
				byte[] buf = s.getBytes("UTF8");
				String s555 = new String(buf);
				String noSpaces = s555.replace("\\u0000", "");
				System.out.println(noSpaces);
				for (Client IPForSend : IPList) {
					packet = new DatagramPacket(noSpaces.getBytes(), noSpaces.getBytes().length,
							InetAddress.getByName(IPForSend.IP), clientPort);
					socket.send(packet);
				}
			} else {

				for (int i = 0; i < sourceData.length; i++) {
					dataWithIP[i] = sourceData[i];
				}
				for (int i = 0; i < address.length(); i++) {
					dataWithIP[8501 + i] = (byte) address.charAt(i);
				}

				for (Client IPForSend : IPList) {
					packet = new DatagramPacket(dataWithIP, dataWithIP.length,
							InetAddress.getByName(IPForSend.IP), clientPort);
					socket.send(packet);
				}
			}
		}
	}
}