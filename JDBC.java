import java.io.*;
import java.sql.*;
import java.util.Scanner;

class StudentData {
	public int id;
	public String name;
	public int age;
}

interface GetStudent {
	public boolean student_callback(StudentData data);
}

class Student {

	private final String table = "student";
	private Connection conn;

	public Student(Connection conn) {
		this.conn = conn;
	}

	public Student insert(String name, int age) {
		try {
			String sql = "INSERT INTO "+ table +" (name, age) VALUE (?, ?)";
			PreparedStatement preparedStmt = conn.prepareStatement(sql);
			preparedStmt.setString(1, name);
			preparedStmt.setInt(2, age);
			preparedStmt.execute();
			preparedStmt.close();
		} catch (SQLException e) {
			System.out.println("Can not insert data!");
		}
		return this;
	}

	public Student delete(int id) {
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

	public Student modify(int id, String name, int age) {
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

	public Student find(GetStudent gs) {
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

	public Student createTable() {
		try {
			Statement stmt = conn.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS "+ table +" ( "+
				"id INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT, "+
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

	public Student dropTable() {
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

		Student stu = new Student(conn);
		stu.dropTable();
		stu.createTable();
		try {
			Scanner sc = new Scanner(new File("data/student.txt"), "utf8");
			while (sc.hasNext()) {
				String name = sc.next();
				int age = sc.nextInt();
				stu.insert(name, age);
			}
		} catch (FileNotFoundException e) {
			System.out.println("Failed to open file!");
			System.exit(-2);
		}
		System.out.println("Original data\r\n------------------");
		stu.find(new GetStudent() {
			public boolean student_callback(StudentData data) {
				System.out.println("id: " + data.id);
				System.out.println("name: " + data.name);
				System.out.println("age: " + data.age);
				System.out.println();
				stu.modify(data.id, data.name.toUpperCase(), data.age + 1);
				if (data.id == 4)
					stu.delete(data.id);
				return true;
			}
		});
		System.out.println("Data after modified\r\n------------------");
		stu.find(new GetStudent() {
			public boolean student_callback(StudentData data) {
				System.out.println("id: " + data.id);
				System.out.println("name: " + data.name);
				System.out.println("age: " + data.age);
				System.out.println();
				return true;
			}
		});
		System.out.println("Data of id = 3\r\n------------------");
		StudentData data = stu.find(3);
		System.out.println("id: " + data.id);
		System.out.println("name: " + data.name);
		System.out.println("age: " + data.age);
	}
}