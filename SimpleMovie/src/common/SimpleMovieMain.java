package common;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Scanner;

import daum.BoxOfficeDaum;
import daum.ReplyCrawlerDaum;
import naver.BoxOfficeNaver;
import naver.ReplyCrawlerNaver;
import persistence.ReplyDAO;

public class SimpleMovieMain {

	public static void main(String[] args) throws Exception {

		BoxOfficeParser bParser = new BoxOfficeParser();
		BoxOfficeNaver bon = new BoxOfficeNaver();
		BoxOfficeDaum don = new BoxOfficeDaum();
		ReplyCrawlerNaver nCrawler = new ReplyCrawlerNaver();
		ReplyCrawlerDaum dCrawler = new ReplyCrawlerDaum();
		ReplyDAO rDao = new ReplyDAO();
		// 순위, 영화제목, 예매율, 장르, 상영시간, 개봉일자, 감독
		// 출연진 누적관객수, 누적매출액, 네이버코드, 다음코드

		String[][] mvRank = new String[10][12];

		// 1. 박스오피스 정보 + 네이버 영화정보 + 다음영화 정보 (1~ 10위)
		// 1-1. BoxOffice parsing (완료)
		mvRank = bParser.getParser();

		// 1-2 Naver BoxOffice Crawling
		mvRank = bon.naverMovieRank(mvRank);

		// 1-3 Daum BoxOffice Crawling
		mvRank = don.daumMovieRank(mvRank);
		// printArr(mvRank); 나오는지 확인하는 것

		// View단 실행
		// ueerVal = 사용자가 입력한 영화번호(순위)
		int userVal = userInterface(mvRank);

		// 3.사용자가 선택한 영화의 네이버, 다음 댓글 정보를 수집 및 분석
		// 3-1. MongoDB 데이터 삭제
		// 수집하는 댓글의 영화가 MongoDB에 저장되어 있는 영화라면
		// 해당 영화 댓글 우선 삭제 후 새로운 댓글 저장
		rDao.deleteReply(mvRank[userVal -1][1]);
		
		//3-2. NAVER 댓글 수집 + MongoDB 저장
		HashMap<String,Integer> nMap = nCrawler.naverCrawler(mvRank[userVal -1][1], mvRank[userVal -1][10]);
		
		//3-3. DAUM 댓글 수집 + MongoDB 저장
		HashMap<String,Integer> dMap = dCrawler.daumCrawler(mvRank[userVal -1][1], mvRank[userVal -1][11]);
		
		// 4. 사용자에게 결과 출력
		double nTotal = nMap.get("total");
		double avgNaver = nTotal/nMap.get("count"); 
		double dTotal = dMap.get("total");
		double avgDaum = dTotal/nMap.get("count"); 
		DecimalFormat dropDot = new DecimalFormat(".#");
		DecimalFormat threeDot = new DecimalFormat("###,###");
		// BigInteger money = new BigInteger(mvRank[userVal -1][9]);
		//4. 사용자에게 결과 출력
		System.out.println("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		System.out.println("■ Discription of \"" + mvRank[userVal -1][0]);
		System.out.println("■ ========================================================================================================================");
		System.out.println("■ 장르 → ["+ mvRank[userVal -1][3]+"], ■ 예매율→["+mvRank[userVal -1][2] + "%]");
		System.out.println("■ 상영시간→ [" + mvRank[userVal -1][4]+ "], ■ 개봉일자: [" + mvRank[userVal -1][5]+"]");
		System.out.println("■ 감독 → ["+mvRank[userVal -1][6]+"]");
		System.out.println("■ 출연진 →["+mvRank[userVal -1][7]+"]");
		System.out.println("■ 누적 → [관객수" + threeDot.format(Integer.parseInt(mvRank[userVal -1][8])) + "명] [매출액:￦" + threeDot.format(Integer.parseInt(mvRank[userVal -1][9])) + "원]");
		System.out.println("■ 네이버  → [댓글수:"+ nMap.get("count") + "건] [★평점: " + dropDot.format(avgNaver)+"점]");
		System.out.println("■ 다음  →  [댓글수:"+ dMap.get("count") + "건] [★평점: " + dropDot.format(avgDaum)+"점]");
		System.out.println("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		
	}

	// 현재 날짜 계산하기

	// VIEW: 프로그램 시작 인터페이스 + 사용자 값 입력
	public static int userInterface(String[][] mvRank) {
		int userVal = 0; // 사용자 입력 값 변수

		Scanner sc = new Scanner(System.in); // 필드변수로 static 선언해서 static Scanner sc = new Scanner(System.in); 를 할수도 있지만
												// 필드변수는 함부로 선언하는 것이 아니다. 그래서 지역변수로 선언을 해준다.
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyy.mm.dd hh:mm:ss");
		String today = sdf.format(cal.getTime());
		System.out.println(
				"■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		System.out.println("■ >> simple Movie ◆Ver1.2");
		System.out.println(
				"■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		System.out.println("■ >> Developer: joo min kim(zoooom_in)");
		System.out.println(
				"■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		System.out.println("■ >> TODAY:" + today);
		System.out.println(
				"■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		System.out.println("■ >> Real-time BoxOfficeRank(" + (cal.get(Calendar.MONTH) + 1) + "월"
				+ cal.get(Calendar.DATE) + "일 박스오피스 1~10위)"); // month 는 0부터 계산해서 +1해줘야 정상적이됨

		// String information = "";
		for (int i = 0; i < mvRank.length; i++) {
			// 조건문 ? 참 : 거짓 ;
			// information = (mvRank[i][5] == null) ? "(상영정보 없음!)" : ""; //3항연산자
			String noneCode = "";
			if (mvRank[i][10] == null) {
				noneCode = "(정보없음!)";
			}
			System.out.println("■ >> " + mvRank[i][0] + "위:◆" + mvRank[i][1] + noneCode + "◆ ");
		}
		// 2-2. 사용자가 입력하는 부분

		while (true) {
			System.out.println(
					"■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
			System.out.println("■ >> 보고싶은 영화 번호(순위)를 입력하세요.");
			System.out.printf("■ >> 번호: ");
			userVal = sc.nextInt();

			// 유효성 체크 -> 1~10까지의 값(정상) > 1~10 이외의 숫자를 넣었을때 > 정보없는 영화 선택했을 때 >
			// id
			// 1) NULL값 체크
			// 2) 길이 체크
			// 3) ccw 1104 공백 값 체크
			// 4) 정규식 체크 ex) #$%^&*특수문자 쓰지마시오
			// 5) 길이체크 (ID의 길이)
			if (userVal < 0 || userVal > 10) {
				// 잘못된값!! 1보다 작고 10보다 큰 값을 입력했기때문
				System.out.println("■ >> [Warning]1~10사이의 숫자를 입력하세요 :(");

			} else if (mvRank[userVal - 1][10] == null) {
				// 사용자가 입력한 번호의 영화가 정보가 있는지 없는지 체크
				System.out.println("■ >> [Warning] 해당 영화는 상영정보가 없습니다. 다른영화를 선택해주세요. ");
			} else {
				sc.close();
				break;
			}
			// 통과: 사용자의 값이 0~10

		}
		System.out.println(
				"■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");

		return userVal;
	}

	public static void printArr(String[][] mvRank) {

		System.out.println(
				"■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		for (int i = 0; i < mvRank.length; i++) {

			System.out.printf(mvRank[i][0] + "\t");
			System.out.printf(mvRank[i][1] + "\t");
			System.out.printf(mvRank[i][2] + "\t");
			System.out.printf(mvRank[i][3] + "\t");
			System.out.printf(mvRank[i][4] + "\t");
			System.out.printf(mvRank[i][5] + "\t");
			System.out.printf(mvRank[i][6] + "\t");
			System.out.printf(mvRank[i][7] + "\t");
			System.out.printf(mvRank[i][8] + "\t");
			System.out.printf(mvRank[i][9] + "\t");
			System.out.printf(mvRank[i][10] + "\t");
			System.out.println(mvRank[i][11]);
			System.out.println(
					"■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		}

	}
}
