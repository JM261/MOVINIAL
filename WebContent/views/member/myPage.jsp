<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!doctype html>
<html lang="ko">
  <head>
    <meta charset="utf-8">
    <title>my Page</title>

    <style>

      /* --------------- */
  
      
      #dt-sidebar {
        width: 350px;
        padding: 20px;
        margin-bottom: 20px;
        float: left;
        border: 1px solid #bcbcbc;
      }

      #dt-sidebar>ul{
          list-style: none;
          font-size: 20pt;
          margin-left:30px;
          line-height: 1.9cm;
        }
        
        #dt-sidebar>ul>li>a{
            text-decoration: none;
            color:black;
      }


      #dt-sidebar>ul>li>a:hover{
          color:coral;
      }
      
   

	/* ----------------------------------------------------------------- */

	.countmovie, .countlike, .countreview{
		border : 1px solid white;
		width : 150px;
		height : 150px;
		margin-left : 60px;
		margin-top: 10px;
		margin-bottom :50px;
		float:right;
	}
  
	  #cntseen, #cntlike, #cntreview{
	    font-size:70pt;
	    font-style:italic;
	    padding-left:20px;   
	  }
	  
	  #cntseenlabel, #cntlikelabel, #cntreviewlabel{
	   
	    width:150px;
	    height:20px;
	    float:right;
	    margin-left :900px;
	    margin-top:10px;
	    text-align:center;
	    font-size:30pt;
	  }
	  
	  .profile img{
	  	width:150px;
	  	height:150px;
	  	margin-left:150px;
	  	margin-top:40px;
	  	border-radius:60px;
	  	border: 1px solid lightgray;
	  
	  }
	
	  #profile_nickname{
	 	margin-left:150px;
	 	font-size:18pt;
	 	text-align:center;
	 	margin-left:175px;
	 }
	 
	 .posters>img{
	 	width:100%;
	 	height:500px;
	 	border-radius:30px;
	 	
	 	float:left;

	 }
	 
	 .posters{
	 	border: 1px solid lightgray;
	 	border:30px;
	 	
	 
	 }
        </style>
        
  </head>
  <body>
  	<%@ include file="../../views/common/headerSidebar.jsp" %>


      <!-- --------------------------------------------------------------헤더 끝----------------------- -->
    
        <div class="dt-content">
          <h2>My Contents</h2>
          		
             <div class ="countreview">
              <span id="cntreview">${reviewCount}</span>
              <span id="cntreviewlabel">review</span>
            </div>
            
            <div class ="countmovie">
              <span id="cntseen">${seenCount}</span>
              <span id="cntseenlabel">seen</span>
            </div>
            
            <div class ="countlike">
              <span id="cntlike">${likeCount}</span>
              <span id="cntlikelabel">like</span>
            </div>
            
            <div class="profile">
				<form id="updateForm" action="<%= contextPath %>/myPage.me" method="post" enctype="multipart/form-data">
	          		
	          		<img id="img" src="<%= contextPath %>${profileImage}" alt="프로필">
					<input id="reUpfile" type="file" style="display: none;"
						class="form-control-file border" name="reUpfile" onchange="contentImg(this);">
				</form>
				
          		<br>
          		
          		<span id="profile_nickname"><%= loginUser.getMemberNickname() %> </span>
       		</div>
            
        </div>
        
        <div class="dt-content content2">
        <h2>My Posters</h2>
	        <br>
	        <div class="posters">
			  <img src="<%= contextPath %>/resources/images/lala.png" alt="">
			</div> 
        </div>
        
        <div class="dt-content">
          <h2>My Contents3</h2>
          	
          
        </div>

		
      <!-- --------------------------------------------------------------푸터 시작----------------------- -->

		
	<%@ include file="../../views/common/footer.jsp" %>

	<script>
		$("#img").click(function(){
			$("#reUpfile").click();
		})
		
		$('#reUpfile').change(function() {
			$('#updateForm').submit();
		})

		function contentImg(inputFile){

			if(inputFile.files.length == 1){ // 파일이 있는 경우

				var reader = new FileReader();

				reader.readAsDataURL(inputFile.files[0]);

					reader.onload = function(e){
					// 이미지 미리보기, 선택한 영역에 이미지를 넣으며 감추어져있던 영역이 보여지게함
					$("#img").attr("src", e.target.result).css('display', 'flex');
				}
			}
			else { // 파일 선택후 취소하는 경우
					// 미리보기 사라지게 하기
					$("#img").attr("src", null);
			}
		}
	</script>
  </body>
</html>
