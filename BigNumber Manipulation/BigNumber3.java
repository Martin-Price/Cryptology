import java.util.LinkedList;
import java.util.Iterator;

public class BigNumber
{
	private LinkedList<Integer> number;
	private boolean negated = false;

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
		LinkedList<Integer> temp = new LinkedList();
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
		return new BigNumber3(temp);
	}

	/**
	 * adds together two big numbers
	 * @author Martin Price
	 * @param BigNumber: other bigNumber to add
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
	 * @return BigNumber from addition
	 */
	private BigNumber addOrganizer(BigNumber3 big, boolean adding)
	{
		if(isPositive() && adding)
			return add(big, false, adding);
		if(!(isPositive()) && adding)
			return add(big, true, adding);
		return add(big, true, adding);
	}

	public BigNumber add(BigNumber3 big)
	{ return addOrganizer(big, true); }

	public BigNumber sub(BigNumber3 big)
	{ return addOrganizer(big, false); }

	/**
	 * Resizes the linkedList to a new size, adds zeros to the front
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
	 {
		 if(number.getLast() < 5)
		 	return true;
		 return false;
	 }

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
}
