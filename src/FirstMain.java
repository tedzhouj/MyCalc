
import java.util.concurrent.CountDownLatch;

public class FirstMain {



	public static void main(String[] args){

		long t1=System.currentTimeMillis();
		ShareImportor si=new ShareImportor("D:\\BigDataDemo\\测试表2-12.06更新\\份额变化流水明细.csv");
		si.read();
		IncomeImportor ii=new IncomeImportor("D:\\BigDataDemo\\测试表2-12.06更新\\交易确认明细.csv");
		ii.read();
		NetValueImport ni=new NetValueImport();
		ni.firstCopy("D:\\BigDataDemo\\测试表2-12.06更新\\产品行情.csv");

		long t2=System.currentTimeMillis();
		System.out.println("total:"+(t2-t1));
	}

}
