package myclass;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class ConnectAsync {
    
	public static void main(String[] args) throws Exception{
		String host = "localhost";
		int port = 8888;
		if (args.length == 2) {
			host = args[0];
			port = Integer.parseInt(args[1]);
		}
		InetSocketAddress addr = new InetSocketAddress(host, port);
		SocketChannel sc = SocketChannel.open();
		sc.configureBlocking(false);
		System.out.println("initiating connection");
		sc.connect(addr);
		/*Selector selector = Selector.open();
		System.out.println(selector);*/
		
		System.out.println(sc.validOps());
		/*while (!sc.finishConnect()) {
			doSomethingUseful();
		}*/
		sc.write(ByteBuffer.allocate(1024).put("hellow_word".getBytes()));
		System.out.println("connection established");
		sc.close();
	}

	private static void doSomethingUseful() {
		System.out.println("doing something useless");
	}
}
