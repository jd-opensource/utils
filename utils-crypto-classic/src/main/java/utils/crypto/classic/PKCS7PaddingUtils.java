package utils.crypto.classic;

import org.bouncycastle.crypto.InvalidCipherTextException;

/**
 * PKCS7 填充：
 * <p>
 * 
 * 假设块大小为 C ，目标数组需要填充 n 个字节后长度才能对其到整块，那么填充的 n 个字节每个的值都是 n；<br>
 * 如果目标数据本身已经对齐了，则填充一个完整的块长度的数据，填充的 C 个字节每个的值都是 C；
 */
public class PKCS7PaddingUtils {
	
	/**
	 * 向目标数组填充；
	 * 
	 * @param target 要填充的数组；
	 * @param paddingOffset 填充的起始位置；
	 * @param paddingCode 填充值；同时，也是填充的字节长度；
	 * @return
	 */
	public static int addPadding(byte[] target, int paddingOffset, byte paddingCode) {
		int i = 0;
		while (i < paddingCode) {
			target[paddingOffset + i] = paddingCode;
			i++;
		}

		return paddingCode;
	}

	/**
	 * return the number of pad bytes present in the block.
	 */
	public int padCount(byte[] in) throws InvalidCipherTextException {
		int count = in[in.length - 1] & 0xff;
		byte countAsbyte = (byte) count;

		// constant time version
		boolean failed = (count > in.length | count == 0);

		for (int i = 0; i < in.length; i++) {
			failed |= (in.length - i <= count) & (in[i] != countAsbyte);
		}

		if (failed) {
			throw new InvalidCipherTextException("pad block corrupted");
		}

		return count;
	}
}