

public class ComparatorUtil {
	
	public static int compareDate(byte[] date1,byte[] date2,int start2){
		for(int i=0;i<8;i++){
			if(date1[i]>date2[start2+i]){
				return 1;
			}else if(date1[i]<date2[start2+i]){
				return -1;
			}
		}
		return 0;
	}

}
