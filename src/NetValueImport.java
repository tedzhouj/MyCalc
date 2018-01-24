

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class NetValueImport {

	public void firstCopy(String path) {
		try {
			File fi = new File(path);
			long size = fi.length();
			File fo = new File(Config.netValueFilePath);
			RandomAccessFile rafo = new RandomAccessFile(fo, "rw");
			RandomAccessFile rafi = new RandomAccessFile(fi, "r");
			FileChannel fco = rafo.getChannel();
			FileChannel fci = rafi.getChannel();
			MappedByteBuffer mbbo = fco.map(FileChannel.MapMode.READ_WRITE, 0,
					size);
			MappedByteBuffer mbbi = fci.map(FileChannel.MapMode.READ_ONLY, 0,
					size);
			mbbo.put(mbbi);
			mbbo.clear();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateCopy(String path) {
		try {
			File fi = new File(path);
			long size = fi.length();
			File fo = new File(Config.netValueUpdatePath);
			RandomAccessFile rafo = new RandomAccessFile(fo, "rw");
			RandomAccessFile rafi = new RandomAccessFile(fi, "r");
			FileChannel fco = rafo.getChannel();
			FileChannel fci = rafi.getChannel();
			MappedByteBuffer mbbo = fco.map(FileChannel.MapMode.READ_WRITE, 0,
					size);
			MappedByteBuffer mbbi = fci.map(FileChannel.MapMode.READ_ONLY, 0,
					size);
			mbbo.put(mbbi);
			mbbo.clear();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void read(String path) {
		try {
			FileInputStream in = new FileInputStream(new File(path));
			int fileLen = in.available();
			FileChannel fc = in.getChannel();
			byte[] buf = new byte[fileLen];
			MappedByteBuffer mbb = fc
					.map(FileChannel.MapMode.READ_ONLY, 0, fileLen);
			mbb.get(buf);
			int start = 0;

			for (int i = start; i < fileLen; i++) {
				if (buf[i] == 10) {
					start = i + 1;
					break;
				}
			}
			int si=start;//开始处
			int commaNo=0;//逗号数
			byte[] pc=new byte[6];
			byte[] date=new byte[8];
			byte[] value=new byte[Config.netValueLen];
			for (int j = start; j < fileLen; j++) {
				if (buf[j]==44||buf[j] == 10) {
					if(commaNo==0){
						int len=j-si-2;
						System.arraycopy(buf, si+1, pc, 0, len);
						commaNo++;
						si=j+1;
						continue;
					}
					if(commaNo==1){
						date[0]=buf[si+1];
						date[1]=buf[si+2];
						date[2]=buf[si+3];
						date[3]=buf[si+4];
						date[4]=buf[si+6];
						date[5]=buf[si+7];
						date[6]=buf[si+9];
						date[7]=buf[si+10];
						commaNo++;
						si=j+1;
						continue;
					}
					if(buf[j] == 10){
						int len=j-si-2;
						System.arraycopy(buf, si+1, value,0, len);
						NetValueIndex.getNetValueIndex().addEntry(pc,date,value);
						pc=new byte[6];
						date=new byte[8];
						value=new byte[Config.netValueLen];
						commaNo=0;
						si=j+1;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
