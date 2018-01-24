

public class Config {
	
	public static final int BlockLen=1048576*5;//100M
	
	public static final String ShareFilePath="D:\\bigdata\\share.out";
	public static final String IncomeFilePath="D:\\bigdata\\income.out";
	public static final String netValueFilePath="D:\\bigdata\\netValue1.out";
	public static final String netValueUpdatePath="D:\\bigdata\\netValue2.out";
	
	
	public static final int shareClientCodeIndex=0;
	public static final int shareProductCodeIndex=12;
	public static final int shareConfirmDateIndex=18;
	public static final int shareEffectDateIndex=26;
	public static final int shareIndex=34;
	
	public static final int incomeClientCodeIndex=0;
	public static final int incomeConfirmDateIndex=12;
	public static final int incomeDirectionIndex=20;
	public static final int incomeIndex=21;
	public static final int incomeDirectionPlus=43;
	public static final int incomeDirectionSub=45;
	
	public static final int shareEntryLen=50;
	public static final int incomeEntryLen=37;
	
	public static final int netValueProduce=6;
	public static final int netValueIndex=14;
	public static final int netValueLen=7;
	
	
	public static final String answerFilePath1="D:\\bigdata\\answer1.out";
	public static final String answerFilePath2="D:\\bigdata\\answer2.out";
	public static final int answerSize=1048576*5;//5M
	public static final int queryThread=5;
	
	public static final int shareQueryThread=10;
	public static final int shareReadNum=10;
	public static final int incomeQueryThread=10;
	public static final int incomeReadNum=10;

}
