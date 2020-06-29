package naver;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BoxOfficeNaver {

	
	
			String url = "https://movie.naver.com/movie/running/current.nhn";
			String title = ""; // 제목
			String starScore = "dd > span.link_txt > a"; // 평점
			String type = ""; // 장르
			String bookRate = ""; // 예매율
			String movieTime = ""; // 상영시간
			String openDt = ""; // 개봉일
			String director = ""; // 감독
			String actor = ""; // 출연진
			String naverCode = ""; // 네이버 영화 코드
			
			int finalCnt = 0; // 수집을 멈추기 위한 변수(1~10위까지 완료)
			
	public String[][] naverMovieRank(String[][] mvRank) throws IOException {
			Document doc = Jsoup.connect(url).get();
			Elements movieList = doc.select("div.lst_wrap > ul.lst_detail_t1 > li");

			
			//Label1:for (Element movie : movieList) {  
				for (Element movie : movieList) {  //Label 을써서 continue로 나가는곳을 지정할수 있다.
				if(finalCnt == 10) {
					break;
				}
				
				// 네이버 영화정보 크롤링
				title = movie.select("dt.tit > a ").text(); //영화제목
				
				int flag = 999;
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
				if(flag == 999) {
					continue;
				}
				
				// 예매율 감독 출연진 초기화
				bookRate = "0";
				director = "";
				actor = "";
				

				if (movie.select("span.num").size() == 2) {
					bookRate = movie.select("span.num").get(1).text(); //예매율
				}
				starScore = movie.select("span.num").get(0).text(); // 영화평점
				type = movie.select("dd > span.link_txt").get(0).text(); // 영화 장르
				
				String temp = movie.select("dl.info_txt1 > dd").get(0).text();
				int beginTimeIndex = temp.indexOf("|");
				int endTimeIndex = temp.lastIndexOf("|");

				if (beginTimeIndex == endTimeIndex) { // 상영시간
					movieTime = temp.substring(0, endTimeIndex);
				} else {
					movieTime = temp.substring(beginTimeIndex + 2, endTimeIndex);
				}
				
				int dCode = 0;
				int aCode = 0;
				if (!movie.select("dt.tit_t2").text().equals("")) {
					dCode = 1; // 감독있음!
				}
				if (!movie.select("dt.tit_t3").text().equals("")) {
					aCode = 1; // 출연진있음!
				}

				if (dCode == 1 && aCode == 0) {
					director = movie.select("dd > span.link_txt").get(1).text();// 감독
				} else if (dCode == 0 && aCode == 1) {
					actor = movie.select("dd > span.link_txt").get(1).text();// 출연진
				} else if (dCode == 1 && aCode == 1) {
					director = movie.select("dd > span.link_txt").get(1).text();
					actor = movie.select("dd > span.link_txt").get(2).text();// 출연진
				}
				
				String naverHref = movie.select("dt.tit > a ").attr("href");// 네이버 영화 코드
				naverHref.lastIndexOf("=");
				naverCode = naverHref.substring(naverHref.lastIndexOf("=") + 1);
				// 영화 개봉일자.
				int openDtTxtIndex = temp.lastIndexOf("개봉");
				openDt = temp.substring(endTimeIndex + 2, openDtTxtIndex);
				
				mvRank[flag][2] = bookRate;
				mvRank[flag][3] = type;
				mvRank[flag][4] = movieTime.trim();
				mvRank[flag][5] = openDt.trim();
				mvRank[flag][6] = director;
				mvRank[flag][7] = actor;
				mvRank[flag][10] = naverCode;
				finalCnt += 1;

			}
			return mvRank;

		}
	

	}


