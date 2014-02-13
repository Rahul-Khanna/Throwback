

import java.io.IOException;
/**
 * The class to run to start the program.
 * @author rahulkhanna
 *
 */
public class Throwback {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Controller c= new Controller();
		try {
			c.run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
