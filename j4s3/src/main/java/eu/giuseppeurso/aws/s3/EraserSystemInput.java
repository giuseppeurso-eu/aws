package eu.giuseppeurso.aws.s3;

public class EraserSystemInput extends Thread  {

	public EraserSystemInput() {
		// TODO Auto-generated constructor stub
	}
	
	private boolean running = true;
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
