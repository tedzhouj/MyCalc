

public class ThirdMain {

	public static void main(String[] args) {
		long t1=System.currentTimeMillis();
		Calc cc=new Calc("D:\\BigDataDemo\\测试表2-12.06更新\\抽查账号列表.csv");
		cc.start();
		long t2=System.currentTimeMillis();
		System.out.println("total:"+(t2-t1));
	}

}
