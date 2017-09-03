package Crawling;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jdbc.JdbcUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import util.Util;

public class Crawl {
	
	
	private static void print(String msg, Object... args) {
	    System.out.println(String.format(msg, args));
	}

	private static String trim(String s, int width) {
	    if (s.length() > width)
	        return s.substring(0, width-1) + ".";
	    else
	        return s;
	}
	
	/**
	 * 取新闻门户网表的id传入
	 */
	public static void main(String[] args)
	{

		FetchTwoLevel(2);

	}
	
	
	
	
	/**
	 * 输入二级url获取father url
	 */
	public static void FetchTwoLevel(int i)
	{
		News news=GetFromDB(i);
		
		
        Document doc = null;
		try {
			doc = Jsoup.connect(news.url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
        Elements links = doc.select("a[href]");
        System.out.println(links);
        String l=null;
        for (Element link : links) {
        	l=link.attr("abs:href").toString();
        	
        	if(!l.contains(".htm"))
        	{
        		System.out.println(l);
        		news.url=l;
        		if(!FetchOneLevel(news))
        			continue;
        	}
        }
	
	}
	/**
	 * 一级URL是father url
	 * @param news
	 */
	public static boolean FetchOneLevel(News news)
	{
        print("Fetching %s...", news.url);
        Document doc = null;
		try {
			doc = Jsoup.connect(news.url).get();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
        Elements links = doc.select("a[href]");
        
        String l=null;
        
        for (Element link : links) {
        	l=link.attr("abs:href").toString();
        	if(!l.contains("#")&&l.contains(".http://")&&l.contains(".html")||l.contains(".htm")||l.contains(".shtml"))
        	{
        		news.url=link.attr("abs:href");
        		SavaToDB(getNewsContents(news));
        	}
        }
        return true;
	}
	
	/**
	 * id为某网站的编号，返回正则化的信息
	 */
	public static News GetFromDB(int id)
	{
		JdbcUtils jt=new JdbcUtils();  
	    String sql = "select * from news_site where id="+id;
		List<Map<String, Object>> list = null;
		Map<String, Object> map=null;
		
		try {
			list = jt.findModeResult(sql, null);
			map=list.get(0);
		} catch (SQLException e) {
			//e.printStackTrace();
		}
		String url=(String) map.get("url");
		String find_updatetime=(String) map.get("find_updatetime");
		String find_type=(String) map.get("find_type");
		String find_publisher =(String) map.get("find_publisher");
		String find_src_website = (String) map.get("find_srcwebsite");
		String find_labels = (String) map.get("find_labels");
		String find_title=(String) map.get("find_title");
		String find_news_summary =(String) map.get("find_summary");
		String find_contents=(String) map.get("find_contents");
		
		
		News news=new News(url,find_updatetime,find_type,find_publisher,find_src_website,find_labels,find_title,find_news_summary,find_contents);

		//System.out.println(news);
		
		return news;
		
	}
	
	
	
	
	
	
	/**
	 * 将爬取到的一个news对象存到数据库中
	 * @param news
	 */
	public static boolean SavaToDB(News news)
	{
		JdbcUtils jt=new JdbcUtils();  

		String sql="INSERT INTO `News`.`news` ("+
				  "hash_value,"+
				  "url,"+
				  "update_time,"+
				  "news_type,"+
				  "publisher,"+
				  "src_website,"+
				  "labels,"+
				  "news_title,"+
				  "news_summary,"+
				  "news_all_contents"+
				  
				")values(?,?,?,?,?,?,?,?,?,?)";
				
		List<Object> params = new ArrayList<Object>();
		params.add(Util.hashFunc(news.url));
		params.add(news.url);
		params.add(news.updatetime);
		params.add(news.type);
		params.add(news.publisher);
		params.add(news.src_website);
		params.add(news.labels);
		params.add(news.title);
		params.add(news.summary);
		params.add(news.contents);
		
		boolean flag=false;
		try {
			flag= jt.updateByPreparedStatement(sql, params);
		} catch (SQLException e) {
			//e.printStackTrace();
		}
		return flag;
	
	}
		
	
	/**
	 * 访问特定新闻内容页的唯一内容
	 */
	public static News getNewsContents(News news) {
		Document doc = null;
		 long start = System.currentTimeMillis();
		try {
			doc = Jsoup.connect(news.url).userAgent("Mozilla/5.0").timeout(3000).get();
		} catch (IOException e) {
			System.out.println("链接访问超时"+news.url);
			//e.printStackTrace();
		}
		finally{
			  //System.out.println("Process Time is:"+(System.currentTimeMillis()-start) + "ms");
		 }

		//查找发行时间
		Element element=null;
		String text=null;
		String find_css=news.find_updatetime;
		try {
		element =doc.select(find_css).first();
		text=element.text().toString();
		} catch (Exception e) {
			text=null;
			System.out.println("元素未能找到"+news.url+"  "+find_css+" ");
			//e.printStackTrace();
		}
		finally{

			//System.out.println(text);
			news.updatetime=Util.DateConvert(text);
		 }
		

		//查找新闻类型
		element=null;
		text=null;
		find_css=news.find_type;
		try {
		element =doc.select(find_css).first();
		text=element.text().toString();
		} catch (Exception e) {
			text=null;
			System.out.println("元素未能找到"+news.url+"  "+find_css+" ");
			//e.printStackTrace();
		}
		finally{
			news.type=text;
		 }
		

		//查找src网站
		element=null;
		text=null;
		find_css=news.find_src_website;
		try {
		element =doc.select(find_css).first();
		text=element.text().toString();
		} catch (Exception e) {
			text=null;
			System.out.println("元素未能找到"+news.url+"  "+find_css+" ");
			//e.printStackTrace();
		}
		finally{
			news.src_website=text;
		 }
		
		//查找标签
		Elements elements=null;
		StringBuffer sbtext=new StringBuffer();
		find_css=news.find_labels;
		try {
			elements =doc.select(find_css);
			for(Element e:elements)
			{
				sbtext.append("|"+e.text().toString());
			}
			news.labels=sbtext.toString();
		} catch (Exception e) {
			news.labels=null;
			System.out.println("元素未能找到"+news.url+"  "+find_css+" ");
			//e.printStackTrace();
		}

		
		//查找标题
		element=null;
		text=null;
		find_css=news.find_title;
		try {
		element =doc.select(find_css).first();
		text=element.text().toString();
		} catch (Exception e) {
			System.out.println("元素未能找到"+news.url+"  "+find_css+" ");
			//e.printStackTrace();
		}
		finally{
			news.title=text;
		 }
		
		//查找摘要	
		element=null;
		text=null;
		find_css=news.find_summary;
		try {
		element =doc.select(find_css).first();
		text=element.text().toString();
		} catch (Exception e) {
			text=null;
			System.out.println("元素未能找到"+news.url+"  "+find_css+" ");
			//e.printStackTrace();
		}
		finally{
			news.summary=text;
		 }
		
		//查找正文内容
		element=null;
		text=null;
		find_css=news.find_contents;
		try {
		element =doc.select(find_css).first();
		text=element.text().toString();
		} catch (Exception e) {
			text=null;
			System.out.println("元素未能找到"+news.url+"  "+find_css+" ");
			//e.printStackTrace();
		}
		finally{
			news.contents=text;
		 }
		

		return news;
	}
}
