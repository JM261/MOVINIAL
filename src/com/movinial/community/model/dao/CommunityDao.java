package com.movinial.community.model.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import com.movinial.common.model.vo.PageInfo;
import com.movinial.community.model.vo.Community;
import com.movinial.community.model.vo.CommunityFile;
import com.movinial.community.model.vo.Reply;

import static com.movinial.common.JDBCTemplate.*;

public class CommunityDao {
	
	private Properties prop = new Properties();
	
	public CommunityDao() {
		
		String fileName = CommunityDao.class.getResource("/sql/community/community-mapper.xml").getPath();
		
		try {
			prop.loadFromXML(new FileInputStream(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int selectListCount(Connection conn) { // 게시글  총 갯수 구하기
		// SELECT문을 쓰지만 값은 정수가 필요함(총 게시글의 갯수)
		// 변수
		int listCount = 0;
		
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		
		String sql = prop.getProperty("selectListCount");
		
		try {
			pstmt = conn.prepareStatement(sql);
			
			rset = pstmt.executeQuery();
			
			if(rset.next()) {
				listCount = rset.getInt("COUNT");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rset);
			close(pstmt);
		}
		return listCount;
	}

	public int selectListFilterCount(Connection conn, String communityCategory) { // 카테고리별 게시글 총 갯수 구하기
		// SELECT문을 쓰지만 값은 정수가 필요함(총 게시글의 갯수)
		// 변수
		int listCount = 0;
		
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		
		String sql = prop.getProperty("selectListFilterCount");
		
		try {
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, communityCategory);
			
			rset = pstmt.executeQuery();
			
			if(rset.next()) {
				listCount = rset.getInt("COUNT");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rset);
			close(pstmt);
		}
		return listCount;
	}

	public int selectListSearchCount(Connection conn, String searchType, String keyword) { // 검색된 게시글 총 갯수 구하기
		// SELECT문을 쓰지만 값은 정수가 필요함(총 게시글의 갯수)
		// 변수
		int listCount = 0;
		
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		
		String sql = "";
		
		switch(searchType) {
			case "titleNcontent" : sql = prop.getProperty("selectListSearchTitleNcontentCount"); break;
			case "title" : sql = prop.getProperty("selectListSearchTitleCount"); break;
			case "content" : sql = prop.getProperty("selectListSearchContentCount"); break;
			case "writer" : sql = prop.getProperty("selectListSearchWriterCount"); break;
		}
		
		try {
			pstmt = conn.prepareStatement(sql);
			
			if(searchType.equals("titleNcontent")) {
				pstmt.setString(1, "%" + keyword + "%");
				pstmt.setString(2, "%" + keyword + "%");
			} else {
				pstmt.setString(1, "%" + keyword + "%");
			}
			
			rset = pstmt.executeQuery();
			
			if(rset.next()) {
				listCount = rset.getInt("COUNT");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rset);
			close(pstmt);
		}
		return listCount;
	}	

	
	public ArrayList<Community> selectList(Connection conn, PageInfo pi) { // 게시글 전체 조회
		
		// SELECT문 => ResultSet => 0개 또는 여러 행이므로 ArrayList<Board>
		
		// 필요한 변수 셋팅
		ArrayList<Community> list = new ArrayList<>();
		
		PreparedStatement pstmt = null;
		
		ResultSet rset = null;
		
		String sql = prop.getProperty("selectList");
		
		try {
			pstmt = conn.prepareStatement(sql);
			
			int startRow = (pi.getCurrentPage()-1) * pi.getBoardLimit() + 1;
			int endRow = startRow + pi.getBoardLimit() -1 ;
			
			pstmt.setInt(1, startRow);
			pstmt.setInt(2, endRow);
			
			rset = pstmt.executeQuery();
			
			while(rset.next()) {
				list.add(new Community(rset.getInt("COMMUNITY_NO"),
										rset.getString("COMMUNITY_TITLE"),
										rset.getString("COMMUNITY_CATEGORY"),
										rset.getString("NICKNAME"),
										rset.getInt("VIEWS"),
										rset.getInt("LIKES"),
										rset.getDate("CREATE_DATE"),
										rset.getString("SPOILER"),
										rset.getInt("REPORT_COUNT")));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rset);
			close(pstmt);
		}
		return list;
	}
	
	public ArrayList<Community> selectListFilter(Connection conn, PageInfo pi, String communityCategory) { // 게시글 카테고리별 필터링 조회
		// SELECT문 => ResultSet => 0개 또는 여러 행이므로 ArrayList<Board>
		
		// 필요한 변수 셋팅
		ArrayList<Community> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		String sql = prop.getProperty("selectListFilter");
		
		try {
			pstmt = conn.prepareStatement(sql);
			
			int startRow = (pi.getCurrentPage()-1) * pi.getBoardLimit() + 1;
			int endRow = startRow + pi.getBoardLimit() -1 ;
			
			pstmt.setString(1, communityCategory);
			pstmt.setInt(2, startRow);
			pstmt.setInt(3, endRow);
			
			rset = pstmt.executeQuery();
			
			while(rset.next()) {
				list.add(new Community(rset.getInt("COMMUNITY_NO"),
						rset.getString("COMMUNITY_TITLE"),
						rset.getString("COMMUNITY_CATEGORY"),
						rset.getString("NICKNAME"),
						rset.getInt("VIEWS"),
						rset.getInt("LIKES"),
						rset.getDate("CREATE_DATE"),
						rset.getString("SPOILER"),
						rset.getInt("REPORT_COUNT")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rset);
			close(pstmt);
		}
		return list;
	}

	public ArrayList<Community> selectListSearch(Connection conn, PageInfo pi, String searchType, String keyword) {// 게시글 키워드 검색  조회
		
		// SELECT문 => ResultSet => 0개 또는 여러행
		
		// 필요한 변수 셋팅
		ArrayList<Community> list = new ArrayList<>();
		
		PreparedStatement pstmt = null;
		
		ResultSet rset = null;
		
		String sql = "";
		
		switch(searchType) {
			case "titleNcontent" : sql = prop.getProperty("selectListSearchTitleNcontent"); break;
			case "title" : sql = prop.getProperty("selectListSearchTitle"); break;
			case "content" : sql = prop.getProperty("selectListSearchContent"); break;
			case "writer" : sql = prop.getProperty("selectListSearchWriter"); break;
		}
		
		try {
			pstmt = conn.prepareStatement(sql);
			
			int startRow = (pi.getCurrentPage()-1) * pi.getBoardLimit() + 1;
			int endRow = startRow + pi.getBoardLimit() -1 ;
			
			if(searchType.equals("titleNcontent")) {
				pstmt.setString(1, "%" + keyword + "%");
				pstmt.setString(2, "%" + keyword + "%");
				pstmt.setInt(3, startRow);
				pstmt.setInt(4, endRow);
			} else {
				pstmt.setString(1, "%" + keyword + "%");
				pstmt.setInt(2, startRow);
				pstmt.setInt(3, endRow);
			}
			
			rset = pstmt.executeQuery();
			
			while(rset.next()) {
				list.add(new Community(rset.getInt("COMMUNITY_NO"),
										rset.getString("COMMUNITY_TITLE"),
										rset.getString("COMMUNITY_CATEGORY"),
										rset.getString("NICKNAME"),
										rset.getInt("VIEWS"),
										rset.getInt("LIKES"),
										rset.getDate("CREATE_DATE"),
										rset.getString("SPOILER"),
										rset.getInt("REPORT_COUNT")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rset);
			close(pstmt);
		}
		return list;
	}

	public int increaseCount(Connection conn, int communityNo) {

		int result = 0;
		PreparedStatement pstmt = null;
		
		String sql = prop.getProperty("increaseCount");
		
		try {
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, communityNo);
			
			result = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(pstmt);
		}
		return result;
	}

	public Community selectCommunity(Connection conn, int communityNo) { // 게시글 상세조회
		
		// SELECT => ResultSet => PK에 의해 한건만 => Board
		// 변수
		Community c = null;
		
		PreparedStatement pstmt = null;
		
		ResultSet rset = null;
		
		String sql = prop.getProperty("selectCommunity");
		
		try {
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, communityNo);
			
			rset = pstmt.executeQuery();
			
			if(rset.next()) {
				
				c = new Community(rset.getInt("COMMUNITY_NO"),
							rset.getString("COMMUNITY_TITLE"),
							rset.getString("COMMUNITY_CATEGORY"),
							rset.getString("NICKNAME"),
							rset.getString("COMMUNITY_CONTENT"),
							rset.getInt("VIEWS"),
							rset.getInt("LIKES"),
							rset.getDate("CREATE_DATE"),
							rset.getString("SPOILER"),
							rset.getInt("REPORT_COUNT"),
							rset.getInt("ISNOTICE"),
							rset.getInt("COMMUNITY_WRITER"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rset);
			close(pstmt);
		}
		return c;
	}

	public CommunityFile selectCommunityFile(Connection conn, int communityNo) { // 첨부파일 조회
		
		// SELECT문
		
		// 변수
		CommunityFile cf = null;
		
		PreparedStatement pstmt = null;
		
		ResultSet rset = null;
		
		String sql = prop.getProperty("selectCommunityFile");
		
		try {
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, communityNo);
			
			rset = pstmt.executeQuery();
			
			if(rset.next()) {
				cf = new CommunityFile();
				
				cf.setFileNo(rset.getInt("FILE_NO"));
				cf.setOriginName(rset.getString("ORIGIN_NAME"));
				cf.setChangeName(rset.getString("CHANGE_NAME"));
				cf.setFilePath(rset.getString("FILE_PATH"));
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rset);
			close(pstmt);
		}
		return cf;
	}

	public ArrayList<Reply> selectReplyList(Connection conn, int communityNo) { // 댓글 전체 조회
		
		// 필요한 변수 셋팅
		ArrayList<Reply> list = new ArrayList<>(); // 조회 결과값이 0개 또는 여러개 => ArrayList 사용

		PreparedStatement pstmt = null;
		
		ResultSet rset = null;
		
		String sql = prop.getProperty("selectReplyList");
		
		try {
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, communityNo);
			
			rset = pstmt.executeQuery();
			
			while(rset.next()) {
				
				list.add(new Reply(rset.getInt("REPLY_NO"),
						rset.getString("NICKNAME"),
						rset.getString("REPLY_CONTENT"),
						rset.getString("CREATE_DATE"),
						rset.getInt("REPORT_COUNT")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rset);
			close(pstmt);
		}
		return list;
	}

	public int insertReply(Connection conn, Reply r) { // 댓글 작성
		
		// INSERT문 => 처리된 행의 갯수
		// 필요한 변수
		
		int result = 0;
		
		PreparedStatement pstmt = null;
		
		String sql = prop.getProperty("insertReply");
		
		try {
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, r.getRefCno());
			pstmt.setInt(2, Integer.parseInt(r.getReplyWriter()));
			pstmt.setString(3, r.getReplyContent());
			
			result = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(pstmt);
		}
		return result;
	}

	public int insertCommunity(Connection conn, Community c) {
		
		// INSERT => 처리된 행의 갯수
		int result = 0;
		
		PreparedStatement pstmt = null;
		
		String sql = prop.getProperty("insertCommunity");
		
		try {
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, c.getCommunityTitle());
			pstmt.setString(2, c.getCommunityCategory());
			pstmt.setInt(3, Integer.parseInt(c.getCommunityWriter()));
			pstmt.setString(4, c.getCommounityContent());
			pstmt.setString(5, c.getSpoiler());
			
			result = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(pstmt);
		}
		return result;
	}

	public int insertCommunityFile(Connection conn, CommunityFile cf) {
		
		// INSERT문 => 처리된 행의 갯수
		
		int result = 0;
		PreparedStatement pstmt = null;
		
		String sql = prop.getProperty("insertCommunityFile");	
		
		try {
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, cf.getOriginName());
			pstmt.setString(2, cf.getChangeName());
			pstmt.setString(3, cf.getFilePath());
			
			result = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(pstmt);
		}
		return result;
	}


}
