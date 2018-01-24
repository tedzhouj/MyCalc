

import java.io.File;

public class ShareImportor extends Importor {

	public ShareImportor(String path) {
		super(path);
	}

	@Override
	protected File getOutPutFile() {
		File f=new File(Config.ShareFilePath);
		return f;
	}

	@Override
	public void parser(byte[] bs, int endIndex) {

		int commaIndex = 0;
		byte[] buf = new byte[100];
		int bufIndex = 0;
		for (int i = 0; i <=endIndex; i++) {
			byte tempB = bs[i];
			buf[bufIndex++] = tempB;
			if (tempB == 44 || tempB == 10) {
				if (commaIndex == 0) {

					int clientCodeIndex = writeIndex + Config.shareClientCodeIndex;
					int length = bufIndex - 3;
					System.arraycopy(buf, 1, writeByte, clientCodeIndex, length);
					int si=clientCodeIndex+length;
					while(si<writeIndex+Config.shareProductCodeIndex){//不满12位被0
						writeByte[si++]=0;
					}
					commaIndex++;
					bufIndex = 0;
					continue;

				}
				if (commaIndex == 1) {

					int productCodeIndex = Config.shareProductCodeIndex + writeIndex;
					int len = bufIndex - 3;
					System.arraycopy(buf, 1, writeByte, productCodeIndex, len);
					int si=productCodeIndex+len;
					while(si<writeIndex+18){
						writeByte[si++]=0;
					}
					commaIndex++;
					bufIndex = 0;
					continue;

				}

				if (commaIndex == 6) {
					int confirmDateIndex = Config.shareConfirmDateIndex + writeIndex;
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
					int effectDateIndex = Config.shareEffectDateIndex + writeIndex;
					writeByte[effectDateIndex++] = buf[1];
					writeByte[effectDateIndex++] = buf[2];
					writeByte[effectDateIndex++] = buf[3];
					writeByte[effectDateIndex++] = buf[4];
					writeByte[effectDateIndex++] = buf[6];
					writeByte[effectDateIndex++] = buf[7];
					writeByte[effectDateIndex++] = buf[9];
					writeByte[effectDateIndex] = buf[10];
					commaIndex++;
					bufIndex = 0;
					continue;
				}

				if (tempB == 10) {
					count++;
					int shareIndex = Config.shareIndex + writeIndex;
					int len = bufIndex - 3;
					System.arraycopy(buf, 1, writeByte, shareIndex, len);
					commaIndex = 0;
					writeIndex = shareIndex + len;
					while(writeIndex%50!=0){
						writeByte[writeIndex++]=0;
					}
				} else {
					commaIndex++;
				}
				bufIndex = 0;
			}
		}


	}

}
