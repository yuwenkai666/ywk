package myclass;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioService {
	private static ServerSocketChannel serverSocketChannel;
	private static Selector selector;
	private static ByteBuffer receiveBuffer = ByteBuffer.allocate(1024);
	private static ByteBuffer sendBuffer = ByteBuffer.allocate(1024);

	public static void main(String[] args) throws Exception {
        //打开通道
		serverSocketChannel = ServerSocketChannel.open();
		//获取scoket对象并绑定接口
		ServerSocket socket = serverSocketChannel.socket();
		socket.bind(new InetSocketAddress(8080));
		//打开选择器
		selector =Selector.open();
		System.out.println(selector);
		//设置通道为非阻塞的
		serverSocketChannel.configureBlocking(false);
		//注册接受事件
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		//监听端口的状态获取selectkey值
		while (true) {
			//返回端口准备好的操作的键值的数量 
			int keycount =selector.select(2000);
     		//System.out.println(keycount);
			//如果为零说明还没有准备好的操作继续运行
			if (keycount==0) {
				continue;
			}
			//遍历准备好的操作键值
			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			Iterator<SelectionKey> iterator = selectedKeys.iterator();
			while (iterator.hasNext()) {
				SelectionKey key = iterator.next();
				//执行完需要移除当前就绪的操作建值
				iterator.remove();
				if (key.isAcceptable()) {
					//向该通道注册读写事件
					System.out.println("接受事件");
					ServerSocketChannel channel = (ServerSocketChannel) key.channel();
					SocketChannel accept = channel.accept();
					accept.configureBlocking(false);
					accept.register(selector, SelectionKey.OP_READ);
				}
				if (key.isReadable()) {
					//执行读事件
					System.out.println("读事件");
					readDataFromSocket(key);
				}
				if (key.isWritable()) {
					//执行写事件
					System.out.println("写事件");
					writeDataFromSocket(key);
				}
				
			}
		}
	}

	private static void writeDataFromSocket(SelectionKey key) throws IOException {
		SocketChannel  channel = (SocketChannel) key.channel();
		sendBuffer.clear();
		sendBuffer.put("hellow word wo shi fu wu duan".getBytes());
		System.out.println("服务端写的数据："+sendBuffer.toString());
		sendBuffer.flip();
		channel.write(sendBuffer);
		channel.register(selector, SelectionKey.OP_READ);
		
	}

	private static void readDataFromSocket(SelectionKey key) throws IOException {
		
		SocketChannel  channel = (SocketChannel) key.channel();
		//从SocketChannel读取数据到byteBuffer
		receiveBuffer.clear();
	    channel.read(receiveBuffer);
		System.out.println("服务端读取的数据："+receiveBuffer.toString());
		//读完后向通道 注册写感兴趣写入数据
		channel.register(selector, SelectionKey.OP_WRITE);
	}
}
