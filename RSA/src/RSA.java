import java.util.Random;

/**
 *Implementation of the RSA public-key encryption algorithm.
 *
 *@author Kieran Walsh, Martin Price, Mantas Pileckis
 *@version December 2018
 */
public class RSA
{  
	public static void main (String args[]) throws Exception
	{
		Person Alice = new Person();
		Person Bob = new Person();

		String msg = new String ("Bob, you are unriable person"); 
		long []  cipher;
		cipher =  Alice.encryptTo(msg, Bob);			

		System.out.println ("Message is: " + msg);
		System.out.println ("Alice sends:");
		show (cipher);

		System.out.println ("Bob decodes and reads: " + Bob.decrypt (cipher));	
		System.out.println ();

		msg = new String ("Dont contact me again");
		cipher = Bob.encryptTo (msg, Alice);

		System.out.println ("Message is: " + msg);
		System.out.println ("Bob sends:");
		show (cipher);

		System.out.println ("Alice decodes and reads: " + Alice.decrypt (cipher) + "\n");
		
		
	}

	/**
	 * Method to calculate inverse using Euclidean Algorithm
	 * @author Mantas Pileckis
	 * @param x Number which inverse needs to be found of
	 * @param m	Modulus for that inverse
	 * @return The number x inverse with modulus m
	 */
	public static long inverse(long x, long m) 
	{
		//Table set up
		long[] rArr = {x, m};
		long[] uArr = {1, 0};
		long[] vArr = {0, 1};
		long remainder = 0;
		while(remainder != 1) {
			remainder = rArr[1] % rArr[0];					
			long quotient = rArr[1] / rArr[0];			
            long u = uArr[1] - overflow(uArr[0], quotient, m);	
    		if (u % m < 0 ) {
    			u = u + m;
    		} 
            long v = vArr[1] - overflow(vArr[0], quotient, m);
            if (v % m < 0 ) {
    			v = v + m;
    		} 
            //Update the table
            uArr[1] = uArr[0];
            uArr[0] = u;
            vArr[1] = vArr[0];
            vArr[0] = v;
            rArr[1] = rArr[0];
            rArr[0] = remainder;    
		}
		
		//Return inverse from the table
		return uArr[0];
	}
	
	/**
	 * Raise the base to the exponent and take the mod of it
	 * @author Mantas Pileckis
	 * @param base Base
	 * @param exponent The exponent that is used to raise the base 
	 * @param modulus The modulus of the baseToExponent
	 * @return The base raised to exponent and modded
	 */
	public static long modPower(long base, long exponent, long modulus){
		int limit = 0;
        while (Math.pow(2, limit) <= exponent){
            limit++;
        }
        limit--;
        long[] simplifiedExponenet = simplifiedExponenet(base, limit, modulus);  
        long result = 1;
        long currentPower = exponent;
        long iValue = (long)Math.pow(2, limit);    
        for (int i = limit; i >= 0; i--){
            if (currentPower >= iValue){
                currentPower -= iValue;       
                result = overflow(result, simplifiedExponenet[i], modulus);
            }
            iValue /= 2;
        }
        return result;
	}
	
	/**
	 * Function to create an array of Exponents for faster modPower calculation
	 * @param base Base
	 * @param max The limit to which we are calculating the exponent powers
	 * @param modulus The modulus we are using
	 * @return The array of values associated with the bases and exponents.
	 */
	private static long[] simplifiedExponenet(long base, int max, long modulus)
	{
		long[] returnList = new long[max + 1];
		long currentValue = base;
		returnList[0] = base;
		for(int i = 1; i <= max; i ++){
			currentValue = overflow(currentValue, currentValue, modulus); 
			returnList[i] = currentValue;
		}
		return returnList;
	}
	
	/**
	 * Method to take care of mdMultiplication and overflow
	 * @author Mantas Pileckis
	 * @param arrVariable first number
	 * @param arrVariable2 second number
	 * @param modulus The modulus that we are working with
	 * @return The result % modulus
	 */
	public static long overflow(long arrVariable, long arrVariable2, long modulus)
	{
		long newValue = 0;
		while(arrVariable2 > 0){
			// If rrrVariable2 is odd, add a to return
			if (arrVariable2 % 2 == 1) {
				newValue = (newValue + arrVariable) % modulus;
			}
			arrVariable = (arrVariable * 2) % modulus;			
			arrVariable2 /= 2;
		}
		return newValue;
	}
	

	/**
	 * Method that generates a random prime number within the limits of min and max
	 * @param minValue the min limit
	 * @param maxValue the max limit
	 * @param rand Random value
	 * @return rand random prime between the min and max limit
	 * @author Kieran Walsh
	 */
	public static long randPrime(long minValue, long maxValue, Random rand)
	{
		long randRelPrime = 0;
		do {
			randRelPrime = (long) (minValue + (rand.nextDouble()*(maxValue-minValue)));
		}
		while(!isPrime(randRelPrime)); 
		return randRelPrime;
	}

	/**
	 * Method that generates a random long relatively prime to num
	 * @param num value which we are checking
	 * @param rand Random value
	 * @return Random value which is relatively prime and less than num
	 * @author Kieran Walsh
	 */
	public static long relPrime(long num, Random rand)
	{
		long randRelPrime = 0;
		do {
			randRelPrime = (long) (rand.nextDouble()*num);
		}
		while(!isRelativelyPrime(num, randRelPrime));
		return randRelPrime;
	}

	/**
	 * Method that figures out  whether a given long is prime or not
	 * @author Kieran Walsh
	 * @param a long value
	 * @return whether or not a long is prime
	 */
	private static boolean isPrime(long num)
	{
		boolean prime = true;
		double sqrt = Math.sqrt(num);
		for(int i = 2; prime && i <= sqrt; i++) {
			if(num%i == 0) {
				prime = false;
			}
		}
		return prime;
	}

	/**
	 * Determine whether or not two longs are relative prime.
	 * This means their gcd is 1.
	 * This method uses the Euclidean Algorithm to determine the gcd.
	 * 
	 * @param  long x
	 * @param  long n
	 * @return whether or not the two longs are relatively prime
	 * @author Kieran Walsh
	 */
	private static boolean isRelativelyPrime(long x, long n)
	{
		long a = x, b = n, r = 2; 
		if(x < n) 
		{ 
			a = n;
			b = x;
		}
		
		while(r != 0) 
		{ 
			r = a%b; 	
			a = b; 		
			b = r; 		
		}
		if(a == 1){
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * Show method to display the array of longs
	 * 
	 * @param data The array of longs that we want to show
	 * @author Kieran Walsh
	 */
	public static void show(long[] data)
	{
		int size = data.length;
		String allData = "";
		for(int i = 0; i < size; i++) 
		{
			allData += data[i] + ", ";
		}
		
		System.out.println(allData.substring(0, allData.length()-2));
	}	

	/**
	 * Convert a long to n chars, where n 
	 * is the block size of the message.
	 * 
	 * @param  x The long that we are converting
	 * @return String chars
	 * @author Kieran Walsh
	 */
	public static String longToChars(long x) 
	{
		String padding = Long.toBinaryString(x);
		//Take care of the padding
		while(padding.length()%8 != 0) 
		{
			padding = '0' + padding;
		}
		
		String bits = padding;
		int numChars = bits.length()/8; 
		String chars = "";
		for(int i = 0; i < numChars; i++) 
		{
			int currByte = 0;
			for(int j = 0; j < 8; j++) 
			{
				currByte +=  (1 << (j))*Integer.parseInt(bits.substring((i*8) + 8-(j+1),(i*8) + 8-j));
			}
			chars += Character.toString((char) currByte); 
		}
		return chars;
	}

	
	/**
	 * Converts a specified number of numeric chars, n, to a long
	 * 
	 * @param 	msg The message that is being converted
	 * @param 	p Position 
	 * @param 	n limit of the message
	 * @return 	long converted
	 * @author Kieran Walsh
	 */
	public static long toLong(String msg, int p, int n) 
	{
		String bits = "";
		for(int i = 0; i < n; i++) 
		{
			String padding = Integer.toBinaryString(msg.charAt(p+i));
			//Take care of the padding
			while(padding.length()%8 != 0) 
			{
				padding = '0' + padding;
			}
			bits += padding;
			
		}
		
		long converted = 0;
		int size = bits.length();
		for(int i = 0; i < size; i++) 
		{
			converted += (1l << (i))*Long.parseLong(bits.substring(size-i-1, size-i));
		}	
		return converted;
	}


}
