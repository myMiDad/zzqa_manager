<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@page
	import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="com.zzqa.service.interfaces.user.UserManager"%>
<%@page import="com.zzqa.pojo.user.User"%>
<%@page import="com.zzqa.util.DataUtil"%>
<%
	request.setCharacterEncoding("UTF-8");
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	UserManager userManager = (UserManager) WebApplicationContextUtils
			.getRequiredWebApplicationContext(getServletContext())
			.getBean("userManager");
	if (session.getAttribute("uid") == null) {
		request.getRequestDispatcher("/login.jsp").forward(request,
				response);
		return;
	}
	int uid = (Integer) session.getAttribute("uid");
	User mUser = userManager.getUserByID(uid);
	if (mUser == null) {
		request.getRequestDispatcher("/login.jsp").forward(request,
				response);
		return;
	}
	String[] departmentArray = DataUtil.getdepartment();
	String[] cost_attributable=DataUtil.getCost_attributable();
	pageContext.setAttribute("departmentArray", departmentArray);
	pageContext.setAttribute("cost_attributable", cost_attributable);
%>

<!DOCTYPE html>
<html>
<head>
<base href="<%=basePath%>">

<title>新建用车申请流程</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<link rel="stylesheet" type="text/css" href="css/top.css">
<link rel="stylesheet" type="text/css" href="css/flowmanager.css">
<link rel="stylesheet" type="text/css" href="css/vehicle.css">
<link rel="stylesheet" type="text/css" href="css/jquery-ui.min.css">
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript" src="js/jquery-ui.min.js"></script>
<script type="text/javascript" src="js/showdate2.js"></script>
<script type="text/javascript" src="js/public.js"></script>
<script type="text/javascript" src="js/dialog.js"></script>
<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script type="text/javascript">
		$(function(){
			var temp='';
			for(var i=0;i<24;i++){
				temp+='<option value="'+i+'">'+i+'时</option>';
			}
			$("#starthour").html(temp);
			$("#endhour").html(temp);
			var today=timeTransLongToStr(0, 4, "/",false);
			$("#startdate").val(today);
			$("#enddate").val(today);
			$("#starthour").val(0);
			$("#endhour").val(23);
		});
		function addFlow(){
			var department=$("#department").val();
			if(department==0){
				initdiglog2("提示信息","请选择部门");
				return;
			}
			var initial_address=$("#initial_address").val();
			if(initial_address.trim().length==0){
				initdiglog2("提示信息", "请输入起始地");
				return;
			}
			var address=$("#address").val();
			if(address.trim().length==0){
				initdiglog2("提示信息", "请输入目的地");
				return;
			}
			var vehicle_person=$("#vehicle_person").val();
			if(vehicle_person.trim().length==0){
				initdiglog2("提示信息", "请输入乘用人员");
				return;
			}
			var starttime=timeTransStrToLong2($("#startdate").val()+" "+$("#starthour").val()+":0:0");
			var endtime=timeTransStrToLong2($("#enddate").val()+" "+$("#endhour").val()+":0:0");
			if(!(endtime>starttime)){
				initdiglog2("提示信息", "结束时间必须晚于开始时间");
				return;
			}
			$("#starttime").val(starttime);
			$("#endtime").val(endtime);
			var vehicle_reason=$("#vehicle_reason").val();
			if(vehicle_reason.trim().length==0){
				initdiglog2("提示信息", "请输入使用事由");
				return;
			}
			document.flowform.submit();
		}
	</script>
</head>

<body>
	<jsp:include page="/top.jsp">
		<jsp:param name="name" value="<%=mUser.getName()%>" />
		<jsp:param name="level" value="<%=mUser.getLevel()%>" />
		<jsp:param name="index" value="1" />
	</jsp:include>
	<div class="div_center">
		<table class="table_center">
			<tr>
				<jsp:include page="/flowmanager/flowTab.jsp">
					<jsp:param name="index" value="3" />
				</jsp:include>
				<td class="table_center_td2_notfull">
					<div class="td2_div2">用车申请表</div>
					<form
							action="NewFlowServlet" method="post" name="flowform">
							<input type="text"  name="hide"  style="display:none"/><!-- 防止按回车直接上传 -->
							<input type="hidden"  name="type" value="addvehicleflow"/>
					<table class="td2_table">
						<tr class="table_tr1">
							<td class="table_tr1_td1">用车部门</td>
							<td class="table_tr1_td2">
								<select name="department" id="department">
									<c:forEach items="${departmentArray}" var="department" varStatus="d_status">
									<c:if test="${d_status.index!=3}">
									<option value="<c:out value="${d_status.index}"></c:out>"><c:out value="${department}"></c:out></option>
									</c:if>
									</c:forEach>
								</select>
							</td>
							<td class="table_tr1_td3">起始地</td>
							<td class="table_tr1_td4">
								<input type="text" name="initial_address" id="initial_address" maxlength="100" placeholder="请输入起始地">
							</td>
							<td class="table_tr1_td3">目的地</td>
							<td class="table_tr1_td4">
								<input type="text" name="address" id="address" maxlength="100" placeholder="请输入目的地">
							</td>
						</tr>
						<tr class="table_tr4">
							<td class="table_tr4_td1">乘用人员</td>
							<td class="table_tr4_td2"  colspan="5">
								<input  id="vehicle_person" name="vehicle_person" maxlength="100" placeholder="请输入乘用人员">
							</td>
						</tr>
						<tr class="table_tr2">
							<td class="table_tr2_td1">用车时间</td>
							<td class="table_tr2_td2" colspan="5">
								<input type="hidden" name="starttime" id="starttime">
								<input type="hidden" name="endtime" id="endtime">
								<input type="text" id="startdate" class="input-show-time" readonly="" onclick="return Calendar('startdate');">
								<select id="starthour">
								</select>
								<div>至</div>
								<input type="text" id="enddate" class="input-show-time" readonly="" onclick="return Calendar('enddate');">
								<select id="endhour">
								</select>
							</td>
						</tr>
						<tr class="table_tr3">
							<td class="table_tr3_td1">使用事由</td>
							<td class="table_tr3_td2"  colspan="5">
								<textarea  id="vehicle_reason" name="vehicle_reason" maxlength="500" placeholder="请输入使用事由"></textarea>
							</td>
						</tr>
					</table>
					<div class="btn_group">
						<div class="div_agree" onclick="addFlow();">提交</div>
						<div class="div_cancel" onclick="window.location.href='<%=basePath %>flowmanager/newflow.jsp'">取消</div>
					</div>
					</form>
				</td>
			</tr>
		</table>

	</div>
</body>
</html>
