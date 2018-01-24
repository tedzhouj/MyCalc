

import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class NetValueIndex {


	ConcurrentHashMap<String,TreeMap<DateObj,byte[]>> netValues = new ConcurrentHashMap<String, TreeMap<DateObj,byte[]>>();

	private static NetValueIndex index;

	private NetValueIndex() {
	}

	private static synchronized void init() {
		if (null == index) {
			index = new NetValueIndex();
		}
	}

	public static NetValueIndex getNetValueIndex() {
		if (null == index) {
			init();
		}
		return index;
	}


	public void addEntry(byte[] pc,byte[] date,byte[] value) {
		String pcStr=new String(pc);
		DateObj nv=new DateObj(date);
		TreeMap<DateObj,byte[]> nvs=netValues.get(pcStr);
		if(nvs==null){
			nvs=new TreeMap<DateObj, byte[]>();
			netValues.put(pcStr, nvs);
		}
		byte[] ve=nvs.get(nv);
		if(ve!=null){
			nvs.remove(nv);
		}
		nvs.put(nv, value);
	}


	/**
	 * 获取当天净值
	 *
	 * @param productCode
	 * @param date
	 * @return
	 */
	public byte[] getNetValue(byte[] productCode, byte[] date) {

		String pcStr=new String(productCode);

		DateObj dateO=new DateObj(date);
		TreeMap<DateObj,byte[]> values= netValues.get(pcStr);
		byte[] value=values.get(dateO);
		while (value == null) {
			dateO=values.lowerKey(dateO);
			if (dateO == null)
				return null;
			value = values.get(dateO);
		}
		return value;
	}

	/**
	 * 获取当一天净值
	 *
	 * @param productCode
	 * @param date
	 * @return
	 */
	public byte[] getPreviousDateValue(byte[] productCode, byte[] date) {
		String pcStr=new String(productCode);
		DateObj dateO=new DateObj(date);
		TreeMap<DateObj,byte[]> values= netValues.get(pcStr);
		dateO=values.lowerKey(dateO);//获取前一天净值
		if(dateO==null){
			byte[] b=new byte[1];
			b[0]=48;
			System.out.println("#");
			return b;
		}
		byte[] value=values.get(dateO);
		while (value == null) {
			dateO=values.lowerKey(dateO);
			if (dateO == null)
				return null;
			value = values.get(dateO);
		}
		return value;
	}

}
