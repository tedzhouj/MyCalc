

import java.io.File;

public class IncomeImportor extends Importor {

	public IncomeImportor(String path) {
		super(path);
	}

	@Override
	protected File getOutPutFile() {
		return new File(Config.IncomeFilePath);
	}

	@Override
	public void parser(byte[] bs, int endIndex) {
		int commaIndex = 0;
		byte[] buf = new byte[100];
		int bufIndex = 0;

		int incomeEnd = 0;

		boolean flag = false;// 是否入索引
		for (int i = 0; i<=endIndex; i++) {
			byte tempB = bs[i];
			buf[bufIndex++] = tempB;
			if (tempB == 44 || tempB == 10) {
				if (commaIndex == 0) {

					int clientCodeIndex = Config.incomeClientCodeIndex + writeIndex;
					int length = bufIndex - 3;
					System.arraycopy(buf, 1, writeByte, clientCodeIndex, length);
					int si=clientCodeIndex+length;
					while(si<writeIndex+Config.incomeConfirmDateIndex){//不满12位被0
						writeByte[si++]=0;
					}
					commaIndex++;
					bufIndex = 0;
					continue;
				}
				if (commaIndex == 6) {

					int confirmDateIndex = Config.incomeConfirmDateIndex + writeIndex;
					writeByte[confirmDateIndex++] = buf[1];
					writeByte[confirmDateIndex++] = buf[2];
					writeByte[confirmDateIndex++] = buf[3];
					writeByte[confirmDateIndex++] = buf[4];
					writeByte[confirmDateIndex++] = buf[6];
					writeByte[confirmDateIndex++] = buf[7];
					writeByte[confirmDateIndex++] = buf[9];
					writeByte[confirmDateIndex] = buf[10];
					commaIndex++;
					bufIndex = 0;
					continue;

				}
				if (commaIndex == 7) {



					int directionIndex = Config.incomeDirectionIndex + writeIndex;
					int tempIndex = bufIndex - 6;

					if (buf[tempIndex] == -55 && buf[tempIndex + 1] == -22
							&& buf[tempIndex + 2] == -71
							&& buf[tempIndex + 3] == -70) {// 申购
						writeByte[directionIndex] = Config.incomeDirectionSub;
						flag = true;
						commaIndex++;
						bufIndex = 0;
						continue;
					}
					if (buf[tempIndex] == -54 && buf[tempIndex + 1] == -22
							&& buf[tempIndex + 2] == -69
							&& buf[tempIndex + 3] == -40) {// 赎回
						writeByte[directionIndex] = Config.incomeDirectionPlus;
						flag = true;
						commaIndex++;
						bufIndex = 0;
						continue;
					}
					if (buf[tempIndex] == -73 && buf[tempIndex + 1] == -42
							&& buf[tempIndex + 2] == -70
							&& buf[tempIndex + 3] == -20) {// 分红
						writeByte[directionIndex] = Config.incomeDirectionPlus;
						flag = true;
						commaIndex++;
						bufIndex = 0;
						continue;
					}
					if (buf[tempIndex] == -77 && buf[tempIndex + 1] == -55
							&& buf[tempIndex + 2] == -63
							&& buf[tempIndex + 3] == -94) {// 成立
						writeByte[directionIndex] = Config.incomeDirectionSub;
						flag = true;
						commaIndex++;
						bufIndex = 0;
						continue;
					}
					if (buf[tempIndex] == -69 && buf[tempIndex + 1] == -89
							&& buf[tempIndex + 2] == -77
							&& buf[tempIndex + 3] == -100) {// 户入
						writeByte[directionIndex] = Config.incomeDirectionSub;
						flag = true;
						commaIndex++;
						bufIndex = 0;
						continue;
					}
					if (buf[tempIndex] == -69 && buf[tempIndex + 1] == -89
							&& buf[tempIndex + 2] == -56
							&& buf[tempIndex + 3] == -21) {// 户出
						writeByte[directionIndex] = Config.incomeDirectionSub;
						flag = true;
						commaIndex++;
						bufIndex = 0;
						continue;
					}
					flag = false;
					commaIndex++;
					bufIndex = 0;
					continue;

				}
				if (commaIndex == 10) {// 确认金额

					int incomeIndex = Config.incomeIndex + writeIndex;
					int len = bufIndex - 3;
					System.arraycopy(buf, 1, writeByte, incomeIndex, len);
					incomeEnd = incomeIndex + len;
					commaIndex++;
					bufIndex = 0;
					continue;
				}

				if (tempB == 10) {
					if (flag && buf[bufIndex - 3] == -90) {
						flag = true;
					} else {
						flag = false;
					}
					if (flag) {// 有效就加数据
						writeIndex = incomeEnd;
						while(writeIndex%37!=0){
							writeByte[writeIndex++] = 0;
						}
					}
					commaIndex = 0;
				} else {
					commaIndex++;
				}
				bufIndex = 0;
			}
		}
	}

}
