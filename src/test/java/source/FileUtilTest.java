package source;

import cn.gz.cm.networkoptimization.props.PropertiesUtil;
import cn.gz.cm.networkoptimization.transport.DataPool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 加载文件，将文件内容读成一行行数据，并将数据写入队列
 */
public class FileUtilTest {

    private  int readLineBuffer ;

    public FileUtilTest(){
        Properties propertiesUtil=PropertiesUtil.getPublicConf();
//      readLineBuffer=Integer.parseInt(propertiesUtil.getProperty("readline.buffer"));
    }
    public void readLineUseMemMap(File csvFile) throws IOException, InterruptedException {
           LinkedBlockingQueue<String> pool = DataPool.getPool();
            FileChannel channel = new FileInputStream(csvFile).getChannel();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0L,csvFile.length());
            ByteBuffer stringBuffer = ByteBuffer.allocate(readLineBuffer);
            System.out.println("读取字节数：" + buffer);
            // buffer.flip();// 切换模式，写->读
            while (buffer.hasRemaining()) {
                byte b = buffer.get();
                if (b == 10) { // 换行或回车 b == 10 || b == 13
                    String line = getLine(stringBuffer);
                    pool.put(line);
                } else {
                    if (stringBuffer.hasRemaining())
                        stringBuffer.put(b);
                    else { // 空间不够扩容
                        stringBuffer = reAllocate(stringBuffer);
                        stringBuffer.put(b);
                    }
                }
            }
            buffer.clear();// 清空,position位置为0，limit=capacity
            String line = getLine(stringBuffer);//读取最后一行
            pool.put(line);
    }

    public LinkedList<File> getChildDirs(File file){
        File[] files = file.listFiles();
        if (null==files||files.length==0){
            return null;
        }
        LinkedList<File> listFile = new LinkedList();
        for(File fileChild:files){
            if (fileChild.isDirectory()){
                listFile.add(fileChild);
            }
        }
        return listFile;
    }

    public LinkedList<File>   getChildFiles(File file){
        File[] files = file.listFiles();
        if (null==files||files.length==0){
            return null;
        }
        LinkedList<File> listFile = new LinkedList();
        for(File fileChild:files){
            if (fileChild.isFile()){
                listFile.add(fileChild);
            }
        }
        return listFile;
    }

    private static void readLine() throws Exception {
        RandomAccessFile randomAccessFile = new RandomAccessFile("H:\\tmp\\1.txt", "rw");
        FileChannel channel = randomAccessFile.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
        int bytesRead = channel.read(buffer);
        ByteBuffer stringBuffer = ByteBuffer.allocate(1024);
        while (bytesRead != -1) {
            System.out.println("读取字节数：" + bytesRead);
            //之前是写buffer，现在要读buffer
            buffer.flip();// 切换模式，写->读
            while (buffer.hasRemaining()) {
                byte b = buffer.get();
                if (b == 10 || b == 13) { // 换行或回车
                    stringBuffer.flip();
                    // 这里就是一个行
                    final String line = Charset.forName("utf-8").decode(stringBuffer).toString();
                    System.out.println(line + "----------");// 解码已经读到的一行所对应的字节
                    stringBuffer.clear();
                } else {
                    if (stringBuffer.hasRemaining())
                        stringBuffer.put(b);
                    else { // 空间不够扩容
                        stringBuffer = reAllocate(stringBuffer);
                        stringBuffer.put(b);
                    }
                }
            }
            buffer.clear();// 清空,position位置为0，limit=capacity
            //  继续往buffer中写
            bytesRead = channel.read(buffer);
        }
        stringBuffer.flip();
        // 这里就是一个行
        final String line = Charset.forName("utf-8").decode(stringBuffer).toString();
        System.out.println(line + "1---1-------");// 解码已经读到的一行所对应的字节
        stringBuffer.clear();
        randomAccessFile.close();
    }

    private static ByteBuffer reAllocate(ByteBuffer stringBuffer) {
        final int capacity = stringBuffer.capacity();
        byte[] newBuffer = new byte[capacity * 2];
        System.arraycopy(stringBuffer.array(), 0, newBuffer, 0, capacity);
        return (ByteBuffer) ByteBuffer.wrap(newBuffer).position(capacity);
    }

    private String getLine(ByteBuffer stringBuffer){
        stringBuffer.flip();// 切换模式，写->读
        // 这里就是一个行
        final String line = Charset.forName("utf-8").decode(stringBuffer).toString();
        System.out.println(line + "---------1---1-------");// 解码已经读到的一行所对应的字节
        stringBuffer.clear();
        return line;
    }
}
