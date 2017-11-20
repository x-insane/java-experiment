import java.io.*;

public class Division {

	private static double f(double x) {
		return x*x*x - x*10 + 23;
	}

	public static void main(String[] args) {
		try {
			PrintStream ps = new PrintStream(new FileOutputStream("out/01-division.txt"));
        	System.setOut(ps);
		} catch (IOException e) {
			System.out.println("Failed to open file!");
			System.exit(-1);
		}
		double delta = 0.001;
		double x1 = -10.0;
		double x2 = 5.0;
		while (true) {
			double x = (x1 + x2) / 2.0;
			double y1 = f(x1);
			double y2 = f(x2);
			double y = f(x);
			if (x2 - x1 < delta || y == 0) {
				System.out.println(x);
				System.exit(0);
			}
			if (y2 * y < 0)
				x1 = x;
			else
				x2 = x;
		}
	}
}