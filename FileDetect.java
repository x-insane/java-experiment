import java.io.*;
import java.security.MessageDigest;
import java.security.DigestInputStream;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.ArrayList;

class FileAttr {
	public String filename;
	public String md5;
	public int parent; // union find parent
	public int size;

	public static FileAttr[] list;

	public static int find(int a) {
		if (list[a].parent == -1 || list[a].parent == a)
			return a;
		return list[a].parent = find(list[a].parent);
	}

	public static void union(int a, int b) {
		list[a].parent = a = find(a);
		list[b].parent = b = find(b);
		if (list[a].size > list[b].size) {
			list[b].parent = a;
			list[a].size += list[b].size;
		} else {
			list[a].parent = b;
			list[b].size += list[a].size;
		}
	}

	public FileAttr(String filename, String md5) {
		this.filename = filename;
		this.md5 = md5;
		parent = -1;
		size = 0;
	}

	public static void buildTree() {
		for (int i=0;i<list.length-1;++i) {
			for (int j=i+1;j<list.length;++j) {
				if (list[i].equals(list[j])) {
					union(i, j);
				}
			}
		}
		for (int i=0;i<list.length;++i) {
			if (list[i].parent != -1)
				find(i);
		}
	}

	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof FileAttr))
			return false;
		final FileAttr fa = (FileAttr)o;
		if (!fa.md5.equals(md5))
			return false;
		try {
			int bufferSize = 256 * 1024;
			FileInputStream f1 = new FileInputStream(filename);
			FileInputStream f2 = new FileInputStream(fa.filename);
			byte[] buffer1 = new byte[bufferSize];
			byte[] buffer2 = new byte[bufferSize];
			int n1, n2;
			do {
				n1 = f1.read(buffer1);
				n2 = f2.read(buffer2);
				if (n1 != n2)
					return false;
				for (int i=0;i<n1;++i) {
					if (buffer1[i] != buffer2[i])
						return false;
				}
			} while (n1 > 0);
			f1.close();
			f2.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public static void show() {
		for (int i=0;i<list.length-1;++i) {
			if (list[i].parent == -1)
				continue;
			System.out.println("md5 = " + list[i].md5);
			System.out.println("--------------------------------------");
			System.out.println(list[i].filename);
			for (int j=i+1;j<list.length;++j) {
				if (list[i].parent == list[j].parent) {
					System.out.println(list[j].filename);
					list[j].parent = -1;
				}
			}
			list[i].parent = -1;
			System.out.println();
		}
	}
}

public class FileDetect {

	public static String byteArrayToHex(byte[] byteArray) {
		char[] hexDigits = {'0','1','2','3','4','5','6','7','8','9', 'A','B','C','D','E','F'};
		char[] resultCharArray =new char[byteArray.length * 2];
		int index = 0;
		for (byte b : byteArray) {
			resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
			resultCharArray[index++] = hexDigits[b & 0xf];
		}
		return new String(resultCharArray);
	}

	public static String md5_file(File file) throws IOException {
		int bufferSize = 256 * 1024;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			FileInputStream fis = new FileInputStream(file);
			DigestInputStream dis = new DigestInputStream(fis, md);
			byte[] buffer = new byte[bufferSize];
			while (dis.read(buffer) > 0);
			md = dis.getMessageDigest();
			byte[] resultByteArray = md.digest();
			dis.close();
			fis.close();
			return byteArrayToHex(resultByteArray);
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	public static void getFileAttr(List<FileAttr> list, File dir) throws IOException {
		File[] files = dir.listFiles();
		for (File f : files) {
			if (f.isFile())
				list.add(new FileAttr(f.getAbsolutePath(), md5_file(f)));
			else if (f.isDirectory())
				getFileAttr(list, f);
		}
	}

	public static void main(String args[]) {
		try {
			PrintStream ps = new PrintStream(new FileOutputStream("out/10-fileDetect.txt"));
        	System.setOut(ps);
		} catch (IOException e) {
			System.out.println("Failed to open file!");
			System.exit(-1);
		}
		try {
			List<FileAttr> filelist = new ArrayList<FileAttr>();
			getFileAttr(filelist, new File("data/files"));
			FileAttr.list = filelist.toArray(new FileAttr[filelist.size()]);
			FileAttr.buildTree();
			FileAttr.show();
		} catch (IOException e) {
			System.out.println("Can not read files!");
		}
	}
}