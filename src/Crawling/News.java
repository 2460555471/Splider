package Crawling;
import java.util.Calendar;  
import java.util.Date;  


public class News {
	
	public String url;
	
	/**
	 * 以下内容用于html中的匹配
	 */
	public String find_updatetime;
	public String find_type;
	public String find_publisher;
	public String find_src_website;
	public String find_labels;
	public String find_title;
	public String find_summary;
	public String find_contents;
	
	/**
	 * 存储返回的结果
	 */
	public Date updatetime;
	public String type;
	public String publisher;
	public String src_website;
	public String labels;
	public String title;
	public String summary;
	public String contents;
	public Date timestamp;
	
	
	public News()
	{
		super();
	}
	public News(String url, String find_updatetime, String find_type,
			String find_publisher, String find_src_website, String find_labels,
			String find_title, String find_summary, String find_contents) {
		super();
		this.url = url;
		this.find_updatetime = find_updatetime;
		this.find_type = find_type;
		this.find_publisher = find_publisher;
		this.find_src_website = find_src_website;
		this.find_labels = find_labels;
		this.find_title = find_title;
		this.find_summary = find_summary;
		this.find_contents = find_contents;
		
		this.type=null;
		this.publisher=null;
		this.src_website=null;
		this.labels=null;
		this.title=null;
		this.summary=null;
		this.contents=null;

	}
	@Override
	public String toString() {
		return "News [url=" + url + "\n, updatetime=" + updatetime + "\n, type="
				+ type + "\n, publisher=" + publisher + "\n, src_website="
				+ src_website + "\n, labels=" + labels + "\n, title=" + title
				+ "\n, summary=" + summary + "\n, contents=" + contents
				+ "\n"+ "]";
	}


}
