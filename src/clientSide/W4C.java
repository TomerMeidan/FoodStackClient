package clientSide;

import java.util.Random;

/**
 * W4C
 * 
 * This class creates W4C codes.
 * Has static method createW4C().
 * Code generated from static 'storage' variable.
 * @author Roman Milman
 */
public class W4C {
	private static String storage = "0123456789abcdefghijklmnopqrstuvwxyz";

	/**
	 * createW4C
	 * 
	 * This method creates w4c code.
	 * Code is 10 char's long.
	 * Each char in code is selected from 'storage'.
	 * @return String
	 * @author Roman Milman
	 */
	public static String createW4C() {
		Random random = new Random();
		String newW4C = "";
		
		for (int i = 0; i < 10; i++) {
			int index = random.nextInt(36);
			newW4C += storage.charAt(index);
		}
		return newW4C;
	}
}
