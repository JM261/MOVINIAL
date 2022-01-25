package com.movinial.community.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.movinial.common.model.vo.PageInfo;
import com.movinial.community.model.service.CommunityService;
import com.movinial.community.model.vo.Community;


/**
 * Servlet implementation class CommunityListController
 */
@WebServlet("/search.cm")
public class CommunityListSearchController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CommunityListSearchController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// 1) 인코딩 설정
		request.setCharacterEncoding("UTF-8");
		
		// 2) request로 부터 값 뽑기
		
		// ------- 페이징처리 -------
		// 필요한 변수들
		int listCount; // 현재 일반게시판의 게시글 총 갯수 => BOARD 테이블로부터 조회 COUNT(*)활용
		int currentPage; // 현재페이지(사용자가 요청한 페이지)
		int pageLimit; // 페이지 하단에 보여질 페이징바의 최대 갯수 = 10개
		int boardLimit; // 한 페이지에 보여질 게시글 최대 갯수 => 10개
		
		int maxPage; // 가장 마지막 페이지가 몇번 페이지인지 (== 총 페이지 갯수)
		int startPage; // 페이지 하단에 보여질 첫번째 페이징바
		int endPage; // 페이지 하단에 보여질 마지막 페이징바
		
		// *listCount : 검색한 게시글 총 갯수
		// 넘겨받은 검색할 항목과 검색할 내용 받아서 변수에 저장
		String searchType = request.getParameter("searchType");
		String keyword = request.getParameter("keyword");
		listCount = new CommunityService().selectListSearchCount(searchType, keyword);
		
		
		// * currentPage : 현재페이지 (== 사용자가 요청한 페이지)
		currentPage = Integer.parseInt(request.getParameter("currentPage")); // : String
		
		// * pageLimit : 페이징바의 최대 갯수
		pageLimit = 10;
		
		// * boardLimit : 한 페이지에 보여질 게시글의 최대 갯수
		boardLimit = 30;
		
		// maxPage : 가장 마지막 페이지가 몇번 페이지 인지 (총 페이지의 갯수)
		maxPage = (int)Math.ceil((double)listCount / boardLimit); 
		
		// startPage : 페이지 하단에 보여질 페이징바의 시작수
		startPage = (currentPage - 1) / pageLimit * pageLimit + 1; 
		
		// endpage : 페이지 하단에 보여질 페이징바의 끝 수
		endPage = startPage + pageLimit - 1; 
		
		//startPage가 11이어서 endPage가 20이 돼야 하는데
		// maxPage가 11까지 밖에 없다면???
		// => endPage를 maxPage로 변경
		if(endPage > maxPage) {
			endPage = maxPage;
		}
		
		// 7개의 변수를 가지고 해당되는 범위에 따른 게시글 10개씩만
		// 3) VO로 가공
		PageInfo pi = new PageInfo(listCount, currentPage, pageLimit, boardLimit, maxPage, startPage, endPage);
		
		// 4) Service 단으로 토스
		ArrayList<Community> list = new CommunityService().selectListSearch(pi, searchType, keyword);
		
		// 5) 응답 뷰 지정 => list, pi, 검색항목,검색어 넘기기
		request.setAttribute("list", list);
		request.setAttribute("pi", pi);
		request.getSession().setAttribute("searchType", searchType);
		request.getSession().setAttribute("keyword", keyword);
		
		// views/board/boardListView.jsp로 포워딩~
		request.getRequestDispatcher("views/community/communityListSearchView.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
