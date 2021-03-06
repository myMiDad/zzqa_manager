<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@page
	import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="com.zzqa.service.interfaces.user.UserManager"%>
<%@page import="com.zzqa.pojo.user.User"%>
<%@page import="com.zzqa.service.interfaces.position_user.Position_userManager"%>
<%@page import="com.zzqa.pojo.position_user.Position_user"%>
<%@page import="com.zzqa.util.DataUtil"%>
<%request.setCharacterEncoding("UTF-8");
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	UserManager userManager = (UserManager) WebApplicationContextUtils
			.getRequiredWebApplicationContext(getServletContext())
			.getBean("userManager");
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
	if (session.getAttribute("user_id") == null) {
		response.sendRedirect("login.jsp");
		return;
	}
	int user_id=(Integer)session.getAttribute("user_id");
	User user = userManager.getUserByID(user_id);
	if (user == null) {
		response.sendRedirect("login.jsp");
		return;
	}
	List<Position_user> posiList=position_userManager.getPositionOrderByparent();
	pageContext.setAttribute("posiList", posiList);
	String[] flowArray=DataUtil.getFlowTypeArray();
	pageContext.setAttribute("flowArray", flowArray);
	pageContext.setAttribute("flowLen", flowArray.length);
	pageContext.setAttribute("user", user);
%>

<!DOCTYPE HTML>
<html>
	<head>
		<base href="<%=basePath%>">
		<title>修改用户</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		<link rel="stylesheet" type="text/css" href="css/top.css">
		<link rel="stylesheet" type="text/css" href="css/jquery.searchableSelect.css">
		<link rel="stylesheet" type="text/css" href="css/usermanager.css">
		<link rel="stylesheet" type="text/css" href="css/jquery-ui.min.css">
		<script type="text/javascript" src="js/jquery.min.js"></script>
		<script type="text/javascript" src="js/jquery-ui.min.js"></script>
		<script type="text/javascript" src="js/dialog.js"></script>
		<script type="text/javascript" src="js/public.js"></script>
		
		<script type="text/javascript" src="js/jquery.searchableSelect.js"></script>
		<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
		<script language="javascript">
		var m=0;//表示用户名验证通过
		$(function(){
			var sendEmial="${user.sendEmail}";
			if(sendEmial=="null"||sendEmial==""){
				$(".sendMail_div input").prop("checked",true);
			}else{
				var array=sendEmial.split(";");
				var len=array.length;
				$(".sendMail_div input").each(function(i){
					if(len>i&&array[i]==1){
						$(this).prop("checked",false);
					}else{
						$(this).prop("checked",true);
					}
				});
			}
			$("#select_position").searchableSelect();
			//修改 指定选中的项
			$("#select_position").next(".searchable-select").find(".searchable-select-item[data-value="+<%=user.getPosition_id()%>+"]").click();
			$("#select_position").next(".searchable-select").css("width","224px");
		}); 
		function updateUser(){
			var name=document.userform.username.value;
		   	if(name.length>0){
		   		$("#name_error").text("");
				$.ajax({
					type:"post",//post方法
					url:"UserManagerServlet",
					data:{"type":"CheckName","username":name,"user_id":<%=user_id%>},
					//ajax成功的回调函数
					success:function(returnData){
						m=returnData;
						if(m==1){
							$("#name_error").text("用户名已存在");
						}
						updateUser2();
					}
				});
			}else{
				$("#name_error").text("请输入用户名");
			}
		}
		function updateUser2(){
			var k=0;
			if(m==1){
				k++;
			}
	   		if(document.userform.truename.value.replace(/[ ]/g,"").length<1){
	   			$("#truename_error").text("请输入姓名");
		   		k++;
	   		}else{
	   			$("#truename_error").text("");
	   		}
	   		var email=document.userform.email.value;
	   		if(email.length<1){
	   			$("#email_error").text("请输入邮箱");
		   		k++;
	   		}else{
	   			if(testEmail(email)){
	   				$("#email_error").text("");
	   			}else{
	   				$("#email_error").text("邮箱格式不正确");
	   				k++;
	   			}
	   		}
	   		var parent_id=$("#select_position").next(".searchable-select").find(".searchable-select-item.selected").attr("data-value");
	   		if (parent_id<1){
		   		$("#position_error").text("请选择职位");
		   		k++;
	   		}else{
	   			document.userform.position.value=parent_id;
	   			$("#position_error").text("");
	   			if(m!=0&&m!=1){
		   			var positionID=String(m).split("の")[0];
			   		if(positionID==parent_id){
			   			initdiglog2("信息提示", "只允许将一个用户设为最高职位");
			   			return;
			   		}
		   		}
	   		}
	   		var sendEmail='';
	   		$(".sendMail_div label").each(function(i){
	   			if(i>0){
	   				sendEmail+=";";
	   			}
	   			sendEmail+=$(this).find("input").prop("checked")?0:1;
	   		});
	   		document.userform.sendEmail.value=sendEmail;
			 if(k==0){
			 	document.userform.submit();
			 }
   		}
		document.onkeydown = function(e){
	        if(!e) e = window.event;
	        if((e.keyCode || e.which) == 13){
	        	if($( "#twobtndialog" ).length>0){
	        		$( "#confirm2" ).click();
	        	}
	        }
	    };
	</script>
	</head>

	<body>
		<jsp:include page="/top.jsp" >
	<jsp:param name="name" value="<%=mUser.getName() %>" />
	<jsp:param name="level" value="<%=mUser.getLevel() %>" />
	<jsp:param name="index" value="3" />
	</jsp:include>
		<div class="div_center">
			<jsp:include page="/usermanager/userTab.jsp">
			<jsp:param name="index" value="0" />
			</jsp:include>
			<div class="div_center_right">
				<form action="UserManagerServlet?type=update&user_id=<%=user_id %>" method="post"
					name="userform">
					<input type="hidden" name="sendEmail" >
					<input type="hidden" name="position" >
					<div class="td2_div2">
						<table class="tab_createuser">
							<tr>
								<td class="tab_createuser_td1">
									<span class="star">*</span>用户名：
								</td>
								<td class="tab_createuser_td2">
									<input type="text" name="username" maxlength="25" value="<%=user.getName()%>" onkeydown="if(event.keyCode==32) return false;">
									<span id="name_error"></span>
								</td>
							</tr>
							<tr>
								<td class="tab_createuser_td1">
									密码：
								</td>
								<td class="tab_createuser_td2">
									<div class="changepass" onclick="updatePassword(true)" style="width:75px;">修改密码</div>
								</td>
							</tr>
							<tr>
								<td class="tab_createuser_td1">
									<span class="star">*</span>邮箱：
								</td>
								<td class="tab_createuser_td2">
									<input type="email" name="email" maxlength="30" value="<%=user.getEmail()%>"
									onkeydown="if(event.keyCode==32) return false">
									<span id="email_error"></span>
								</td>
							</tr>
							<tr>
								<td class="tab_createuser_td1">
									<span class="star">*</span>姓名：
								</td>
								<td class="tab_createuser_td2">
									<input type="text" name="truename" maxlength="10" value="<%=user.getTruename()%>"
									onkeydown="if(event.keyCode==32) return false">
									<span id="truename_error"></span>
								</td>
							</tr>
							<tr>
								<td class="tab_createuser_td1">
									<span class="star">*</span>所属职位：
								</td>
								<td class="tab_createuser_td2">
								<select id="select_position">
									<option value="0">请选择职位</option>
									<c:forEach items="${posiList}" var="position_user">
										<option value="<c:out value='${position_user.id}'></c:out>" <c:if test="${position_user.id==user.position_id}">selected</c:if> >			
											<c:out value="${position_user.position_name}"></c:out>
										</option>
									</c:forEach>
								</select>
								<span id="position_error"></span>
								</td>
							</tr>
							<tr>
								<td class="tab_createuser_td1">
									邮件提醒：
								</td>
								<td class="tab_createuser_td2">
									<c:forEach begin="1"  end="${flowLen}"  step="3"  varStatus="flow_status">
								<div class="sendMail_div">
									<c:if test="${flow_status.index<flowLen}">
									<label title="${flowArray[flow_status.index]}">
										<input name="flowType" type="checkbox">
										<c:out value="${flowArray[flow_status.index]}"></c:out>
									</label>
									</c:if>
									<c:if test="${(flow_status.index+1)<flowLen}">
									<label title="${flowArray[flow_status.index+1]}">
										<input name="flowType" type="checkbox">
										<c:out value="${flowArray[flow_status.index+1]}"></c:out>
									</label>
									</c:if>
									<c:if test="${(flow_status.index+2)<flowLen}">
									<label title="${flowArray[flow_status.index+2]}">
										<input name="flowType" type="checkbox">
										<c:out value="${flowArray[flow_status.index+2]}"></c:out>
									</label>
									</c:if>
									</div>
								</c:forEach>
								</td>
							</tr>
						</table>
					</div>
					<div class="div_btn">
						<img src="images/alter_flow.png" class="btn_agree"
							onclick="updateUser();">
					</div>
				</form>
			</div>
		</div>
	</body>
</html>
