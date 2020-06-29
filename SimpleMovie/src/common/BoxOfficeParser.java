package common;

import java.io.BufferedInputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

	public class BoxOfficeParser {
	String key = "f1cc87102d88dd6a0dc7fcae6b537c6a";
	String today = "";
	String[][] mvRank = new String[10][12];
	String url = "";

	public BoxOfficeParser(){
		this.url = makeURL();
		//System.out.println("=========================================================================================================================");
		//System.out.println("== LOG: parsing URL Completed >>>" + url);  로그 찍는것은 개발할때 사용
		//System.out.println("=========================================================================================================================");
	}
	// 1. 파싱하고싶은 URL 생성(일일 박스 오피스 : 하루 전날)
	// URL = 기본 URL + key + 날짜
	public String makeURL() {
		// 오늘 날짜 구하기
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		
		
		// System.out.println("포맷 전: " + cal.getTime());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		today = sdf.format(cal.getTime());

		String url = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/"
				+ "searchDailyBoxOfficeList.json"
				+ "?key=" + key 
				+ "&targetDt=" + today;

		return url;

	}
	// 웹상에있는 데이터를 가져오기위한 작업 
	// 2. 웹상의 URL 주소를 읽음
	private String readUrl(String preUrl) throws Exception{
		//
		BufferedInputStream reader = null;
		
		try {
			URL url = new URL(preUrl);
			reader = new BufferedInputStream(url.openStream());
			StringBuffer buffer = new StringBuffer();
			int i;
			byte[] b = new byte[4096];  // 4096 byte만큼 데이터를 쪼개서 받아라
			while((i = reader.read(b)) != -1) { // while 문으로 계속 반복하는데 읽을 데이터가 없으면 -1이나오는데 -1 값이 나올때까지만 읽어라
												//reader 에서 읽을것이다 b 사이즈만큼 그값을 i 한테 줘라
				buffer.append(new String(b, 0, i));   //buffer에 4096byte사이즈만큼 데이터를 받은것을 축적해라
			}
			return buffer.toString(); //parser.parse(readUrl(url)) 이쪽으로 데이터가 return이 된다.
		}finally {
			if(reader != null) {
				reader.close();
			}
		}
	}
       // 데이터 파싱
	public String[][] getParser() throws Exception {
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(readUrl(url));
		JSONObject json = (JSONObject) obj.get("boxOfficeResult"); // url의 key 값인 boxOfficeResult 를 json에 넣어라 
		JSONArray array = (JSONArray) json.get("dailyBoxOfficeList"); // dailyBoxOfficeList에 담긴 1~10위 까지의  BoxOfficeList를 array에 넣어라
		
		for (int i = 0; i < array.size(); i++) {
			JSONObject entity = (JSONObject) array.get(i);
			String rank = (String) entity.get("rank");		// 순위	
			String movieNm = (String) entity.get("movieNm"); // 영화제목
			String audiAcc = (String) entity.get("audiAcc"); // 누적관객수
			String saleAcc = (String) entity.get("salesAcc"); // 누적매출액
			
			mvRank[i][0] = rank;
			mvRank[i][1] = movieNm;
			mvRank[i][8] = audiAcc;
			mvRank[i][9] = saleAcc;

		}
		return mvRank;

	}
}



