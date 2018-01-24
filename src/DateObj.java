


public class DateObj implements Comparable<DateObj>{
	public byte[] date;
	
	public DateObj(byte[] date){
		this.date=date;
	}
	
	@Override
	public boolean equals(Object obj) {
		DateObj netV=(DateObj) obj;
		int cmp=ComparatorUtil.compareDate(this.date, netV.date, 0);
		if(cmp==0){
			return true;
		}
		return false;
	}

	
	
	@Override
	public int compareTo(DateObj o) {
		return ComparatorUtil.compareDate(this.date, o.date, 0);
	}
	

}
