import java.io.*;
import java.util.Scanner;
import java.util.Vector;

class Point {
	public double x;
	public double y;
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
}

public class Linear {

	private static void linear(String filename) throws FileNotFoundException {
		Vector<Point> v = new Vector<Point>();
		Scanner sc = new Scanner(new File(filename));
		System.out.println(filename);
		System.out.println("---------------------");
		Point total = new Point(0, 0);
		while (sc.hasNextDouble()) {
			sc.nextDouble();
			double x = sc.nextDouble();
			double y = sc.nextDouble();
			v.add(new Point(x, y));
			total.x += x;
			total.y += y;
		}
		Point avg = new Point(total.x/v.size(), total.y/v.size());
		double sub = 0.0;
		double sup = 0.0;
		for (int i=0;i<v.size();++i) {
			Point p = v.get(i);
			sub += (p.x - avg.x) * (p.y - avg.y);
			sup += (p.x - avg.x) * (p.x - avg.x);
		}
		double a = sub / sup;
		double b = avg.y - a * avg.x;
		System.out.format("y = %fx + %f\r\n\r\n", a, b);
	}

	public static void main(String[] args) {
		try {
			PrintStream ps = new PrintStream(new FileOutputStream("out/04-linear.txt"));
        	System.setOut(ps);
        	linear("data/LR_ex0.txt");
        	linear("data/LR_ex1.txt");
		} catch (IOException e) {
			System.out.println("Failed to open file!");
			System.exit(-1);
		}
	}
}