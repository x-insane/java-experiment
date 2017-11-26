// Need jsoup.jar (https://jsoup.org/)

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class News {
	public String title;
	public String url;

	public News(String title, String url) {
		this.title = title;
		this.url = url;
	}
}

class NewSpider {

	private Document doc;

	public NewSpider(String url) throws IOException {
		doc = Jsoup.connect(url).get();
	}

	public void parseNews(String selector, String part_name) {
		Elements news = doc.select(selector);
		System.out.println(part_name);
		System.out.println("--------------------------------------");
		int skip = 0;
		for (int i=1;i<=news.size();++i) {
			Element e = news.get(i-1);
			String title = e.text().trim();
			String url = e.attr("href").trim();
			if (url.isEmpty()) {
				Elements urls = e.select("a[href]");
				url = urls.first().attr("href").trim();
				for (int j=1;j<urls.size();++j)
					url += ", " + urls.get(j).attr("href").trim();
			}
			if (title.length() >= 6)
				System.out.println( (i-skip) + "、" + title + "(" + url + ")" );
			else
				skip ++;
		}
		System.out.println();
	}
}

public class Spider {

	// public static News[] parseNews(String html) {
	// 	Document doc = Jsoup.parse(html);
	// 	final String[] selectors = {
	// 		".list_12>li>a",
	// 		".list_14>li>a"
	// 	};
	// 	List<News> list = new ArrayList<News>();
	// 	for (String selector : selectors) {
	// 		Elements news = doc.select(selector);
	// 		for (Element e : news) {
	// 			String title = e.text().trim();
	// 			String url = e.attr("href").trim();
	// 			if (title.length() >= 8)
	// 				list.add(new News(title, url));
	// 		}
	// 	}
	// 	News[] result = new News[list.size()];
	// 	return list.toArray(result);
	// }

	public static String fetchHTML(String u) {
		StringBuffer content = new StringBuffer();
		try {
			URL url = new URL(u);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			int status = con.getResponseCode();
			if (status != 200)
				return null;
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			String inputLine;
			while ((inputLine = in.readLine()) != null)
				content.append(inputLine + "\r\n");
			in.close();
			con.disconnect();
		} catch (ProtocolException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
		return content.toString();
	}

	public static void main(String[] args) {
		try {
			PrintStream ps = new PrintStream(new FileOutputStream("out/08-spider.txt"), false, "UTF-8");
			System.setOut(ps);
		} catch (IOException e) {
			System.out.println("Failed to open file!");
			System.exit(-1);
		}

		try {
			InetAddress local = InetAddress.getLocalHost();
			InetAddress target = InetAddress.getByName("news.sina.com.cn");
			System.out.println("Target Host Name: " + target.getHostName());
			System.out.println("Target Host IP: " + target.getHostAddress());
			System.out.println("Local Host Name: " + local.getHostName());
			System.out.println("Local Host IP: " + local.getHostAddress());
			System.out.println();
		} catch (UnknownHostException e) {
			System.out.println("Fail to resolve the host!");
		}

		// News[] all = parseNews(fetchHTML("http://news.sina.com.cn/"));
		// for (News news : all) {
		// 	System.out.println(news.title + "(" + news.url + ")");
		// }

		try {
			NewSpider ns = new NewSpider("http://news.sina.com.cn/");
			ns.parseNews(".ct_t_01>h1", "大字要闻");
			ns.parseNews("#ad_entry_b2_b>.list_14>li>a[href]", "次级要闻");
			ns.parseNews("#blk_08_cont01 a[href]", "军事");
			ns.parseNews("div[data-sudaclick=history_1] a[href]", "历史");
			ns.parseNews("div[data-sudaclick=book_1] a[href]", "读书");
			ns.parseNews("#blk_gnxw_011 a[href]", "内地新闻");
			ns.parseNews("#blk_gjxw_011 a[href]", "国际新闻");
			ns.parseNews("#blk_cjkjqcfc_011 a[href]", "财经·科技·汽车·房产·地产·教育 1");
			ns.parseNews("#blk_cjkjqcfc_012 a[href]", "财经·科技·汽车·房产·地产·教育 2");
			ns.parseNews("#blk_lctycp_011 a[href]", "娱乐·体育·彩票·游戏 1");
			ns.parseNews("#blk_lctycp_012 a[href]", "娱乐·体育·彩票·游戏 2");
			ns.parseNews("#blk_sh_011 a[href]", "社会 1");
			ns.parseNews("#blk_sh_012 a[href]", "社会 2");
			ns.parseNews("#blk_gntltop_01 + .part_02 a[href]", "国内新闻");
			ns.parseNews("#blk_gjtltop_01 + .part_02 a[href]", "国际新闻");
			ns.parseNews("#blk_jstltop_01 + .part_03 a[href]", "军事新闻");
			ns.parseNews("#blk_jktltop_01 + .part_03 a[href]", "健康新闻");
			ns.parseNews("#blk_tytltop_01 + .part_02 a[href]", "体育新闻");
			ns.parseNews("#blk_cjtltop_01 + .part_02 a[href]", "财经新闻");
			ns.parseNews("#blk_spsctltop_01 + .part_03 a[href]", "收藏");
			ns.parseNews("#blk_fctltop_01 + .part_03 a[href]", "房产");
			ns.parseNews("#blk_qctltop_01 + .part_03 a[href]", "汽车");
			ns.parseNews("#blk_kjtltop_01 + .part_02 a[href]", "科技新闻");
			ns.parseNews("#blk_bktltop_01 + .p_box a[href]", "博客");
			ns.parseNews("#blk_dstltop_01 + .p_box a[href]", "读书");
			ns.parseNews("#blk_jytltop_01 + .p_box a[href]", "教育 考试");
			ns.parseNews("#blk_nxtltop_01 + .part_03 a[href]", "历史");
			ns.parseNews(".newpart a[href]", "时尚 女性 星座");
			ns.parseNews("#blk_yxtltop_01 + .part_03 a[href]", "游戏");
			ns.parseNews("#blk_yltltop_01 + .part_02 a[href]", "娱乐新闻");
			ns.parseNews("#blk_shtltop_01 + .part_04 a[href]", "社会新闻");
		} catch (IOException e) {
			System.out.println("Can not load HTML!");
		}
	}
}