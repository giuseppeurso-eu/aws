package eu.giuseppeurso.aws.s3.jclient.ui;

/**
 * A class to manually mask the Command-Line input.
 * @author Giuseppe Urso - <a href="http://www.giuseppeurso.eu">www.giuseppeurso.eu</a>
 * @see <a href="http://www.cse.chalmers.se/edu/course/TDA602/Eraserlab/pwdmasking.html">Password Masking in Java</a>
 */
public class EraserSystemInput extends Thread  {
	private boolean running = true;

	/**
	 * Masking the standard input using a separate thread to erase the echoed characters as they are being entered, and replacing them with asterisks.
	 * 
	 */
	public void run() {
        while (running) {
        	System.out.print("\010*");
            try {
                Thread.currentThread().sleep(1);
            }
            catch(InterruptedException e) {
                break;
            }
        }
    }
	
    public synchronized void halt() {
        running = false;
    }

}
