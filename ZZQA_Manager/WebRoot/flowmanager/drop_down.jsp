<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page
	import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@ page import="com.zzqa.pojo.task.Task"%>
<%@page
	import="com.zzqa.service.interfaces.task.TaskManager"%>
<% 
TaskManager taskManager = (TaskManager) WebApplicationContextUtils
	.getRequiredWebApplicationContext(getServletContext())
	.getBean("taskManager");
List<Task> taskList = taskManager.getFinishTaskList();
%>
<div id="bts-ex-4" class="selectpicker" data-live="true">
	<button data-id="prov" type="button" class="btn btn-lg btn-block btn-default dropdown-toggle">
		<span class="placeholder"
			style="font: 13px/ 17px 'SimSun';">选择任务单</span>
		<span class="caret"></span>
	</button>
	<div class="dropdown-menu">
		<div class="live-filtering" data-clear="true" data-autocomplete="true" data-keys="true">
			<label class="sr-only" for="input-bts-ex-4">在列表中查找</label>
			<div class="search-box">
				<div class="input-group">
					<span class="input-group-addon" id="search-icon3">
					<span class="fa fa-search"></span>
					<a href="#0" class="fa fa-times hide filter-clear"><span class="sr-only">清空过滤</span></a>
					</span>
					<input type="text" placeholder="在列表中搜索" id="input-bts-ex-4" class="form-control live-search" aria-describedby="search-icon3" tabindex="1" style="font:13px/20px 'SimSun';"/>
				</div>
			</div>
			<div class="list-to-filter">
				<ul class="list-unstyled">
				<%for(Task task:taskList){ 
				String pname=task.getProject_name();
				String filter_name=pname+"---项目编号："+task.getProject_id()+"---任务单编号："+task.getId();%>
				<li class="filter-item items" data-filter="<%=filter_name%>" data-value="<%=task.getId()%>"><%=filter_name%></li>
					<%} %>
				</ul>
				<div class="no-search-results">
					<div class="alert alert-warning" role="alert"><i class="fa fa-warning margin-right-sm"></i>找不到<strong>'<span></span>'</strong> </div>
				</div>
			</div>
		</div>
	</div>
	<input type="hidden" name="bts-ex-4" id="task_id_input" value="">
</div>