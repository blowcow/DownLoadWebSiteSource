package download;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Saver {
	
	private final String url;
	private Path path;
	private final String filename;
	
	public Saver(String url, String basepath, String filename) {
		super();
		this.url = url;
		this.path = Paths.get(basepath);
		this.filename = filename;
	}

	public void urlsavetofile() throws IOException  {
		// TODO Auto-generated method stub
				
				if(!path.toFile().exists()) Files.createDirectories(path);
				
				path = Paths.get(path.toString(), filename);
				
				if(path.toFile().exists()){	System.out.println("已存在:"+filename);return;}
				
				InputStream input = getconnectioninputstream();
				
				try { 
					
					Files.copy(input, path);
					System.out.println("save successfully:"+filename);
				} 
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					IOException ConnectionFailException = new IOException("保存文件失败:"+filename);
					ConnectionFailException.initCause(e);
					throw ConnectionFailException;
					
				}
				finally{
					input.close();
				}
			}

	public InputStream getconnectioninputstream() throws IOException {
		try {
			HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
			connection.connect();
			connection.setReadTimeout(10000);
			return connection.getInputStream();
			
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			IOException ConnectionFailException = new IOException("获取失败:"+url);
			ConnectionFailException.initCause(e);
			throw ConnectionFailException;
		}
	}
		

}
