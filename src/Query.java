

import java.util.Arrays;


public class Query {
	public byte[] clientCode;
	byte[] startDate;
	byte[] endDate;
	Double previousMarkValue=new Double(0);
	Double lastMarkValue=new Double(0);
	Double income=new Double(0);


	public Query(byte[] clientCode,byte[] startDate,byte[] endDate){
		this.startDate=startDate;
		this.endDate=endDate;
		this.clientCode=clientCode;
	}


	public double getProfit() {
		return lastMarkValue - previousMarkValue + income;
	}

	public void shareAccept(byte[] buf, int start, int end) {
		// 是否前一天持仓
		int scmp = ComparatorUtil.compareDate(startDate, buf, start
				+ Config.shareConfirmDateIndex);
		int ecmp = ComparatorUtil.compareDate(endDate, buf, start
				+ Config.shareConfirmDateIndex);
		int scmp2 = ComparatorUtil.compareDate(startDate, buf, start
				+ Config.shareEffectDateIndex);
		int ecmp2 = ComparatorUtil.compareDate(endDate, buf, start
				+ Config.shareEffectDateIndex);
		if (scmp == 1 && ecmp2 == -1)
			return;// 持仓在期间内不变
		if (scmp == 1 && scmp2 <= 0) {// 前一天持仓
			byte[] productCode = Arrays.copyOfRange(buf, start
					+ Config.shareProductCodeIndex, start + Config.shareConfirmDateIndex);
			byte[] share = Arrays.copyOfRange(buf, start + Config.shareIndex,
					end);
			Double shareDouble = Double.valueOf(new String(share).trim());
			byte[] netValue = NetValueIndex.getNetValueIndex()
					.getPreviousDateValue(productCode, startDate);
			Double mv=shareDouble
					* Double.valueOf(new String(netValue).trim());
			synchronized (previousMarkValue) {
				this.previousMarkValue+=mv;
			}
		}

		// 是否最后一天持仓
		if (ecmp >= 0 && ecmp2 == -1) {
			byte[] productCode = Arrays.copyOfRange(buf, start
					+ Config.shareProductCodeIndex, start + Config.shareConfirmDateIndex);
			byte[] share = Arrays.copyOfRange(buf, start + Config.shareIndex,
					end);
			Double shareDouble = Double.valueOf(new String(share).trim());
			byte[] netValue = NetValueIndex.getNetValueIndex()
					.getNetValue(productCode, endDate);
			Double mv=shareDouble
					* Double.valueOf(new String(netValue).trim());
			synchronized (lastMarkValue) {
				this.lastMarkValue+=mv;
			}
		}
	}

	public void acceptIncome(byte[] buf, int start, int end) {
		int cmp = ComparatorUtil.compareDate(startDate, buf, start
				+ Config.incomeConfirmDateIndex);
		int cmp1 = ComparatorUtil.compareDate(endDate, buf, start
				+ Config.incomeConfirmDateIndex);
		if (cmp <= 0 && cmp1 >= 0) {
			byte dir = buf[start + Config.incomeDirectionIndex];
			byte[] incomeByte = Arrays.copyOfRange(buf, start
					+ Config.incomeIndex, end);
			if (dir == Config.incomeDirectionPlus) {
				Double im = Double.valueOf(new String(incomeByte).trim());
				synchronized (income) {
					this.income+=im;
				}
			} else {
				Double im = Double.valueOf(new String(incomeByte).trim());
				synchronized (income) {
					this.income-=im;
				}
			}
		}
	}

}
