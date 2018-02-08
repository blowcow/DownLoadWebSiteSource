package download;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Pattern;

public class Client {

	public static void main(String[] args) throws  ExecutionException, InterruptedException  {
		// TODO Auto-generated method stub
			
			String baseurl ="";//论坛主页面url
			String basepath = "";//本地下载保存的路径
			System.out.println(basepath);
			ExecutorService serchpagethreadPool = Executors.newFixedThreadPool(10);
			ExecutorCompletionService<ArrayList<String>> completionService = new ExecutorCompletionService<>(serchpagethreadPool);
			
			Pattern pattern = Pattern.compile("(thread-[^\"]*)(\"\\s*style)");//各个帖子的路径的正则表达式，位于论坛主页面url后紧跟的部分
			int group= 1;//正则表达式的群组
			int start=1;//起始页码
			int end= 2;//终止页码
			for( int i=start;i <= end;i++){
				String url = baseurl+"forum-230-"+i+".html"; //论坛主页url和各个帖子的uri拼接形成完整url
				SearchPageTask searchPageTask = new SearchPageTask(url, pattern, group, baseurl);
				completionService.submit(searchPageTask);
			}
			
			
			ExecutorService serchcontentthreadPool = Executors.newFixedThreadPool(30);
			
			Pattern contentpattern = Pattern.compile("(http[^\"]*.jpg)|(attachment.php[^\"]*)");//访问进入各个帖子后，帖子中资源请求地址的正则表达式
			int contentgroup = 0;
			for(int i=start;i <= end;i++){
				ArrayList<String> pageurls = completionService.take().get();
				for(String pageurl : pageurls){
					SearchAndDownLoadContentTask contentTask = new SearchAndDownLoadContentTask(pageurl, baseurl, contentpattern, contentgroup, basepath);
					serchcontentthreadPool.submit(contentTask);
				}
				
			}
			
				serchcontentthreadPool.shutdown();
				serchpagethreadPool.shutdown();
		}
				
}
	class SearchPageTask implements Callable<ArrayList<String>>{
		private String url;
		private Pattern pattern;
		private int group;
		private String baseurl;
		
		public SearchPageTask(String url, Pattern pattern, int group, String baseurl) {
			super();
			this.url = url;
			this.pattern = pattern;
			this.group = group;
			this.baseurl = baseurl;
		}
	
		@Override
		public ArrayList<String> call() throws InterruptedException {
			// TODO Auto-generated method stub
			Fileter fileter = new Fileter(url, pattern);
			try {
				ArrayList<String> list = fileter.getGourpMatch(group);
				return list;
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			
		}
		
	}
		
	class SearchAndDownLoadContentTask implements Runnable{
		private String url;
		private String baseurl;
		private Pattern contentpattern;
		private int contentgroup;
		private String basepath;
		private String filename ;

		public SearchAndDownLoadContentTask(String url, String baseurl, Pattern contentpattern, int contentgroup,
				String basepath) {
			super();
			this.url = url;
			this.baseurl = baseurl;
			this.contentpattern = contentpattern;
			this.contentgroup = contentgroup;
			this.basepath = basepath;
			this.filename = url.substring(0, 16);
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String contenturl = baseurl+url;
			
			
			Fileter imagefileter = new Fileter(contenturl, contentpattern);
			
			try {
				ArrayList<String> contentlist = imagefileter.getGourpMatch(contentgroup);
				
				for(String content : contentlist){
					
					
					try{
						save(content);
					}
					catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.println("save fail!!!!!!!!!:"+content);
						
					} 
					
				}
				
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			} 
			
		}

		public void save(String content) throws IOException {
			if(content.endsWith("jpg")){
				if(content.contains("?")) return; 
				String suffix=(content.substring(content.length()-6, content.length()));
				Saver saver = new Saver(content, basepath,filename+suffix );
				saver.urlsavetofile();
				
			}
			else {
				String suffix=".torrent";
				content = baseurl + content;
				Saver saver = new Saver(content, basepath, filename+suffix);
				saver.urlsavetofile();
				
			}
			
		}
	
	}			


