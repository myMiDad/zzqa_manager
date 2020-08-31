<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="com.zzqa.service.interfaces.user.UserManager"%>
<%@page import="com.zzqa.pojo.user.User"%>
<%@page import="com.zzqa.service.interfaces.communicate.CommunicateManager"%>
<%@page import="com.zzqa.util.DataUtil"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="com.zzqa.service.interfaces.position_user.Position_userManager"%>
<%@page import="com.zzqa.pojo.position_user.Position_user"%>
<%request.setCharacterEncoding("UTF-8");
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	UserManager userManager = (UserManager) WebApplicationContextUtils
			.getRequiredWebApplicationContext(getServletContext())
			.getBean("userManager");
	CommunicateManager communicateManager=(CommunicateManager)WebApplicationContextUtils
			.getRequiredWebApplicationContext(getServletContext())
			.getBean("communicateManager");
	Position_userManager position_userManager = (Position_userManager) WebApplicationContextUtils
			.getRequiredWebApplicationContext(getServletContext())
			.getBean("position_userManager");
	if (session.getAttribute("uid") == null) {
		response.sendRedirect("login.jsp");
		return;
	}
	int uid = (Integer) session.getAttribute("uid");
	User mUser = userManager.getUserByID(uid);
	if (mUser == null) {
		response.sendRedirect("login.jsp");
		return;
	}
	List<User> userList=userManager.getAllUserNoLeave();
%>

<!DOCTYPE HTML>
<html>
	<head>
		<base href="<%=basePath%>">

		<title>发布建议</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		<link rel="stylesheet" type="text/css" href="css/top.css">
		<link rel="stylesheet" type="text/css" href="css/advise.css">
		<link rel="stylesheet" type="text/css" href="css/communicate.css">
		<link rel="stylesheet" type="text/css" href="css/jquery-ui.min.css">
		<link rel="stylesheet" type="text/css" href="css/custom.css">
		<link rel="stylesheet" type="text/css" href="css/jquery.filer.css">
		<link rel="stylesheet" type="text/css" href="css/jquery-filer.css">
		<link rel="stylesheet" type="text/css" href="css/default.css">
		<link rel="stylesheet" type="text/css"
			href="css/jquery.filer-dragdropbox-theme.css">
		<link rel="stylesheet" type="text/css"
			href="css/font-awesome.min.css">
		<script type="text/javascript" src="js/jquery.min.js"></script>
		<script type="text/javascript" src="js/jquery-ui.min.js"></script>
		<script src="js/prettify.js" type="text/javascript"></script>
		<script src="js/jquery.filer.min.js" type="text/javascript"></script>
		<script type="text/javascript" src="js/dialog.js"></script>
		<script type="text/javascript" src="js/public.js"></script>
		<script type="text/javascript" src="js/communicate.js"></script>
		<!-- 现将隐藏的文件上传控件添加到body中，再渲染 -->
		<script src="js/custom.js" type="text/javascript"></script>
		
		<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
		<script type="text/javascript">
		    var userArray=[
                 <%boolean flag=false;for(User user:userList){
	                 if(user.getId()!=uid){
	               		if("admin".equals(user.getName())||mUser.getId()==user.getId()){
	               			continue;
	               		}else{
	               			if(flag){
		                  		out.write(",");
		                  	}else{
		                  		flag=!flag;
		                  	}
	               			out.write("["+user.getId()+",'"+user.getTruename()+"',"+false+"]");
	               		}
	                 }	
                 }%>
                 ];
		    var userArray_temp=new Array();
		    function initLabel(){
		    	var temp="";
		    	var temp1="";
		    	selected_num=0;
		    	for(var i=0;i<userArray.length;i++){
		    		temp+='<label id="label-user-'+i+'" title="'+userArray[i][1]+'"><input type="checkbox" '+(userArray[i][2]?'checked':'')+' onchange="selectUser('+i+')"><span>'+userArray[i][1]+'</span></label>';
		    		if(userArray[i][2]){
		    			selected_num++;
		    			temp1+='<li id="li-user-'+i+'"><span title="'+userArray[i][1]+'">'+userArray[i][1]+'</span> <span title="移除" onclick="cancelSelectUser('+i+')">x</span></li>';
		    		}
		    	}
		    	$(".dialog_selected_title span").text("已选（"+selected_num+"）");
		    	$(".dialog_canselectuser").html(temp);
		    	$(".dialog_selected_name").html(temp1);
		    	userArray_temp=copyArray(userArray);
		    }
		    function copyArray(array){
		    	var newArray=new Array();
		    	for(var i=0;i<array.length;i++){
		    		newArray[i]=new Array().concat(array[i]);
		    	}
		    	return newArray;
		    }
		    var selected_num=0;
		    function clear_searchword(){
		    	$(".dialog_selectuser_input_div input").val("");
		    	searchLabel();
		    }
		    function searchLabel(){
		    	var searchword=$(".dialog_selectuser_input_div input").val();
		    	if(searchword.length>0){
		    		$(".dialog_selectuser_input_div img").prop("src","images/close_user.png");
		    	}else{
		    		$(".dialog_selectuser_input_div img").prop("src","images/search_emp.png");
		    	}
		    	for(var i=0;i<userArray_temp.length;i++){
		    		var label=$("#label-user-"+i);
		    		if($("#label-user-"+i+" span").text().indexOf(searchword)!=-1){
		    			label.css("display","inline-block");
		    		}else{
		    			label.css("display","none");
		    		}
		    	}
		    }
		    function selectUser(i){
		    	if($("#label-user-"+i+" input").prop("checked")){
		    		userArray_temp[i][2]=true;
		    		selected_num++;
		    		$(".dialog_selected_title span").text("已选（"+selected_num+"）");
		    		$(".dialog_selected_name").append('<li id="li-user-'+i+'"><span title="'+userArray_temp[i][1]+'">'+userArray_temp[i][1]+'</span> <span title="移除" onclick="cancelSelectUser('+i+')">x</span></li>');
		    	}else{
		    		cancelSelectUser(i);
		    	}
		    }
		    //取消选中
		    function cancelSelectUser(i){
		    	userArray_temp[i][2]=false;
		    	selected_num--;
		    	$("#label-user-"+i).find("input").prop("checked",false);
		    	$("#li-user-"+i).remove();
		    	$(".dialog_selected_title span").text("已选（"+selected_num+"）");
		    }
		    //清空
		    function clear_selectedUser(){
		    	$(".dialog_selected_name").html("");
		    	for(var i=0;i<userArray_temp.length;i++){
		    		if(userArray_temp[i][2]){
		    			userArray_temp[i][2]=false;
		    			$("#label-user-"+i+" input").prop("checked",false);
		    		}
		    	}
		    	selected_num=0;
		    	$(".dialog_selected_title span").text("已选（0）");
		    }
			function publish(){
				if(document.adviseform.advise_title.value.replace(/\s/g,"").length==0){
					initdiglog2("提示信息", "标题不能为空！");
					return;
				}
				if(document.adviseform.advise_content.value.replace(/\s/g,"").length==0){
					initdiglog2("提示信息", "通知内容不能为空！");
					return;
				}
				if($(".advise_select_type_select").val()==2){
					var uidsTemp="";
					for(var i=0;i<userArray.length;i++){
						if(userArray[i][2]){
							uidsTemp+=userArray[i][0]+"-";
						}
					}
					if(uidsTemp.length>0){
						document.adviseform.UIDs.value=uidsTemp.substring(0,uidsTemp.length-1);
					}else{
						initdiglog2("提示信息", "请选择私信人！");
						return;
					}
				}
				document.adviseform.submit();
			}
			function changeType(){
				if($(".advise_select_type_select").val()==1){
					$(".username_add_a").css("display","none");
				}else{
					$(".username_add_a").css("display","block");
				}
			}
			function showUserDialog(){
				if($(".dialog_selectuser_bg").length==0){
					$("body").append('<div class="dialog_selectuser_bg"></div>');
				}
				$(".dialog_selectuser_bg").css("display","block");
				$(".dialog_selectuser").css("display","block");
				$(".dialog_selectuser_input_div img").prop("src","images/search_emp.png");
				$(".dialog_selectuser_input_div input").val("");
				initLabel();
			}
			function closeDialog(ifsave){
				if(ifsave){
					$(".username_add_a").text("选择私信人（"+selected_num+"）");
					userArray=copyArray(userArray_temp);
				}
				$(".dialog_selectuser_bg").css("display","none");
				$(".dialog_selectuser").css("display","none");
			}
			
		</script>
	</head>

	<body>
		<jsp:include page="/top.jsp" >
	<jsp:param name="name" value="<%=mUser.getName() %>" />
	<jsp:param name="level" value="<%=mUser.getLevel() %>" />
	<jsp:param name="index" value="4" />
	</jsp:include>
		<div class="div_center">
			<jsp:include page="/communicate/communicateTab.jsp">
			<jsp:param name="index" value="2" />
			</jsp:include>
			<div class="div_center_right">
				<form action="CommunicateServlet?type=addadvise&file_time=<%=System.currentTimeMillis() %>" method="post" name="adviseform">
					<input type="text" style="display:none"><!-- 防止只有一个input时，按回车自动提交 -->
					<div class="advise_border">
						<input type="text" class="advise_title_input" name="advise_title" placeholder="填写标题"  maxlength="100">
						<div class="div_dashed2"></div>
						<textarea class="advise_content_textarea" placeholder="内容"  maxlength="2000" name="advise_content" ></textarea>
						<div class="communicate_file_num">附件</div>
						<div class="communicate_file_publish_list">
						</div>
						<div class="advise_select_div" >
							<input type="hidden" name="UIDs">
							<div class="advise_select_type_div">类型：</div><select name="advise_type" class="advise_select_type_select" onchange="changeType()"><option value="1">公开</option><option value="2">私信</option></select>
							<a href="javascript:showUserDialog()" class="username_add_a">选择私信人（0）</a>
							<div class="add_file_div" onclick="$('#communicate_file_div .jFiler-input').click();">添加附件</div>
							<label class="anonymous_label"><input type="checkbox" name="anonymous">匿名</label>
						</div>
					</div>
					<div class="advise_btngroup_div">
						<div class="add_advise_div" onclick="publish()">发 布</div>
						<div class="cancel_advise_div" onclick="window.location.href='communicate/advise.jsp'">取 消</div>
					</div>
				</form>
			</div>
		</div>
		<div class="dialog_selectuser" id="dialog_selectuser">
			<div class="dialog_title">选择私信人<div class="dialog_selectuser_input_div"><input type="text" placeholder="搜索姓名" maxlength="5" oninput="searchLabel()"><img src="images/search_emp.png" onclick="clear_searchword()"></div><div class="dialog_close" onclick="closeDialog(false)"></div></div>
			<div class="dialog_canselectuser">
			
			</div>
			<div class="dialog_selected_title"><span>已选</span><a href="javascript:;" onclick="clear_selectedUser()">清空</a>
			</div>
			<ul class="dialog_selected_name">
					
			</ul>
			<div class="dialog_bottom">
				<div class="dialog_btn_sure" onclick="closeDialog(true)">确 定</div><div class="dialog_btn_cancle" onclick="closeDialog(false)">取 消</div>
			</div>
		</div>
	</body>
</html>