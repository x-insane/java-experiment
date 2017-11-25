/***************************************************************************
*     Need these jar files into CLASSPATH (path and version are custom):
* C:\Program Files\Java\mysql-connector\mysql-connector-java-5.1.44-bin.jar;
* C:\Program Files\Java\excel-poi\poi-3.17.jar;
* C:\Program Files\Java\excel-poi\poi-ooxml-3.17.jar;
* C:\Program Files\Java\excel-poi\poi-ooxml-schemas-3.17.jar;
* C:\Program Files\Java\excel-poi\lib\commons-collections4-4.1.jar;
* C:\Program Files\Java\excel-poi\ooxml-lib\xmlbeans-2.6.0.jar;
***************************************************************************/

import java.io.*;
import java.sql.*;
import java.util.Scanner;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

class StudentData {
	public int id;
	public String name;
	public int age;
}

interface GetStudent {
	public boolean student_callback(StudentData data);
}

class StudentExcel {

	private Workbook wb;
	private Sheet sheet;
	private int num;

	public StudentExcel() {
		wb = new XSSFWorkbook();
		sheet = wb.createSheet();
		num = 0;
	}
	
	public StudentExcel(String filename) throws IOException, InvalidFormatException {
		wb = WorkbookFactory.create(new File(filename));
		sheet = wb.getSheetAt(0);
		num = sheet.getLastRowNum() + 1;
	}

	public StudentExcel insert(int id, String name, int age) {
		Row row = sheet.createRow(num++);
		row.setHeightInPoints(30);
		CellStyle cs = wb.createCellStyle();
		cs.setAlignment(HorizontalAlignment.CENTER);
		cs.setVerticalAlignment(VerticalAlignment.CENTER);
		cs.setBorderBottom(BorderStyle.THIN);
		cs.setBorderLeft(BorderStyle.THIN);
		cs.setBorderRight(BorderStyle.THIN);
		cs.setBorderTop(BorderStyle.THIN);
		Cell cell = row.createCell(0);
		cell.setCellStyle(cs);
		cell.setCellValue(id);
		cell = row.createCell(1);
		cell.setCellStyle(cs);
		cell.setCellValue(name);
		cell = row.createCell(2);
		cell.setCellStyle(cs);
		cell.setCellValue(age);
		return this;
	}

	public StudentExcel find(GetStudent gs) {
		boolean back = true;
		for(int i=0;back && i<num;i++) {
			Row row = sheet.getRow(i);
			StudentData data = new StudentData();
			data.id = (int)row.getCell(0).getNumericCellValue();
			data.name = row.getCell(1).getStringCellValue().trim();
			data.age = (int)row.getCell(2).getNumericCellValue();
			back = gs.student_callback(data);
		}
		return this;
	}

	public StudentExcel writeTo(String filename) throws IOException {
		FileOutputStream fos = new FileOutputStream(filename);
		wb.write(fos);
		if(null != fos)
			fos.close();
		return this;
	}
}

class StudentDb {

	private final String table = "student";
	private Connection conn;

	public StudentDb(Connection conn) {
		this.conn = conn;
	}

	public StudentDb insert(int id, String name, int age) {
		try {
			String sql = "INSERT INTO "+ table +" (id, name, age) VALUE (?, ?, ?)";
			PreparedStatement preparedStmt = conn.prepareStatement(sql);
			preparedStmt.setInt(1, id);
			preparedStmt.setString(2, name);
			preparedStmt.setInt(3, age);
			preparedStmt.execute();
			preparedStmt.close();
		} catch (SQLException e) {
			System.out.println("Can not insert data!");
		}
		return this;
	}

	public StudentDb delete(int id) {
		try {
			String sql = "DELETE FROM "+ table +" WHERE id = ?";
			PreparedStatement preparedStmt = conn.prepareStatement(sql);
			preparedStmt.setInt(1, id);
			preparedStmt.execute();
			preparedStmt.close();
		} catch (SQLException e) {
			System.out.println("Can not delete data(id="+ id +")!");
		}
		return this;
	}

	public StudentDb modify(int id, String name, int age) {
		try {
			String sql = "UPDATE "+ table +" SET name=?, age=? WHERE id=?";
			PreparedStatement preparedStmt = conn.prepareStatement(sql);
			preparedStmt.setString(1, name);
			preparedStmt.setInt(2, age);
			preparedStmt.setInt(3, id);
			preparedStmt.execute();
			preparedStmt.close();
		} catch (SQLException e) {
			System.out.println("Can not modify data!");
		}
		return this;
	}

	public StudentData find(int id) {
		StudentData data = null;
		try {
			String sql = "SELECT * FROM "+ table +" WHERE id = ?";
			PreparedStatement preparedStmt = conn.prepareStatement(sql);
			preparedStmt.setInt(1, id);
			ResultSet rs = preparedStmt.executeQuery();
			if (rs.next()) {
				data = new StudentData();
				data.id = rs.getInt("id");
				data.name = rs.getString("name");
				data.age = rs.getInt("age");
			}
			preparedStmt.close();
		} catch (SQLException e) {
			System.out.println("SQL Error");
		}
		return data;
	}

	public StudentDb find(GetStudent gs) {
		try {
			String sql = "SELECT * FROM " + table;
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			boolean back = true;
			while (back && rs.next()) {
				StudentData data = new StudentData();
				data.id = rs.getInt("id");
				data.name = rs.getString("name");
				data.age = rs.getInt("age");
				back = gs.student_callback(data);
			}
			stmt.close();
		} catch (SQLException e) {
			System.out.println("SQL Error");
		}
		return this;
	}

	public StudentDb createTable() {
		try {
			Statement stmt = conn.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS "+ table +" ( "+
				"id INT(11) PRIMARY KEY NOT NULL, "+
				"name TINYTEXT NOT NULL, "+
				"age INT(3) NOT NULL"+
			") DEFAULT CHARSET=utf8";
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e){
			System.out.println("Can not create table!");
		}
		return this;
	}

	public StudentDb dropTable() {
		try {
			Statement stmt = conn.createStatement();
			String sql = "DROP TABLE IF EXISTS " + table;
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e){
			System.out.println("Can not create table!");
		}
		return this;
	}
}

public class JDBC {

	private static final String database = "java_exp";
	private static final String db_user = "java_exp";
	private static final String db_passwd = "ii0Yjvixn0Lif0SL";

	private static Connection conn;

	public static void close() {
		try {
			conn.close();
		} catch (SQLException e){
			System.out.println("Can not close the connection!");
			System.exit(-2);
		}
	}

	public static void main(String[] args) {
		try {
			PrintStream ps = new PrintStream(new FileOutputStream("out/07-jdbc.txt"));
			System.setOut(ps);
		} catch (IOException e) {
			System.out.println("Failed to open file!");
			System.exit(-1);
		}

		String url="jdbc:mysql://localhost:3306/"+ database +"?characterEncoding=utf8&useSSL=true";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url, db_user, db_passwd);
		} catch(ClassNotFoundException e) {
			System.out.println("Can not find MySQL driver!");
			System.exit(-100);
		} catch (SQLException e){
			System.out.println("Can not connect the database!");
			System.exit(-101);
		}

		StudentDb stu_db = new StudentDb(conn);
		stu_db.dropTable();
		stu_db.createTable();

		// try {
		// 	Scanner sc = new Scanner(new File("data/student.txt"), "utf8");
		// 	while (sc.hasNext()) {
		// 		int id = sc.nextInt();
		// 		String name = sc.next();
		// 		int age = sc.nextInt();
		// 		stu_db.insert(id, name, age);
		// 	}
		// } catch (FileNotFoundException e) {
		// 	System.out.println("Failed to open file!");
		// 	System.exit(-2);
		// }

		try {
			StudentExcel se_in = new StudentExcel("data/student.xlsx");
			se_in.find(new GetStudent() {
				public boolean student_callback(StudentData data) {
					stu_db.insert(data.id, data.name, data.age);
					return true;
				}
			});
		} catch (IOException e) {
			System.out.println("Failed to open file!");
			System.exit(-3);
		} catch (InvalidFormatException e) {
			System.out.println("Invalid Format Exception in the input file!");
			System.exit(-4);
		}

		System.out.println("Original data\r\n------------------");
		stu_db.find(new GetStudent() {
			public boolean student_callback(StudentData data) {
				System.out.println("id: " + data.id);
				System.out.println("name: " + data.name);
				System.out.println("age: " + data.age);
				System.out.println();
				stu_db.modify(data.id, data.name.toUpperCase(), data.age + 1);
				if (data.id == 204)
					stu_db.delete(data.id);
				return true;
			}
		});
		System.out.println("Data after modified\r\n------------------");
		stu_db.find(new GetStudent() {
			public boolean student_callback(StudentData data) {
				System.out.println("id: " + data.id);
				System.out.println("name: " + data.name);
				System.out.println("age: " + data.age);
				System.out.println();
				return true;
			}
		});
		System.out.println("Data of id = 203\r\n------------------");
		StudentData data = stu_db.find(203);
		System.out.println("id: " + data.id);
		System.out.println("name: " + data.name);
		System.out.println("age: " + data.age);

		try {
			StudentExcel se_out = new StudentExcel();
			stu_db.find(new GetStudent() {
				public boolean student_callback(StudentData data) {
					se_out.insert(data.id, data.name, data.age);
					return true;
				}
			});
			se_out.writeTo("out/07-jdbc.xlsx");
		} catch (IOException e) {
			System.out.println("Failed to open file!");
		}
	}
}