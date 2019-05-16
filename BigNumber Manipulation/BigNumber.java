/**
 * BigNumber holds a collection of digits, representing a large number
 *
 * @author Matt Halloran
 * @author Martin Price
 * @author David Smits
 * Martin P and Matt H worked together to design the overall BigNumber Layout
 * @Martin Price: Adding, subtracting, division, factoring
 * @Matt Halloran: Equals, CompareTo, Normalizse, Sign
 * @David Smits: Multiplication
 *
 * October 23, 2018
 * Cryptology Professor Bergmann
 * Project 1
 */

 import java.util.LinkedList;
 import java.util.ListIterator;
 import java.util.Iterator;

public class BigNumber implements Comparable<BigNumber>
{
	private LinkedList<Integer> number;

	/** @param bigNumber: String literal of the bigNumber */
	public BigNumber(String bigNumber)
	{
		number = new LinkedList<>();
		for(int i = 0; i < bigNumber.length(); i++)
			number.addFirst(Integer.parseInt(bigNumber.substring(i, i+1)));
	}

	/** @param bigNumber: precreated LinkedList of a BigNumber */
	public BigNumber(LinkedList<Integer> bigNumber)
	{ number = new LinkedList<>(bigNumber); }

	/**
	 * negates the BigNumber based on 10s complement
	 * @author Martin Price
	 * @return: a local negated BigNumber
	 */
	public BigNumber negate()
	{
		LinkedList<Integer> temp = new LinkedList<Integer>();
		Iterator<Integer> itty = number.listIterator(0);

		//copies the zeros first
		int local;
		boolean stillZero = true;
		boolean firstDigit = true;
		while(itty.hasNext())
		{
			local = itty.next();
			if(local == 0 && stillZero)
			{
				temp.addLast(0);
			}
			else if(stillZero)
				stillZero = false;

			if(!stillZero && firstDigit)
			{
				temp.addLast(10-local);
				firstDigit = false;
			}
			else if(!stillZero && !firstDigit)
				temp.addLast(9-local);
		}
		return new BigNumber(temp);
	}

	/**
	 * adds together two big numbers
	 * @author Martin Price
	 * @param BigNumber: other bigNumber to add
	 * @param dropCarry: certain operations (positive and negative) will drop
	 *									 their final carry digit
	 * @param adding: if not adding, negate the number
	 * @return BigNumber representing two added numbers
	 */
	private BigNumber add(BigNumber big, boolean dropCarry, boolean adding)
	{
		//Make sure both numbers are same length
		if(big.getNumber().size() < number.size())
			big.resize(number.size());
		if(number.size() < big.getNumber().size())
			resize(big.getNumber().size()-1);

		//If not adding, subtracting, negate
		if(!adding)
			big = big.negate();

		LinkedList<Integer> adder = big.getNumber();
		LinkedList<Integer> base = number;
		LinkedList<Integer> result = new LinkedList<>();
		Iterator<Integer> itAdd = adder.listIterator(0);
		Iterator<Integer> itBase = base.listIterator(0);
		//Assumes by now both are same length
		int carry = 0;
		while(itAdd.hasNext())
		{
			int a = itAdd.next();
			int b = itBase.next();

			int r = a + b + carry;
			if(r > 9)
			{
				r -= 10;
				carry = 1;
			}
			else
				carry = 0;
			result.addLast(r);
		}
		if(carry == 1 && !dropCarry)
			result.addLast(1);

		return new BigNumber(result);
	}

	/**
	 * Organizes and passes information to the add method
	 * information will determine whether to negate, drop carry
	 * @author Martin Price
	 * @param big: BigNumber to be added
	 * @param adding: whether this is an adding operation or not
	 * @return BigNumber from addition
	 */
	private BigNumber addOrganizer(BigNumber big, boolean adding)
	{
		if(isPositive() && adding)
			return add(big, false, adding);
		if(!(isPositive()) && adding)
			return add(big, true, adding);
		return add(big, true, adding);
	}

	/** @return BigNumber from adding process */
	public BigNumber add(BigNumber big)
	{
		BigNumber a = addOrganizer(big, true);
		a.normalize();
		return a;
	}

	/** @return BigNumber from subtracting process */
	public BigNumber sub(BigNumber big)
	{
		BigNumber s = addOrganizer(big, false);
		s.normalize();
		return s;
	}

	/**
	 * Resizes the linkedList to a new size, adds zeros to the front
	 * used for adding and subtracting
	 * @author Martin Price
	 * @param newSize: new size of the linkedList
	 */
	 private void resize(int newSize)
	 {
		 for(int i = 1; i <= newSize; i++)
				number.addLast(0);
	 }

	 /** @return : 10s complment > 4 = negative, False */
	 private boolean isPositive()
	 { return number.getLast() < 5; }

	/** @return : number */
	public LinkedList<Integer> getNumber()
	{ return number; }

	@Override
	/** @return : String of the number */
	public String toString()
	{
		String temp = "";
		Iterator<Integer> itty = number.descendingIterator();
		while(itty.hasNext())
			temp += itty.next();
		return temp;
	}

	/*------------------Matt Halloran-------------------------------------------*/
	/**
	 * Comapares to bigNumbers for size and values
	 * @author Matt Halloran
	 * @param other : object to test
	 * @return true : if other represents the same value as this
	 */
	public boolean equals(Object other)
	{
		if(!(other instanceof BigNumber)) return false;
		BigNumber otherNumber = (BigNumber)other;
		normalize();
		otherNumber.normalize();
		if(otherNumber.number.size() != number.size()) return false;
		Iterator<Integer> numberIt = number.descendingIterator();
		Iterator<Integer> otherNumberIt = number.descendingIterator();
		while(numberIt.hasNext() && otherNumberIt.hasNext())
		{
			if(numberIt.next() != otherNumberIt.next()) return false;
		}
		return true;
	}

	/**
	 * @param other: The other BigNumber this is compared to
	 * @return Positive if other
	 */
	public int compareTo(BigNumber other)
	{
		return clone().sub(other).sign();
	}

	/**
	 * @return A copy of the current object
	 * @author Matt Halloran
	 */
	public BigNumber clone()
	{
		String s = new String();
		Iterator<Integer> it = number.descendingIterator();
		while(it.hasNext())
			s += it.next();
	 	return new BigNumber(s);
	}

	/**
	 * Removes unnecessary leading digits from the linked list
	 * @author Matt Halloran
	 */
	public void normalize()
	{
		LinkedList<Integer> normalizedList = new LinkedList<>();
		Iterator<Integer> it = number.descendingIterator();
		int leadingDigit = number.getLast() < 5 ? 0 : 9;
		int currentNumber = leadingDigit;
		boolean done = false;
		while(it.hasNext())
		{
			currentNumber = it.next();
			if(!done && currentNumber != leadingDigit)
			{
				done = true;
				if(leadingDigit != 0 || currentNumber > 4)
					normalizedList.addFirst(leadingDigit);
			}

			if(done)
				normalizedList.addFirst(currentNumber);
		}
		if(normalizedList.size() == 0)
			normalizedList.addFirst(0);
		number = normalizedList;
	}

	/**
	 * uses last digit for quick Posiitve / negative reading
	 * @author Matt Halloran
	 * @return 1 : positive
	 */
	public int sign()
	{
		normalize();
		if(number.size() == 0) return 0;
		return number.getLast() < 5 ? 1 : -1;
	}

	/**
	 * Multiplication
	 * @author David Smits
	 */
	public BigNumber multiply(BigNumber b)
	{
		BigNumber factor1 = this;
		BigNumber factor2 = b;
		BigNumber product = new BigNumber("0");

		// Turn negative numbers positive
		if(factor1.number.get(0) != 0)
			factor1 = factor1.negate();
		if(factor2.number.get(0) != 0)
			factor2 = factor2.negate();

		int countUp = 0;
		int countDown = 0;

		for(int i = factor1.number.size() -1; i > 0; i--)
		{
			for(int j = factor2.number.size() -1; j > 0; j--)
			{
				if(factor1.number.get(i) != 0 && factor2.number.get(j) != 0)
				{
					int tNum = factor1.number.get(i) * factor2.number.get(j);
					String zeroes = "";
					for(int k = 0; k < countUp + countDown; k++)
					{
						zeroes = zeroes + "0";
						String temp = tNum + zeroes;
						BigNumber tempBN = new BigNumber(temp);
						product = product.add(tempBN);
					}
					countUp++;
				}
				countUp = 0;
				countDown++;
			}
		}
		if(this.number.get(0) == 0 && b.number.get(0) != 0)
			return product.negate();
		if(this.number.get(0) != 0 && b.number.get(0) == 0)
			return product.negate();
		return product;
	}

	/**
	 * Factorization
	 * Estimates the sqroot
	 * finds values upto the square root
	 * @author Martin Price
	 * @return A List of a List: each index in the list is a list of 2 factor values
	 */
	public LinkedList<LinkedList<BigNumber>> factor()
	{
		//Can stop factoring once the length is more than half the number
		//If no values are found by half way (sqaure root), none will be found
		Divide d;
		LinkedList<BigNumber> fact;
		LinkedList<LinkedList<BigNumber>> factors = new LinkedList<>();
		BigNumber temp = new BigNumber("2");
		LinkedList<Integer> half = new LinkedList<>();
		for(int i = 0; i <= number.size() / 2; i++)
			half.add(4);
		do
		{
			// If division results in a 0 remainder, factor found.
			d = new Divide(this, temp);
			if(d.getMod().equals(new BigNumber("0")))
			{
				fact = new LinkedList<>();
				fact.add(temp);
				fact.add(d.getQuotient());
				factors.add(fact);
			}
			temp = temp.add(new BigNumber("1"));
			temp.normalize();
		} while(!(temp.equals(half)));

		//If no factors from 2 - sqrt number, relatively prime
		if(factors.size() == 0)
		{
			fact = new LinkedList<>();
			fact.add(new BigNumber("1"));
			fact.add(temp);
			factors.add(fact);
		}
		return factors;
	}

	/** @return: new division object */
	public Divide div(BigNumber b)
	{ return new Divide(this, b); }

	/**
	 * Every division object holds the two part answer of BigNumber division
	 * it holds the quotient and the remainder respectfully
	 * @author Martin Price
	 */
	class Divide
	{
		//instance fields
		private BigNumber quotient;
		private BigNumber remainder;

		/**
		 * @param dividend : base number
		 * @param divisor : divide by
		 */
		public Divide(BigNumber dividend, BigNumber divisor)
		{ div(dividend, divisor); }

		/**
		 * Divivision
		 * Uses shifts to speed up repeated subtracting
		 * @author Martin Price
		 * @param dividend: BigNumber to divide from
		 * @param divisor: BigNumber to divide by
		 */
		 private void div(BigNumber dividend, BigNumber divisor)
 		{
 			if(divisor.equals(dividend))
 			{
 				this.quotient = new BigNumber("1");
 				this.remainder = new BigNumber("0");
 				return;
 			}
 			if(divisor.compareTo(dividend) > -1)
 			{
 				this.quotient = new BigNumber("0");
 				this.remainder = new BigNumber(dividend.getNumber());
 				return;
 			}

 			int gap = dividend.getNumber().size() - divisor.getNumber().size();
 			LinkedList<Integer> quotient = new LinkedList<>();
 			BigNumber remainder = new BigNumber("0");
 			BigNumber temp, tempDivisor, local;
 			int count;

 			//While there is still a gap
 			while(gap >= 0)
 			{
 				tempDivisor = divisor;
 				if(tempDivisor.getNumber().getLast() >= dividend.getNumber().getLast())
 					gap --;
 				for(int i = 0; i < gap; i++)
 					tempDivisor.getNumber().addFirst(0);

				//Subtract until zero
 				local = dividend;
 				count = 0;
 				boolean run = true;
 				while(run)
 				{
 					temp = local.sub(tempDivisor);
 					if(temp.isPositive() )
 					{
 						count ++;
 						local = local.sub(tempDivisor);
 					}
 					else
 						run = false;
 				}
				//adds the count to quotient
				quotient.addFirst(count);
				//adds the remainder
				remainder = remainder.add(local);

				//removes the most significant diigit
				dividend = local;
				dividend.getNumber().removeLast();
 				gap --;
 			}
			if(quotient.getLast() > 4)
				quotient.addLast(0);
 			this.quotient = new BigNumber(quotient);
 			this.remainder = new BigNumber(remainder.getNumber());
		}

		/** @return quotient: the normalized quotiesnt */
		public BigNumber getQuotient()
		{
			quotient.normalize();
			return quotient;
		}

		/** @return remainder: the normalized remainder */
		public BigNumber getMod()
		{
			remainder.normalize();
			return remainder;
		}
	}
}
