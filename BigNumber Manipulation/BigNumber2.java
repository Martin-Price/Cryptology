import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Iterator;

/**
 * BigNumber holds a collection of digits, representing a large number
 *
 * @author Matt Halloran
 * @author Martin Price
 * @author David Smits
 * Martin P and Matt H worked together to design the overall BigNumber Layout
 * @Martin Price: Adding and subtracting,
 * @Matt Halloran: Equals, CompareTo, Normalizse, Sign
 * @David Smits: Division and Multiplication
 *
 * October 23, 2018
 * Cryptology Professor Bergmann
 * Project 1
 */

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
	 * @author Matt Halloran
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
	 * @param other The other BigNumber this is compared to
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
	 * @author Matt Halloranß
	 */
	public int sign()
	{
		normalize();
		if(number.size() == 0) return 0;
		return number.getLast() < 5 ? 1 : -1;
	}


/*---------------------David Smits--------------------------------------------*/
	public Divide div(BigNumber b)
	{ return new Divide(this, b); }

	class Divide
	{
		private BigNumber quotient;
		private BigNumber remainder;

		public Divide(BigNumber dividend, BigNumber divisor)
		{ div(dividend, divisor); }

		/**
		 * Divivision
		 * Uses shifts to speed up repeated subtracting
		 * @author Martin Price
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
				if(tempDivisor.getNumber().getLast() > dividend.getNumber().getLast())
					gap --;
				for(int i = 0; i < gap; i++)
					tempDivisor.getNumber().addFirst(0);
				//Subtract until zero
				local = dividend;
				count = 0;
				boolean run = true;
				while(run)
				{
					temp = local.sub(divisor);
					if(temp.isPositive() )
					{
						count ++;
						local = local.sub(divisor);
					}
					else
					{
						//adds the count to quotient
						quotient.addFirst(count);
						//adds the remainder
						local.normalize();
						remainder = remainder.add(local);

						//removes the most significant diigit
						dividend = local;
						dividend.getNumber().removeLast();
						run = false;
					}
				}
				gap --;
			}
			this.quotient = new BigNumber(quotient);
			this.remainder = new BigNumber(remainder.getNumber());
		}

		public BigNumber getQuotient()
		{
			quotient.normalize();
			return quotient;
		}

		public BigNumber getMod()
		{
			remainder.normalize();
			return remainder;
		}
	}
}
