import java.io.*;

public class Prime {

	public static void calc(boolean[] list, int n, int a, int b) {
		int i=b;
		while(i<a) {
			while(!list[++i]);
			int j = a / i;
			j = j<2 ? 2 : j;
			for (;i*j<=n;++j) {
				list[i*j] = false;
			}
		}
		if (a*a < n)
			calc(list, n, a*a, a);
	}

	public static void main(String args[]) {
		try {
			PrintStream ps = new PrintStream(new FileOutputStream("out/02-prime.txt"));
			System.setOut(ps);
		} catch (IOException e) {
			System.out.println("Failed to open file!");
			System.exit(-1);
		}
		int n = 10000000;
		boolean[] list = new boolean[n+1];
		int[] tmp = {0,0,1,1,0,1,0,1,0,0,0,1,0,1,0,0,0,1,0,1,0,0,0,1};
		for (int i=1;i<=n;++i)
			list[i] = (i<24 && tmp[i]==0) ? false : true;
		calc(list, n, 23, 1);
		while (!list[--n]);
		System.out.println(n);
	}
}