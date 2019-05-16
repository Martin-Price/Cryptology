/**
 * Person Class
 * Crypto Project 3
 * @author martinprice
 * @author Mantas P
 * @author Kieren Walsh
 */

import java.util.Random;
public class Person
{
	/**
	 * Instance variables
	 */
	//encrypting
	private long pubKey;
	private long privKey;
	private long mod;
	
	//block sizes and mods
	private final int block = 7; 
	private long max =  (long)Math.sqrt(Long.MAX_VALUE / 2);
	private long min = (long)(Math.sqrt(max));
	

	/**
	 * Constructor
	 */
	public Person()
	{
        long p = RSA.randPrime(min, max, new Random());
        long q = RSA.randPrime(min, max, new Random());
	    mod = p * q; 
	    pubKey = RSA.relPrime((p - 1) * (q - 1), new Random());
	    privKey = RSA.inverse(pubKey, (p-1) * (q-1));
    }

	/**
	 * Encrypted a message into cipher text
	 * @param msg plain text message to encrypt
	 * @param p person to encrpyt to
	 * @return encrypted message in form of long[]
	 */
	public long[] encryptTo(String msg, Person p)
	{
		long theirKey = p.getE();     
		long theirMod = p.getM();
		int blocks = (msg.length() / block) + 1; 
		long[] encrypted = new long[blocks];		
		            
		//Pad With Spaces for the last block
        while (msg.length() % block > 0)
            msg += " ";
        
        int index = 0;
        for(int i = 0; i < msg.length(); i+= block)
        {
            if(i + block > msg.length())
            	encrypted[index++] =  RSA.modPower(RSA.toLong(msg, i, block), theirKey, theirMod);
            else
            	encrypted[index++] =  RSA.modPower(RSA.toLong(msg, i, block), theirKey, theirMod);
		}
		return encrypted;
	}
	
	/**
	 * Decrypts the incoming message and returns the plain text
	 * @param message cipher message
	 * @return msg string plain text message
	 */
	public String decrypt(long[] message)
	{
		String msg = "";
		for(int i = 0; i < message.length; i++)
			msg += RSA.longToChars(RSA.modPower(message[i], privKey, mod));
		return msg;
	}

	/**
	 * @return publicKey
	 */
	public long getE()
	{ return pubKey; }

	/**
	 * @return publicModulus
	 */
	public long getM()
	{ return mod; }
}
