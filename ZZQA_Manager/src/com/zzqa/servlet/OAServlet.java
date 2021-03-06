package com.zzqa.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zzqa.pojo.deliver.Deliver;
import com.zzqa.pojo.deliver_content.Deliver_content;
import com.zzqa.pojo.departePuchase_content.DepartePuchase_content;
import com.zzqa.pojo.departmentPuchase.DepartmentPuchase;
import com.zzqa.pojo.flow.Flow;
import com.zzqa.pojo.operation.Operation;
import com.zzqa.pojo.performance.Performance;
import com.zzqa.pojo.performance_content.Performance_content;
import com.zzqa.pojo.position_user.Position_user;
import com.zzqa.pojo.user.User;
import com.zzqa.service.interfaces.deliver.DeliverManager;
import com.zzqa.service.interfaces.departmentPuchase.DepartPuchaseManager;
import com.zzqa.service.interfaces.file_path.File_pathManager;
import com.zzqa.service.interfaces.flow.FlowManager;
import com.zzqa.service.interfaces.operation.OperationManager;
import com.zzqa.service.interfaces.performance.PerformanceManager;
import com.zzqa.service.interfaces.permissions.PermissionsManager;
import com.zzqa.service.interfaces.position_user.Position_userManager;
import com.zzqa.service.interfaces.user.UserManager;
import com.zzqa.util.DataUtil;

public class OAServlet extends HttpServlet {
	private File_pathManager file_pathManager;
	private FlowManager flowManager;
	private OperationManager operationManager;
	private PerformanceManager performanceManager;
	private Position_userManager position_userManager;
	private UserManager userManager;
	private DeliverManager deliverManager;
	private PermissionsManager permissionsManager;
	private DepartPuchaseManager departPuchaseManager;
	private static final ReadWriteLock lock19 = new ReentrantReadWriteLock(false);
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setHeader("Access-Control-Allow-Origin", "*");
		resp.setCharacterEncoding("UTF-8");
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		String type = req.getParameter("type");
		HttpSession session=req.getSession();
		Object uidObject=session.getAttribute("uid");
		if(uidObject==null){
			resp.sendRedirect("login.jsp");
			return;
		}
		int uid=((Integer)uidObject);
		String sessionID=session.getId();
		String time_str=req.getParameter("file_time");
		long save_time=0l;
		if("savePerformanceFlow".equals(type)){
			int flag=1;//成功 
			try {
				lock19.writeLock().lock();
				String items_json=req.getParameter("items_json");
				String performance_json=req.getParameter("performance_json");
				String flow_json=req.getParameter("flow_json");
				String items_json2=req.getParameter("items_json2");
				String performance_json2=req.getParameter("performance_json2");
				String flow_json2=req.getParameter("flow_json2");
				long nowTime=System.currentTimeMillis();
				Operation operation=new Operation();
				operation.setCreate_time(nowTime);
				operation.setUid(uid);
				int op1=0;
				int create_id=0;
				if(items_json!=null&&performance_json!=null&&flow_json!=null){
					List<Performance_content> items = JSONArray.parseArray(items_json, Performance_content.class);
					Performance performance=JSONObject.parseObject(performance_json, Performance.class);
					Flow flow=JSONObject.parseObject(flow_json, Flow.class);
					int opera=flow.getOperation();
					if(flow.getUid()>0){
						create_id=flow.getUid();
					}
					op1=opera;
					int p_id=performance.getId();
					Performance performance_temp=null;
					List<User> leaders=null;
					if(p_id==0){
						performance_temp=performanceManager.getPerformanceByMonth(uid, performance.getPerformance_month());
						if(performance_temp!=null){
							flag=0;
							resp.setContentType("text/json");
					        resp.setCharacterEncoding("UTF-8");
					        PrintWriter out = resp.getWriter();  
					        out.print(flag);
					        return;
						}
								if(create_id>0){
									leaders=userManager.getParentListByChildUid(create_id);
								}else{
									leaders=userManager.getParentListByChildUid(uid);
								}
						if(leaders!=null&&leaders.size()>0){
							performance.setLeader(leaders.get(0).getId());
						}
						performance.setCreate_id(uid);
						performance.setCreate_time(nowTime);
						performanceManager.insertPerformance(performance);
						p_id=performance.getId();
					}else{
						performance_temp=performanceManager.getPerformanceByMonth(performance.getCreate_id(), performance.getPerformance_month());
						if(performance_temp!=null&&performance_temp.getOperation()!=performance.getOperation_old()){
							flag=0;
							resp.setContentType("text/json");
					        resp.setCharacterEncoding("UTF-8");
					        PrintWriter out = resp.getWriter();  
					        out.print(flag);
					        return;
						}
						if(create_id>0){
							leaders=userManager.getParentListByChildUid(create_id);
						}else{
							leaders=userManager.getParentListByChildUid(uid);
						}
						if(leaders!=null&&leaders.size()>0){
							performance.setLeader(leaders.get(0).getId());
						}
						performanceManager.delPerformance_content(p_id);
						performanceManager.updatePerformance(performance);
					}
					for (Performance_content performance_content:items) {
						performance_content.setP_id(p_id);
						performanceManager.insertPerformanceContent(performance_content);
					}
					flow.setFlowcode(performance.getCreate_id()+"_"+performance.getLeader()+"_");
					flow.setUid(uid);
					flow.setCreate_time(nowTime);
					flow.setType(19);
					flow.setForeign_id(p_id);
					flowManager.insertFlow(flow);
					operation.setContent(DataUtil.getFlowArray(19)[opera]+"id："+p_id);
					operationManager.insertOperation(operation);
				}
				if(items_json2!=null&&performance_json2!=null&&flow_json2!=null){
					List<Performance_content> items2= JSONArray.parseArray(items_json2, Performance_content.class);
					Performance performance2=JSONObject.parseObject(performance_json2, Performance.class);
					Flow flow2=JSONObject.parseObject(flow_json2, Flow.class);
					int opera=flow2.getOperation();
					int p_id=performance2.getId();
					Performance performance_temp=null;
					if(p_id==0){
						performance_temp=performanceManager.getPerformanceByMonth(performance2.getCreate_id(), performance2.getPerformance_month());
						if(performance_temp!=null){
							flag=0;
							resp.setContentType("text/json");
					        resp.setCharacterEncoding("UTF-8");
					        PrintWriter out = resp.getWriter();  
					        out.print(flag);
					        return;
						}
						List<User> leaders=userManager.getParentListByChildUid(uid);
						if(leaders!=null&&leaders.size()>0){
							performance2.setLeader(leaders.get(0).getId());
						}
						performance2.setCreate_id(uid);
						performance2.setCreate_time(nowTime);
						performanceManager.insertPerformance(performance2);
						p_id=performance2.getId();
					}else{
						List<User> leaders=null;
						performance_temp=performanceManager.getPerformanceByMonth(performance2.getCreate_id(), performance2.getPerformance_month());
						if(performance_temp!=null&&performance_temp.getOperation()!=performance2.getOperation_old()){
							flag=0;
							resp.setContentType("text/json");
					        resp.setCharacterEncoding("UTF-8");
					        PrintWriter out = resp.getWriter();  
					        out.print(flag);
					        return;
						}
						if(create_id>0){
							leaders=userManager.getParentListByChildUid(create_id);
						}else{
							leaders=userManager.getParentListByChildUid(uid);
						}
//						List<User> leaders=userManager.getParentListByChildUid(uid);
						if(leaders!=null&&leaders.size()>0){
							performance2.setLeader(leaders.get(0).getId());
						}
						performanceManager.delPerformance_content(p_id);
						performanceManager.updatePerformance(performance2);
					}
					for (Performance_content performance_content:items2) {
						performance_content.setP_id(p_id);
						performanceManager.insertPerformanceContent(performance_content);
					}
					/**
					 * 计划
					 */
					flow2.setFlowcode(performance2.getCreate_id()+"_"+performance2.getLeader()+"_"+op1+"_"+opera);
					flow2.setUid(uid);
					flow2.setCreate_time(nowTime);
					flow2.setType(19);
					flow2.setForeign_id(p_id);
					flowManager.insertFlow(flow2);
					operation.setContent(DataUtil.getFlowArray(19)[opera]+"id："+p_id);
					operationManager.insertOperation(operation);
				}
				flag=1;//添加成功
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				flag=0;//失败
			}
			finally{
				lock19.writeLock().unlock();
			}
			resp.setContentType("text/json");
	        resp.setCharacterEncoding("UTF-8");
	        PrintWriter out = resp.getWriter();  
	        out.print(flag);
		}else if("getPerformanceByMonth".equals(type)){
			long performance_month=Long.parseLong(req.getParameter("performance_month"));
			int performance_cid=Integer.parseInt(req.getParameter("performance_cid"));
			Performance performance=performanceManager.getPerformanceByMonth(performance_cid,performance_month);
			if(performance!=null){
				List<Performance_content> items=performanceManager.getPerformance_contentListByPID(performance.getId());
				performance.setItems(items);
				performance.setFlows(flowManager.getFlowListByCondition(19, performance.getId()));
				performance.setDepartment_name(DataUtil.getdepartment()[performance.getDepartment_index()]);
			}else{
				performance=performance;
			}
	        resp.setContentType("text/json"); 
	        resp.setCharacterEncoding("UTF-8"); 
	        PrintWriter out = resp.getWriter();  
	        out.print(JSON.toJSONString(performance));  
		}else if("getLastMoncePerformance".equals(type)){//普通员工升职为林总直属人员
			List<Performance> list=new ArrayList<Performance>();
			Performance performance=null;
			Performance performance1=null;
			int performance_cid=Integer.parseInt(req.getParameter("performance_cid"));
			long performance_month=Long.parseLong(req.getParameter("performance_month"));
			long lastmonth=Long.parseLong(req.getParameter("lastmonth"));
			int bossId = userManager.getUserListByPositionID(position_userManager.getBossParentID()).get(0).getId();
			Performance performance3=performanceManager.getPerformanceByMonth(performance_cid,lastmonth);
			performance3.setLeader(bossId);
			performanceManager.updatePerformance(performance3);
			flowManager.updateFlowOperationByFid(performance3.getId());
			List<Performance> performanceList=performanceManager.getLastMoncePerformance(performance_cid);
			if(performanceList!=null && performanceList.size()>0){
				for (int i=0;i< performanceList.size();i++) {
					performance=performanceList.get(i);
					List<Performance_content> items=performanceManager.getPerformance_contentListByPID(performance.getId());
					List<Flow> flowList = flowManager.getFlowListByCondition(19, performance.getId());
					performance.setItems(items);
					performance.setFlows(flowList);
					performance.setDepartment_name(DataUtil.getdepartment()[performance.getDepartment_index()]);
					if(i==0){
						long millis = System.currentTimeMillis();
						performance.setPerformance_month(performance_month);
						performance1=performance;
						performance1.setLeader(bossId);
						performance1.setPerformance_month(performance_month);
						performance1.setCreate_time(millis);
						performanceManager.insertPerformance(performance1);
						for (Performance_content performance_content : items) {
							performance_content.setP_id(performance1.getId());
							performance_content.setAssessor(userManager.getParentListByChildUid(uid).get(0).getTruename());
							performanceManager.insertPerformanceContent(performance_content);
						}
							Flow flow2 = flowList.get(flowList.size()-1);
							flow2.setType(-3);
							flow2.setCreate_time(millis);
							flow2.setForeign_id(performance1.getId());
							flowManager.insertFlow(flow2);
					}else{
						performance.setOperation(5);
					}
					list.add(performance);
				}
			}else{
				list=performanceList;
			}
	        resp.setContentType("text/json"); 
	        resp.setCharacterEncoding("UTF-8"); 
	        PrintWriter out = resp.getWriter();  
	        out.print(JSON.toJSONString(list)); 
		}else if("getPerformanceByID".equals(type)){
			/*int p_id=Integer.parseInt(req.getParameter("p_id"));
			Performance performance=performanceManager.getPerformanceByID(p_id);
			if(performance!=null){
				List<Performance_content> items=performanceManager.getPerformance_contentListByPID(performance.getId());
				performance.setItems(items);
			}else{
				performance=new Performance();
			}
	        resp.setContentType("text/json"); 
	        resp.setCharacterEncoding("UTF-8"); 
	        PrintWriter out = resp.getWriter();  
	        out.print(JSONObject.fromObject(performance));*/
		}else if("getPerformanceReport".equals(type)){
			Long begin_month=Long.parseLong(req.getParameter("begin_month"));
			Long end_month=Long.parseLong(req.getParameter("end_month"));
			int create_id=Integer.parseInt(req.getParameter("create_id"));
			int department_index=Integer.parseInt(req.getParameter("department_index"));
			Map map=new HashMap();
			map.put("begin_month", begin_month);
			map.put("end_month", end_month);
			map.put("create_id", create_id);
			map.put("department_index", department_index);
			List<Performance> list=performanceManager.getPerformancesByCondition(map);
			resp.setContentType("text/json"); 
	        resp.setCharacterEncoding("UTF-8"); 
	        PrintWriter out = resp.getWriter();  
	        out.print(JSON.toJSONString(list));
		}else if("saveDeliverFlow".equals(type)){
			int flag=0;//成功 
			try {
				lock19.writeLock().lock();
				String items_json=req.getParameter("items_json");
				String deliver_json=req.getParameter("deliver_json");
				String flow_json=req.getParameter("flow_json");
				String del_ids=req.getParameter("del_ids");
				long nowTime=System.currentTimeMillis();
				Operation operation=new Operation();
				operation.setCreate_time(nowTime);
				operation.setUid(uid);
				if(items_json!=null&&deliver_json!=null&&flow_json!=null){
					List<Deliver_content> items = JSONArray.parseArray(items_json, Deliver_content.class);
					Deliver deliver=JSONObject.parseObject(deliver_json, Deliver.class);
					Flow flow=JSONObject.parseObject(flow_json, Flow.class);
					int opera=flow.getOperation();
					int deliver_id=deliver.getId();
					if(deliver_id==0){
						deliver.setCreate_id(uid);
						deliver.setCreate_time(nowTime);
						deliverManager.insertDeliver(deliver);
						deliver_id=deliver.getId();
					}else{
						deliverManager.updateDeliver(deliver);
					}
					if(del_ids!=null&&del_ids.length()>0){
						String[] idsStr=del_ids.split("の");
						for (String id_str:idsStr) {
							deliverManager.delDeliverContent(Integer.parseInt(id_str));
						}
					}
					for (Deliver_content deliver_content:items) {
						deliver_content.setDeliver_id(deliver_id);
						if(deliver_content.getId()==0){
							deliverManager.insertDeliverContent(deliver_content);
						}else{
							deliverManager.updateDeliverContent(deliver_content);
						}
					}
					flow.setUid(uid);
					flow.setCreate_time(nowTime);
					flow.setType(20);
					flow.setForeign_id(deliver_id);
					flow.setId(deliver.getCreate_id());//方便直接读取create_id,防止未插入flow前查询带operation报错
					flowManager.insertFlow(flow);
					operation.setContent("出库单"+DataUtil.getFlowArray(20)[opera]+"id："+deliver_id);
					operationManager.insertOperation(operation);
					flag=deliver_id;//添加成功
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				flag=0;//失败
			}finally{
				lock19.writeLock().unlock();
			}
			resp.setContentType("text/json"); 
	        resp.setCharacterEncoding("UTF-8"); 
	        PrintWriter out = resp.getWriter();  
	        out.print(flag);  
		}else if("getDeliverByID".equals(type)){
			int deliver_id=Integer.parseInt(req.getParameter("deliver_id"));
			Deliver deliver=deliverManager.getDeliverByID(deliver_id);
			if(deliver==null){
				deliver=new Deliver();
			}else{
				deliver.setFlows(flowManager.getFlowListByCondition(20, deliver_id));
				deliver.setItems(deliverManager.getItemsByDid(deliver_id));
				List<User> parents=userManager.getParentListByChildUid(deliver.getCreate_id());
				boolean isLeader=(parents!=null&&parents.size()>0)?parents.get(0).getId()==uid:false;
				User mUser=userManager.getUserByID(uid);
				boolean isKeeper=mUser!=null&&permissionsManager.checkPermission(mUser.getPosition_id(), 149);
				deliver.setLeader(isLeader);
				deliver.setKeeper(isKeeper);
			}
	        resp.setContentType("text/json"); 
	        resp.setCharacterEncoding("UTF-8"); 
	        PrintWriter out = resp.getWriter();  
	        out.print(JSON.toJSONString(deliver));
		}else if("saveDepartePurchaseFlow1".equals(type)){
			int flag=0;//成功
			try {
				lock19.writeLock().lock();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String items_json=req.getParameter("items_json");
				String departPurcahse_json=req.getParameter("departPurcahse_json");
				String flow_json=req.getParameter("flow_json");
				String del_ids=req.getParameter("del_ids");
				long nowTime=System.currentTimeMillis();
				Operation operation=new Operation();
				operation.setCreate_time(nowTime);
				operation.setUid(uid);
				String delFileIDs=req.getParameter("delFileIDs");
				if(delFileIDs!=null&&delFileIDs.length()>0){
					StringTokenizer stringTokenizer=new StringTokenizer(delFileIDs, "の");
					while (stringTokenizer.hasMoreTokens()) {
						int fid=Integer.parseInt(stringTokenizer.nextToken());
						file_pathManager.delFileByID(fid);
					}
				}
				if(items_json!=null&&departPurcahse_json!=null&&flow_json!=null){
					List<DepartePuchase_content> items = JSONArray.parseArray(items_json, DepartePuchase_content.class);
					DepartmentPuchase departPuchase=JSONObject.parseObject(departPurcahse_json, DepartmentPuchase.class);
					Flow flow=JSONObject.parseObject(flow_json, Flow.class);
					int opera=flow.getOperation();
					int departPuchase_id=departPuchase.getId();
					if(departPuchase_id==0){
						departPuchase.setCreate_id(uid);
						departPuchase.setCreate_time(nowTime);
						departPuchaseManager.insertDepartPuchase(departPuchase);
						departPuchase_id=departPuchase.getId();
					}else{
//						departPuchaseManager.updateDepartPuchase(departPuchase);
					}
					file_pathManager.saveFile(uid, sessionID, 7, departPuchase_id, 8, 0, save_time);
					file_pathManager.saveFile(uid, sessionID, 7, departPuchase_id, 7, 0, save_time);
					if(del_ids!=null&&del_ids.length()>0){
						String[] idsStr=del_ids.split("の");
						for (String id_str:idsStr) {
							departPuchaseManager.delDepartPuchaseContent(Integer.parseInt(id_str));
						}
					}
					for (DepartePuchase_content departePuchase_content:items) {
						String predict_date = departePuchase_content.getPredict_date();
						String aog_date = departePuchase_content.getAog_date();
						if(predict_date==null || predict_date.equals("")){
							departePuchase_content.setPredict_time(0);
						}else {
							departePuchase_content.setPredict_time(sdf.parse(predict_date).getTime());
						}
						if(aog_date==null || aog_date.equals("")){
							departePuchase_content.setAog_time(0);
						}else {
							departePuchase_content.setAog_time(sdf.parse(aog_date).getTime());
						}
						departePuchase_content.setDepartePuchase_id(departPuchase_id);
						if(departePuchase_content.getId()==0){
							departPuchaseManager.insertDepartPuchaseContent(departePuchase_content);
						}else{
							departPuchaseManager.updateDepartPuchaseContent(departePuchase_content);
						}
					}
					flow.setUid(uid);
					flow.setCreate_time(nowTime);
					flow.setType(22);
					flow.setForeign_id(departPuchase_id);
					flow.setId(departPuchase.getCreate_id());//方便直接读取create_id,防止未插入flow前查询带operation报错
					flowManager.insertFlow(flow);
					operation.setContent("其他部门采购单"+DataUtil.getFlowArray(22)[opera]+"id："+departPuchase_id);
					operationManager.insertOperation(operation);
					flag=departPuchase_id;//添加成功
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				flag=0;//失败
			}finally{
				lock19.writeLock().unlock();
			}
			resp.setContentType("text/json");
			resp.setCharacterEncoding("UTF-8");
			PrintWriter out = resp.getWriter();
			out.print(flag);
		}else if("saveDepartePurchaseFlow".equals(type)){
			int flag=0;//成功 
			try {
				lock19.writeLock().lock();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String items_json=req.getParameter("items_json");
				String departPurcahse_json=req.getParameter("departPurcahse_json");
				String flow_json=req.getParameter("flow_json");
				String del_ids=req.getParameter("del_ids");
				long nowTime=System.currentTimeMillis();
				Operation operation=new Operation();
				operation.setCreate_time(nowTime);
				operation.setUid(uid);
				String delFileIDs=req.getParameter("delFileIDs");
				if(delFileIDs!=null&&delFileIDs.length()>0){
					StringTokenizer stringTokenizer=new StringTokenizer(delFileIDs, "の");
					while (stringTokenizer.hasMoreTokens()) {
						int fid=Integer.parseInt(stringTokenizer.nextToken());
						file_pathManager.delFileByID(fid);
					}
				}
				if(items_json!=null&&departPurcahse_json!=null&&flow_json!=null){
					List<DepartePuchase_content> items = JSONArray.parseArray(items_json, DepartePuchase_content.class);
					DepartmentPuchase departPuchase=JSONObject.parseObject(departPurcahse_json, DepartmentPuchase.class);
					Flow flow=JSONObject.parseObject(flow_json, Flow.class);
					int opera=flow.getOperation();
					int departPuchase_id=departPuchase.getId();
					if(departPuchase_id==0){
						departPuchase.setCreate_id(uid);
						departPuchase.setCreate_time(nowTime);
						departPuchaseManager.insertDepartPuchase(departPuchase);
						departPuchase_id=departPuchase.getId();
					}else{
//						departPuchaseManager.updateDepartPuchase(departPuchase);
					}
					file_pathManager.saveFile(uid, sessionID, 7, departPuchase_id, 8, 0, save_time);
					file_pathManager.saveFile(uid, sessionID, 7, departPuchase_id, 7, 0, save_time);
					if(del_ids!=null&&del_ids.length()>0){
						String[] idsStr=del_ids.split("の");
						for (String id_str:idsStr) {
							departPuchaseManager.delDepartPuchaseContent(Integer.parseInt(id_str));
						}
					}
					for (DepartePuchase_content departePuchase_content:items) {
						String predict_date = departePuchase_content.getPredict_date();
						String aog_date = departePuchase_content.getAog_date();
						if(predict_date==null || predict_date.equals("")){
							departePuchase_content.setPredict_time(0);
						}else {
							departePuchase_content.setPredict_time(sdf.parse(predict_date).getTime());
						}
						if(aog_date==null || aog_date.equals("")){
							departePuchase_content.setAog_time(0);
						}else {
							departePuchase_content.setAog_time(sdf.parse(aog_date).getTime());
						}
						departePuchase_content.setDepartePuchase_id(departPuchase_id);
						if(departePuchase_content.getId()==0){
							departPuchaseManager.insertDepartPuchaseContent(departePuchase_content);
						}else{
							departPuchaseManager.updateDepartPuchaseContent(departePuchase_content);
						}
					}
					flow.setUid(uid);
					flow.setCreate_time(nowTime);
					flow.setType(22);
					flow.setForeign_id(departPuchase_id);
					flow.setId(departPuchase.getCreate_id());//方便直接读取create_id,防止未插入flow前查询带operation报错
					flowManager.insertFlow(flow);
					operation.setContent("其他部门采购单"+DataUtil.getFlowArray(22)[opera]+"id："+departPuchase_id);
					operationManager.insertOperation(operation);
					flag=departPuchase_id;//添加成功
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				flag=0;//失败
			}finally{
				lock19.writeLock().unlock();
			}
			resp.setContentType("text/json"); 
	        resp.setCharacterEncoding("UTF-8"); 
	        PrintWriter out = resp.getWriter();  
	        out.print(flag);  
		}else if("saveDepartePurchaseTime".equals(type)){
			int flag=0;//成功 
			try {
				lock19.writeLock().lock();
				String items_json=req.getParameter("items_json");
				String parameter = req.getParameter("departPuchase_id");
				String oper = req.getParameter("operation");
				if(parameter!=null && oper != null){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					int departPuchase_id=Integer.parseInt(parameter);
					int opera=Integer.parseInt(oper);
					long nowTime=System.currentTimeMillis();
					Operation operation=new Operation();
					operation.setCreate_time(nowTime);
					operation.setUid(uid);
					if(items_json!=null){
						List<DepartePuchase_content> items = JSONArray.parseArray(items_json, DepartePuchase_content.class);
						for (DepartePuchase_content departePuchase_content:items) {
							String predict_date = departePuchase_content.getPredict_date();
							String aog_date = departePuchase_content.getAog_date();
							if(predict_date==null || predict_date.equals("")){
								departePuchase_content.setPredict_time(0);
							}else {
								departePuchase_content.setPredict_time(sdf.parse(predict_date).getTime());
							}
							if(aog_date==null || aog_date.equals("")){
								departePuchase_content.setAog_time(0);
							}else {
								departePuchase_content.setAog_time(sdf.parse(aog_date).getTime());
							}
							departePuchase_content.setDepartePuchase_id(departPuchase_id);
							departPuchaseManager.updateDepartPuchaseContentTime(departePuchase_content);
						}
						if(opera==9){
							operation.setContent("其他部门采购单,验收保存到货时间id："+departPuchase_id);
						}else {
							operation.setContent("其他部门采购单,采购保存预计到货时间id："+departPuchase_id);
						}
						operationManager.insertOperation(operation);
						flag=departPuchase_id;//添加成功
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				flag=0;//失败
			}finally{
				lock19.writeLock().unlock();
			}
			resp.setContentType("text/json"); 
	        resp.setCharacterEncoding("UTF-8"); 
	        PrintWriter out = resp.getWriter();  
	        out.print(flag);  
		}else if("getDepartPuchaseByID".equals(type)){
			int departPuchase_id=Integer.parseInt(req.getParameter("departPuchase_id"));
			DepartmentPuchase departPuchase=departPuchaseManager.getDepartPuchaseByID(departPuchase_id);
			if(departPuchase==null){
				departPuchase=new DepartmentPuchase();
			}else{
				departPuchase.setFlows(flowManager.getFlowListByCondition(22, departPuchase_id));
				departPuchase.setItems(departPuchaseManager.getItemsByDid(departPuchase_id));
				boolean flags=false;
				List<User> parents=userManager.getParentListByChildUid(departPuchase.getCreate_id());
				for (User user : parents) {
					flags=user.getId()==uid;
					if(flags){
						break;
					}
				}
				boolean isLeader=(parents!=null&&parents.size()>0)?flags:false;
				User mUser=userManager.getUserByID(uid);
				boolean isKeeper=mUser!=null&&permissionsManager.checkPermission(mUser.getPosition_id(),17);
//				boolean isBuyer=mUser!=null&&permissionsManager.checkPermission(mUser.getPosition_id(),21);
				boolean isBuyer=mUser!=null&&permissionsManager.checkPermission(mUser.getPosition_id(),181);
				boolean isChecker=mUser!=null&&permissionsManager.checkPermission(mUser.getPosition_id(), 22);
				departPuchase.setLeader(isLeader);
				departPuchase.setKeeper(isKeeper);
				departPuchase.setBuyer(isBuyer);
				departPuchase.setChecker(isChecker);
			}
	        resp.setContentType("text/json"); 
	        resp.setCharacterEncoding("UTF-8"); 
	        PrintWriter out = resp.getWriter();  
	        out.print(JSON.toJSONString(departPuchase));
		}
		
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		String type = req.getParameter("type");
		HttpSession session=req.getSession();
		Object uidObject=session.getAttribute("uid");
		if(uidObject==null){
			resp.sendRedirect("login.jsp");
			return;
		}
		String sessionID=session.getId();
		int uid=((Integer)uidObject);
	}

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		file_pathManager = (File_pathManager) WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"file_pathManager");
		flowManager = (FlowManager) WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"flowManager");
		operationManager = (OperationManager) WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"operationManager");
		performanceManager = (PerformanceManager) WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"performanceManager");
		position_userManager=(Position_userManager) WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"position_userManager");
		userManager = (UserManager) WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"userManager");
		deliverManager = (DeliverManager) WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"deliverManager");
		permissionsManager = (PermissionsManager) WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"permissionsManager");
		departPuchaseManager = (DepartPuchaseManager) WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"departPuchaseManager");
	}
}
