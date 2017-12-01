import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

enum Player {
	None, Black, Blue;

	public static Player op(Player p) {
		if (p == Player.Black)
			return Player.Blue;
		if (p == Player.Blue)
			return Player.Black;
		return Player.None;
	}
}

public class GoBang extends JFrame {

	private static final int nx = 18; // 列数
	private static final int ny = 12; // 行数
	private static final Rectangle r = new Rectangle(50, 75, 700, 500);
	private static final double dx = (double)r.width / nx;
	private static final double dy = (double)r.height / ny;
	private static final int gor = 15;
	private Player data[][];
	private Player last = Player.None;
	private int last_x = -1;
	private int last_y = -1;
	private int status = 0;

	public GoBang() {
		setSize(800, 625);
		setLocation(200, 50);
		setResizable(false);
		setTitle("五子棋");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		data = new Player[nx+1][ny+1];
		for (int i=0;i<=nx;++i)
			for (int j=0;j<=ny;++j)
				data[i][j] = Player.None;
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (status != 0) {
					reset();
					return;
				}
				java.awt.Point p = e.getPoint();
				if (p.x + dx/2 < r.x || p.y + dy/2 < r.y)
					return;
				int x = (int)((p.x - r.x + dx/2) / dx);
				int y = (int)((p.y - r.y + dy/2) / dy);
				if (x > nx || y > ny)
					return;
				if (data[x][y] != Player.None)
					return;
				Player player = Player.op(last);
				if (player == Player.None)
					player = Player.Black;
				Go(x, y, player);
			}
		});
	}

	public void reset() {
		status = 0;
		last = Player.None;
		last_x = last_y = -1;
		for (int i=0;i<=nx;i++)
			for (int j=0;j<=ny;j++)
				data[i][j] = Player.None;
		repaint();
	}

	public void Go(int x, int y, Player p) {
		data[x][y] = p;
		repaint();
		last = p;
		last_x = x;
		last_y = y;
		for (int i=0;i<=1;i++) {
			for (int j=-1;j<=1;j++) {
				if (i==0 && j==0)
					continue;
				if (i==0 && j==-1)
					continue;
				int n = 1;
				int px = x;
				int py = y;
				while(true) {
					px += i;
					py += j;
					if (px <= nx && py <= ny && data[px][py] == p)
						n ++;
					else
						break;
				}
				px = x;
				py = y;
				while(true) {
					px -= i;
					py -= j;
					if (px >= 0 && py >= 0 && data[px][py] == p)
						n ++;
					else
						break;
				}
				if (n >= 5) {
					status = -1;
					if (p == Player.Black)
						JOptionPane.showMessageDialog(this, "黑方胜！点击屏幕重新开始", "游戏结束", JOptionPane.INFORMATION_MESSAGE);
					else
						JOptionPane.showMessageDialog(this, "蓝方胜！点击屏幕重新开始", "游戏结束", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
		}
		for (int i=0;i<=nx;i++)
			for (int j=0;j<=ny;j++)
				if (data[i][j] == Player.None)
					return;
		status = -1;
		JOptionPane.showMessageDialog(this, "平局！点击屏幕重新开始", "游戏结束", JOptionPane.INFORMATION_MESSAGE);
	}

	public void DrawGo(Graphics g) {
		for (int i=0;i<=nx;++i) {
			for (int j=0;j<=ny;++j) {
				if (data[i][j] == Player.None)
					continue;
				else if (data[i][j] == Player.Black)
					g.setColor(Color.black);
				else
					g.setColor(Color.blue);
				g.fillOval((int)(r.x+i*dx-gor), (int)(r.y+j*dy-gor), gor*2, gor*2);
			}
		}
	}

	public void DrawBoard(Graphics g) {
		g.setColor(Color.black);
		for (int i=0;i<=nx;++i)
			g.drawLine((int)(r.x+i*dx), (int)(r.y), (int)(r.x+i*dx), (int)(r.y+r.height));
		for (int i=0;i<=ny;++i)
			g.drawLine((int)(r.x), (int)(r.y+i*dy), (int)(r.x+r.width), (int)(r.y+i*dy));
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		DrawBoard(g);
		DrawGo(g);
	}

	public static void main(String[] args) {
		new GoBang().setVisible(true);
	}
}