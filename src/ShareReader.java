

import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class ShareReader {

	ConcurrentHashMap<String, List<Query>> queryClient;
	
	int processLen;
	
	public ShareReader(ConcurrentHashMap<String, List<Query>> queryClient) {
		this.queryClient = queryClient;
	}

	public void read() {
		try {
			File f = new File(Config.ShareFilePath);
			FileInputStream fis = new FileInputStream(f);
			FileChannel fc=fis.getChannel();
			int fileLen=fis.available();
			int entryNum=fileLen/50;
			int readNum=Config.shareReadNum;
			int readLen=entryNum/readNum;
			int threadNum=Config.shareQueryThread;
			processLen=readLen/threadNum*50+50;
			int start=0;
			int len=processLen;
			for(int i=1;i<=readNum;i++){
				CountDownLatch cdl=new CountDownLatch(threadNum);
				for(int j=1;j<=threadNum;j++){
					byte[] buf=new byte[len];
					MappedByteBuffer mbb=fc.map(FileChannel.MapMode.READ_ONLY, start, len);
					mbb.get(buf);
					ShareRead sr=new ShareRead(buf,len,cdl);
					sr.start();
					start+=processLen;
					if(start+processLen>=fileLen){
						len=fileLen-start;
					}
				}
				cdl.await();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	class ShareRead extends Thread{
		byte[] buf;
		CountDownLatch cdl;
		int len;
		
		ShareRead(byte[] buf,int len,CountDownLatch cdl){
			this.buf=buf;
			this.cdl=cdl;
			this.len=len;
		}
		
		public void run(){
			for(int i=0;i<len;i+=Config.shareEntryLen){
				byte[] cc=new byte[12];
				System.arraycopy(buf, i, cc, 0, 12);
				
				String ccStr=new String(cc);
				
//				if(ccStr.equals("021001681751")){
//					System.out.println("$$");
//				}
				
				List<Query> qs=queryClient.get(ccStr);
				if(qs!=null&&qs.size()>0){
					for(Query q:qs){
						q.shareAccept(buf, i, i+50);
					}
				}
			}
			cdl.countDown();
		}
	}
}
