/**
 * Test the big number class
 * Cryptology project 1
 *
 * @author Matt Halloran
 * @author Martin Price
 * @author David Smits
 * @version Ovtober 23, 2018
 */
 import java.util.Scanner;
public class Driver
{
	public static void main(String args[])
	{
		Scanner scanner = new Scanner (System.in);
		BigNumber x,y,z;

		System.out.println("Enter two BigNumbers, on separate lines,"
												+ " or hit Enter to terminate");
		String line = scanner.nextLine();

		while (true)//line.length() > 0)
		{
				x = new BigNumber(line);
				System.out.println ("Enter a second BigNumber");
				line = scanner.nextLine();
				y = new BigNumber(line);
				z = new BigNumber("2168211218041261");
				System.out.println ("Sum: " + x.add(y));
				System.out.println ("Sum: " + y.add(x));
				System.out.println ("First - Second: " + x.sub(y));
				System.out.println ("Second - First: " + y.sub(x));
				System.out.println ("Product: " + x.multiply(y));
				System.out.println ("Product: " + y.multiply(x));
				System.out.println ("First / Second: " + x.div(y).getQuotient());
				System.out.println ("Second / First: " + y.div(x).getQuotient());
				System.out.println ("First % Second: " + x.div(y).getMod());
				System.out.println ("Second % First: " + y.div(x).getMod());

				System.out.println("Factors of Z: " + y.factor());
				/ine = scanner.nextLine();
				return;
	 	}
	}
}
