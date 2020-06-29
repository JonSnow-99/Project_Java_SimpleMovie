package daum;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BoxOfficeDaum {
	String baseurl = "http://ticket2.movie.daum.net/Movie/MovieRankList.aspx";
	int finalCnt = 0; //수집을 멈추기 위한 변수(1~10위 까지 완료)
	
	
	public String[][] daumMovieRank(String[][] mvRank) throws IOException{
			Document doc = Jsoup.connect(baseurl).get();
			Elements movieList = doc.select("div.desc_boxthumb > strong.tit_join > a");
			
			for(Element movie: movieList) {
				if(finalCnt == 10) {
					// 1~10위까지의 영화정보 수집 완료! 빠져 나가세요 ~
					break;
				}
				
				// ################################ 제목을 가져와야합니다.
				String title = movie.text();
				int flag = 0;
				for(int i = 0 ; i <mvRank.length; i++) {
					if(mvRank[i][1].equals(title)) {
						//BoxOffice 1~10위 권내의 영화로 판별 크롤링
						flag = i; //0~9까지의 값만 input
						break;
					}
				}
				
				
				// 1~10 위권 외의 영화 -> 크롤링 x
				// flag가 0~9사이의 값이면 크롤링 시작
				// 그외의 값이면 크롤링 하지말아라        
				// 다른방법
				//1. if(flag < 0 || flag >9)
				//2. flag에 특정값 입력  int flag = 99;
				// if(flag == 99)
				if(flag == 0) {
					continue;
				}
				
				String url = movie.attr("href");
				Document movieDoc = Jsoup.connect(url).get();
				
				// 상세 영화 페이지로 연결이 안되는 경우 제거 해주는 코드
				if(movieDoc.select("span.txt_name").size() == 0) {
					continue;
				}
				
				
				
				String daumHref = movieDoc.select("a.area_poster").get(0).attr("href");
				String daumCode = daumHref.substring(daumHref.lastIndexOf("=")+1, daumHref.lastIndexOf("#"));
				//String daumCode = daumHref.split("=")[1].split("#")[0];
				
				// 수집된 영화정보를 mvRank에 INPUT
				
				mvRank[flag][11] = daumCode;
				finalCnt += 1;;
			}
		
	return mvRank;
}

}
