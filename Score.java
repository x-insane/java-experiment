import java.io.*;

public class Score {
	public static void main(String[] args) {
		int total = 0;
		int n = 0;
		int max = 0, min = 100;
		int[] part = new int[11];
		try {
			PrintStream ps = new PrintStream(new FileOutputStream("out/05-score.txt"));
			System.setOut(ps);
			BufferedReader in = new BufferedReader(new FileReader("data/score.csv"));
			String line = null;
			while ((line = in.readLine()) != null) {
				String[] items = line.split(",");
				int score = Integer.parseInt(items[items.length-1]);
				total += score;
				n ++;
				part[score/10] ++;
				if (score > max)
					max = score;
				if (score < min)
					min = score;
			}
			System.out.format("The highest grade: %d\r\n", max);
			System.out.format("The lowest grade: %d\r\n", min);
			System.out.format("Average grade: %d\r\n", total/n);
			System.out.format("Number of grade 60-69: %d\r\n", part[6]);
			System.out.format("Number of grade 70-79: %d\r\n", part[7]);
			System.out.format("Number of grade 80-89: %d\r\n", part[8]);
			System.out.format("Number of grade 90-100: %d\r\n", part[9]+part[10]);
		} catch (IOException e) {
			System.out.println("Failed to open file!");
			System.exit(-1);
		}
	}
}