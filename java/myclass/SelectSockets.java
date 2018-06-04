package myclass;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class SelectSockets {
	// 端口号8888
	public static int PORT_NUMBER = 8888;
	// 缓冲区字节数组
	private ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

	public static void main(String[] args) throws Exception {
		System.out.println(System.getProperty("line.separator").hashCode()+"111");
		System.out.println("\r\n".hashCode()+"111");
		new SelectSockets().go(args);
	}

	/**
	 * 服务端口接受数据
	 * 
	 * @param args
	 * @throws Exception
	 */
	public void go(String[] args) throws Exception {
		int port = PORT_NUMBER;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		}
			System.out.println("Listening on port " + port);
			// 开启serverSocketChannel并获取serverSocket
			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			ServerSocket serverSocket = serverSocketChannel.socket();
			// 开启选择器
			Selector selector = Selector.open();
			System.out.println(selector);
			// 绑定服务端口 并将当前通道注册到选择器中
			serverSocket.bind(new InetSocketAddress(port));
			serverSocketChannel.configureBlocking(false);
			System.out.println(serverSocketChannel.accept());
			System.out.println(serverSocketChannel.validOps());
			SelectionKey selectionKey =serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println(selectionKey);
			System.out.println(serverSocketChannel.validOps());
			System.out.println(selectionKey.interestOps());
			System.out.println("SelectionKey.OP_ACCEPT:" + SelectionKey.OP_ACCEPT);
			System.out.println(serverSocketChannel.isRegistered());
			selectionKey =serverSocketChannel.keyFor(selector);
			System.out.println(selectionKey.readyOps());
			System.out.println(selectionKey);
			System.out.println(selectionKey.isValid());
			System.out.println(selectionKey.isReadable());
			System.out.println(selectionKey.isWritable());
			System.out.println(selectionKey.isAcceptable());
			System.out.println(selectionKey.isConnectable());
			while (true) {
				int n = selector.select();// 已更新其准备就绪操作集的键的数目，该数目可能为零
				System.out.println(n+"<<<<<<<<<<<<<<<<<<<");
				System.out.println(selector.keys().size());
				
				if (n == 0) {
					continue;
				}
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				while (it.hasNext()) {
					SelectionKey key = (SelectionKey) it.next();
					System.out.println(selectionKey);
					if (key.isAcceptable()) {
						ServerSocketChannel server = (ServerSocketChannel) key.channel();
						SocketChannel channel = server.accept();
						registerChannel(selector, channel, SelectionKey.OP_READ);
						sayHello(channel);
					}
					if (key.isReadable()) {
						readDataFromSocket(key);
					}
					it.remove();
				}

			}

	}

	/**
	 * 向选择器注册通道
	 * 
	 * @param selector
	 * @param channel
	 * @param ops
	 */
	public void registerChannel(Selector selector, SelectableChannel channel, int ops) {
		if (channel == null) {
			return;
		}
		try {
			channel.configureBlocking(false);// 设置非阻塞
			channel.register(selector, ops);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 从scoket读取数据
	 * 
	 * @param key
	 * @throws IOException
	 */
	public void readDataFromSocket(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		int count;
		System.out.println(byteBuffer.getChar());
		/*byteBuffer.clear();
		System.out.println(byteBuffer.get(0));*/
		while ((count = socketChannel.read(byteBuffer)) != -1) {
	/*		byteBuffer.flip();
			while (byteBuffer.hasRemaining()) {
				socketChannel.write(byteBuffer);
			}
			byteBuffer.clear();*/
			if (count < 0) {
				socketChannel.close();
			}
		}

	}

	/**
	 * 往通道里面写入字节数组里面缓存数据
	 * 
	 * @param channel
	 */
	public void sayHello(SocketChannel channel) {
		byteBuffer.clear();
		byteBuffer.put("hellow word".getBytes());
		byteBuffer.flip();// 反转缓冲区为什么呢？
		try {
			channel.write(byteBuffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
