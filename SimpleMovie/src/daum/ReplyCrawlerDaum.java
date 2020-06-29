package daum;

import java.io.IOException;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import domain.ReplyDTO;
import persistence.ReplyDAO;

public class ReplyCrawlerDaum {
	
	int page = 1;
	int cnt = 0;
	int total = 0;
	String prepage = "";
	
	ReplyDAO rDao = new ReplyDAO(); 

	public HashMap<String, Integer> daumCrawler(String movieNm, String daumCode) throws IOException{
		int page = 1;
		while(true){
			String url = "https://movie.daum.net/moviedb/grade?movieId="+daumCode+"&type=netizen&page="+page;
			Document doc = Jsoup.connect(url).get();
			Elements movieList = doc.select("div.review_info");
			
			if(movieList.size() == 0) {
				break;
			}
			
			
			for(Element movie : movieList) {
				Elements stScoreList = movie.select("em.emph_grade");
				Elements contentList = movie.select("p.desc_review");
				Elements writeList = movie.select("em.emph_grade");
				Element regdateList = movie.select("span.info_append").get(0);
				
				Double stScore = Double.parseDouble(stScoreList.text());
				String content = contentList.text();
				String write = writeList.text();
				String regdate = regdateList.text().substring(0, 10);
				
				
				System.out.println("■[DAUM] ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
				System.out.println(String.format("■ 평점: %s\n"
				+"■ 내용 :%s\n " 
				+"■ 작성자 :%s\n"
				+ "■ 작성일자 :%s\n",stScore, content, write, regdate));
				
				ReplyDTO rDto = new ReplyDTO(movieNm, content, write, stScore, regdate);
				rDao.addReply(rDto);
				total += stScore;
				cnt++;
		}
			page += 1;
	  } 
		System.out.println("\n");
		System.out.println("■[DAUM Reply End Line] =======================[DAUM Reply End Line]");
		System.out.println("■[DAUM] ============================================================");
		System.out.println("■[DAUM] [총"+cnt+"페이지 수집했습니다.]");
		System.out.println("■[DAUM Reply End Line] =======================[DAUM Reply End Line]");
		System.out.println("■[DAUM] ============================================================");
		System.out.println("\n");
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("count", cnt);
		map.put("total", total);
		
		return map;
	}
		
	}

