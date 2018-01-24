

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class Calc {
	private String path = null;

	ConcurrentHashMap<String, List<Query>> queryClient = new ConcurrentHashMap<String, List<Query>>();
	List<List<Query>> querySS = new ArrayList<List<Query>>();

	public Calc(String path) {
		NetValueIndex.getNetValueIndex();
		this.path = path;
	}

	public void start() {
		long t1 = System.currentTimeMillis();
		CountDownLatch cdl = new CountDownLatch(Config.queryThread);
		this.readQueryList(cdl);
		this.netValueUpdate();
		try {
			cdl.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		long t2 = System.currentTimeMillis();
		System.out.println("init time:" + (t2 - t1));
		System.out.println("share...");
		ShareReader r1 = new ShareReader(queryClient);
		r1.read();
		System.out.println("income...");
		IncomeReader r2=new IncomeReader(queryClient);
		r2.read();
		System.out.println("output...");
		outResult();
	}

	public void outResult() {
		byte[] wb = new byte[Config.answerSize];
		int index = 0;
		double totalProfit = 0;

		for (List<Query> qs : querySS) {
			for (Query q : qs) {
				byte[] clientCode = q.clientCode;
				double profit = q.getProfit();
				wb[index++] = 34;
				System.arraycopy(clientCode, 0, wb, index, 12);
				index += 12;
				wb[index++] = 34;
				wb[index++] = 44;
				wb[index++] = 34;
				DecimalFormat df = new DecimalFormat("#.00");
				byte[] pt = df.format(profit).getBytes();
				totalProfit += profit;
				int len = pt.length;
				System.arraycopy(pt, 0, wb, index, len);
				index += len;
				wb[index++] = 34;
				wb[index++] = 13;
				wb[index++] = 10;
			}
		}
		try {
			File f = new File(Config.answerFilePath1);
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(wb, 0, index);
			fos.flush();
			fos.close();
			File f2 = new File(Config.answerFilePath2);
			FileOutputStream fos2 = new FileOutputStream(f2);
			fos2.write(Double.valueOf(totalProfit).toString().getBytes());
			fos2.flush();
			fos2.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(totalProfit);

	}

	// 读取问题列表
	public void readQueryList(CountDownLatch cdl) {
		try {
			FileInputStream fis = new FileInputStream(path);
			int size = fis.available();
			FileChannel fci = fis.getChannel();
			byte[] qb = new byte[size];
			MappedByteBuffer mbb = fci.map(FileChannel.MapMode.READ_ONLY, 0,
					size);
			mbb.get(qb);

			int startIndex = 0;
			int endIndex = 0;

			// 第一个开始处
			for (; startIndex < size; startIndex++) {
				if (qb[startIndex] == 10) {
					startIndex++;
					break;
				}
			}

			for (int i = 1; i <= Config.queryThread; i++) {
				endIndex = startIndex + size / Config.queryThread;
				if (endIndex > size - 1) {
					endIndex = size - 1;
				}
				for (; endIndex < size; endIndex++) {
					if (qb[endIndex] == 10) {
						break;
					}
				}
				ArrayList<Query> qs = new ArrayList<Query>();
				this.querySS.add(qs);
				new CreateQueryThread(qb, startIndex, endIndex, cdl, qs)
						.start();
				startIndex = endIndex + 1;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class CreateQueryThread extends Thread {
		byte[] qb;
		int start;
		int end;
		List<Query> qs;
		CountDownLatch cdl;

		CreateQueryThread(byte[] qb, int start, int end, CountDownLatch cdl,
						  List<Query> qs) {
			this.qb = qb;
			this.start = start;
			this.end = end;
			this.cdl = cdl;
			this.qs = qs;
		}

		public void run() {
			// 创建问题列表
			int si = start;// 上一个开始处
			for (; start <= end; start++) {
				if (qb[start] == 10) {
					byte[] endDate = Arrays.copyOfRange(qb, start - 9,
							start - 1);
					byte[] startDate = Arrays.copyOfRange(qb, start - 20,
							start - 12);
					byte[] clientCode = new byte[12];
					int len = start - si - 24;
					System.arraycopy(qb, si + 1, clientCode, 0, len);
					Calc.this.addToQuery(clientCode, startDate, endDate, qs);
					si = start + 1;
				}
			}

			cdl.countDown();
		}

	}

	public void addToQuery(byte[] clientCode, byte[] startDate, byte[] endDate,
						   List<Query> qs) {
		String cc=new String(clientCode);
		Query qy = new Query(clientCode, startDate, endDate);
		List<Query> querys = queryClient.get(cc);
		qs.add(qy);
		if (querys == null) {
			querys = new ArrayList<Query>();
			queryClient.put(cc, querys);
		}
		querys.add(qy);
	}

	// 合并行情
	public void netValueUpdate() {
		String path1 = Config.netValueFilePath;
		String path2 = Config.netValueUpdatePath;
		NetValueImport nr = new NetValueImport();
		nr.read(path1);
		nr.read(path2);
	}

}
