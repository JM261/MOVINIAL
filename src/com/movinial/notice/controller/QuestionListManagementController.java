package com.movinial.notice.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.movinial.common.model.vo.PageInfo;
import com.movinial.member.model.vo.Member;
import com.movinial.notice.model.service.NoticeService;
import com.movinial.notice.model.vo.Question;

/**
 * Servlet implementation class QuestionListManagementController
 */
@WebServlet("/questionListManagement.no")
public class QuestionListManagementController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QuestionListManagementController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// 이동
		int listCount; 
		int currentPage; 
		int pageLimit; 
		int boardLimit; 
						
		int maxPage; 
		int startPage; 
		int endPage; 
				
		currentPage = Integer.parseInt(request.getParameter("currentPage")); 
		pageLimit = 10;
		boardLimit = 10;

		listCount = new NoticeService().selectListManagementCount();
		
		maxPage = (int)Math.ceil((double)listCount / boardLimit); 
		startPage = (currentPage - 1) / pageLimit * pageLimit + 1;
		endPage = startPage + pageLimit - 1;
				
		if(endPage > maxPage) {
			endPage = maxPage;
		}
				
		PageInfo pi = new PageInfo(listCount, currentPage, pageLimit, boardLimit, maxPage, startPage, endPage);
		ArrayList<Question> list = new NoticeService().selectListManagement(pi);
		
		//System.out.println(list.toString());	
		
		request.setAttribute("list", list);
		request.setAttribute("pi", pi);
				
		request.getRequestDispatcher("views/notice/QuestionListManagementView.jsp").forward(request, response);
	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
