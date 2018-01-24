

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.CountDownLatch;

public abstract class Importor {

	int count=0;

	String path = null;

	int fileLen = 0;
	int start = 0;
	int end=0;

	int writeIndex;
	byte[] writeByte = new byte[Config.BlockLen];

	CountDownLatch cdl;

	FileChannel fco;
	int writeStart = 0;

	public Importor(String path) {
		this.path = path;
	}

	protected abstract File getOutPutFile();

	//找后面的回车
	public int splitBlock(byte[] buf, int size) {
		cdl = new CountDownLatch(1);

		int endIndex = 0;
		for (int i = size - 1; i >= 0; i--) {
			if (buf[i] == 10) {
				endIndex = i;
				break;
			}
		}
		new ParserThread(buf,endIndex,cdl).start();
		return endIndex;

	}

	class ParserThread extends Thread{
		byte[] buf;int endIndex;CountDownLatch cdl;

		ParserThread(byte[] buf,int endIndex,CountDownLatch cdl){
			this.buf=buf;
			this.endIndex=endIndex;
			this.cdl=cdl;
		}

		public void run(){
			parser(buf,endIndex);
			cdl.countDown();
		}

	}

	public abstract void parser(byte[] buf,int endIndex);

	//收集到的数据，写入
	public void write(){
		try {
			MappedByteBuffer mbbo = fco.map(FileChannel.MapMode.READ_WRITE,
					writeStart, writeIndex);
			mbbo.put(writeByte, 0, writeIndex);
			writeStart += writeIndex;
			writeIndex = 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	public void read() {
		try {

			File fo = getOutPutFile();
			RandomAccessFile rafo = new RandomAccessFile(fo, "rw");
			fco = rafo.getChannel();

			File f = new File(path);

			FileInputStream fis = new FileInputStream(f);
			fileLen = fis.available();
			FileChannel fc = fis.getChannel();

			byte[] buf0 = new byte[200];
			MappedByteBuffer mbb0 = fc.map(FileChannel.MapMode.READ_ONLY, 0,
					200);
			mbb0.get(buf0);
			for (int i = 50; i < 200; i++) {
				if (buf0[i] == 10) {
					start = i + 1;
					break;
				}
			}
			int readLen = Config.BlockLen;
			do {
				byte[] buf1 = new byte[readLen];
				MappedByteBuffer mbb1 = fc.map(FileChannel.MapMode.READ_ONLY,
						start, readLen);
				System.out.println("R");// 1
				mbb1.get(buf1);
				System.out.println("RO");// 1
				if (cdl != null) {
					cdl.await();
					System.out.println("W");
					this.write();
					System.out.println("WO");
				}
				start += this.splitBlock(buf1, readLen) + 1;
				end = start + Config.BlockLen;

			} while (end < fileLen);
			readLen=fileLen-start;
			byte[] buf2=new byte[readLen];
			MappedByteBuffer mbb2=fc.map(FileChannel.MapMode.READ_ONLY, start, readLen);
			System.out.println("R");// 1
			mbb2.get(buf2);
			System.out.println("RO");// 1
			cdl.await();
			System.out.println("W");
			this.write();
			System.out.println("WO");
			//最后一处解析
			this.parser(buf2, readLen-1);
			System.out.println("W");
			this.write();
			System.out.println("WO");
			System.out.println("count:"+count);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
