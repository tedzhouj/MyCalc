


public class SecondMain {

	public static void main(String[] args) {
		long t1=System.currentTimeMillis();
		NetValueImport ni=new NetValueImport();
		ni.updateCopy("D:\\BigDataDemo\\测试表2-12.06更新\\产品行情更新.csv");
		long t2=System.currentTimeMillis();
		System.out.println("total:"+(t2-t1));
	}

}
