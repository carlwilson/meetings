import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class LinkCrawl {
	static String fs = System.getProperty("file.separator");

	protected static class kongress {
		protected String url;
		protected String kurzID;

		kongress(String url) {
			this.url = new String(url);
			String[] tokens = new String[10];
			tokens = this.url.split("/");
			this.kurzID = tokens[tokens.length - 2];
		}
	}

	private static void makeDifference(String fileName) throws IOException {
		FileWriter fileWriter = new FileWriter(fileName, true);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		PrintWriter printWriter = new PrintWriter(bufferedWriter);
		printWriter.println("X");
		printWriter.close();
	}

	public static void main(String[] args) throws IOException, SQLException {
		String protokoll = "https://";
		String hostname = "www.egms.de";
		String landingPage = protokoll + hostname + "/static/de/meetings/index.htm";

		String mainPath = "C:\\Users\\hixel\\workspace\\Meetings\\Ueberordnungen\\";

		File checksum = new File(mainPath + "landingPage" + fs + "content" + fs + "checksum.md5");
		if (checksum.exists()) {
			makeDifference(mainPath + "landingPage" + fs + "content" + fs + "checksum.md5");
		}
		// lade die Webseite herrunter
		MyWget myWget = new MyWget(landingPage, mainPath + "landingPage" + fs, false);
		int res = myWget.getPage();
		// myWget.explainResult();

		String propertypfad = System.getProperty("user.home") + fs + "properties.txt";
		String password = Utilities.readStringFromProperty(propertypfad, "password");
		SqlManager sqlManager = new SqlManager("jdbc:mariadb://localhost/meetings", "root", password);
		ResultSet resultSet = null;

		File htmlFile = new File(mainPath + "landingPage" + fs + "content" + fs + "index.htm");
		Document doc = Jsoup.parse(htmlFile, "ISO-8859-1", protokoll + hostname);
		Element content = doc.getElementById("content");
		// List<kongress> listNew = new ArrayList<kongress>();
		String insertSQL = null;
		for (int i = 2; i < content.getElementsByTag("a").size(); i++) {
			// listNew.add(new kongress(content.getElementsByTag("a").get(i).attr("href")));
			resultSet = sqlManager.executePreparedSql(
					"INSERT INTO urls (URL, Status) VALUES (\"" + content.getElementsByTag("a").get(i).attr("href") + "\", 10);");
		}

		// resultSet = sqlManager.executeSql("SELECT * FROM url_status WHERE id=0");

		while (resultSet.next()) {
			System.out.println(resultSet.getString(1) + " " + resultSet.getString(2));
		}
	}

}
