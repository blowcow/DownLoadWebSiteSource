package download;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Fileter {
	
	private final Pattern p;
	private final String url;
	
	public Fileter(String url,Pattern p) {
		this.url = url;
		this.p = p;
	}
	
	public InputStream getconnetioninputstream() throws IOException  {
		
		HttpURLConnection connection;
		
		try {
			connection = (HttpURLConnection)new URL(url).openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
			connection.connect();
			return connection.getInputStream();
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			IOException ConnectionFailException = new IOException("连接失败:"+url);
			ConnectionFailException.initCause(e);
			throw ConnectionFailException;
		}

	}
	
	public StringBuilder getAllString() throws IOException  {
		
		try(InputStream input = getconnetioninputstream()) {
			
			Scanner scanner = new Scanner(input, "gbk");
			StringBuilder stringBuilder = new StringBuilder();
			while(scanner.hasNextLine()){
				stringBuilder.append(scanner.nextLine());
			}
			scanner.close();
			return stringBuilder;
		}
	}
	
	
	public ArrayList<String> getAllMatch() throws  IOException  {
		StringBuilder stringBuilder = getAllString();
//		System.out.print(stringBuilder);
		Matcher matcher = p.matcher(stringBuilder);
		ArrayList<String> list = new ArrayList<>();
		while(matcher.find()){
			String match =stringBuilder.substring(matcher.start(), matcher.end());
//			System.out.println(match);
			list.add(match);
			}
		return list;
		}
		
	public ArrayList<String> getGourpMatch(int group) throws IOException  {
		StringBuilder stringBuilder = getAllString();

//			System.out.print(stringBuilder);
		Matcher matcher = p.matcher(stringBuilder);
		ArrayList<String> list = new ArrayList<>();
		while(matcher.find()){
			String match =stringBuilder.substring(matcher.start(group), matcher.end(group));
			System.out.println(match);
			list.add(match);
		}
	return list;
	
	}
	
	
	
}
