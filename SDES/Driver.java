import java.util.*;

/**
 * Test the implementation of SDES
 *
 * @author (sdb)
 * @version (Oct 2018)
 */
public class Driver
{
  public static void main(String[] args)
  {
		SDES s = new SDES();
		Scanner scan = new Scanner(System.in);
		byte[] cipher = {-115, -17, -47, -113, -43, -47, 15, 84, -43, -113, -17, 84,
			-43, 79, 58, 15, 64, -113, -43, 65, -47, 127, 84, 64, -43, -61, 79, -43, 93,
			-61, -14, 15, -43, 127, -43, 127, 84, 127, 10, 84, 15, 64, 43};


		byte[] plain = s.decrypt(cipher);
		System.out.println(s.byteArrayToString(plain));
		//s.decryptByte((byte)56);
  }
}
