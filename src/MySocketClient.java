import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class MySocketClient {

	private Socket soc = null;
	private String server = "192.168.99.100";
	private int port = 6666;

	public MySocketClient(String server, int port) {
		super();
		this.server = server;
		this.port = port;
		try {
			soc = new Socket(server, port);
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	} // 给服务器发送消息

	public void sendMsgToServer(String msg) {
		try {
			OutputStream out = soc.getOutputStream();
			out.write(msg.getBytes());
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	} // 从服务器接收消息

	public void recMsgFromServer() {
		byte[] b = null;
		StringBuilder text = null;
		try {
			InputStream in = soc.getInputStream();
			b = new byte[1024];
			int len = 0;
			while ((len = in.read(b)) != -1) {
				String strText = new String(b, 0, len);
				System.out.println("[receive]" + strText);
				break;
			}
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

	public void writeDataToTxt(String strText) {
		String fileName = "d\\01.txt";
		File f = new File(fileName);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(f, f.exists());
			strText = "\n" + strText;
			out.write(strText.getBytes());
			out.close();
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			try {
				out.close();
			} catch (IOException e1) {
				// TODO 自动生成的 catch 块
				e1.printStackTrace();
			}
		}
	}
}
