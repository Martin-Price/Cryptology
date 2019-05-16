
/**
 * Cryptology Project 2
 * Implementing the SDES encryption method
 *
 * @author Matt Halloran
 * @author David Liotta
 * @author Martin Price
 */

import java.util.Arrays;
import java.util.Scanner;

public class SDES
{
	private boolean[] key;
	private int[] ip = new int[] {1, 5, 2, 0, 3, 7, 4, 6};
	private int[] ipInverse = new int[] {3, 0, 2, 4, 6, 1, 7, 5};
	private int[] ep = new int[] {3, 0, 1, 2, 1, 2, 3, 0};
	private int[] k1 = new int[] {0, 6, 8, 3, 7, 2, 9, 7};
	private int[] k2 = new int[] {7, 2, 5, 4, 9, 1, 8, 0};
	private int[] p4 = new int[] {1, 3, 2, 0};

	/* Constructor */
	public SDES()
	{ key = new boolean[10]; }

	/**
	* expand or permutate
	* ASSUMES correct user input for @param epv
	* @param inp the array to be permuatated
	* @param evp the set of indexes to permuate @param inp
	* @return new bit array
	* @author Martin Price
	*/
	public boolean[] expPerm(boolean[] inp, int[] epv)
	{
	 boolean[] perm = new boolean[epv.length];
	 int i = 0;
	 for(int j: epv)
	 {
	   perm[i] = inp[j];
	   i ++;
	 }
	 return perm;
	}

	/**
	* Exclusive or
	* ASSUMES correct user input for param lengths
	* @param x boolean array
	* @param y boolean array
	* @return z boolean array of the @param x XOR @param y
	* @author Martin Price
	*/
	public boolean[] xor(boolean[] x, boolean[] y)
	{
	 boolean[] z = new boolean[x.length];
	 for(int i = 0; i < x.length; i++)
	   z[i] = (x[i] != y[i]);
	 return z;
	}

	/**
	* takes in a key from the user
	* ASSUMES only inputs correct formatting
	* @author Martin Price
	*/
	public void getKey10(Scanner scanner)
	{
	 System.out.print("10 bit key >");
	 String input = scanner.next();
	 for(int i = 0; i < 10; i ++)
	 {
	   String j = input.substring(i, i+1);
	   if(j.equals("1"))
	     key[i] = true;
	   else
	     key[i] = false;
	 }
	}

	/**
	 * converts an array of bytes to strings
	 * uses the ASCII values of characters
	 * @param inp byte array holding ASCII values
	 * @return String of characters
	 * @author Martin Price
	 */
	public String byteArrayToString(byte[] inp)
	{
		String code = "";
		for(byte b : inp)
		  code += (char)b;
		return code;
	}

	/**
	 * Converts the @param inp array into a byte (number)
	 * ASSUMES index 0 = Most Significant Digit
	 * @param inp boolean array
	 * @return byte representation of the @param
	 * @author Martin Price
	 */
	public byte bitArrayToByte(boolean[] inp)
	{
		int e = inp.length - 1;
		byte sum = 0;
		for(boolean b : inp)
		{
			if(b)
				sum += (byte)Math.pow(2, e);
			e --;
		}
		return sum;
	}

	/**
	 * generates a boolean array based of @param break;
	 * @param b byte number
	 * @param size new array size
	 * @return a new boolean array
	 * @author Martin Price
	 */
	public boolean[] byteToBitArray(byte b, int size)
	{
		boolean[] z = new boolean[size];
		int e = size - 1;
		int index = 0;
		byte currPow;
		while(b > 0)
		{
			currPow = (byte)Math.pow(2, e);
			z[index] = (b >= currPow);
			if(z[index])
				b -= currPow;
			index ++;
			e --;
		}
		return z;
	}

	/**
	* @return the left half of the @param inp
	* @author Martin Price
	*/
	public boolean[] lh(boolean[] inp)
	{
	 boolean[] left = new boolean[inp.length / 2];
	 for(int i = 0; i < inp.length / 2; i++)
	   left[i] = inp[i];
	 return left;
	}

	/**
	* @return the right half of the @param inp
	* @author Martin Price
	*/
	public boolean[] rh(boolean[] inp)
	{
		boolean[] right = new boolean[inp.length / 2];
	 	int index = 0;
	 	for(int i = inp.length / 2; i < inp.length; i++)
	 	{
	  	right[index] = inp[i];
			index ++;
	 	}
	 	return right;
	}

	/**
	 * combines two boolean arrays
	 * @param x left half of new array
	 * @param y right half of new array
	 * @return combined array
	 * @author Martin Price
	 */
	public boolean[] concat(boolean[] x, boolean[] y)
	{
		boolean[] z = new boolean[x.length + y.length];
		int index = 0;

		for(boolean b : x)
		{
			z[index] = b;
			index ++;
		}
		for(boolean b : y)
		{
			z[index] = b;
			index ++;
		}
		return z;
	}

	/** Used by @Martin Price for testing **/
	public boolean[] getKey()
	{ return key; }

	public String booleanArraytoString(boolean[] x)
	{
	 String s = "";
	 for (boolean b : x)
	 {
		 if(b)
			 s += "1";
		 else
			 s += "0";
	 }
	 return s;
	}

	/**
	 * Encrypt the given string using SDES.
	 * Each character produces a byte of cipher
	 * @param msg The message to be encrypted
	 * @return The encrypted byte array
	 * @author Matt Halloran
	 */
	public byte[] encrypt(String msg)
	{
		byte[] messageBytes = msg.getBytes();
		byte[] encryptedBytes = new byte[messageBytes.length];
		for(int i = 0; i < messageBytes.length; i++)
			encryptedBytes[i] = encryptByte(messageBytes[i]);
		return encryptedBytes;
	}

	/**
	 * Decrypt the given byte array.
	 * @param cipher The cipher text
	 * @return The decrypted byte array
	 * @author Matt Halloran
	 */
	public byte[] decrypt(byte[] cipher)
	{
		byte[] decryptedBytes = new byte[cipher.length];
		for(int i = 0; i < cipher.length; i++)
			decryptedBytes[i] = decryptByte(cipher[i]);
		return decryptedBytes;
	}
	/**
	 * Round function
	 * @param the original message
	 * @param the key used
	 * @author David Liotta
	 */
	public boolean[] f(boolean[] x, boolean[] k) {
		return concat((xor(lh(x), feistel(k, rh(x)))),rh(x));
	}

	/**
	 * The feistel function
	 * @param x
	 * @param k
	 * @return the permutation of the feistel function
	 * @author David Liotta
	 */
	public boolean[] feistel(boolean[] x, boolean[] k) {
		boolean[] LH = s0(lh(xor(k, expPerm(x, ep))));
		boolean[] RH = s1(rh(xor(k, expPerm(x, ep))));
		return expPerm(concat(LH, RH), p4);
	}

	// These will be used in the s blocks for comparison

	private boolean[] zero = new boolean[] {false, false, false, false};
	private boolean[] one = new boolean[] {false, false, false, true};
	private boolean[] two = new boolean[] {false, false, true, false};
	private boolean[] three = new boolean[] {false, false, true, true};
	private boolean[] four = new boolean[] {false, true, false, false};
	private boolean[] five = new boolean[] {false, true, false, true};
	private boolean[] six = new boolean[] {false, true, true, false};
	private boolean[] seven = new boolean[] {false, true, true, true};
	private boolean[] eight = new boolean[] {true, false, false, false};
	private boolean[] nine = new boolean[] {true, false, false, true};
	private boolean[] ten = new boolean[] {true, false, true, false};
	private boolean[] eleven = new boolean[] {true, false, true, true};
	private boolean[] twelve = new boolean[] {true, true, false, false};
	private boolean[] thirteen = new boolean[] {true, true, false, true};
	private boolean[] fourteen = new boolean[] {true, true, true, false};
	private boolean[] fifteen = new boolean[] {true, true, true, true};

	/**
	 * The s0 block function
	 * @param a four bit boolean array
	 * @return a two bit boolean array corresponding to the correct s0 value
	 * @author David Liotta
	 */
	public boolean[] s0(boolean[] x) {
		byte y = bitArrayToByte(x);
		boolean[] result = null;
		if(y == 0000)
			result = new boolean[]{false, true};
		else if(y == 0001)
			result = new boolean[]{true, true};
		else if(y == 0010)
			result = new boolean[]{false, false};
		else if(y == 0011)
			result = new boolean[]{true, false};
		else if(y == 0100)
			result = new boolean[]{true, true};
		else if(y == 0101)
			result = new boolean[]{false, true};
		else if(y == 0110)
			result = new boolean[]{true, false};
		else if(y == 0111)
			result = new boolean[]{false, false};
		else if(y == 1000)
			result = new boolean[]{false, false};
		else if(y == 1001)
			result = new boolean[]{true, true};
		else if(y == 1010)
			result = new boolean[]{true, false};
		else if(y == 1011)
			result = new boolean[]{false, true};
		else if(y == 1100)
			result = new boolean[]{false, true};
		else if(y == 1101)
			result = new boolean[]{true, true};
		else if(y == 1110)
			result = new boolean[]{true, true};
		else if(y == 1111)
			result = new boolean[]{true, false};
		return result;
	}

	/**
	 * The s1 block function
	 * @param a four bit boolean array
	 * @return a two bit boolean array corresponding to the correct s1 value
	 * @author David Liotta
	 */
	public boolean[] s1(boolean[] x) {
		boolean[] result = new boolean[] {false, false};
		if(x.equals(zero))
			result = new boolean[]{false, false};
		else if(x.equals(one))
			result = new boolean[]{true, false};
		else if(x.equals(two))
			result = new boolean[]{false, true};
		else if(x.equals(three))
			result = new boolean[]{false, false};
		else if(x.equals(four))
			result = new boolean[]{true, false};
		else if(x.equals(five))
			result = new boolean[]{false, true};
		else if(x.equals(six))
			result = new boolean[]{true, true};
		else if(x.equals(seven))
			result = new boolean[]{true, true};
		else if(x.equals(eight))
			result = new boolean[]{true, true};
		else if(x.equals(nine))
			result = new boolean[]{true, false};
		else if(x.equals(ten))
			result = new boolean[]{false, false};
		else if(x.equals(eleven))
			result = new boolean[]{false, true};
		else if(x.equals(twelve))
			result = new boolean[]{false, true};
		else if(x.equals(thirteen))
			result = new boolean[]{false, false};
		else if(x.equals(fourteen))
			result = new boolean[]{false, false};
		else if(x.equals(fifteen))
			result = new boolean[]{true, true};
		return result;
	}


	/**
	 * Encrypt a single byte using SDES
	 * @param b The byte being encrypted
	 * @return The encrypted byte
	 * @author Matt Halloran
	 */
	public byte encryptByte(byte b)
	{
		boolean[] t = byteToBitArray(b, 8);
		//step 1: t = IP(x)
		t = expPerm(t, ip);
		//step 2: t = fk1(t)
		t = f(t, expPerm(key, k1));
		//step 3: t  = R(t) || L(t)
		t = concat(rh(t), lh(t));
		//step 4: t = fk2(t)
		t = f(t, expPerm(key, k2));
		//step 5: y = IP-1(t)
		return bitArrayToByte(expPerm(t, ipInverse));
	}

	/**
	 * Decrypt a single byte using SDES
	 * @param b The byte being decrypted
	 * @return The decrypted byte
	 * @author Matt Halloran
	 */
	public byte decryptByte(byte b)
	{
		boolean[] t = byteToBitArray(b, 8);
		//step 1: t = IP(x)
		t = expPerm(t, ip);
		//step 2: t = fk1(t)
		t = f(t, expPerm(key, k2));
		//step 3: t  = R(t) || L(t)
		t = concat(rh(t), lh(t));
		//step 4: t = fk2(t)
		t = f(t, expPerm(key, k1));
		//step 5: y = IP-1(t)
		return bitArrayToByte(expPerm(t, ipInverse));
	}

	/**
	 * Send the byteArray to stdout
	 * @param byteArray The byte array being printed
	 * @author Matt Halloran
	 */
	public void show(byte[] byteArray)
	{
		System.out.println(Arrays.toString(byteArray));
	}

	/**
	 * Send the bitArray to stdout as 1's and 0's
	 * @param inp An array of booleans
	 * @author Matt Halloran
	 */
	public void show(boolean[] inp)
	{
		String s = new String();
		for(int i = 0; i < inp.length; i++)
			s += inp[i] ? "1" : "0";
		System.out.println(s);
	}
}
