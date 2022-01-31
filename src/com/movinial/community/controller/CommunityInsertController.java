package com.movinial.community.controller;


import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

import com.movinial.common.MyFileRenamePolicy;
import com.movinial.community.model.service.CommunityService;
import com.movinial.community.model.vo.Community;
import com.movinial.community.model.vo.CommunityFile;
import com.oreilly.servlet.MultipartRequest;

/**
 * Servlet implementation class CommunityInsertController
 */
@WebServlet("/insert.cm")
public class CommunityInsertController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CommunityInsertController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// 1) 게시글작성 내용 POST 방식으로 전달 -> 인코딩 설정
		request.setCharacterEncoding("UTF-8");
		
		// 2) 값 뽑기
		// 폼 전송할 때 일반방식이 아닌 multipart/form-data방식으로 전송하는 경우 // null
		// request.getParameter로 리퀘스트 객체에서 값 뽑기가 불가능!!
		// => multipart라는 객체에 값을 이관시켜서 다뤄야 한다.
		
		// 스텝 0) enctype이 multipart/form-data로 잘 전송되었을 경우
		//					전반적인 내용들이 수정되도록 조건을 걸어줌
		
		if(ServletFileUpload.isMultipartContent(request)) {
			
			// 파일 업로드를 위한 라이브러리 다운로드(http://www.servlets.com/)
			// 파일 업로드를 위한 라이브러리명 : cos.jar(com.oreilly.servlet의 약자)
			
			// 스텝 1) 전송되는 파일의 처리할 작업내용
			// 		(용량 제한, 전달된 파일을 저장할 경로)
			// 1_1. 전송파일 용량 제한
			//			(int maxSize => 10Mbyte로 제한)

			int maxSize = 1024 * 1024 * 10; // 10Mbyte
			
			// 1_2. 전달된 파일을 저장할 서버의 폴더 경로 알아내기
			//			 (String savePath)
			// => getRealPath메소드를 통해 알아내기
			// 		다만 인자로 WebContent 폴더로부터 board_upfiles 폴더까지의 경로를 제시
			
			// ServletContext application
			// HttpSession session
			// HttpServletRequest request
			
			String savePath = request.getSession().getServletContext().getRealPath("/resources/community_upfiles/");
			
			// 스텝 2) 전달된 파일명 수정 및 서버에 업로드 작업
			
			MultipartRequest multiRequest = new MultipartRequest(request, savePath, maxSize, "UTF-8", new MyFileRenamePolicy());
			
			String communityTitle = multiRequest.getParameter("title"); // 글제목
			String category =  multiRequest.getParameter("category"); // 글 카테고리
			String communityWriter = multiRequest.getParameter("memberNo"); // 글 작성자(회원번호)
			String communityContent = multiRequest.getParameter("content"); // 글 내용,본문
			String spoiler =  multiRequest.getParameter("spoiler"); // 스포일러 포함여부
			int isNotice = 0; // 공지사항여부
			
			if(spoiler != null) {
				spoiler = "Y";
			} else {
				spoiler = "N";
			}
			
			if(category.equals("공지")) { // 글 카테고리가 "공지" 라면
				isNotice = 1;
			}
			
			// 3) VO 객체로 가공 => Community 객체로 만들자
			Community c = new Community(communityTitle,category,communityWriter,communityContent,spoiler,isNotice);
			
			// 두번째 INSERT => 선택적(첨부파일이 있을 경우에만 INSERT)
			CommunityFile cf = null;
			
			// 원본명, 수정파일명, 파일경로
			
			// 첨부파일 유무를 가려내는 메소드(원본파일명이 존재하는지 안하는지)
			// multiRequest.getOriginalFileName("키값");
			// => 첨부파일이 있을 경우 "원본파일명" / 첨부파일이 없을 경우 null 리턴
			if(multiRequest.getOriginalFileName("upfile") != null) {
				
				// 첨부파일이 있다면 VO 객체로 가공
				cf = new CommunityFile();
				cf.setOriginName(multiRequest.getOriginalFileName("upfile")); // 원본명
				
				// 수정파일명 알아오기 : 실제 서버에 업로드된 파일의 이름을 리턴해주는 메소드
				// multiRequest.getFilesystemName("키값");
				cf.setChangeName(multiRequest.getFilesystemName("upfile")); // 수정파일명
				
				// 파일경로
				cf.setFilePath("/resources/community_upfiles/");

			}
			
			// 4) Service 단으로 넘기기
			int result = new CommunityService().insertCommunity(c, cf);
			
			// 5) 응답페이지 결정
			if(result > 0) { // 게시글 등록 성공 => 알림 띄우고 커뮤니티 메인으로 이동
				
				request.getSession().setAttribute("alertMsg", "게시글이 등록 되었습니다");
				response.sendRedirect(request.getContextPath() + "/list.cm?currentPage=1");
				
			} else { // 게시글 등록 실패 => 저장한 첨부파일 삭제 후 에러페이지로 이동
				
				if(cf != null) { // 첨부파일이 있었을 경우 이미 업로드된 첨부파일을 굳이 서버에 저장해둘 필요가 없음
					new File(savePath + cf.getChangeName()).delete(); // File 클래스의 delete 메소드 호출
				} 				
				request.getRequestDispatcher("views/common/errorPage.jsp").forward(request, response);
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
