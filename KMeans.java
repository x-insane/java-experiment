import java.io.*;
import java.util.Random;
import java.util.Scanner;

class Point {
	public double x;
	public double y;
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
}

public class KMeans {

	private int n;
	private Point[] data;

	private static Point[] create_keys(int n) {
		Point[] keys = new Point[n];
		Random rand = new Random(System.currentTimeMillis());
		for (int i=0;i<keys.length;++i)
			keys[i] = new Point(rand.nextDouble(), rand.nextDouble());
		return keys;
	}

	private static double d(Point a, Point b) {
		return (a.x-b.x)*(a.x-b.x) + (a.y-b.y)*(a.y-b.y);
	}

	public KMeans(String filename) throws FileNotFoundException {
		data = new Point[100];
		n = 0;
		Scanner sc = new Scanner(new File(filename));
		while (sc.hasNextDouble()) {
			double x = sc.nextDouble();
			double y = sc.nextDouble();
			data[n++] = new Point(x, y);
		}
		System.out.println(filename);
		System.out.println("---------------------");
	}

	public KMeans calc(int loop, Point keys[]) {
		Point[] k = new Point[keys.length];
		int[] nn = new int[keys.length];
		for (int i=0;i<k.length;++i) {
			nn[i] = 0;
			k[i] = new Point(0, 0);
		}
		for (int i=0;i<n;++i) {
			double mind = -1;
			int minp = -1;
			for (int j=0;j<keys.length;++j) {
				double tmp = d(data[i], keys[j]);
				if (minp == -1 || mind > tmp) {
					mind = tmp;
					minp = j;
				}
			}
			k[minp].x += data[i].x;
			k[minp].y += data[i].y;
			nn[minp] ++;
		}
		boolean is_change = false;
		for (int i=0;i<keys.length;++i) {
			if (nn[i] == 0)
				continue;
			double x = k[i].x / nn[i];
			double y = k[i].y / nn[i];
			if (keys[i].x != x || keys[i].y != y)
				is_change = true;
			keys[i].x = x;
			keys[i].y = y;
		}
		if (is_change && loop > 1)
			calc(loop - 1, keys);
		return this;
	}

	public void print(Point keys[]) {
		for (int i=0;i<keys.length;++i)
			System.out.format("%f, %f\r\n", keys[i].x, keys[i].y);
		System.out.println("");
	}

	public static void main(String args[]) {
		Point[] key1 = create_keys(4);
		Point[] key2 = create_keys(3);
		try {
			PrintStream ps = new PrintStream(new FileOutputStream("out/03-kMeans.txt"));
			System.setOut(ps);
			new KMeans("data/KMeans_Set.txt").calc(10, key1).print(key1);
			new KMeans("data/KMeans_Set2.txt").calc(10, key2).print(key2);
		} catch (IOException e) {
			System.out.println("Failed to open file!");
			System.exit(-1);
		}
	}
}