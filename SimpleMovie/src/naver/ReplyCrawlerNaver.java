package naver;

import java.io.IOException;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import domain.ReplyDTO;
import persistence.ReplyDAO;

public class ReplyCrawlerNaver {
	int page = 1;
	int count = 0;
	String prepage = "";
	int total = 0;

	ReplyDAO rDao = new ReplyDAO();

	public HashMap<String, Integer> naverCrawler(String movieNm, String naverCode) throws IOException {

		// 수집하는 댓글의 영화가 MongoDB에 저장되어 있는 영화라면
		// 해당 영화 댓글 우선 삭제 후 새로운 댓글 저장
		

		while (true) {
			String url = "https://movie.naver.com/movie/bi/mi/pointWriteFormList.nhn?code=" + naverCode
					+ "&type=after&isActualPointWriteExecute=false&isMileageSubscriptionAlready=false&isMileageSubscriptionReject=false&page="
					+ page;

			Document doc = Jsoup.connect(url).get();
			Elements replyList = doc.select("div.score_result li");
			String nowPage = doc.select("input#page").attr("value");

			if (nowPage.equals(prepage)) {
				break;
			} else {
				prepage = nowPage;
			}

			String content = "";
			String writer = "";
			String regdate = "";
			int score = 0;
			for (Element one : replyList) {
				content = one.select("div.score_reple p ").get(0).text();
				writer = one.select("div.score_reple a span").get(0).text();
				score = Integer.parseInt(one.select("div.star_score em").get(0).text());
				regdate = one.select("div.score_reple em").get(1).text();

				System.out.println("■[NAVER] ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
				System.out.println("작성자:" + writer);
				System.out.println("내용:" + content);
				System.out.println("평점:" + score);
				System.out.println("작성일자" + regdate);
				

				// MongoDB에 (댓글 1건 저장)
				ReplyDTO rDto = new ReplyDTO(movieNm, content, writer, score, regdate);
				// System.out.println(rDto.toString());
				rDao.addReply(rDto);
				total += score;
				count += 1;
			}
			page += 1;
		}
		System.out.println("\n");
		System.out.println("■[NAVER Reply End Line] =======================[NAVER Reply End Line]");
		System.out.println("■[NAVER] ============================================================");
		System.out.println("■[NAVER] [총" + count + "건 수집했습니다.]");
		System.out.println("■[NAVER] ============================================================");
		System.out.println("■[NAVER Reply End Line] =======================[NAVER Reply End Line]");
		System.out.println("\n");
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("count", count);
		map.put("total", total);
		
		return map;
	}

}
