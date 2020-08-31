package com.zzqa.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.zzqa.pojo.device.Device;
import com.zzqa.pojo.file_path.File_path;
import com.zzqa.pojo.flow.Flow;
import com.zzqa.pojo.leave.Leave;
import com.zzqa.pojo.linkman.Linkman;
import com.zzqa.pojo.manufacture.Manufacture;
import com.zzqa.pojo.material.Material;
import com.zzqa.pojo.operation.Operation;
import com.zzqa.pojo.outsource_product.Outsource_product;
import com.zzqa.pojo.performance.Performance;
import com.zzqa.pojo.procurement.Procurement;
import com.zzqa.pojo.product_procurement.Product_procurement;
import com.zzqa.pojo.project_procurement.Project_procurement;
import com.zzqa.pojo.resumption.Resumption;
import com.zzqa.pojo.shipments.Shipments;
import com.zzqa.pojo.task.Task;
import com.zzqa.pojo.task_conflict.Task_conflict;
import com.zzqa.pojo.task_updateflow.Task_updateflow;
import com.zzqa.pojo.track.Track;
import com.zzqa.pojo.travel.Travel;
import com.zzqa.pojo.user.User;
import com.zzqa.service.interfaces.device.DeviceManager;
import com.zzqa.service.interfaces.equipment.EquipmentManager;
import com.zzqa.service.interfaces.file_path.File_pathManager;
import com.zzqa.service.interfaces.flow.FlowManager;
import com.zzqa.service.interfaces.leave.LeaveManager;
import com.zzqa.service.interfaces.linkman.LinkmanManager;
import com.zzqa.service.interfaces.manufacture.ManufactureManager;
import com.zzqa.service.interfaces.material.MaterialManager;
import com.zzqa.service.interfaces.operation.OperationManager;
import com.zzqa.service.interfaces.outsource_product.Outsource_productManager;
import com.zzqa.service.interfaces.performance.PerformanceManager;
import com.zzqa.service.interfaces.procurement.ProcurementManager;
import com.zzqa.service.interfaces.product_procurement.Product_procurementManager;
import com.zzqa.service.interfaces.project_procurement.Project_procurementManager;
import com.zzqa.service.interfaces.resumption.ResumptionManager;
import com.zzqa.service.interfaces.shipments.ShipmentsManager;
import com.zzqa.service.interfaces.task.TaskManager;
import com.zzqa.service.interfaces.task_conflict.Task_conflictManager;
import com.zzqa.service.interfaces.task_updateflow.Task_updateflowManager;
import com.zzqa.service.interfaces.track.TrackManager;
import com.zzqa.service.interfaces.travel.TravelManager;
import com.zzqa.service.interfaces.user.UserManager;
import com.zzqa.util.DataUtil;
import com.zzqa.util.FileUploadUtil;
import com.zzqa.util.FormTransform;

public class FlowManagerServlet extends HttpServlet {
	/**
	 *
	 */

	private static final long serialVersionUID = 1L;
	private Task_conflictManager task_conflictManager;
	private TaskManager taskManager;
	private File_pathManager file_pathManager;
	private LinkmanManager linkmanManager;
	private FlowManager flowManager;
	private Product_procurementManager product_procurementManager;
	private ProcurementManager procurementManager;
	private Project_procurementManager project_procurementManager;
	private Outsource_productManager outsource_productManager;
	private ManufactureManager manufactureManager;
	private MaterialManager materialManager;
	private DeviceManager deviceManager;
	private ShipmentsManager shipmentsManager;
	private OperationManager operationManager;
	private UserManager userManager;
	private TravelManager travelManager;
	private LeaveManager leaveManager;
	private ResumptionManager resumptionManager;
	private TrackManager trackManager;
	private EquipmentManager equipmentManager;
	private PerformanceManager performanceManager;
	private Task_updateflowManager task_updateflowManager;
	private static final ReadWriteLock lock1 = new ReentrantReadWriteLock(false);
	private static final ReadWriteLock lock2 = new ReentrantReadWriteLock(false);
	private static final ReadWriteLock lock3 = new ReentrantReadWriteLock(false);
	private static final ReadWriteLock lock4 = new ReentrantReadWriteLock(false);
	private static final ReadWriteLock lock5 = new ReentrantReadWriteLock(false);
	private static final ReadWriteLock lock6 = new ReentrantReadWriteLock(false);
	private static final ReadWriteLock lock7 = new ReentrantReadWriteLock(false);
	private static final ReadWriteLock lock8 = new ReentrantReadWriteLock(false);
	private static final ReadWriteLock lock9 = new ReentrantReadWriteLock(false);
	private static final ReadWriteLock lock10 = new ReentrantReadWriteLock(false);
	private static final ReadWriteLock lock11 = new ReentrantReadWriteLock(false);

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		String type = req.getParameter("type");
		HttpSession session = req.getSession();
		Object uidObject = session.getAttribute("uid");
		if (uidObject == null) {
			resp.sendRedirect("login.jsp");
			return;
		}
		String sessionID = session.getId();
		int uid = ((Integer) uidObject);
		String time_str = req.getParameter("file_time");
		long save_time = 0l;
		if (time_str != null) {
			try {
				save_time = Long.parseLong(time_str);
			} catch (Exception e) {
				// TODO: handle exception
				save_time = 0l;
			}
		}
		if ("allfilter".equals(type)) {
			String newtime_flows = req.getParameter("newtime_flows");
			if ("on".equals(newtime_flows)) {
				session.setAttribute("newtime_flows", 1);
				session.setAttribute("starttime1_flows",
						req.getParameter("starttime1_flows"));
				session.setAttribute("endtime1_flows",
						req.getParameter("endtime1_flows"));
			} else {
				session.setAttribute("newtime_flows", 0);
			}
			String nowtime_flows = req.getParameter("nowtime_flows");
			if ("on".equals(nowtime_flows)) {
				session.setAttribute("nowtime_flows", 1);
				session.setAttribute("starttime2_flows",
						req.getParameter("starttime2_flows"));
				session.setAttribute("endtime2_flows",
						req.getParameter("endtime2_flows"));
			} else {
				session.setAttribute("nowtime_flows", 0);
			}
			String nowpage_flows = req.getParameter("nowpage_flows");
			String keywords_flows = req.getParameter("keywords_flows");
			String type_flows = req.getParameter("type_flows");
			String process = req.getParameter("process");
			String hangUp = req.getParameter("hangUp");
			String stage = req.getParameter("stage");
			String isjoin = req.getParameter("isjoin");
			session.setAttribute("nowpage_flows",
					Integer.parseInt(nowpage_flows));
			session.setAttribute("keywords_flows", keywords_flows);
			session.setAttribute("type_flows", type_flows);
			session.setAttribute("process", Integer.parseInt(process));
			session.setAttribute("hangUp", Integer.parseInt(hangUp));
			session.setAttribute("stage", Integer.parseInt(stage));
			session.setAttribute("isjoin", Integer.parseInt(isjoin));
			resp.sendRedirect("flowmanager/flowlist.jsp");
			return;
		} else if ("addflow".equals(type)) {
			String flowTypeString = req.getParameter("selectflowtype");
			if (flowTypeString == "") {
				req.getRequestDispatcher("/login.jsp").forward(req, resp);
				return;
			}
			int flowType = Integer.parseInt(flowTypeString);
			switch (flowType) {
			case 1:
				resp.sendRedirect("flowmanager/create_taskflow.jsp");
				break;
			case 2:
				resp.sendRedirect("flowmanager/create_productflow.jsp");
				break;
			case 3:
				resp.sendRedirect("flowmanager/create_projectflow.jsp");
				break;
			case 4:
				resp.sendRedirect("flowmanager/create_outproductflow.jsp");
				break;
			case 5:
				resp.sendRedirect("flowmanager/create_manufactureflow.jsp");
				break;
			case 6:
				resp.sendRedirect("flowmanager/create_shipmentsflow.jsp");
				break;
			case 7:
				resp.sendRedirect("flowmanager/create_travelflow.jsp");
				break;
			case 8:
				resp.sendRedirect("flowmanager/create_leaveflow.jsp");
				break;
			case 9:
				resp.sendRedirect("flowmanager/create_resumptionflow.jsp");
				break;
			case 10:
				resp.sendRedirect("flowmanager/create_trackflow.jsp");
				break;
			case 11:
				resp.sendRedirect("flowmanager/create_salesflow.jsp");
				break;
			case 12:
				resp.sendRedirect("flowmanager/create_purchaseflow.jsp");
				break;
			case 13:
				resp.sendRedirect("flowmanager/create_aftersalestaskflow.jsp");
				break;
			case 14:
				resp.sendRedirect("flowmanager/create_sealflow.jsp");
				break;
			case 15:
				resp.sendRedirect("flowmanager/create_vehicleflow.jsp");
				break;
			case 16:
				resp.sendRedirect("flowmanager/create_workflow.jsp");
				break;
			case 17:
				resp.sendRedirect("flowmanager/create_startuptaskflow.jsp");
				break;
			case 18:
				resp.sendRedirect("flowmanager/create_shippingflow.jsp");
				break;
			case 19:
				resp.sendRedirect("flowmanager/create_performanceflow.jsp");
				break;
			case 20:
				resp.sendRedirect("flowmanager/create_deliverflow.jsp");
				session.setAttribute("update_did", 0);
				break;
			case 22:
				session.setAttribute("update_did",0);
				session.setAttribute("curr_time", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
				resp.sendRedirect("flowmanager/create_departmentPuchaseflow.jsp");
				break;
			case 10000:
				session.setAttribute("device_pageType", 4);
				resp.sendRedirect("devicemanager/devicemanager.jsp");
				break;
			default:
				req.getRequestDispatcher("/login.jsp").forward(req, resp);
				break;
			}
			return;
		} else if ("addtaskflow".equals(type)) {
			ServletFileUpload sfu = FileUploadUtil.initFileUpload();
			int pCategory = 0;				//项目类型
			int productType = -1;			//产品类型（非风电项目的产品类型）在前台判断时，如果这个值为-1，则说明这个项目是风电项目
			String project_name = null;		//项目名称
			String project_id = null;		//项目编号
			int project_case = 0;			//普项 OR 急项
			String customer = null;			//用户名称
			String linkman_user = null;		//用户联系人
			String linkman_device = null;	//设备接收人
			String delivery_time = null;	//要求发货时间
			String address = null;			//预计发货地址

			int inspection = 0;				//是否要求施工前现场开箱验货
			int verify = 0;					//发货前是否要求需和销售经理确认
			String description = null;		//项目说明
			String other = null;			//特殊情况备注
			int hasProtocol = 1;

			//新加字段
			String fan_num = null;			//风机数量
			String factory = null;		//主机厂家
			String submit_date = null;		//项目交期
			String contract_type = null;//合同类型
			String equipment_type = null;//设备类型
			String consignee = null;	//收货人
			String fan_product_type = null;//风电项目产品类型
			//根据需求删除的字段
			int stage = 0;
			int project_type = 0;
//			String linkman_bill = null;		//数据库表task中没有该字段，直接删除
			String project_life = null;//项目质保期
			String project_report_peried = null;//项目诊断报告周期



			try {
				List<FileItem> list = sfu.parseRequest(req);
				int fLen = list.size();
				for (int j = 0; j < fLen; j++) {
					FileItem item = list.get(j);
					// 普通表单
					String formname = item.getFieldName();// 获取form中的字段名
					if (item.isFormField()) {
						if ("project_name".equals(formname)) {
							project_name = new String(item.getString("utf-8"));// 获取form中的内容
						} else if ("project_id".equals(formname)) {
							project_id = new String(item.getString("utf-8"));
						} else if ("project_case".equals(formname)) {
							project_case = Integer.parseInt(new String(item
									.getString("utf-8")));
						}else if ("customer".equals(formname)) {
							customer = new String(item.getString("utf-8"));
						} else if ("linkman_user".equals(formname)) {
							linkman_user = new String(item.getString("utf-8"));
						}else if ("linkman_device".equals(formname)) {
							linkman_device = new String(item.getString("utf-8"));
						} else if ("delivery_time".equals(formname)) {
							delivery_time = new String(item.getString("utf-8"));
						} else if ("address".equals(formname)) {
							address = new String(item.getString("utf-8"));
						} else if ("inspection".equals(formname)) {
							inspection = Integer.parseInt(new String(item
									.getString("utf-8")));
						} else if ("verify".equals(formname)) {
							verify = Integer.parseInt(new String(item
									.getString("utf-8")));
						} else if ("description".equals(formname)) {
							description = new String(item.getString("utf-8"));
						} else if ("other".equals(formname)) {
							other = new String(item.getString("utf-8"));
						} else if ("pCategory".equals(formname)) {
							pCategory = Integer.parseInt(new String(item
									.getString("utf-8")));
						} else if ("fan_num".equals(formname)) {
							fan_num = new String(item.getString("utf-8"));
						}else if ("factory".equals(formname)) {
							factory = new String(item.getString("utf-8"));
						}else if ("submit_date".equals(formname)){
							submit_date = new String(item.getString("utf-8"));
						}else if ("contract_type".equals(formname)){
							contract_type = new String(item.getString("utf-8"));
						}else if ("equipment_type".equals(formname)){
							equipment_type = new String(item.getString("utf-8"));
						}else if ("consignee".equals(formname)){
							consignee = new String(item.getString("utf-8"));
						}else if ("productTypeValue".equals(formname)){
							fan_product_type = new String(item.getString("utf-8"));
						}else if ("productType".equals(formname)){
							productType = Integer.parseInt(new String(item.getString("utf-8")));
						}
					} else {
						if (hasProtocol == 1) {
							hasProtocol = 0;// 0有协议
						}
					}
				}

				Task task = new Task();
				task.setProject_name(project_name);
				task.setProject_id(project_id);
				task.setProject_case(project_case);
				task.setProject_category(pCategory);
				task.setCustomer(customer);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				task.setDelivery_time(sdf.parse(delivery_time).getTime());
				task.setAddress(address);
				task.setDescription(description);
				task.setInspection(inspection);
				task.setVerify(verify);
				task.setProtocol(hasProtocol);
				task.setOther(other);
				task.setIsedited(0);
				task.setCreate_id(uid);
				long time = System.currentTimeMillis();
				task.setCreate_time(time);
				task.setUpdate_time(time);
				//新需求删除的字段
				task.setStage(stage);
				task.setProject_type(project_type);
				task.setProject_life(project_life);
				task.setProject_report_peried(project_report_peried);
				//新加字段
				task.setFan_num(fan_num);
				task.setFactory(factory);
				task.setSubmit_date(submit_date);
				task.setContract_type(contract_type);
				task.setEquipment_type(equipment_type);
				task.setConsignee(consignee);
				task.setIs_new_data("1");	//标记数据为新数据
				if (pCategory==0){
					//风电项目
//					task.setProduct_type(-1);
					if (fan_product_type!=null && fan_product_type.length()>0){
						task.setFan_product_type(fan_product_type.substring(0, fan_product_type.length()-1));
					}

				}else{
					//非风电项目
					task.setProduct_type(productType);
				}

				int task_id = taskManager.insertTask(task);
				String[] linkman_userArray = linkman_user.split("い");
				for (int i = 0; i < linkman_userArray.length; i++) {
					String[] userArray = linkman_userArray[i].split("の");
					Linkman linkman = new Linkman();
					linkman.setType(1);
					linkman.setForeign_id(task_id);
					linkman.setCreate_time(time);
					linkman.setState(0);
					linkman.setLinkman(userArray[0]);
					linkman.setPhone(userArray[1]);
					linkman.setLinkman_case(1);
					linkmanManager.insertLinkman(linkman);
				}

				String[] linkman_deviceArray = linkman_device.split("い");
				for (int i = 0; i < linkman_deviceArray.length; i++) {
					String[] userArray = linkman_deviceArray[i].split("の");
					Linkman linkman = new Linkman();
					linkman.setType(1);
					linkman.setForeign_id(task_id);
					linkman.setCreate_time(time);
					linkman.setState(0);
					linkman.setLinkman(userArray[0]);
					linkman.setPhone(userArray[1]);
					linkman.setLinkman_case(3);
					linkmanManager.insertLinkman(linkman);
				}
				Flow flow = new Flow();
				flow.setCreate_time(time);
				flow.setForeign_id(task_id);
				if (project_case == 0) {
					flow.setOperation(1);//普项
				} else {
					flow.setOperation(2);//急项
				}
				file_pathManager.saveFile(uid, sessionID, 1, task_id, 1, 0, save_time);
				file_pathManager.saveFile(uid, sessionID, 1, task_id, 2, 0, save_time);
				Operation op = new Operation();
				op.setUid(uid);
				op.setContent("创建任务单 id:" + task_id);
				op.setCreate_time(time);
				operationManager.insertOperation(op);
				flow.setType(1);
				flow.setUid(uid);
				flow.setReason("");
				flowManager.insertFlow(flow);//添加流程，并向有关人发送邮件
				session.setAttribute("task_id", task_id);
				resp.sendRedirect("flowmanager/taskflow_detail.jsp");
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				req.getRequestDispatcher("/login.jsp").forward(req, resp);
			}
		} else if ("verifytaskflow".equals(type)) {
			try {
				lock1.writeLock().lock();
				int task_id = (Integer)req.getSession().getAttribute("task_id");
				int operation = flowManager.getNewFlowByFID(1, task_id)
						.getOperation();
				int opera = Integer.parseInt(req.getParameter("operation"));

				if (opera != operation) {
					session.setAttribute("task_id", task_id);
					resp.sendRedirect("flowmanager/taskflow_detail.jsp");
					return;
				}
				Task task = taskManager.getTaskByID(task_id);
				int project_case = task.getProject_case();
				int project_type = task.getProject_type();
				int project_category=task.getProject_category();
				ServletFileUpload sfu = FileUploadUtil.initFileUpload();
				int isagree = 0;
				String reason = "";
				List<FileItem> list = sfu.parseRequest(req);
				int fLen = list.size();
				for (int j = 0; j < fLen; j++) {
					FileItem item = list.get(j);
					// 普通表单
					String formname = item.getFieldName();// 获取form中的字段名
					if (item.isFormField()) {
						if ("isagree".equals(formname)) {
							String isagreeStr = new String(
									item.getString("utf-8"));
							isagree = (isagreeStr == null && isagreeStr == "") ? 0
									: Integer.parseInt(isagreeStr);
						} else if ("reason".equals(formname)) {
							reason = new String(item.getString("utf-8"));
						}
					}
				}
				if (isagree == 2){
					operation = 1000;
				}
				Flow flow = new Flow();
				flow.setUid(uid);
				flow.setCreate_time(System.currentTimeMillis());
				flow.setForeign_id(task_id);
				flow.setType(1);
				flow.setReason(reason);
				long time = System.currentTimeMillis();
				Operation op = new Operation();
				op.setUid(uid);
				op.setCreate_time(time);
				//CPU类型项目
				if(task.getProduct_type()==10){
					if(operation == 1 || operation == 2){
						flow.setOperation(isagree == 0 ? 19 : 20);
						String content = isagree == 0 ? "销售经理审核通过" : "销售经理审核不予通过";
						op.setContent("任务单 id:" + task_id + content + "<br/>理由："
								+ reason);
					} else if (operation == 19) {
						flow.setOperation(isagree == 0 ? 21 : 22);
						String content = isagree == 0 ? "总经理审核通过" : "总经理审核不予通过";
						op.setContent("任务单 id:" + task_id + content + "<br/>理由："
								+ reason);
					}else if (operation == 20) {
						if (isagree == 0) {
							flow.setOperation(19);
							String content = "销售经理审核通过";
							op.setContent("任务单 id:" + task_id + content
									+ "<br/>理由：" + reason);
						} else {
							req.getRequestDispatcher("/login.jsp").forward(req,
									resp);
						}
					} else if (operation == 22) {
						if (isagree == 0) {
							flow.setOperation(21);
							String content = "总经理审核通过";
							op.setContent("任务单 id:" + task_id + content
									+ "<br/>理由：" + reason);
						} else {
							req.getRequestDispatcher("/login.jsp").forward(req,
									resp);
						}
					}
				}else if(project_category==5){//新产品试装项目
					if(operation == 1 || operation == 2){
						flow.setOperation(isagree == 0 ? 13 : 14);
						String content = isagree == 0 ? "产品经理审核通过" : "产品经理审核不予通过";
						op.setContent("任务单 id:" + task_id + content + "<br/>理由："
								+ reason);
					}else if(operation == 13){
						file_pathManager.saveFile(uid, sessionID, 1, task_id, 3, 0, save_time);
						flow.setOperation(isagree == 0 ? 17 : 18);
						String content = isagree == 0 ? "创建人上传文件成功"
								: "创建人上传文件失败";
						op.setContent("任务单 id:" + task_id + content
								+ "<br/>理由：" + reason);
					}else if(operation == 14){
						if (isagree == 0) {
							file_pathManager.saveFile(uid, sessionID, 1, task_id, 3, 0, save_time);
							flow.setOperation(13);
							String content = "产品经理审核通过";
							op.setContent("任务单 id:" + task_id + content
									+ "<br/>理由：" + reason);
						} else {
							req.getRequestDispatcher("/login.jsp").forward(req,
									resp);
						}
					}else if(operation ==17){
						flow.setOperation(isagree == 0 ? 19 : 20);
							file_pathManager.saveFile(uid, sessionID, 1, task_id, 3, 0, save_time);
							String content = isagree == 0 ? "产品部审核通过"
									: "产品部审核不予通过";
							op.setContent("任务单 id:" + task_id + content
									+ "<br/>理由：" + reason);
					}else if(operation == 18){
						if (isagree == 0) {
							file_pathManager.saveFile(uid, sessionID, 1, task_id, 3, 0, save_time);
							flow.setOperation(17);
							String content = "创建人上传文件成功";
							op.setContent("任务单 id:" + task_id + content
									+ "<br/>理由：" + reason);
						} else {
							req.getRequestDispatcher("/login.jsp").forward(req,
									resp);
						}
					} else if (operation == 19) {
						flow.setOperation(isagree == 0 ? 21 : 22);
						String content = isagree == 0 ? "总经理审核通过" : "总经理审核不予通过";
						op.setContent("任务单 id:" + task_id + content + "<br/>理由："
								+ reason);
					} else if (operation == 20) {
						if (isagree == 0) {
							flow.setOperation(19);
							String content = "产品部审核通过";
							op.setContent("任务单 id:" + task_id + content
									+ "<br/>理由：" + reason);
						} else {
							req.getRequestDispatcher("/login.jsp").forward(req,
									resp);
						}
					}else if (operation == 22) {
						if (isagree == 0) {
							flow.setOperation(21);
							String content = "总经理审核通过";
							op.setContent("任务单 id:" + task_id + content
									+ "<br/>理由：" + reason);
						} else {
							req.getRequestDispatcher("/login.jsp").forward(req,
									resp);
						}
					}
					
				}else if (operation == 1) {
					flow.setOperation(isagree == 0 ? 13 : 14);
					String content = isagree == 0 ? "商务助理审核通过" : "商务助理审核不予通过";
					op.setContent("任务单 id:" + task_id + content + "<br/>理由："
							+ reason);
				} else if (operation == 2) {
					flow.setOperation(isagree == 0 ? 19 : 20);
					String content = isagree == 0 ? "销售经理审核通过" : "销售经理审核不予通过";
					op.setContent("任务单 id:" + task_id + content + "<br/>理由："
							+ reason);
				} else if (operation == 13) {
					if (project_case == 0) {
						// 普项
//						file_pathManager.saveFile(uid, sessionID, 1, task_id, 3, 0, save_time);
						file_pathManager.saveFile(uid, sessionID, 1, task_id, 4, 0, save_time);
						file_pathManager.saveFile(uid, sessionID, 1, task_id, 5, 0, save_time);
						if(project_type == 2){
							flow.setOperation(isagree == 0 ? 24 : 25);
							String content = isagree == 0 ? "诊断审核通过"
									: "诊断审核不予通过";
							op.setContent("任务单 id:" + task_id + content
									+ "<br/>理由：" + reason);
						}else {
//							flow.setOperation(isagree == 0 ? 17 : 18);
//							String content = isagree == 0 ? "工程审核通过"
//									: "工程审核不予通过";
//							op.setContent("任务单 id:" + task_id + content
//									+ "<br/>理由：" + reason);
							flow.setOperation(isagree == 0 ? 26 : 18);
							String content = isagree == 0 ? "工程设计审核通过"
									: "工程设计审核不予通过";
							op.setContent("任务单 id:" + task_id + content
									+ "<br/>理由：" + reason);
						}
					} else {
						// 急项 已结束
						req.getRequestDispatcher("/login.jsp").forward(req,
								resp);
					}
				} else if(operation == 26){
					flow.setOperation(isagree == 0 ? 17 : 27);
					String content = isagree == 0 ? "工程经理审核通过"
							: "工程经理审核不予通过";
					op.setContent("任务单 id:" + task_id + content
							+ "<br/>理由：" + reason);
				} else if(operation == 27){
					if (isagree == 0) {
						flow.setOperation(17);
						String content = "工程经理审核通过";
						op.setContent("任务单 id:" + task_id + content
								+ "<br/>理由：" + reason);
					} else {
						req.getRequestDispatcher("/login.jsp").forward(req,
								resp);
					}
				}else if (operation == 14) {
					if (isagree == 0) {
						file_pathManager.saveFile(uid, sessionID, 1, task_id, 3, 0, save_time);
						flow.setOperation(13);
						String content = "商务助理审核通过";
						op.setContent("任务单 id:" + task_id + content
								+ "<br/>理由：" + reason);
					} else {
						req.getRequestDispatcher("/login.jsp").forward(req,
								resp);
					}
				} else if (operation == 17) {
					if (project_case == 0) {
						// 普项
						flow.setOperation(isagree == 0 ? 19 : 20);
						String content = isagree == 0 ? "销售经理审核通过"
								: "销售经理审核不予通过";
						op.setContent("任务单 id:" + task_id + content
								+ "<br/>理由：" + reason);
					} else {
						// 急项
						if(project_type==2){//售后
							flow.setOperation(isagree == 0 ? 24 : 25);
							String content = isagree == 0 ? "诊断审核通过"
									: "诊断审核不予通过";
							op.setContent("任务单 id:" + task_id + content
									+ "<br/>理由：" + reason);
						}else{
							flow.setOperation(isagree == 0 ? 13 : 14);
							String content = isagree == 0 ? "商务助理审核通过"
									: "商务助理审核不予通过";
							op.setContent("任务单 id:" + task_id + content
									+ "<br/>理由：" + reason);
						}
					}
				} else if (operation == 18) {
					if (project_case == 0) {
						if (isagree == 0) {
//							flow.setOperation(17);
							if(project_type==2){
								flow.setOperation(26);
//								file_pathManager.saveFile(uid, sessionID, 1, task_id, 3, 0, save_time);
								String content = "售后审核通过";
								op.setContent("任务单 id:" + task_id + content
										+ "<br/>理由：" + reason);
							}else {
								flow.setOperation(26);
								String content = "工程审核通过";
								op.setContent("任务单 id:" + task_id + content
										+ "<br/>理由：" + reason);
							}
						} else {
							req.getRequestDispatcher("/login.jsp").forward(req,
									resp);
						}
					}else{
						if (isagree == 0) {
//							flow.setOperation(17);
							if(project_type==2){
//								file_pathManager.saveFile(uid, sessionID, 1, task_id, 3, 0, save_time);
								flow.setOperation(26);
								String content = "售后审核通过";
								op.setContent("任务单 id:" + task_id + content
										+ "<br/>理由：" + reason);
							}else {
								flow.setOperation(26);
								String content = "工程审核通过";
								op.setContent("任务单 id:" + task_id + content
										+ "<br/>理由：" + reason);
							}
						} else {
							req.getRequestDispatcher("/login.jsp").forward(req,
									resp);
						}
					}
					file_pathManager.saveFile(uid, sessionID, 1, task_id, 3, 0, save_time);
				} else if (operation == 24) {
					if (project_case == 0) {
						// 普项
						flow.setOperation(isagree == 0 ? 26 : 18);
						if(project_type==2){
							file_pathManager.saveFile(uid, sessionID, 1, task_id, 3, 0, save_time);
							String content = isagree == 0 ? "售后审核通过"
									: "售后审核不予通过";
							op.setContent("任务单 id:" + task_id + content
									+ "<br/>理由：" + reason);
						}else{
							String content = isagree == 0 ? "工程审核通过"
									: "工程审核不予通过";
							op.setContent("任务单 id:" + task_id + content
									+ "<br/>理由：" + reason);
						}
					} else {
						// 急项
						flow.setOperation(isagree == 0 ? 13 : 14);
						if(project_type==2){
							String content = isagree == 0 ? "商务助理审核通过"
									: "商务助理审核不予通过";
							op.setContent("任务单 id:" + task_id + content
									+ "<br/>理由：" + reason);
						}else{
							String content = isagree == 0 ? "工程审核通过"
									: "工程审核不予通过";
							op.setContent("任务单 id:" + task_id + content
									+ "<br/>理由：" + reason);
						}
					}
				} else if (operation == 25) {
					if (isagree == 0) {
						flow.setOperation(24);
						String content = "诊断审核通过";
						op.setContent("任务单 id:" + task_id + content
								+ "<br/>理由：" + reason);
					} else {
						req.getRequestDispatcher("/login.jsp").forward(req,
								resp);
					}
				} else if (operation == 19) {
					flow.setOperation(isagree == 0 ? 21 : 22);
					String content = isagree == 0 ? "总经理审核通过" : "总经理审核不予通过";
					op.setContent("任务单 id:" + task_id + content + "<br/>理由："
							+ reason);
				} else if (operation == 20) {
					if (isagree == 0) {
						flow.setOperation(19);
						String content = "销售经理审核通过";
						op.setContent("任务单 id:" + task_id + content
								+ "<br/>理由：" + reason);
					} else {
						req.getRequestDispatcher("/login.jsp").forward(req,
								resp);
					}
				} else if (operation == 21) {
					if (project_case == 0) {
						// 普项
						req.getRequestDispatcher("/login.jsp").forward(req,
								resp);
					} else {
						// 急项
						file_pathManager.saveFile(uid, sessionID, 1, task_id, 3, 0, save_time);
						if(project_type == 2){
							flow.setOperation(isagree == 0 ? 26 : 18);
							String content = isagree == 0 ? "售后审核通过"
									: "售后审核不予通过";
							op.setContent("任务单 id:" + task_id + content
									+ "<br/>理由：" + reason);
						}else {
							flow.setOperation(isagree == 0 ? 26 : 18);
							String content = isagree == 0 ? "工程审核通过"
									: "工程审核不予通过";
							op.setContent("任务单 id:" + task_id + content
									+ "<br/>理由：" + reason);
						}
					}
				} else if (operation == 22) {
					if (isagree == 0) {
						flow.setOperation(21);
						String content = "总经理审核通过";
						op.setContent("任务单 id:" + task_id + content
								+ "<br/>理由：" + reason);
					} else {
						req.getRequestDispatcher("/login.jsp").forward(req,
								resp);
					}
				}else if (operation == 1000) {
					if (project_case == 0) {
						// 普项
						file_pathManager.saveFile(uid, sessionID, 1, task_id, 4, 0, save_time);
						file_pathManager.saveFile(uid, sessionID, 1, task_id, 5, 0, save_time);
						if(project_type == 2){
							flow.setOperation(isagree == 2 ? 24 : 25);
							String content = isagree == 2 ? "诊断完成修改"
									: "诊断审核不予通过";
							op.setContent("任务单 id:" + task_id + content
									+ "<br/>理由：" + reason);
						}else {
							flow.setOperation(isagree == 2 ? 26 : 18);
							String content = isagree == 2 ? "工程设计完成修改"
									: "工程设计审核不予通过";
							op.setContent("任务单 id:" + task_id + content
									+ "<br/>理由：" + reason);
						}
					} else {
						// 急项 已结束
						req.getRequestDispatcher("/login.jsp").forward(req,
								resp);
					}
				}

				operationManager.insertOperation(op);
				flowManager.insertFlow(flow);
				boolean flag = project_category>5 && project_category<9 && project_type != 2;
				boolean canFinish = (flow.getOperation()==13 && !flag && project_category!=5 && project_case!=0) || (flow.getOperation()==21 && (task.getProduct_type()==10 || project_category==5 || project_case==0));
				
				if (canFinish) {
					flow.setOperation(23);
					flow.setReason(null);
					flowManager.insertFlow2(flow);
					flowManager.finishFlow(1, task_id);
				}
				session.setAttribute("task_id", task_id);
				resp.sendRedirect("flowmanager/taskflow_detail.jsp");
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				req.getRequestDispatcher("/login.jsp").forward(req, resp);
			} finally {
				lock1.writeLock().unlock();
			}
		} else if ("alerttaskflow".equals(type)) {
			try {
				lock1.writeLock().lock();
				int task_id = (Integer)req.getSession().getAttribute("task_id");
				ServletFileUpload sfu = FileUploadUtil.initFileUpload();
				String linkman_bill = null;
				String remarks = null;
				String reason = null;

				int pCategory = 0;				//项目类型
				int productType = -1;			//产品类型（非风电项目的产品类型）在前台判断时，如果这个值为-1，则说明这个项目是风电项目
				String project_name = null;		//项目名称
				String project_id = null;		//项目编号
				int project_case = 0;			//普项 OR 急项
				String customer = null;			//用户名称
				String linkman_user = null;		//用户联系人
				String linkman_device = null;	//设备接收人
				String delivery_time = null;	//要求发货时间
				String address = null;			//预计发货地址

				int inspection = 0;				//是否要求施工前现场开箱验货
				int verify = 0;					//发货前是否要求需和销售经理确认
				String description = null;		//项目说明
				String other = null;			//特殊情况备注
				int hasProtocol = 1;

				//新加字段
				String fan_num = null;			//风机数量
				String factory = null;		//主机厂家
				String submit_date = null;		//项目交期
				String contract_type = null;//合同类型
				String equipment_type = null;//设备类型
				String consignee = null;	//收货人
				String fan_product_type = null;//风电项目产品类型
//				String is_new_data = "1";//是否是新数据
				//根据需求删除的字段
				int stage = 0;
				int project_type = 0;
				String project_life = null;//项目质保期
				String project_report_peried = null;//项目诊断报告周期

				List<FileItem> list = sfu.parseRequest(req);
				int fLen = list.size();
				for (int j = 0; j < fLen; j++) {
					FileItem item = list.get(j);
					// 普通表单
					String formname = item.getFieldName();// 获取form中的字段名
					if (item.isFormField()) {
						if ("project_name".equals(formname)) {
							project_name = new String(item.getString("utf-8"));// 获取form中的内容
						} else if ("project_id".equals(formname)) {
							project_id = new String(item.getString("utf-8"));
						} else if ("project_case".equals(formname)) {
							project_case = Integer.parseInt(new String(item
									.getString("utf-8")));
						} else if ("stage".equals(formname)) {
							stage = Integer.parseInt(new String(item
									.getString("utf-8")));
						} else if ("project_type".equals(formname)) {
							project_type = Integer.parseInt(new String(item
									.getString("utf-8")));
						} else if ("customer".equals(formname)) {
							customer = new String(item.getString("utf-8"));
						} else if ("linkman_user".equals(formname)) {
							linkman_user = new String(item.getString("utf-8"));
						} else if ("linkman_bill".equals(formname)) {
							linkman_bill = new String(item.getString("utf-8"));
						} else if ("linkman_device".equals(formname)) {
							linkman_device = new String(item.getString("utf-8"));
						} else if ("delivery_time".equals(formname)) {
							delivery_time = new String(item.getString("utf-8"));
						} else if ("project_life".equals(formname)) {
							project_life = new String(item.getString("utf-8"));
						} else if ("project_report_peried".equals(formname)) {
							project_report_peried = new String(item.getString("utf-8"));
						} else if ("address".equals(formname)) {
							address = new String(item.getString("utf-8"));
						} else if ("inspection".equals(formname)) {
							inspection = Integer.parseInt(new String(item
									.getString("utf-8")));
						} else if ("verify".equals(formname)) {
							verify = Integer.parseInt(new String(item
									.getString("utf-8")));
						} else if ("description".equals(formname)) {
							description = new String(item.getString("utf-8"));
						} else if ("other".equals(formname)) {
							other = new String(item.getString("utf-8"));
						} else if ("remarks".equals(formname)) {
							remarks = new String(item.getString("utf-8"));
						} else if ("reason".equals(formname)) {
							reason = new String(item.getString("utf-8"));
						} else if ("pCategory".equals(formname)) {
							pCategory = Integer.parseInt(new String(item
									.getString("utf-8")));
						} else if ("productType".equals(formname)) {
							productType = Integer.parseInt(new String(item
									.getString("utf-8")));
						}else if ("fan_num".equals(formname)) {
							fan_num = new String(item.getString("utf-8"));
						}else if ("factory".equals(formname)) {
							factory = new String(item.getString("utf-8"));
						}else if ("submit_date".equals(formname)){
							submit_date = new String(item.getString("utf-8"));
						}else if ("contract_type".equals(formname)){
							contract_type = new String(item.getString("utf-8"));
						}else if ("equipment_type".equals(formname)){
							equipment_type = new String(item.getString("utf-8"));
						}else if ("consignee".equals(formname)){
							consignee = new String(item.getString("utf-8"));
						}else if ("productTypeValue".equals(formname)){
							fan_product_type = new String(item.getString("utf-8"));
						}
					} else {
						if ("file_technical[]".equals(formname)
								&& hasProtocol == 1) {
							hasProtocol = 0;// 0有协议
						}
					}
				}
				List<File_path> fileList_first = file_pathManager
						.getAllFileByCondition(1, task_id, 1, 0);
				List<File_path> fileList_second = file_pathManager
						.getAllFileByCondition(1, task_id, 2, 0);
				List<File_path> fileList6 = file_pathManager
						.getAllFileByCondition(1, task_id, 6, 0);
				Task task = taskManager.getTaskByID(task_id);
				long time = System.currentTimeMillis();
				int operation = flowManager.getNewFlowByFID(1, task_id)
						.getOperation();
				//之前加的有operation > 2 条件，但是由于会出现修改后之前数据不正确现象删除掉
				//现象出翔情况：在提交任务后没有人点击不同意，但是对任务单进行修改，就会使对比数据发生错乱
				if (operation != 5 && operation != 8) {
					Task_conflict task_conflict = task_conflictManager
							.getTask_conflictByTaskID(task_id);
					task_conflict.setTask_id(task_id);
					task_conflict.setProject_category(task
							.getProject_category());
					task_conflict.setProduct_type(task.getProduct_type());
					task_conflict.setProject_name(task.getProject_name());
					task_conflict.setProject_id(task.getProject_id());
					task_conflict.setProject_case(task.getProject_case());
					task_conflict.setStage(task.getStage());
					task_conflict.setProject_type(task.getProject_type());
					task_conflict.setCustomer(task.getCustomer());
					task_conflict.setDelivery_time(task.getDelivery_time());
					task_conflict.setDescription(task.getDescription());
					task_conflict.setInspection(task.getInspection());
					task_conflict.setVerify(task.getVerify());
					task_conflict.setProtocol(task.getProtocol());
					task_conflict.setAddress(task.getAddress());
					task_conflict.setOther(task.getOther());
					task_conflict.setRemarks(task.getRemarks());
					task_conflict.setProject_life(task.getProject_life());
					task_conflict.setProject_report_peried(task.getProject_report_peried());
					task_conflict.setFan_num(task.getFan_num());
					task_conflict.setFactory(task.getFactory());
					task_conflict.setSubmit_date(task.getSubmit_date());
					task_conflict.setContract_type(task.getContract_type());
					task_conflict.setEquipment_type(task.getEquipment_type());
					task_conflict.setConsignee(task.getConsignee());
					task_conflict.setFan_product_type(task.getFan_product_type());
					task_conflict.setIs_new_data(task.getIs_new_data());
					task_conflictManager.updateTask_conflict(task_conflict);
					task.setIsedited(1);
					linkmanManager.deleteLinkmanLimit(1, task_id, 0, 1);
					List<Linkman> linkList = linkmanManager
							.getLinkmanListLimit(1, task_id, 0, 0);
					for (Linkman linkman : linkList) {
						linkman.setState(1);
						linkmanManager.updateLinkman(linkman);
					}
					file_pathManager.delAllFileByCondition2(1, task_id, 0, 1);
					for (File_path file_path : fileList_first) {
						// 在对比任务单中备份文件记录
						file_path.setState(1);
//						file_path.setCreate_time(time);
						file_pathManager.insertFile(file_path);
					}
					for (File_path file_path : fileList_second) {
						// 在对比任务单中备份文件记录
						file_path.setState(1);
//						file_path.setCreate_time(time);
						file_pathManager.insertFile(file_path);
					}
					for (File_path file_path : fileList6) {
						// 在对比任务单中备份文件记录
						file_path.setState(1);
//						file_path.setCreate_time(time);
						file_pathManager.insertFile(file_path);
					}
				}
				task.setProject_category(pCategory);
				task.setProject_name(project_name);
				task.setProject_id(project_id);
				task.setProject_case(project_case);
				task.setStage(stage);
				task.setProject_type(project_type);
				task.setCustomer(customer);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				task.setDelivery_time(sdf.parse(delivery_time).getTime());
				task.setProject_life(project_life);
				task.setProject_report_peried(project_report_peried);
				task.setAddress(address);
				task.setDescription(description);
				task.setInspection(inspection);
				task.setVerify(verify);
				task.setProtocol(hasProtocol == 0 || fileList_first.size() > 0 ? 0
						: 1);
				task.setOther(other);
				task.setRemarks(remarks);
				task.setCreate_id(uid);
				task.setUpdate_time(time);
				//新加字段
				task.setFan_num(fan_num);
				task.setFactory(factory);
				task.setSubmit_date(submit_date);
				task.setContract_type(contract_type);
				task.setEquipment_type(equipment_type);
				task.setConsignee(consignee);
				task.setFan_product_type(fan_product_type);
//				task.setIs_new_data(is_new_data);//不用进行修改，保持原来
				if (pCategory==0){
					//风电项目
//					task.setProduct_type(-1);
					if (fan_product_type!=null && fan_product_type.length()>0){
						task.setFan_product_type(fan_product_type.substring(0, fan_product_type.length()-1));
					}

				}else{
					//非风电项目
					task.setProduct_type(productType);
				}
				taskManager.updateTask(task);
				linkmanManager.deleteLinkmanLimit(1, task_id, 0, 0);

				if (linkman_user!=null){
					String[] linkman_userArray = linkman_user.split("い");
					for (int i = 0; i < linkman_userArray.length; i++) {
						String[] userArray = linkman_userArray[i].split("の");
						Linkman linkman = new Linkman();
						linkman.setType(1);
						linkman.setForeign_id(task_id);
						linkman.setCreate_time(time);
						linkman.setState(0);
						linkman.setLinkman(userArray[0]);
						linkman.setPhone(userArray[1]);
						linkman.setLinkman_case(1);
						linkmanManager.insertLinkman(linkman);
					}
				}

				if ("1".equals(task.getIs_new_data()) && linkman_bill!=null){
					String[] linkman_billArray = linkman_bill.split("い");
					for (int i = 0; i < linkman_billArray.length; i++) {
						String[] userArray = linkman_billArray[i].split("の");
						Linkman linkman = new Linkman();
						linkman.setType(1);
						linkman.setForeign_id(task_id);
						linkman.setCreate_time(time);
						linkman.setState(0);
						linkman.setLinkman(userArray[0]);
						linkman.setPhone(userArray[1]);
						linkman.setLinkman_case(2);
						linkmanManager.insertLinkman(linkman);
					}
				}

				if (linkman_device!=null){
					String[] linkman_deviceArray = linkman_device.split("い");
					for (int i = 0; i < linkman_deviceArray.length; i++) {
						String[] userArray = linkman_deviceArray[i].split("の");
						Linkman linkman = new Linkman();
						linkman.setType(1);
						linkman.setForeign_id(task_id);
						linkman.setCreate_time(time);
						linkman.setState(0);
						linkman.setLinkman(userArray[0]);
						linkman.setPhone(userArray[1]);
						linkman.setLinkman_case(3);
						linkmanManager.insertLinkman(linkman);
					}
				}


				if (reason != null && reason.length() > 0) {
					Flow flow_reason = new Flow();
					flow_reason.setUid(uid);
					flow_reason.setCreate_time(time);
					flow_reason.setForeign_id(task_id);
					flow_reason.setType(1);
					flow_reason.setReason(reason);
					flowManager.insertFlow2(flow_reason);
				}
				file_pathManager.saveFile(uid, sessionID, 1, task_id, 1, 0, save_time);
				file_pathManager.saveFile(uid, sessionID, 1, task_id, 2, 0, save_time);
				Operation op = new Operation();
				op.setUid(uid);
				op.setContent("修改任务单 id:" + task_id);
				op.setCreate_time(time);
				operationManager.insertOperation(op);
				Flow flow = new Flow();
				flow.setType(1);
				flow.setForeign_id(task_id);
				flow.setCreate_time(time);
				flow.setOperation(project_case == 0 ? 1 : 2);
				flowManager.insertFlow(flow);
				session.setAttribute("task_id", task_id);
				resp.sendRedirect("flowmanager/taskflow_detail.jsp");
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				req.getRequestDispatcher("/login.jsp").forward(req, resp);
			} finally {
				lock1.writeLock().unlock();
			}
		} else if ("addproductproflow".equals(type)) {
			String product_value = req.getParameter("product_value");
			String product_name = req.getParameter("product_name");
			Product_procurement product = new Product_procurement();
			product.setCreate_id(uid);
			product.setProduct_name(product_name);
			product.setCreate_time(System.currentTimeMillis());
			int product_pid = product_procurementManager.insertProduct(product);
			Operation op = new Operation();
			op.setUid(uid);
			op.setContent("创建生产采购单id:" + product_pid);
			op.setCreate_time(System.currentTimeMillis());
			operationManager.insertOperation(op);
			String[] product_valueArray = product_value.split("い");
			for (int i = 0; i < product_valueArray.length; i++) {
				String[] productArray = product_valueArray[i].split("の");
				Procurement procurement = new Procurement();
				procurement.setType(1);
				procurement.setForeign_id(product_pid);
				procurement.setName(productArray[0]);
				procurement.setAgent(productArray[1]);
				procurement.setModel(productArray[2]);
				procurement.setMaterials_code(productArray[3]);
				procurement.setNum(Integer.parseInt(productArray[4]));
				procurement.setUnit(productArray[5]);
				product_procurementManager.insertProcurement(procurement);
			}
			file_pathManager.saveFile(uid, sessionID, 2, product_pid, 3, 0, save_time);
			Flow flow = new Flow();
			flow.setCreate_time(System.currentTimeMillis());
			flow.setForeign_id(product_pid);
			flow.setType(2);
			flow.setUid(uid);
			flow.setOperation(1);
			flowManager.insertFlow(flow);
			session.setAttribute("product_pid", product_pid);
			resp.sendRedirect("flowmanager/productflow_detail.jsp");
		} else if ("updateproductproflow".equals(type)) {
			try {
				lock2.writeLock().lock();
				int product_pid = Integer.parseInt(req
						.getParameter("product_pid"));
				int opera = Integer.parseInt(req.getParameter("operation"));
				Product_procurement product_procurement = product_procurementManager
						.getProduct_procurementByID(product_pid);
				String product_name = req.getParameter("product_name");
				String product_value = req.getParameter("product_value");
				int operation = flowManager.getNewFlowByFID(2, product_pid)
						.getOperation();
				if (opera != operation || product_procurement==null) {
					req.getRequestDispatcher("/login.jsp").forward(req, resp);
					return;
				}
				product_procurement.setProduct_name(product_name);
				product_procurementManager.updateProduct_procurement(product_procurement);
				procurementManager.deleteProcurementLimit(1, product_pid);
				String[] product_valueArray = product_value.split("い");
				for (int i = 0; i < product_valueArray.length; i++) {
					String[] productArray = product_valueArray[i].split("の");
					Procurement procurement = new Procurement();
					procurement.setType(1);
					procurement.setForeign_id(product_pid);
					procurement.setName(productArray[0]);
					procurement.setAgent(productArray[1]);
					procurement.setModel(productArray[2]);
					procurement.setMaterials_code(productArray[3]);
					procurement.setNum(Integer.parseInt(productArray[4]));
					procurement.setUnit(productArray[5]);
					product_procurementManager.insertProcurement(procurement);
				}

				file_pathManager.saveFile(uid, sessionID, 2, product_pid, 3, 0, save_time);
				Operation op = new Operation();
				op.setUid(uid);
				op.setContent("修改生产采购单id:" + product_pid);
				op.setCreate_time(System.currentTimeMillis());
				operationManager.insertOperation(op);
				Flow flow = new Flow();
				flow.setCreate_time(System.currentTimeMillis());
				flow.setForeign_id(product_pid);
				flow.setType(2);
				flow.setUid(uid);
				flow.setOperation(1);
				flowManager.insertFlow(flow);
				session.setAttribute("product_pid", product_pid);
				resp.sendRedirect("flowmanager/productflow_detail.jsp");
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				lock2.writeLock().unlock();
			}

		} else if ("productpflow".equals(type)) {
			try {
				lock2.writeLock().lock();
				int product_pid = Integer.parseInt(req
						.getParameter("product_pid"));
				int opera = Integer.parseInt(req.getParameter("operation"));
				int operation = flowManager.getNewFlowByFID(2, product_pid)
						.getOperation();
				if (opera != operation) {
					session.setAttribute("product_pid", product_pid);
					resp.sendRedirect("flowmanager/productflow_detail.jsp");
					return;
				}
				Flow flow = new Flow();
				flow.setUid(uid);
				flow.setCreate_time(System.currentTimeMillis());
				flow.setForeign_id(product_pid);
				flow.setType(2);
				long time = System.currentTimeMillis();
				Operation op = new Operation();
				op.setUid(uid);
				op.setCreate_time(time);
				if (operation == 4 || operation == 5) {
					int flag = 0;// 出错
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Product_procurement pp = product_procurementManager
							.getProduct_procurementByID(product_pid);
					boolean finished = Boolean.parseBoolean(req
							.getParameter("finished"));
					String pre_time = req.getParameter("pre_time");
					String[] preArray = pre_time.split("の");
					String aog_time = req.getParameter("aog_time");
					String[] aogArray = aog_time.split("の");
					String pass_percent = req.getParameter("pass_percent");
					String[] passArray = pass_percent.split("の");
					List<Procurement> list = procurementManager
							.getProcurementList(1, product_pid);
					boolean flag_pro = false;
					boolean flag_pre = false;
					boolean flag_aog = false;
					boolean flag_pass = false;
					for (int i = 0; i < list.size(); i++) {
						Procurement procurement = list.get(i);
						boolean flag_pre1 = false;
						boolean flag_aog1 = false;
						boolean flag_pass1 = false;
						if (pre_time.length() > 0 && preArray[i].length() > 1) {
							if (procurement.getPredict_time() == 0) {
								flag_pro = true;
							}
							procurement.setPredict_time(sdf.parse(preArray[i])
									.getTime());
							flag_pre = true;
							flag_pre1 = true;
						}
						if (aog_time.length() > 0 && aogArray[i].length() > 1) {
							// 有新的采购生成
							procurement.setAog_time(sdf.parse(aogArray[i])
									.getTime());
							flag_aog = true;
							flag_aog1 = true;
						}
						if (pass_percent.length() > 0
								&& (!"@".equals(passArray[i]))) {
							procurement.setPass_percent(Float
									.parseFloat(passArray[i]));
							flag_pass = true;
							flag_pass1 = true;
						}
						if (flag_pre1 || flag_aog1 || flag_pass1) {
							procurementManager.updateProcurement(procurement);
						}
					}
					if (flag_pre && !finished) {
						op.setContent("修改采购单id：" + product_pid + "的预计到货时间");
						operationManager.insertOperation(op);
					}
					if (flag_aog && !finished) {
						op.setContent("修改采购单id：" + product_pid + "的到货时间");
						operationManager.insertOperation(op);
					}
					if (flag_pass && !finished) {
						op.setContent("修改采购单id：" + product_pid + "的合格率");
						operationManager.insertOperation(op);
					}
					// 采购未完成
					String a = req.getParameter("finished");
					if (finished) {
						flag = 2;// 采购完成（或验货完毕），进入下一流程
						flow.setReason("");
						if (opera == 5) {
							pp.setAog_id(uid);
							pp.setCheck_id(uid);
							pp.setAog_time(sdf.parse(aog_time).getTime());
							product_procurementManager
									.updateProduct_procurement(pp);
							flow.setOperation(7);
							flowManager.insertFlow(flow);
							op.setContent("生产单id：" + product_pid + "验货完成");
							operationManager.insertOperation(op);
						} else {
							flow.setOperation(5);
							flowManager.insertFlow(flow);
							op.setContent("生产单id：" + product_pid + "完成采购");
							operationManager.insertOperation(op);
						}
					} else {
						flag = 1;// 保存成功
					}
					resp.setContentType("application/text;charset=utf-8");
					resp.setHeader("pragma", "no-cache");
					resp.setHeader("cache-control", "no-cache");
					PrintWriter out = resp.getWriter();
					out.println(flag);
					out.flush();
					return;
				}
				ServletFileUpload sfu = FileUploadUtil.initFileUpload();
				int isagree = 0;
				String reason = null;
				String predict_time = null;
				String aog_time = null;
				String pass_percent = null;
				List<FileItem> list = sfu.parseRequest(req);
				int fLen = list.size();
				for (int j = 0; j < fLen; j++) {
					FileItem item = list.get(j);
					// 普通表单
					String formname = item.getFieldName();// 获取form中的字段名
					if (item.isFormField()) {
						if ("isagree".equals(formname)) {// 获取form中的内容
							if (new String(item.getString("utf-8")) != "") {
								isagree = Integer.parseInt(new String(item
										.getString("utf-8")));
							}
						} else if ("reason".equals(formname)) {
							reason = new String(item.getString("utf-8"));
						} else if ("predict_time".equals(formname)) {
							predict_time = new String(item.getString("utf-8"));
						} else if ("aog_time".equals(formname)) {
							aog_time = new String(item.getString("utf-8"));
						} else if ("pass_percent".equals(formname)) {
							pass_percent = new String(item.getString("utf-8"));
						}
					}
				}
				flow.setReason(reason);
				if (operation == 1 || operation == 3) {
					if (isagree == 0) {
						opera = 2;
						op.setContent("生产采购单id:" + product_pid + "审核通过"
								+ "<br/>理由：" + reason);
					} else {
						opera = 3;
						op.setContent("生产采购单id:" + product_pid + "审核不予通过"
								+ "<br/>理由：" + reason);
					}
				} else if (operation == 2) {
					// 确认采购
					opera = 4;
					Product_procurement pp = product_procurementManager
							.getProduct_procurementByID(product_pid);
					pp.setReceive_id(uid);
					product_procurementManager.updateProduct_procurement(pp);
					op.setContent("生产采购单id:" + product_pid + "确认采购");
				} else if (operation == 5) {
					// 到货
					opera = 6;
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Product_procurement pp = product_procurementManager
							.getProduct_procurementByID(product_pid);
					pp.setAog_id(uid);
					pp.setAog_time(sdf.parse(aog_time).getTime());
					product_procurementManager.updateProduct_procurement(pp);
					file_pathManager.saveFile(uid, sessionID, 2, product_pid, 2, 0, save_time);
					op.setContent("生产采购单id:" + product_pid + "填写到货时间:"
							+ aog_time);
				} else if (operation == 6) {
					// 验货
					opera = 7;
					Product_procurement pp = product_procurementManager
							.getProduct_procurementByID(product_pid);
					pp.setCheck_id(uid);
					product_procurementManager.updateProduct_procurement(pp);
					String[] procurementArray = pass_percent.split("い");
					int plen = procurementArray.length;
					for (int i = 0; i < plen; i++) {
						String[] pArray = procurementArray[i].split("の");
						int id = Integer.parseInt(pArray[0]);
						float pass_p = Float.parseFloat(pArray[1]);
						Procurement procu = procurementManager
								.getProcurementByID(id);
						procu.setPass_percent(pass_p);
						procurementManager.updateProcurement(procu);
					}
					op.setContent("生产采购单id:" + product_pid + "验货");
				} else if (operation == 7) {
					// 入库
					opera = 8;
					Product_procurement pp = product_procurementManager
							.getProduct_procurementByID(product_pid);
					pp.setPutin_id(uid);
					product_procurementManager.updateProduct_procurement(pp);
					op.setContent("生产采购单id:" + product_pid + "确认入库");
				}
				flow.setOperation(opera);
				flowManager.insertFlow(flow);

				operationManager.insertOperation(op);
				session.setAttribute("product_pid", product_pid);
				resp.sendRedirect("flowmanager/productflow_detail.jsp");
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				req.getRequestDispatcher("/login.jsp").forward(req, resp);
			} finally {
				lock2.writeLock().unlock();
			}
		} else if ("taskupdateflow".equals(type)) {
			try {
				lock11.writeLock().lock();
				int task_id = (Integer)req.getSession().getAttribute("task_id");
				int task_updateflow_id = (Integer)req.getSession().getAttribute("task_updateflow_id");
				String reason = req.getParameter("reason");
				int operation = flowManager.getNewFlowByFID(21, task_updateflow_id)
						.getOperation();
				int isagree = Integer.parseInt(req.getParameter("isagree")==null?"0":req.getParameter("isagree"));
				int opera = Integer.parseInt(req.getParameter("operation")==null?"0":req.getParameter("operation"));
				if (opera != operation) {
					session.setAttribute("task_id", task_id);
					session.setAttribute("task_updateflow_id", task_updateflow_id);
					resp.sendRedirect("flowmanager/task_updateflow.jsp");
					return;
				}
				long time = System.currentTimeMillis();
				Flow flow = new Flow();
				flow.setUid(uid);
				flow.setCreate_time(time);
				flow.setForeign_id(task_updateflow_id);
				flow.setType(21);
				flow.setReason(reason);
				Operation op = new Operation();
				op.setUid(uid);
				op.setCreate_time(time);
				if (operation == 1) {
					flow.setOperation(isagree == 0 ? 2 : 3);
					String content = isagree == 0 ? "财务审核通过" : "财务审核不予通过";
					op.setContent("提前启动任务单 id:" + task_updateflow_id + content + "<br/>理由："
							+ reason);
				} else if (operation == 2) {
					flow.setOperation(isagree == 0 ? 4 : 5);
					String content = isagree == 0 ? "生产审核通过" : "生产审核不予通过";
					op.setContent("提前启动任务单 id:" + task_updateflow_id + content + "<br/>理由："
							+ reason);
				} else if (operation == 4) {
					flow.setOperation(isagree == 0 ? 6 : 7);
					String content = isagree == 0 ? "部门负责人审核通过" : "部门负责人审核不予通过";
					op.setContent("提前启动任务单 id:" + task_updateflow_id + content + "<br/>理由："
							+ reason);
				} else if (operation == 6) {
					flow.setOperation(isagree == 0 ? 8 : 9);
					String content = isagree == 0 ? "总经理审核通过" : "总经理审核不予通过";
					op.setContent("提前启动任务单 id:" + task_updateflow_id + content + "<br/>理由："
							+ reason);
				}
				operationManager.insertOperation(op);
				flowManager.insertFlow(flow);
				if ((flow.getOperation() == 8)) {
					flow.setOperation(11);
					flow.setReason(null);
					flowManager.insertFlow2(flow);
				}
				session.setAttribute("task_id", task_id);
				session.setAttribute("task_updateflow_id", task_updateflow_id);
				resp.sendRedirect("flowmanager/task_updateflow.jsp");
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				req.getRequestDispatcher("/login.jsp").forward(req, resp);
			} finally {
				lock11.writeLock().unlock();
			}
		}else if("cancleUpdateFlow".equals(type)){
			try {
				lock11.writeLock().lock();
				long time = System.currentTimeMillis();
				int task_updateflow_id = (Integer)req.getSession().getAttribute("task_updateflow_id");
				String reason = req.getParameter("reason");
				Flow flow = new Flow();
				flow.setCreate_time(time);
				flow.setUid(uid);
				flow.setType(21);
				flow.setReason(reason);
				flow.setForeign_id(task_updateflow_id);
				flow.setOperation(10);
				flowManager.insertFlow(flow);
				Operation operation = new Operation();
				operation.setContent("撤销提前启动任务修改单 id：" + task_updateflow_id);
				operation.setCreate_time(time);
				operation.setUid(uid);
				operationManager.insertOperation(operation);
				resp.sendRedirect("flowmanager/task_updateflow.jsp");
			} catch (Exception e) {
				// TODO: handle exception
				resp.sendRedirect("flowmanager/task_updateflow.jsp");
			}finally{
				lock11.writeLock().unlock();
			}
		} else if("saveProjectproflow".equals(type)){
			try {
				lock3.writeLock().lock();
				// 新建的项目单为还找不到数据
				int foreign_id = 0;// 关联的项目采购单
				int project_pid = 0;
				int opera = 0;
				int operation = 0;
				String opera_str = req.getParameter("operation");
				String projectID_Str = req.getParameter("project_pid");
				if (projectID_Str != null) {
					project_pid = Integer.parseInt(projectID_Str);
					operation = flowManager.getNewFlowByFID(3, project_pid)
							.getOperation();
				}
				if (opera_str != null) {
					opera = Integer.parseInt(opera_str);
				}
				if (operation != opera && project_pid != 0) {
					session.setAttribute("project_pid", project_pid);
					resp.sendRedirect("flowmanager/projectflow_detail.jsp");
					return;
				}
				long time = System.currentTimeMillis();
				// 操作日志
				Operation op = new Operation();
				op.setUid(uid);
				op.setCreate_time(time);

				String product_value = "";

                List<Procurement> procurementList = procurementManager.getProcurementListLimit(2, project_pid);

				product_value = req.getParameter("product_value");


				if (opera == 14 || opera == 16) {
					// 提交采购单
//					operation = 4;
					String[] product_valueArray = product_value.split("い");

					//判断是否保存过
                    if (procurementList!=null && procurementList.size()!=0){    //有记录 ，先删除，后插入
                        procurementManager.deleteProcurementLimit(2, project_pid);
                    }


					for (int i = 0; i < product_valueArray.length; i++) {
						String[] productArray = product_valueArray[i]
								.split("の");
						Procurement procurement = new Procurement();
						procurement.setType(2);
						procurement.setForeign_id(project_pid);
						procurement.setName(productArray[0]);
						procurement.setAgent(productArray[1]);
						procurement.setModel(productArray[2]);
						procurement.setMaterials_code(productArray[3]);
						procurement.setNum(Integer.parseInt(productArray[4]
								.replace(" ", "")));
						procurement.setUnit(productArray[5]);
						product_procurementManager
								.insertProcurement(procurement);
					}
					file_pathManager.saveFile(uid, sessionID, 3, project_pid, 4, 0,
							save_time);
					op.setContent("项目采购单id:" + project_pid + "保存采购单");
				}

				operationManager.insertOperation(op);

//				flowManager.insertFlow(flow);
				session.setAttribute("project_pid", project_pid);
				resp.sendRedirect("flowmanager/projectflow_detail.jsp");
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				req.getRequestDispatcher("/login.jsp").forward(req, resp);
			} finally {
				lock3.writeLock().unlock();
			}
		}else if ("projectproflow".equals(type)) {
			try {
				lock3.writeLock().lock();
				// 新建的项目单为还找不到数据
				int foreign_id = 0;// 关联的项目采购单
				int project_pid = 0;
				int opera = 0;
				int operation = 0;
				String opera_str = req.getParameter("operation");
				String projectID_Str = req.getParameter("project_pid");
				if (projectID_Str != null) {
					project_pid = Integer.parseInt(projectID_Str);
					operation = flowManager.getNewFlowByFID(3, project_pid)
							.getOperation();
				}
				if (opera_str != null) {
					opera = Integer.parseInt(opera_str);
				}
				if (operation != opera && project_pid != 0) {
					session.setAttribute("project_pid", project_pid);
					resp.sendRedirect("flowmanager/projectflow_detail.jsp");
					return;
				}
				long time = System.currentTimeMillis();
				Flow flow = new Flow();
				flow.setUid(uid);
				flow.setCreate_time(time);
				flow.setType(3);
				// 操作日志
				Operation op = new Operation();
				op.setUid(uid);
				op.setCreate_time(time);
				if (operation == 7 || operation == 8) {
					int flag = 0;// 出错
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Project_procurement pp = project_procurementManager
							.getProject_procurementByID(project_pid);
					String pre_time = req.getParameter("pre_time");
					String[] preArray = pre_time.split("の");
					String aog_time = req.getParameter("aog_time");
					String[] aogArray = aog_time.split("の");
					String pass_percent = req.getParameter("pass_percent");
					String[] passArray = pass_percent.split("の");
					List<Procurement> list = procurementManager
							.getProcurementList(2, project_pid);
					boolean flag_pre = false;
					boolean flag_aog = false;
					boolean flag_pass = false;
					boolean flag_pro = false;
					for (int i = 0; i < list.size(); i++) {
						Procurement procurement = list.get(i);
						boolean flag_pre1 = false;
						boolean flag_aog1 = false;
						boolean flag_pass1 = false;
						if (pre_time.length() > 0 && preArray[i].length() > 1) {
							if (procurement.getPredict_time() == 0) {
								flag_pro = true;
							}
							procurement.setPredict_time(sdf.parse(preArray[i])
									.getTime());
							flag_pre = true;
							flag_pre1 = true;
						}
						if (aog_time.length() > 0 && aogArray[i].length() > 1) {
							// 有新的采购生成
							procurement.setAog_time(sdf.parse(aogArray[i])
									.getTime());
							flag_aog = true;
							flag_aog1 = true;
						}
						if (pass_percent.length() > 0
								&& (!"@".equals(passArray[i]))) {
							procurement.setPass_percent(Float
									.parseFloat(passArray[i]));
							flag_pass = true;
							flag_pass1 = true;
						}
						procurementManager.updateProcurement(procurement);
					}
					if (flag_pre) {
						op.setContent("修改项目采购单id：" + project_pid + "的预计到货时间");
						operationManager.insertOperation(op);
					}
					if (flag_aog) {
						op.setContent("修改项目采购单id：" + project_pid + "的到货时间");
						operationManager.insertOperation(op);
					}
					if (flag_pass) {
						op.setContent("修改项目采购单id：" + project_pid + "的合格率");
						operationManager.insertOperation(op);
					}
					// 采购未完成
					String a = req.getParameter("finished");
					if (Boolean.parseBoolean(req.getParameter("finished"))) {
						flag = 2;// 采购完成（或验货完毕），进入下一流程
						flow.setReason("");
						flow.setForeign_id(project_pid);
						if (opera == 8) {
							pp.setAog_id(uid);
							pp.setCheck_id(uid);
							project_procurementManager
									.updateProject_procurement(pp);
							flow.setOperation(10);
							flowManager.insertFlow(flow);
							op.setContent("项目采购单id：" + project_pid + "验货完成");
							operationManager.insertOperation(op);
						} else {
							flow.setOperation(8);
							flowManager.insertFlow(flow);
							op.setContent("项目采购单id：" + project_pid + "完成采购");
							operationManager.insertOperation(op);
						}
					} else {
						flag = 1;// 保存成功
					}
					resp.setContentType("application/text;charset=utf-8");
					resp.setHeader("pragma", "no-cache");
					resp.setHeader("cache-control", "no-cache");
					PrintWriter out = resp.getWriter();
					out.println(flag);
					out.flush();
					return;
				}

				int task_id = 0;
				int isagree = 0;
				String reason = "";
				String product_value = "";
				String predict_time = null;
				String aog_time = null;
				String pass_percent = null;

				String task_id_str = req.getParameter("task_id");
				if (task_id_str != null && task_id_str.length() > 0) {
					task_id = Integer.parseInt(task_id_str);
				}
				reason = req.getParameter("reason");
				String isagree_str = req.getParameter("isagree");
				if (isagree_str != null && isagree_str.length() > 0) {
					isagree = Integer.parseInt(isagree_str);
				}
				product_value = req.getParameter("product_value");
				predict_time = req.getParameter("predict_time");
				aog_time = req.getParameter("aog_time");
				pass_percent = req.getParameter("pass_percent");
				flow.setReason(reason);
				if (opera == 0) {
					// 新建
					String foreign_id_str = req.getParameter("foreign_id");
					if (foreign_id_str != null && foreign_id_str.length() > 0) {
						foreign_id = Integer.parseInt(foreign_id_str);
					}
					Project_procurement pp = new Project_procurement();
					pp.setCreate_time(time);
					pp.setCreate_id(uid);
					pp.setTask_id(task_id);
					if (foreign_id > 0) {
						pp.setProject_pid(foreign_id);
					}
					project_procurementManager.insertProject_procurement(pp);
					project_pid = project_procurementManager
							.getNewProject_procurementByUID(uid);
					if (foreign_id == 0) {
						file_pathManager.saveFile(uid, sessionID, 3, project_pid, 1, 0,
								save_time);
					}
					op.setContent("创建项目采购单id:" + project_pid);
					operationManager.insertOperation(op);
					flow.setForeign_id(project_pid);
					if (foreign_id == 0) {
						flow.setOperation(1);
						flowManager.insertFlow(flow);
					} else {
						flow.setOperation(16);
						flowManager.insertFlow(flow);
					}
					session.setAttribute("project_pid", project_pid);
					resp.sendRedirect("flowmanager/projectflow_detail.jsp");
					return;
				}

				if (opera == 1 || opera == 3) {
//					operation = isagree == 0 ? 2 : 3;
					operation = isagree == 0 ? 18 : 3;		//中间加入销售经理审核
					String content = isagree == 0 ? "审核通过" : "审核不予通过";
					op.setContent("项目采购单id:" + project_pid + "的预算单项目" + content
							+ "<br/>理由：" + reason);
				}else if (opera == 18 || opera == 19){
					operation = isagree == 0 ? 2 : 19;
					String content = isagree == 0 ? "审核通过" : "审核不予通过";
					op.setContent("项目采购单id:" + project_pid + "的预算单项目" + content
							+ "<br/>理由：" + reason);
				}else if (opera == 2 || opera == 12 || opera == 15 || opera == 13) {
//					operation = isagree == 0 ? 12 : 13;
//					String content = isagree == 0 ? "审核通过" : "审核不予通过";
//					op.setContent("项目采购单id:" + project_pid + "的预算单运营" + content
//							+ "<br/>理由：" + reason);
//				} else if (opera == 12 || opera == 15) {
					operation = isagree == 0 ? 14 : 15;
					String content = isagree == 0 ? "审核通过" : "审核不予通过";
					op.setContent("项目采购单id:" + project_pid + "的预算单最终" + content
							+ "<br/>理由：" + reason);
				} else if (opera == 14 || opera == 16) {
					// 提交采购单
					operation = 4;
					String[] product_valueArray = product_value.split("い");

                    List<Procurement> procurementList = procurementManager.getProcurementListLimit(2, project_pid);
                    //判断是否保存过
                    if (procurementList!=null && procurementList.size()!=0){    //有记录 ，先删除，后插入
                        procurementManager.deleteProcurementLimit(2, project_pid);
                    }
					for (int i = 0; i < product_valueArray.length; i++) {
						String[] productArray = product_valueArray[i]
								.split("の");
						Procurement procurement = new Procurement();
						procurement.setType(2);
						procurement.setForeign_id(project_pid);
						procurement.setName(productArray[0]);
						procurement.setAgent(productArray[1]);
						procurement.setModel(productArray[2]);
						procurement.setMaterials_code(productArray[3]);
						procurement.setNum(Integer.parseInt(productArray[4]
								.replace(" ", "")));
						procurement.setUnit(productArray[5]);
						product_procurementManager
								.insertProcurement(procurement);
					}
					file_pathManager.saveFile(uid, sessionID, 3, project_pid, 4, 0,
							save_time);
					op.setContent("项目采购单id:" + project_pid + "提交采购单");
				} else if (opera == 4) {
					// 采购单审核
					operation = isagree == 0 ? 5 : 6;
					String content = isagree == 0 ? "审核通过" : "审核不予通过";
					op.setContent("项目采购单id:" + project_pid + "的采购单" + content
							+ "<br/>理由：" + reason);
				} else if (opera == 5) {
					// 确认采购
					operation = 7;
					Project_procurement pp = project_procurementManager
							.getProject_procurementByID(project_pid);
					pp.setReceive_id(uid);
					project_procurementManager.updateProject_procurement(pp);
					op.setContent("项目采购单id:" + project_pid + "确认采购");
				} else if (opera == 6) {
					// 运营总监第一次审核操作失误，重新审核
					operation = isagree == 0 ? 5 : 6;
					String content = isagree == 0 ? "审核通过" : "审核不予通过";
					op.setContent("项目采购单id:" + project_pid + content
							+ "<br/>理由：" + reason);
				} else if (opera == 7) {
					// 预计到货
					operation = 8;
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Project_procurement pp = project_procurementManager
							.getProject_procurementByID(project_pid);
					pp.setPredict_time(sdf.parse(predict_time).getTime());
					project_procurementManager.updateProject_procurement(pp);
					file_pathManager.saveFile(uid, sessionID, 3, project_pid, 2, 0, save_time);
					op.setContent("项目采购单id:" + project_pid + "填写预计到货时间:"
							+ predict_time);
				} else if (opera == 8) {
					// 到货
					operation = 9;
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Project_procurement pp = project_procurementManager
							.getProject_procurementByID(project_pid);
					pp.setAog_time(sdf.parse(aog_time).getTime());
					project_procurementManager.updateProject_procurement(pp);
					file_pathManager.saveFile(uid, sessionID, 3, project_pid, 3, 0, save_time);
					op.setContent("项目采购单id:" + project_pid + "填写到货时间:"
							+ aog_time);
				} else if (opera == 9) {
					// 验货
					operation = 10;
					Project_procurement pp = project_procurementManager
							.getProject_procurementByID(project_pid);
					pp.setCheck_id(uid);
					project_procurementManager.updateProject_procurement(pp);
					String[] procurementArray = pass_percent.split("い");
					int plen = procurementArray.length;
					for (int i = 0; i < plen; i++) {
						String[] pArray = procurementArray[i].split("の");
						int id = Integer.parseInt(pArray[0]);
						float pass_p = Float.parseFloat(pArray[1]);
						Procurement procu = procurementManager
								.getProcurementByID(id);
						procu.setPass_percent(pass_p);
						procurementManager.updateProcurement(procu);
					}
					op.setContent("项目采购单id:" + project_pid + "验货");
				} else if (opera == 10) {
					// 入库
					operation = 11;
					Project_procurement pp = project_procurementManager
							.getProject_procurementByID(project_pid);
					pp.setPutin_id(uid);
					project_procurementManager.updateProject_procurement(pp);
					op.setContent("项目采购单id:" + project_pid + "确认入库");
				}

				operationManager.insertOperation(op);

				flow.setForeign_id(project_pid);
				flow.setOperation(operation);
				flowManager.insertFlow(flow);
				session.setAttribute("project_pid", project_pid);
				resp.sendRedirect("flowmanager/projectflow_detail.jsp");
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				req.getRequestDispatcher("/login.jsp").forward(req, resp);
			} finally {
				lock3.writeLock().unlock();
			}
		} else if ("deleteProject".equals(type)) {
			try {
				lock3.writeLock().lock();
				String reason = req.getParameter("reason");
				int project_pid = (Integer) session.getAttribute("project_pid");
				int opera = Integer.parseInt(req.getParameter("operation"));
				int operation = flowManager.getNewFlowByFID(3, project_pid)
						.getOperation();
				Project_procurement project_procurement = project_procurementManager
						.getProject_procurementByID(project_pid);
				if (opera != operation || project_procurement == null) {
					req.getRequestDispatcher("/login.jsp").forward(req, resp);
					return;
				}
				long time = System.currentTimeMillis();
				Flow flow = new Flow();
				flow.setCreate_time(time);
				flow.setForeign_id(project_pid);
				flow.setType(3);
				flow.setUid(uid);
				flow.setOperation(17);
				flow.setReason(reason);
				flowManager.insertFlow(flow);
				Operation op = new Operation();
				op.setCreate_time(time);
				op.setUid(uid);
				op.setContent("撤销项目采购单id：" + project_pid);
				operationManager.insertOperation(op);
				session.setAttribute("project_pid", project_pid);
				resp.sendRedirect("flowmanager/projectflow_detail.jsp");
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				req.getRequestDispatcher("/login.jsp").forward(req, resp);
			} finally {
				lock3.writeLock().unlock();
			}
		} else if ("updateprojectproflow".equals(type)) {
			lock3.writeLock().lock();
			try {
				int project_pid = (Integer) session.getAttribute("project_pid");
				int foreign_id = 0;
				int opera = Integer.parseInt(req.getParameter("operation"));
				Flow flow_new = flowManager.getNewFlowByFID(3, project_pid);
				Project_procurement project_procurement = project_procurementManager
						.getProject_procurementByID(project_pid);
				int operation=0;
				if (flow_new==null||opera != (operation=flow_new.getOperation()) || project_procurement == null) {
					resp.sendRedirect("flowmanager/projectflow_detail.jsp");
					return;
				}
				ServletFileUpload sfu = FileUploadUtil.initFileUpload();
				int task_id = 0;
				String product_value = "";
				List<FileItem> list = sfu.parseRequest(req);
				int fLen = list.size();
				for (int j = 0; j < fLen; j++) {
					FileItem item = list.get(j);
					// 普通表单
					String formname = item.getFieldName();// 获取form中的字段名
					if (item.isFormField()) {
						if ("task_id".equals(formname)) {// 获取form中的内容
							task_id = Integer.parseInt(new String(item
									.getString("utf-8")));
						} else if ("product_value".equals(formname)) {
							product_value = new String(item.getString("utf-8"));
						} else if ("foreign_id".equals(formname)) {
							String pid_str = new String(item.getString("utf-8"));
							foreign_id = pid_str.length() == 0 ? 0 : Integer
									.parseInt(pid_str);
						}
					}
				}
				long time = System.currentTimeMillis();
				Flow flow = new Flow();
				flow.setCreate_time(time);
				flow.setForeign_id(project_pid);
				flow.setType(3);
				flow.setUid(uid);
				Operation op = new Operation();
				op.setCreate_time(time);
				op.setUid(uid);
				if (opera < 4 || opera > 11) {
					project_procurement.setTask_id(task_id);
					if (foreign_id > 0) {
						operation = 16;
						project_procurement.setProject_pid(foreign_id);
						file_pathManager.delAllFileByCondition(3, project_pid,
								1, 0);
					} else {
						operation = 1;
						project_procurement.setProject_pid(0);
						file_pathManager.saveFile(uid, sessionID, 3, project_pid, 1, 0,
								save_time);
					}
					project_procurementManager
							.updateProject_procurement(project_procurement);
					op.setContent("修改项目采购单id:" + project_pid + "的采购预算表");
				} else if (opera == 4 || opera == 6) {
					operation = 4;
					procurementManager.deleteProcurementLimit(2, project_pid);
					String[] product_valueArray = product_value.split("い");
					for (int i = 0; i < product_valueArray.length; i++) {
						String[] productArray = product_valueArray[i]
								.split("の");
						Procurement procurement = new Procurement();
						procurement.setType(2);
						procurement.setForeign_id(project_pid);
						procurement.setName(productArray[0]);
						procurement.setAgent(productArray[1]);
						procurement.setModel(productArray[2]);
						procurement.setMaterials_code(productArray[3]);
						procurement.setNum(Integer.parseInt(productArray[4]));
						procurement.setUnit(productArray[5]);
						product_procurementManager
								.insertProcurement(procurement);
					}
					file_pathManager.saveFile(uid, sessionID, 3, project_pid, 4, 0,
							save_time);
					op.setContent("修改项目采购单id:" + project_pid + "的项目采购需求单");
				}
				operationManager.insertOperation(op);
				flow.setOperation(operation);
				flowManager.insertFlow(flow);
				session.setAttribute("project_pid", project_pid);
				resp.sendRedirect("flowmanager/projectflow_detail.jsp");
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				req.getRequestDispatcher("/login.jsp").forward(req, resp);
			} finally {
				lock3.writeLock().unlock();
			}
		} else if ("outproductflow".equals(type)) {
			lock4.writeLock().lock();
			int out_pid = req.getParameter("out_pid") == null ? 0 : Integer
					.parseInt(req.getParameter("out_pid"));
			int opera = Integer.parseInt(req.getParameter("operation"));
			if (req.getParameter("uid") == null) {
				req.getRequestDispatcher("/login.jsp").forward(req, resp);
			}
			ServletFileUpload sfu = FileUploadUtil.initFileUpload();
			int product_pid = 0;
			String reason = "";
			String predict_time = null;
			String aog_time = null;
			String product_value = null;
			try {
				List<FileItem> list = sfu.parseRequest(req);
				int fLen = list.size();
				for (int j = 0; j < fLen; j++) {
					FileItem item = list.get(j);
					// 普通表单
					String formname = item.getFieldName();// 获取form中的字段名
					if (item.isFormField()) {
						if ("product_pid".equals(formname)) {// 获取form中的内容
							product_pid = Integer.parseInt(new String(item
									.getString("utf-8")));
						} else if ("reason".equals(formname)) {
							reason = new String(item.getString("utf-8"));
						} else if ("predict_time".equals(formname)) {
							predict_time = new String(item.getString("utf-8"));
						} else if ("aog_time".equals(formname)) {
							aog_time = new String(item.getString("utf-8"));
						} else if ("product_value".equals(formname)) {
							product_value = new String(item.getString("utf-8"));
						}
					}
				}
				long time = System.currentTimeMillis();
				Flow flow = new Flow();
				flow.setUid(uid);
				flow.setReason(reason);
				flow.setCreate_time(time);
				flow.setType(4);

				Operation op = new Operation();
				op.setCreate_time(time);
				op.setUid(uid);
				if (opera == 0) {
					// 新建
					Outsource_product Out_p = new Outsource_product();
					Out_p.setCreate_time(time);
					Out_p.setCreate_id(uid);
					Out_p.setProduct_pid(product_pid);
					outsource_productManager.insertOutsource_product(Out_p);
					out_pid = outsource_productManager
							.getNewOutsource_productByUID(uid);
					file_pathManager.saveFile(uid, sessionID, 4, out_pid, 1, 0, save_time);
					op.setContent("创建外协生产id:" + out_pid);
					operationManager.insertOperation(op);
					flow.setForeign_id(out_pid);
					flow.setOperation(1);
					flowManager.insertFlow(flow);
					session.setAttribute("out_pid", out_pid);
					resp.sendRedirect("flowmanager/outproductflow_detail.jsp");
					return;
				}
				int operation = flowManager.getNewFlowByFID(4, out_pid)
						.getOperation();
				if (opera != operation) {
					session.setAttribute("out_pid", out_pid);
					resp.sendRedirect("flowmanager/outproductflow_detail.jsp");
					return;
				}
				if (opera == 1) {
					// 出库
					operation = 2;
					Outsource_product out_pro = outsource_productManager
							.getOutsource_productByID(out_pid);
					out_pro.setPutout_id(uid);
					outsource_productManager.updateOutsource_product(out_pro);
					op.setContent("外协生产单id:" + out_pid + "确认出库");
				} else if (opera == 2) {
					// 生产中
					operation = 3;
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Outsource_product out_pro = outsource_productManager
							.getOutsource_productByID(out_pid);
					out_pro.setPredict_time(sdf.parse(predict_time).getTime());
					outsource_productManager.updateOutsource_product(out_pro);
					file_pathManager.saveFile(uid, sessionID, 4, out_pid, 2, 0, save_time);
					op.setContent("外协生产单id:" + out_pid + "填写预计到货时间:"
							+ predict_time);
				} else if (opera == 3) {
					// 取回
					operation = 4;
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Outsource_product out_pro = outsource_productManager
							.getOutsource_productByID(out_pid);
					out_pro.setAog_time(sdf.parse(aog_time).getTime());
					outsource_productManager.updateOutsource_product(out_pro);
					file_pathManager.saveFile(uid, sessionID, 4, out_pid, 3, 0, save_time);
					op.setContent("外协生产单id:" + out_pid + "填写取回时间:" + aog_time);
				} else if (opera == 4) {
					// 验货
					operation = 5;
					String[] procurementArray = product_value.split("い");
					int plen = procurementArray.length;
					for (int i = 0; i < plen; i++) {
						String[] productArray = procurementArray[i].split("の");
						Procurement procurement = new Procurement();
						procurement.setType(3);
						procurement.setForeign_id(out_pid);
						procurement.setName(productArray[0]);
						procurement.setAgent(productArray[1]);
						procurement.setModel(productArray[2]);
						procurement.setNum(Integer.parseInt(productArray[3]));
						procurement.setUnit(productArray[4]);
						procurement.setPass_percent(Float
								.parseFloat(productArray[5]));
						product_procurementManager
								.insertProcurement(procurement);
					}
					op.setContent("外协生产单id:" + out_pid + "验货");
				} else if (opera == 5) {
					// 入库
					operation = 6;
					Outsource_product out_pro = outsource_productManager
							.getOutsource_productByID(out_pid);
					out_pro.setPutin_id(uid);
					outsource_productManager.updateOutsource_product(out_pro);
					op.setContent("外协生产单id:" + out_pid + "确认入库");
				}
				operationManager.insertOperation(op);
				flow.setForeign_id(out_pid);
				flow.setOperation(operation);
				flowManager.insertFlow(flow);
				session.setAttribute("out_pid", out_pid);
				resp.sendRedirect("flowmanager/outproductflow_detail.jsp");
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				req.getRequestDispatcher("/login.jsp").forward(req, resp);
			} finally {
				lock4.writeLock().unlock();
			}
		} else if ("addmanufactureflow".equals(type)) {
			int num = Integer.parseInt(req.getParameter("num"));
			int task_id = Integer.parseInt(req.getParameter("task_id"));
			String p_time = req.getParameter("predict_time");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Manufacture manufacture = new Manufacture();
			long time = System.currentTimeMillis();
			manufacture.setTask_id(task_id);
			manufacture.setCreate_id(uid);
			manufacture.setCreate_time(time);
			try {
				manufacture.setPredict_time(sdf.parse(p_time).getTime());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				req.getRequestDispatcher("/login.jsp").forward(req, resp);
				return;
			}
			manufacture.setNum(num);
			manufactureManager.insertManufacture(manufacture);
			int m_id = manufactureManager.getNewManufactureByUID(uid);
			Operation op = new Operation();
			op.setUid(uid);
			op.setCreate_time(time);
			op.setContent("创建生产单id:" + m_id + "，设备台数:" + num + "台，计划完成时间:"
					+ p_time);
			operationManager.insertOperation(op);
			Flow flow = new Flow();
			flow.setCreate_time(time);
			flow.setForeign_id(m_id);
			flow.setType(5);
			flow.setUid(uid);
			flow.setOperation(1);
			flowManager.insertFlow(flow);
			session.setAttribute("m_id", m_id);
			resp.sendRedirect("flowmanager/manufactureflow_detail.jsp");
		} else if ("manufactureflow".equals(type)) {
			lock5.writeLock().lock();
			try {
				int opera = Integer.parseInt(req.getParameter("operation"));
				int m_id = Integer.parseInt(req.getParameter("m_id"));
				int operation = flowManager.getNewFlowByFID(5, m_id)
						.getOperation();
				if (opera != operation) {
					session.setAttribute("m_id", m_id);
					resp.sendRedirect("flowmanager/manufactureflow_detail.jsp");
					return;
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				long time = System.currentTimeMillis();
				Flow flow = new Flow();
				flow.setUid(uid);
				flow.setReason("");
				flow.setCreate_time(time);
				flow.setForeign_id(m_id);
				flow.setType(5);
				Operation op = new Operation();
				op.setUid(uid);
				op.setCreate_time(time);
				if (opera == 1) {
					// 请求出库
					operation = 5;
					op.setContent("生产单id:" + m_id + "请求出库");
				} else if (opera == 5) {
					operation = 2;
					op.setContent("生产单id:" + m_id + "确认出库");
				} else if (opera == 2) {
					// 生产完毕
					operation = 3;
					String reason_Str = req.getParameter("reason");
					if (reason_Str != "") {
						Manufacture manufacture = manufactureManager
								.getManufactureByID(m_id);
						manufacture.setReason(reason_Str.trim());
						manufactureManager.updateManufacture(manufacture);
						reason_Str = "<br/>备注:" + reason_Str;
					}
					op.setContent("生产单id:" + m_id + "生产完毕" + reason_Str);
				} else if (opera == 3) {
					// 入库
					operation = 4;
					op.setContent("生产单id:" + m_id + "确认入库");
				}
				operationManager.insertOperation(op);
				flow.setOperation(operation);
				flowManager.insertFlow(flow);
				session.setAttribute("m_id", m_id);
				resp.sendRedirect("flowmanager/manufactureflow_detail.jsp");
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				lock5.writeLock().unlock();
			}

		} else if ("adddevice".equals(type)) {
			lock5.writeLock().lock();
			int m_id = Integer.parseInt(req.getParameter("m_id"));
			ServletFileUpload sfu = FileUploadUtil.initFileUpload();
			int ID = 0;
			String sn = null;
			String sn0 = "";
			String sn1 = "";
			String sn2 = "";
			String sn3 = "";
			String sn4 = "";
			String sn5 = "";
			int qualify = 0;
			try {
				List<FileItem> list = sfu.parseRequest(req);
				int fLen = list.size();
				for (int j = 0; j < fLen; j++) {
					FileItem item = list.get(j);
					// 普通表单
					String formname = item.getFieldName();// 获取form中的字段名
					if (item.isFormField()) {
						if ("ID".equals(formname)) {// 获取form中的内容
							ID = Integer.parseInt(new String(item
									.getString("utf-8")));
						} else if ("sn".equals(formname)) {
							sn = new String(item.getString("utf-8"));
						} else if ("sn0".equals(formname)) {
							sn0 = new String(item.getString("utf-8"));
						} else if ("sn1".equals(formname)) {
							sn1 = new String(item.getString("utf-8"));
						} else if ("sn2".equals(formname)) {
							sn2 = new String(item.getString("utf-8"));
						} else if ("sn3".equals(formname)) {
							sn3 = new String(item.getString("utf-8"));
						} else if ("sn4".equals(formname)) {
							sn4 = new String(item.getString("utf-8"));
						} else if ("sn5".equals(formname)) {
							sn5 = new String(item.getString("utf-8"));
						} else if ("qualify".equals(formname)) {
							qualify = Integer.parseInt(new String(item
									.getString("utf-8")));
						}
					}
				}
				long time = System.currentTimeMillis();
				Device device = new Device();
				device.setM_id(m_id);
				device.setSn(sn);
				device.setQualify(qualify);
				device.setUpdate_id(uid);
				device.setUpdate_time(time);
				device.setId(ID);
				deviceManager.insertDevice(device);
				Material material = new Material();
				material.setDevice_id(ID);
				material.setSn(sn0);
				material.setType(1);
				materialManager.insertMaterial(material);
				material.setSn(sn1);
				material.setType(2);
				materialManager.insertMaterial(material);
				material.setSn(sn2);
				material.setType(3);
				materialManager.insertMaterial(material);
				material.setSn(sn3);
				material.setType(4);
				materialManager.insertMaterial(material);
				material.setSn(sn4);
				material.setType(5);
				materialManager.insertMaterial(material);
				material.setSn(sn5);
				material.setType(6);
				materialManager.insertMaterial(material);

				Operation op = new Operation();
				op.setCreate_time(time);
				op.setUid(uid);
				op.setContent("生产单id:" + m_id + "新添加一个设备，ID:" + ID);
				operationManager.insertOperation(op);
				file_pathManager.saveFile(uid, sessionID, 5, device.getId(), 1, 0, save_time);
				session.setAttribute("m_id", m_id);
				resp.sendRedirect("flowmanager/manufactureflow_detail.jsp");
			} catch (Exception e) {
				e.printStackTrace();
				req.getRequestDispatcher("/login.jsp").forward(req, resp);
			} finally {
				lock5.writeLock().unlock();
			}
		} else if ("updatedevice".equals(type)) {
			lock5.writeLock().lock();
			int device_id = Integer.parseInt(req.getParameter("device_id"));
			ServletFileUpload sfu = FileUploadUtil.initFileUpload();
			String sn = null;
			String sn0 = "";
			String sn1 = "";
			String sn2 = "";
			String sn3 = "";
			String sn4 = "";
			String sn5 = "";
			int qualify = 0;
			try {
				List<FileItem> list = sfu.parseRequest(req);
				int fLen = list.size();
				for (int j = 0; j < fLen; j++) {
					FileItem item = list.get(j);
					// 普通表单
					String formname = item.getFieldName();// 获取form中的字段名
					if (item.isFormField()) {
						if ("sn".equals(formname)) {
							sn = new String(item.getString("utf-8"));
						} else if ("sn0".equals(formname)) {
							sn0 = new String(item.getString("utf-8"));
						} else if ("sn1".equals(formname)) {
							sn1 = new String(item.getString("utf-8"));
						} else if ("sn2".equals(formname)) {
							sn2 = new String(item.getString("utf-8"));
						} else if ("sn3".equals(formname)) {
							sn3 = new String(item.getString("utf-8"));
						} else if ("sn4".equals(formname)) {
							sn4 = new String(item.getString("utf-8"));
						} else if ("sn5".equals(formname)) {
							sn5 = new String(item.getString("utf-8"));
						} else if ("qualify".equals(formname)) {
							qualify = Integer.parseInt(new String(item
									.getString("utf-8")));
						}
					}
				}
				long time = System.currentTimeMillis();
				Device device = deviceManager.getDeviceByID(device_id);
				device.setSn(sn);
				device.setQualify(qualify);
				device.setUpdate_id(uid);
				device.setUpdate_time(time);
				deviceManager.updateDevice(device);
				materialManager.delMaterialByDeviceID(device_id);
				Material material = new Material();
				material.setDevice_id(device_id);
				material.setSn(sn0);
				material.setType(1);
				materialManager.insertMaterial(material);
				material.setSn(sn1);
				material.setType(2);
				materialManager.insertMaterial(material);
				material.setSn(sn2);
				material.setType(3);
				materialManager.insertMaterial(material);
				material.setSn(sn3);
				material.setType(4);
				materialManager.insertMaterial(material);
				material.setSn(sn4);
				material.setType(5);
				materialManager.insertMaterial(material);
				material.setSn(sn5);
				material.setModel(sn5);
				material.setType(6);
				materialManager.insertMaterial(material);
				int m_id = device.getM_id();
				file_pathManager.saveFile(uid, sessionID, 5, device_id, 1, 0, save_time);

				Operation op = new Operation();
				op.setCreate_time(time);
				op.setUid(uid);
				op.setContent("生产单id:" + m_id + "修改ID为" + device_id + "的设备");
				operationManager.insertOperation(op);
				session.setAttribute("m_id", m_id);
				resp.sendRedirect("flowmanager/manufactureflow_detail.jsp");
			} catch (Exception e) {
				e.printStackTrace();
				req.getRequestDispatcher("/login.jsp").forward(req, resp);
			} finally {
				lock5.writeLock().unlock();
			}
		} else if ("addshipmentsflow".equals(type)) {
			int task_id = Integer.parseInt(req.getParameter("task_id"));
			String IDs = req.getParameter("IDs");
			Shipments shipments = new Shipments();
			long time = System.currentTimeMillis();
			shipments.setCreate_id(uid);
			shipments.setCreate_time(time);
			shipments.setTask_id(task_id);
			shipmentsManager.insertshipments(shipments);
			int ship_id = shipmentsManager.getNewShipmentsByUID(uid);
			Operation op = new Operation();
			op.setCreate_time(time);
			op.setUid(uid);
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("创建发货单id:" + ship_id + "<br/>选择设备id:");
			String[] idArray = IDs.split("-");
			for (int i = 0; i < idArray.length; i++) {
				if (i != 0) {
					stringBuilder.append("、");
				}
				stringBuilder.append(idArray[i]);
				int deviceID = Integer.parseInt(idArray[i]);
				equipmentManager.updateEquipmentShipID(deviceID,ship_id);
			}
			op.setContent(stringBuilder.toString());
			operationManager.insertOperation(op);
			Flow flow = new Flow();
			flow.setCreate_time(time);
			flow.setForeign_id(ship_id);
			flow.setType(6);
			flow.setUid(uid);
			flow.setOperation(1);
			flowManager.insertFlow(flow);
			session.setAttribute("ship_id", ship_id);
			resp.sendRedirect("flowmanager/shipmentsflow_detail.jsp");
		} else if ("shipmentsflow".equals(type)) {
			lock6.writeLock().lock();
			int ship_id = Integer.parseInt(req.getParameter("ship_id"));
			int opera = Integer.parseInt(req.getParameter("operation"));
			int operation = flowManager.getNewFlowByFID(6, ship_id)
					.getOperation();
			if (operation != opera) {
				session.setAttribute("ship_id", ship_id);
				resp.sendRedirect("flowmanager/shipmentsflow_detail.jsp");
				return;
			}
			ServletFileUpload sfu = FileUploadUtil.initFileUpload();
			int qualify = 0;
			String linkmans = req.getParameter("linkmans");
			String address = req.getParameter("address");
			String orderid = req.getParameter("orderid");
			String company = req.getParameter("company");
			String aog_time = req.getParameter("aog_time");
			try {
				/*List<FileItem> list = sfu.parseRequest(req);
				int fLen = list.size();
				for (int j = 0; j < fLen; j++) {
					FileItem item = list.get(j);
					// 普通表单
					String formname = item.getFieldName();// 获取form中的字段名
					if (item.isFormField()) {
						if ("linkmans".equals(formname)) {
							linkmans = new String(item.getString("utf-8"));
						} else if ("address".equals(formname)) {
							address = new String(item.getString("utf-8"));
						} else if ("orderid".equals(formname)) {
							orderid = new String(item.getString("utf-8"));
						} else if ("company".equals(formname)) {
							company = new String(item.getString("utf-8"));
						} else if ("aog_time".equals(formname)) {
							aog_time = new String(item.getString("utf-8"));
						}
					}
				}*/
				long time = System.currentTimeMillis();
				Flow flow = new Flow();
				flow.setCreate_time(time);
				flow.setUid(uid);
				flow.setForeign_id(ship_id);
				flow.setType(6);
				Operation op = new Operation();
				op.setCreate_time(time);
				op.setUid(uid);

				if (opera == 1) {
					operation = 2;
					op.setContent("发货单id:" + ship_id + "确认出库");
				}else if (opera == 2) {
					operation = 7;
					file_pathManager.saveFile(uid, sessionID, 6, ship_id, 1, 0, save_time);
					op.setContent("发货单id:" + ship_id + "上传成品出厂检查记录表");
				} else if (opera == 7) {
					operation = 3;
					file_pathManager.saveFile(uid, sessionID, 6, ship_id, 2, 0, save_time);
					file_pathManager.saveFile(uid, sessionID, 6, ship_id, 3, 0, save_time);
					op.setContent("发货单id:" + ship_id + "上传发货单据");
				} else if (opera == 3) {
					String[] linkmanArray = linkmans.split("い");
					for (int i = 0; i < linkmanArray.length; i++) {
						String[] userArray = linkmanArray[i].split("の");
						Linkman linkman = new Linkman();
						linkman.setType(6);
						linkman.setForeign_id(ship_id);
						linkman.setCreate_time(time);
						linkman.setState(0);
						linkman.setLinkman(userArray[0]);
						linkman.setPhone(userArray[1]);
						linkman.setLinkman_case(1);
						linkmanManager.insertLinkman(linkman);
					}
					Shipments shipments = shipmentsManager
							.getShipmentsByID(ship_id);
					shipments.setAddress(address);
					shipments.setLogistics_company(company);
					shipments.setOrder_id(orderid);
					shipmentsManager.updateShipments(shipments);
					op.setContent("发货单id:" + ship_id + "填写物流信息");
					operation = 4;
				} else if (opera == 4) {
					operation = 5;
					Shipments shipments = shipmentsManager
							.getShipmentsByID(ship_id);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					shipments.setAog_time(sdf.parse(aog_time).getTime());
					shipmentsManager.updateShipments(shipments);
					op.setContent("发货单id:" + ship_id + "填写到货时间:" + aog_time);
				} else if (opera == 5) {
					operation = 6;
					file_pathManager.saveFile(uid, sessionID, 6, ship_id, 5, 0, save_time);
					op.setContent("发货单id:" + ship_id + "上传现场开箱验货报告回执单");
				}
				operationManager.insertOperation(op);
				flow.setOperation(operation);
				flowManager.insertFlow(flow);
				session.setAttribute("ship_id", ship_id);
				resp.sendRedirect("flowmanager/shipmentsflow_detail.jsp");
				return;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				req.getRequestDispatcher("/login.jsp").forward(req, resp);
			} finally {
				lock6.writeLock().unlock();
			}
		} else if ("addtravelflow".equals(type)) {
			try {
				lock7.writeLock().lock();
				String department = req.getParameter("department");
				String address = req.getParameter("address");
				String startdate = req.getParameter("startDate");
				String enddate = req.getParameter("endDate");
				String day1 = req.getParameter("day1");
				String day2 = req.getParameter("day2");
				String reason = req.getParameter("reason");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
				Travel travel = new Travel();
				travel.setDepartment(Integer.parseInt(department));
				travel.setAddress(address);
				travel.setStarttime(sdf.parse(startdate).getTime()
						+ Integer.parseInt(day1) * 43200000l);// 加半天表示下午
				travel.setEndtime(sdf.parse(enddate).getTime()
						+ Integer.parseInt(day2) * 43200000);// 下午加0.5天表示为****/**/**
																// 12:00:00
				travel.setCreate_id(uid);
				travel.setReason(new FormTransform().transRNToBR(reason));
				long time = System.currentTimeMillis();
				travel.setCreate_time(time);
				travelManager.insertTravel(travel);
				int travel_id = travelManager.getNewTravelByCreateID(uid)
						.getId();

				Flow flow = new Flow();
				flow.setCreate_time(time);
				flow.setForeign_id(travel_id);
				flow.setUid(uid);
				flow.setType(7);
				flow.setOperation(1);
				flowManager.insertFlow(flow);
				Operation operation = new Operation();
				operation.setContent("创建出差单id:" + travel_id);
				operation.setCreate_time(time);
				operation.setUid(uid);
				operationManager.insertOperation(operation);

				session.setAttribute("travel_id", travel_id);
				resp.sendRedirect("flowmanager/travelflow_detail.jsp");
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				req.getRequestDispatcher("/login.jsp").forward(req, resp);
			} finally {
				lock7.writeLock().unlock();
			}
		} else if ("travelflow".equals(type)) {
			try {
				lock7.writeLock().lock();
				int operation = Integer.parseInt(req.getParameter("operation"));
				int travel_id = Integer.parseInt(req.getParameter("travel_id"));
				Flow flow = flowManager.getNewFlowByFID(7, travel_id);
				if (flow == null || flow.getOperation() != operation) {
					session.setAttribute("travel_id", travel_id);
					resp.sendRedirect("flowmanager/travelflow_detail.jsp");
					return;
				}
				Operation opera = new Operation();
				long time = System.currentTimeMillis();
				opera.setUid(uid);
				opera.setCreate_time(time);
				Flow flow2 = new Flow();
				flow2.setCreate_time(time);
				flow2.setForeign_id(travel_id);
				flow2.setType(7);
				flow2.setUid(uid);
				Travel travel = travelManager.getTravelByID(travel_id);
				if (operation == 6) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
					String endDate = req.getParameter("endDate");
					String halfday2 = req.getParameter("halfday2");
					flow2.setReason(String.valueOf(travel.getEndtime()));
					travel.setEndtime(sdf.parse(endDate).getTime()
							+ Integer.parseInt(halfday2) * 43200000);// 下午加0.5天表示为****/**/**
																		// 12:00:00
					// 修改出差结束时间
					travelManager.updateTravel(travel);
					flow2.setOperation(1);
					opera.setContent("出差单id:" + travel_id + "修改出差结束时间");
				} else if (operation == 1) {
					String reason = req.getParameter("reason");
					boolean isagree = "0".equals(req.getParameter("isagree"));
					flow2.setReason(reason);
					if (isagree) {
						flow2.setOperation(2);
						opera.setContent("出差单id:" + travel_id
								+ "上级领导审批通过。<br/>理由:" + reason);
					} else {
						List<Flow> reasonList = flowManager.getReasonList(7,
								travel_id);
						if (reasonList.size() > 0) {
							// 延时后上级审批失败
							Flow flow3 = reasonList.get(reasonList.size() - 1);
							if (flow3 == null || flow3.getOperation() != 1) {
								travel.setEndtime(travel.getStarttime());// 出现异常等情况用开始时间代替原结束时间，下同
							} else {
								try {
									travel.setEndtime(Long.parseLong(flow3
											.getReason()));
								} catch (Exception e) {
									// TODO: handle exception
									travel.setEndtime(travel.getStarttime());
								}
							}
							travelManager.updateTravel(travel);
							flow2.setOperation(60);
							opera.setContent("出差单id:" + travel_id
									+ "上级领导审批未通过。<br/>理由:" + reason);
						} else {
							flow2.setOperation(3);
							opera.setContent("出差单id:" + travel_id
									+ "上级领导审批未通过,审批结束。<br/>理由:" + reason);
						}
					}
				} else if (operation == 2) {
					// 考勤审批
					boolean isagree = "0".equals(req.getParameter("isagree"));
					if (isagree) {
						// 备案
						travel.setAttendance_id(uid);
						travelManager.updateTravel(travel);
						flow2.setOperation(4);
						opera.setContent("出差单id:" + travel_id + "考勤备案完成");
					} else {
						// 要求延时 该功能取消，现出差单可撤销
						/*
						 * List<Flow> reasonList=flowManager.getReasonList(7,
						 * travel_id); if(reasonList.size()>1){ //延时后上级审批失败 Flow
						 * flow3
						 * =reasonList.get(reasonList.size()-2);//上上步为operation
						 * =1》：1->2->6 if(flow3!=null&&flow3.getOperation()==1){
						 * try {
						 * travel.setEndtime(Long.parseLong(flow3.getReason()));
						 * } catch (Exception e) { // TODO: handle exception
						 * travel.setEndtime(travel.getStarttime()); } }
						 * travelManager.updateTravel(travel); }
						 * flow2.setOperation(6);
						 * opera.setContent("出差单id:"+travel_id+"出差延时");
						 */
					}
				} else if (operation == 3) {
					// 审核未通过
				} else if (operation == 4) {
					// 财务审批
					boolean isagree = "0".equals(req.getParameter("isagree"));
					if (isagree) {
						// 备案
						travel.setFinancial_id(uid);
						travelManager.updateTravel(travel);
						flow2.setOperation(5);
						opera.setContent("出差单id:" + travel_id + "财务备案完成，审批结束");
					} else {
						// 无需备案
						travelManager.updateTravel(travel);
						flow2.setOperation(8);
						opera.setContent("出差单id:" + travel_id + "无需财务备案");
					}
				} else if (operation == 8) {
					// 财务审批
					boolean isagree = "0".equals(req.getParameter("isagree"));
					if (isagree) {
						// 备案
						travelManager.updateTravel(travel);
						flow2.setOperation(5);
						opera.setContent("出差单id:" + travel_id
								+ "财务无责备案通过确认，审批结束");
					} else {
						// 无需备案
						travelManager.updateTravel(travel);
						flow2.setOperation(400);
						opera.setContent("出差单id:" + travel_id + "财务无责备案未通过");
					}
				}
				flowManager.insertFlow(flow2);
				operationManager.insertOperation(opera);
				session.setAttribute("travel_id", travel_id);
				resp.sendRedirect("flowmanager/travelflow_detail.jsp");
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				lock7.writeLock().unlock();
			}
		} else if ("addleaveflow".equals(type)) {
			try {
				lock8.writeLock().lock();
				String department = null;
				String leaveType = null;
				String startdate = null;
				String enddate = null;
				String day1 = null;
				String day2 = null;
				String reason = null;
				ServletFileUpload sfu = FileUploadUtil.initFileUpload();
				List<FileItem> list = sfu.parseRequest(req);
				int fLen = list.size();
				for (int j = 0; j < fLen; j++) {
					FileItem item = list.get(j);
					// 普通表单
					String formname = item.getFieldName();// 获取form中的字段名
					if (item.isFormField()) {
						if ("department".equals(formname)) {
							department = new String(item.getString("utf-8"));
						} else if ("leaveType".equals(formname)) {
							leaveType = new String(item.getString("utf-8"));
						} else if ("startDate".equals(formname)) {
							startdate = new String(item.getString("utf-8"));
						} else if ("endDate".equals(formname)) {
							enddate = new String(item.getString("utf-8"));
						} else if ("day1".equals(formname)) {
							day1 = new String(item.getString("utf-8"));
						} else if ("day2".equals(formname)) {
							day2 = new String(item.getString("utf-8"));
						} else if ("reason".equals(formname)) {
							reason = new String(item.getString("utf-8"));
						}
					}
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
				Leave leave = new Leave();
				leave.setDepartment(Integer.parseInt(department));
				leave.setLeave_type(Integer.parseInt(leaveType));
				leave.setStarttime(sdf.parse(startdate).getTime()
						+ Integer.parseInt(day1) * 43200000l);// 加半天表示下午
				leave.setEndtime(sdf.parse(enddate).getTime()
						+ Integer.parseInt(day2) * 43200000);// 下午加0.5天表示为****/**/**
																// 12:00:00
				leave.setCreate_id(uid);
				leave.setReason(new FormTransform().transRNToBR(reason));
				long time = System.currentTimeMillis();
				leave.setCreate_time(time);
				leaveManager.insertLeave(leave);
				int leave_id = leave.getId();
				file_pathManager.saveFile(uid, sessionID, 8, leave_id, 1, 0, save_time);
				Flow flow = new Flow();
				flow.setCreate_time(time);
				flow.setForeign_id(leave_id);
				flow.setUid(uid);
				flow.setType(8);
				flow.setOperation(1);
				flowManager.insertFlow(flow);
				Operation operation = new Operation();
				operation.setContent("创建请假单id:" + leave_id);
				operation.setCreate_time(time);
				operation.setUid(uid);
				operationManager.insertOperation(operation);
				session.setAttribute("leave_id", leave_id);
				resp.sendRedirect("flowmanager/leaveflow_detail.jsp");
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				req.getRequestDispatcher("/login.jsp").forward(req, resp);
			} finally {
				lock8.writeLock().unlock();
			}
		} else if ("leaveflow".equals(type)) {
			try {
				lock8.writeLock().lock();
				int operation = Integer.parseInt(req.getParameter("operation"));
				int leave_id = Integer.parseInt(req.getParameter("leave_id"));
				Flow flow = flowManager.getNewFlowByFID(8, leave_id);
				if (flow == null || flow.getOperation() != operation) {
					session.setAttribute("leave_id", leave_id);
					resp.sendRedirect("flowmanager/leaveflow_detail.jsp");
					return;
				}
				Operation opera = new Operation();
				long time = System.currentTimeMillis();
				opera.setUid(uid);
				opera.setCreate_time(time);
				Flow flow2 = new Flow();
				flow2.setCreate_time(time);
				flow2.setForeign_id(leave_id);
				flow2.setType(8);
				flow2.setUid(uid);
				if (operation == 1) {
					String reason = req.getParameter("reason");
					boolean isagree = "0".equals(req.getParameter("isagree"));
					flow2.setReason(reason);
					flow2.setOperation(isagree ? 2 : 3);
					opera.setContent("请假单id:" + leave_id
							+ (isagree ? "部门领导审批通过" : "部门领导审批未通过,审批结束。")
							+ "</br>理由:" + reason);
				} else if (operation == 2) {
					Leave leave = leaveManager.getLeaveByID(leave_id);
					if (leaveManager.checkLeaveCan(2, leave,
							userManager.getUserByID(uid))) {
						// 下一步为分管领导审批
						String reason = req.getParameter("reason");
						boolean isagree = "0".equals(req
								.getParameter("isagree"));
						flow2.setReason(reason);
						flow2.setOperation(isagree ? 4 : 5);
						opera.setContent("请假单id:" + leave_id
								+ (isagree ? "分管领导审批通过" : "分管审批未通过,审批结束。")
								+ "</br>理由:" + reason);
					} else {
						// 下一步为考勤备忘
						leave.setAttendance_id(uid);
						leaveManager.updateLeave(leave);
						flow2.setOperation(6);
						opera.setContent("请假单id:" + leave_id + "考勤备案完成，审批结束");
					}
				} else if (operation == 4) {
					Leave leave = leaveManager.getLeaveByID(leave_id);
					if (leaveManager.checkLeaveCan(4, leave,
							userManager.getUserByID(uid))) {
						// 下一步为总经理审批
						String reason = req.getParameter("reason");
						boolean isagree = "0".equals(req
								.getParameter("isagree"));
						flow2.setReason(reason);
						flow2.setOperation(isagree ? 7 : 8);
						opera.setContent("请假单id:" + leave_id
								+ (isagree ? "总经理审批通过" : "总经理审批未通过,审批结束。")
								+ "</br>理由:" + reason);
					} else {
						// 下一步为考勤备忘
						leave.setAttendance_id(uid);
						leaveManager.updateLeave(leave);
						flow2.setOperation(6);
						opera.setContent("请假单id:" + leave_id + "考勤备案完成，审批结束");
					}
				} else if (operation == 7) {
					Leave leave = leaveManager.getLeaveByID(leave_id);
					leave.setAttendance_id(uid);
					leaveManager.updateLeave(leave);
					flow2.setOperation(6);
					opera.setContent("请假单id:" + leave_id + "考勤备案完成，审批结束");
				}
				flowManager.insertFlow(flow2);
				operationManager.insertOperation(opera);
				session.setAttribute("leave_id", leave_id);
				resp.sendRedirect("flowmanager/leaveflow_detail.jsp");
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			} finally {
				lock8.writeLock().unlock();
			}
		} else if("delleave".equals(type)){
			int leave_id=(Integer)session.getAttribute("leave_id");
			String reason=req.getParameter("reason");
			long time = System.currentTimeMillis();
			Flow flow = new Flow();
			flow.setOperation(9);
			flow.setForeign_id(leave_id);
			flow.setType(8);
			flow.setUid(uid);
			flow.setReason(reason);
			flow.setCreate_time(time);
			flowManager.insertFlow(flow);
			Operation opera = new Operation();
			opera.setUid(uid);
			opera.setCreate_time(time);
			opera.setContent("撤销请假单id:" + leave_id+"<br/>理由："+reason);
			operationManager.insertOperation(opera);
			resp.sendRedirect("flowmanager/leaveflow_detail.jsp");
		}else if ("addResumption".equals(type)) {
			lock9.writeLock().lock();
			try {
				int foreign_id = Integer.parseInt(req
						.getParameter("foreign_id"));
				int resumptionType = Integer.parseInt(req
						.getParameter("resumptionType"));
				Resumption resumption = new Resumption();
				resumption.setForeign_id(foreign_id);
				resumption.setType(2);// 新版本只有请假单
				resumption.setCreate_id(uid);
				resumption.setReason("");
				long time = System.currentTimeMillis();
				resumption.setCreate_time(time);
				resumptionManager.insertResumption(resumption);
				int resumption_id = resumptionManager
						.getNewResumptionByCreateID(uid).getId();
				Operation operation = new Operation();
				operation.setContent("新建销假流程id:" + resumption_id + ",关联请假单id:"
						+ foreign_id);
				operation.setUid(uid);
				operation.setCreate_time(time);
				operationManager.insertOperation(operation);
				Flow flow = new Flow();
				flow.setCreate_time(time);
				flow.setForeign_id(resumption_id);
				flow.setOperation(1);
				flow.setUid(uid);
				flow.setType(9);
				flowManager.insertFlow(flow);
				session.setAttribute("resumption_id", resumption_id);
				resp.sendRedirect("flowmanager/resumptionflow_detail.jsp");
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			} finally {
				lock9.writeLock().unlock();
			}
		} else if ("resumptionflow".equals(type)) {
			lock9.writeLock().lock();
			try {
				int resumption_id = Integer.parseInt(req
						.getParameter("resumption_id"));
				int operation = Integer.parseInt(req.getParameter("operation"));
				Resumption resumption = resumptionManager
						.getResumptionByID(resumption_id);
				Flow flow = flowManager.getNewFlowByFID(9, resumption_id);
				if (flow == null || flow.getOperation() != operation) {
					session.setAttribute("resumption_id", resumption_id);
					resp.sendRedirect("flowmanager/resumptionflow_detail.jsp");
					return;
				}
				flow = new Flow();
				flow.setUid(uid);
				long time = System.currentTimeMillis();
				flow.setCreate_time(time);
				flow.setType(9);
				flow.setForeign_id(resumption_id);
				Operation opera = new Operation();
				opera.setUid(uid);
				opera.setCreate_time(time);
				if (operation == 1) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
					String start_date = req.getParameter("start_date");
					String reback_date = req.getParameter("reback_date");
					int halfDay0 = Integer.parseInt(req
							.getParameter("halfDay0"));
					int halfDay = Integer.parseInt(req.getParameter("halfDay"));
					resumption.setStarttime(sdf.parse(start_date).getTime()
							+ halfDay0 * 43200000l);// 加半天表示下午
					resumption.setReback_time(sdf.parse(reback_date).getTime()
							+ halfDay * 43200000l);// 加半天表示下午
					String resumption_reason = req
							.getParameter("resumption_reason");
					resumption.setReason(resumption_reason);
					resumptionManager.updateResumption(resumption);
					flow.setOperation(2);
					opera.setContent("销假单id:" + resumption_id + "，起始时间:"
							+ start_date + (halfDay0 == 0 ? "上午" : "下午")
							+ "，返回时间:" + reback_date
							+ (halfDay == 0 ? "上午" : "下午"));
				} else if (operation == 2) {
					String reason = req.getParameter("reason");
					boolean isagree = "0".equals(req.getParameter("isagree"));
					flow.setReason(reason);
					flow.setOperation(isagree ? 5 : 6);
					opera.setContent("销假单id:" + resumption_id
							+ (isagree ? "部门领导审批通过" : "部门领导审批未通过,审批结束。")
							+ "</br>理由:" + reason);
				} else if (operation == 3) {
					// 销假通过
				} else if (operation == 4) {
					// 该状态为老版本的审批结束状态
				} else if (operation == 5) {
					if (resumptionManager.checkResumptionCan(5, resumption,
							userManager.getUserByID(uid))) {
						// 下一步为分管领导审批
						String reason = req.getParameter("reason");
						boolean isagree = "0".equals(req
								.getParameter("isagree"));
						flow.setReason(reason);
						flow.setOperation(isagree ? 7 : 8);
						opera.setContent("销假单id:" + resumption_id
								+ (isagree ? "分管领导审批通过" : "分管审批未通过,审批结束。")
								+ "</br>理由:" + reason);
					} else {
						// 下一步为考勤备忘
						resumption.setCheck_id(uid);
						resumptionManager.updateResumption(resumption);
						flow.setOperation(3);
						opera.setContent("销假单id:" + resumption_id
								+ "考勤备案完成，审批结束");
					}
				} else if (operation == 6) {
					// 上级审批不通过
				} else if (operation == 7) {
					if (resumptionManager.checkResumptionCan(7, resumption,
							userManager.getUserByID(uid))) {
						// 下一步为总经理审批
						String reason = req.getParameter("reason");
						boolean isagree = "0".equals(req
								.getParameter("isagree"));
						flow.setReason(reason);
						flow.setOperation(isagree ? 9 : 10);
						opera.setContent("销假单id:" + resumption_id
								+ (isagree ? "总经理审批通过" : "总经理审批未通过,审批结束。")
								+ "</br>理由:" + reason);
					} else {
						// 下一步为考勤备忘
						resumption.setCheck_id(uid);
						resumptionManager.updateResumption(resumption);
						flow.setOperation(3);
						opera.setContent("销假单id:" + resumption_id
								+ "考勤备案完成，审批结束");
					}
				} else if (operation == 8) {
					// 分管领导审批不通过
				} else if (operation == 9) {
					// 总经理审批通过
					resumption.setCheck_id(uid);
					resumptionManager.updateResumption(resumption);
					flow.setOperation(3);
					opera.setContent("销假单id:" + resumption_id + "考勤备案完成，审批结束");
				} else if (operation == 3) {
					// 总经理审批不通过
				}
				flowManager.insertFlow(flow);
				operationManager.insertOperation(opera);
				session.setAttribute("resumption_id", resumption_id);
				resp.sendRedirect("flowmanager/resumptionflow_detail.jsp");
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				req.getRequestDispatcher("/login.jsp").forward(req, resp);
			} finally {
				lock9.writeLock().unlock();
			}
		} else if ("deleteTravel".equals(type)) {
			int travel_id = Integer.parseInt(req.getParameter("travel_id"));
			long time = System.currentTimeMillis();
			Flow flow = new Flow();
			flow.setOperation(7);
			flow.setForeign_id(travel_id);
			flow.setType(7);
			flow.setUid(uid);
			flow.setCreate_time(time);
			flowManager.insertFlow(flow);
			Operation opera = new Operation();
			opera.setUid(uid);
			opera.setCreate_time(time);
			opera.setContent("撤销出差单id:" + travel_id);
			operationManager.insertOperation(opera);
			resp.setContentType("application/text;charset=utf-8");
			resp.setHeader("pragma", "no-cache");
			resp.setHeader("cache-control", "no-cache");
			PrintWriter out = resp.getWriter();
			out.println(1);
			out.flush();
		} else if ("deleteResumption".equals(type)) {
			int flag = 1;// 撤销
			int resumption_id = Integer.parseInt(req
					.getParameter("resumption_id"));
			Resumption resumption = resumptionManager
					.getResumptionByID(resumption_id);
			Flow flow = flowManager.getNewFlowByFID(9, resumption_id);
			User muser = userManager.getUserByID(uid);
			if (resumption != null && flow != null && muser != null) {
				int operation = flow.getOperation();
				if (operation > 2) {
					flag = 0;// 撤销失败
				} else if (operation <= 2) {
					if (operation == 2) {// 需要提醒上级
						List<User> list = userManager
								.getParentListByChildUid(resumption
										.getCreate_id());
						if (list.size() > 0) {
							flowManager.sendMail(0,9,list.get(0),
									muser.getTruename() + "的销假单已撤销。", true);
						}
					}
					flowManager.deleteByCondition(9, resumption_id);
					resumptionManager.delResumptionByID(resumption_id);
					Operation opera = new Operation();
					opera.setUid(uid);
					opera.setCreate_time(System.currentTimeMillis());
					opera.setContent("撤销销假单id:" + resumption_id);
					operationManager.insertOperation(opera);
				}
			} else {
				if (resumption != null) {
					flag = 0;// 撤销失败
				}
			}
			resp.setContentType("application/text;charset=utf-8");
			resp.setHeader("pragma", "no-cache");
			resp.setHeader("cache-control", "no-cache");
			PrintWriter out = resp.getWriter();
			out.println(flag);
			out.flush();
		} else if ("checkHasSaveThisMonth".equals(type)) {
			int flag = 0;// 0不存在
			long state_time = Long.parseLong(req.getParameter("state_time"));
			Track track = trackManager.getSaveTrackByMonth(uid, state_time);
			if (track != null) {
				flag = track.getId();
			}
			resp.setContentType("text/html");
			resp.setCharacterEncoding("UTF-8");
			PrintWriter out = resp.getWriter();
			out.print(flag);
		} else if ("addtrackflow".equals(type)) {
			try {
				lock10.writeLock().lock();
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM");
				long state_time = sdf1.parse(
						req.getParameter("track_year") + "-"
								+ req.getParameter("track_month")).getTime();
				Track track = trackManager.getSaveTrackByMonth(uid, state_time);
				if (track != null) {
					// 重复提交，保留第一份
					session.setAttribute("track_id", track.getId());
					resp.sendRedirect("flowmanager/trackflow_detail.jsp");
					return;
				}
				String data = req.getParameter("data");
				String[] dataArray = data.split("い");
				if (dataArray.length == 0) {
					req.getRequestDispatcher("/login.jsp").forward(req, resp);
					return;
				}
				long time = System.currentTimeMillis();
				track = new Track();
				track.setCreate_time(time);
				track.setCreate_id(uid);
				track.setUpdate_time(time);
				track.setState_time(state_time);
				trackManager.insertTrack(track);
				int id = trackManager.getNewTrackByCreateID(uid);
				for (String dataStr : dataArray) {
					String[] linkArray = dataStr.split("の");
					long create_time = Long.parseLong(linkArray[0]);
					Linkman linkman = new Linkman();
					linkman.setCreate_time(create_time);
					linkman.setForeign_id(id);
					linkman.setType(10);
					linkman.setState(0);
					linkman.setLinkman_case(Integer.parseInt(linkArray[1]) + 2);// 与实际值偏差2
					linkman.setLinkman("");
					linkman.setPhone("");
					linkmanManager.insertLinkman(linkman);
				}
				Operation operation = new Operation();
				operation.setCreate_time(time);
				operation.setUid(uid);
				operation.setContent("添加跟踪表id：" + id);
				operationManager.insertOperation(operation);
				Flow flow = new Flow();
				flow.setCreate_time(time);
				flow.setForeign_id(id);
				flow.setOperation(1);
				flow.setUid(uid);
				flow.setType(10);
				flowManager.insertFlow(flow);
				session.setAttribute("track_id", id);
				resp.sendRedirect("flowmanager/trackflow_detail.jsp");
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				lock10.writeLock().unlock();
			}
		} else if ("trackflow".equals(type)) {
			lock10.writeLock().lock();
			try {
				int operation = Integer.parseInt(req.getParameter("operation"));
				int track_id = Integer.parseInt(req.getParameter("track_id"));
				Track track = trackManager.getTrackByID(track_id);
				Flow flow = flowManager.getNewFlowByFID(10, track_id);
				if (flow == null || flow.getOperation() != operation) {
					req.getRequestDispatcher(
							"/flowmanager/trackflow_detail.jsp").forward(req,
							resp);
					return;
				}
				long time = System.currentTimeMillis();
				Operation opera = new Operation();
				opera.setCreate_time(time);
				opera.setUid(uid);
				Flow flow2 = new Flow();
				flow2.setCreate_time(time);
				flow2.setType(10);
				flow2.setUid(uid);
				flow2.setForeign_id(track_id);
				if (operation == 1) {
					int isagree = Integer.parseInt(req.getParameter("isagree"));
					String reason = req.getParameter("reason");
					flow2.setReason(reason);
					if (isagree == 1) {
						Flow flow3 = flowManager.getFlowByOperation(10,
								track_id, 2);
						if (flow3 == null) {
							flow2.setOperation(3);
							opera.setContent("状态跟踪表 id：" + track_id
									+ "领导审批不予通过<br/>理由：" + reason);
							flowManager.insertFlow(flow2);
						} else {
							// 已完成的不同意后返回到结束状态
							flow2.setOperation(3);
							flowManager.insertFlow(flow2);
							flow2.setReason(null);
							flow2.setOperation(2);
							flow2.setCreate_time(flow3.getCreate_time());
							flowManager.insertFlow2(flow2);
							opera.setContent("状态跟踪表 id：" + track_id
									+ "领导审批不予通过，返回结束状态<br/>理由：" + reason);
							linkmanManager.deleteLinkmanLimit(10, track_id, 0,
									0);
							List<Linkman> linkList = linkmanManager
									.getLinkmanListLimit(10, track_id, 0, 1);
							for (Linkman linkman : linkList) {
								linkman.setState(0);
								linkmanManager.insertLinkman(linkman);
							}
							linkmanManager.deleteLinkmanLimit(10, track_id, 0,
									1);
							track.setUpdate_time(time);
							trackManager.updateTrack(track);
						}
					} else {
						linkmanManager.deleteLinkmanLimit(10, track_id, 0, 1);// 移除修改前（已完成状态）的记录
						flow2.setOperation(isagree == 0 ? 2 : 3);
						opera.setContent("状态跟踪表 id：" + track_id
								+ (isagree == 0 ? "领导审批通过" : "领导审批不予通过")
								+ "<br/>理由：" + reason);
						flowManager.insertFlow(flow2);
					}
				} else if (operation == 3) {
					int isagree = Integer.parseInt(req.getParameter("isagree"));
					String reason = req.getParameter("reason");
					flow2.setOperation(isagree == 0 ? 2 : 3);
					flow2.setReason(reason);
					opera.setContent("状态跟踪表 id：" + track_id
							+ (isagree == 0 ? "领导审批通过" : "审批不予通过") + "<br/>理由："
							+ reason);
					flowManager.insertFlow(flow2);
				}
				operationManager.insertOperation(opera);
				resp.sendRedirect("flowmanager/trackflow_detail.jsp");
			} catch (Exception e) {
				req.getRequestDispatcher("/loginl.jsp").forward(req, resp);
				return;
			} finally {
				lock10.writeLock().unlock();
			}
		} else if ("altertrackflow".equals(type)) {
			lock10.writeLock().lock();
			try {
				int operation = Integer.parseInt(req.getParameter("operation"));
				int track_id = Integer.parseInt(req.getParameter("track_id"));
				Track track = trackManager.getTrackByID(track_id);
				Flow flow = flowManager.getNewFlowByFID(10, track_id);
				if (flow == null || flow.getOperation() != operation) {
					req.getRequestDispatcher(
							"/flowmanager/trackflow_detail.jsp").forward(req,
							resp);
					return;
				}
				long time = System.currentTimeMillis();
				Operation opera = new Operation();
				opera.setCreate_time(time);
				opera.setUid(uid);
				Flow flow2 = new Flow();
				flow2.setCreate_time(time);
				flow2.setType(10);
				flow2.setUid(uid);
				flow2.setForeign_id(track_id);
				if (operation == 2) {
					List<Linkman> linkList = linkmanManager
							.getLinkmanListLimit(10, track_id, 0, 0);
					for (Linkman linkman : linkList) {
						linkman.setState(1);
						linkmanManager.insertLinkman(linkman);
					}
				}
				linkmanManager.deleteLinkmanLimit(10, track_id, 0, 0);
				String data = req.getParameter("data");
				String[] dataArray = data.split("い");
				if (dataArray.length == 0) {
					req.getRequestDispatcher("/login.jsp").forward(req, resp);
					return;
				}
				for (String dataStr : dataArray) {
					String[] linkArray = dataStr.split("の");
					long create_time = Long.parseLong(linkArray[0]);
					Linkman linkman = new Linkman();
					linkman.setCreate_time(create_time);
					linkman.setForeign_id(track_id);
					linkman.setType(10);
					linkman.setState(0);
					linkman.setLinkman_case(Integer.parseInt(linkArray[1]) + 2);// 与实际值偏差2
																				// 防止影响搜索全部
					linkman.setLinkman("");
					linkman.setPhone("");
					linkmanManager.insertLinkman(linkman);
				}
				flow2.setOperation(1);
				flowManager.insertFlow(flow2);
				opera.setContent("修改状态跟踪表 id：" + track_id);
				operationManager.insertOperation(opera);
				track.setUpdate_time(time);
				trackManager.updateTrack(track);
				resp.sendRedirect("flowmanager/trackflow_detail.jsp");
			} catch (Exception e) {
				req.getRequestDispatcher("/loginl.jsp").forward(req, resp);
				return;
			} finally {
				lock10.writeLock().unlock();
			}
		} else if ("cancleTask".equals(type)) {
			try {
				lock1.writeLock().lock();
				long time = System.currentTimeMillis();
				int task_id = (Integer)req.getSession().getAttribute("task_id");
				Task task = taskManager.getTaskByID(task_id);
				task.setIsedited(0);
				taskManager.updateEdited(task);
				String reason = req.getParameter("reason");
				Flow flow = new Flow();
				flow.setCreate_time(time);
				flow.setUid(uid);
				flow.setType(1);
				flow.setReason(reason);
				flow.setForeign_id(task_id);
				flow.setOperation(12);
				flowManager.insertFlow(flow);
				Operation operation = new Operation();
				operation.setContent("撤销任务单 id：" + task_id);
				operation.setCreate_time(time);
				operation.setUid(uid);
				operationManager.insertOperation(operation);
				task_conflictManager.delTask_conflictByID(task_id);
				session.setAttribute("task_id", task_id);
				resp.sendRedirect("flowmanager/taskflow_detail.jsp");
			} catch (Exception e) {
				resp.sendRedirect("flowmanager/taskflow_detail.jsp");
			} finally {
				lock1.writeLock().unlock();
			}
		} else if ("recoverTask".equals(type)) {
			try {
				lock1.writeLock().lock();
				long time = System.currentTimeMillis();
				int task_id = (Integer)req.getSession().getAttribute("task_id");
				flowManager.deleteRecentOperat(1, task_id, 12);// 恢复到上一步
				Operation operation = new Operation();
				operation.setContent("恢复已撤销的任务单 id：" + task_id);
				operation.setCreate_time(time);
				operation.setUid(uid);
				operationManager.insertOperation(operation);
				session.setAttribute("task_id", task_id);
				resp.sendRedirect("flowmanager/taskflow_detail.jsp");
			} catch (Exception e) {
				resp.sendRedirect("flowmanager/taskflow_detail.jsp");
			} finally {
				lock1.writeLock().unlock();
			}
		} else if ("checkTimeScope".equals(type)) {
			// 判断该段时间是否有重复 仅用于请假和出差流程
			int flag = 0;// 0：:存在 1：不存在
			String flowType = req.getParameter("flowType");
			long starttime = Long.parseLong(req.getParameter("starttime"));
			long endtime = Long.parseLong(req.getParameter("endtime"));
			if ("7".equals(flowType)) {
				if (!travelManager.checkTravelInScope(starttime, endtime, uid)) {
					flag = 1;
				}
			} else if ("8".equals(flowType)) {
				if (!leaveManager.checkLeaveInScope(starttime, endtime, uid)) {
					flag = 1;
				}
			}
			resp.setContentType("application/text;charset=utf-8");
			resp.setHeader("pragma", "no-cache");
			resp.setHeader("cache-control", "no-cache");
			PrintWriter out = resp.getWriter();
			out.print(flag);
			out.flush();
		}else if("getAllFileGroupByState".equals(type)){
			int task_id=(Integer)session.getAttribute("task_id");
			List<File_path> file_paths=file_pathManager.getAllFileGroupByState(1, task_id, 3);
			resp.setContentType("text/json"); 
	        resp.setCharacterEncoding("UTF-8"); 
			PrintWriter out = resp.getWriter();
			out.print(JSONArray.fromObject(file_paths));
			out.flush();
		} else if("alertTaskProjectFile".equals(type)){
			int task_id=(Integer)session.getAttribute("task_id");
			int maxState=file_pathManager.getMaxStateByCondition(1,task_id,3);
			file_pathManager.saveFile(uid, sessionID, 1, task_id, 3, ++maxState, save_time);
			Operation operation=new Operation();
			operation.setContent("工程启动任务单id："+task_id+"重新上传项目材料配置单");
			operation.setUid(uid);
			operation.setCreate_time(System.currentTimeMillis());
			operationManager.insertOperation(operation);
			flowManager.alertTaskProjectFileSendEmail(task_id);
			resp.setContentType("application/text;charset=utf-8");
			resp.setHeader("pragma", "no-cache");
			resp.setHeader("cache-control", "no-cache");
			PrintWriter out = resp.getWriter();
			out.println();
			out.flush();
		}else if("alertTaskProjectFile4".equals(type)){
			int task_id=(Integer)session.getAttribute("task_id");
			int maxState=file_pathManager.getMaxStateByCondition(1,task_id,4);

			file_pathManager.saveFile(uid, sessionID, 1, task_id, 4, ++maxState, save_time);
			Operation operation=new Operation();
			operation.setContent("工程启动任务单id："+task_id+"重新上传项目材料配置单");
			operation.setUid(uid);
			operation.setCreate_time(System.currentTimeMillis());
			operationManager.insertOperation(operation);
			flowManager.alertTaskProjectFileSendEmail(task_id);
			resp.setContentType("application/text;charset=utf-8");
			resp.setHeader("pragma", "no-cache");
			resp.setHeader("cache-control", "no-cache");
			PrintWriter out = resp.getWriter();
			out.println();
			out.flush();
		}else if("alertTaskProjectFile5".equals(type)){
			int task_id=(Integer)session.getAttribute("task_id");
			int maxState=file_pathManager.getMaxStateByCondition(1,task_id,5);

			file_pathManager.saveFile(uid, sessionID, 1, task_id, 5, ++maxState, save_time);
			Operation operation=new Operation();
			operation.setContent("工程启动任务单id："+task_id+"重新上传出厂图纸");
			operation.setUid(uid);
			operation.setCreate_time(System.currentTimeMillis());
			operationManager.insertOperation(operation);
			flowManager.alertTaskProjectFileSendEmail(task_id);
			resp.setContentType("application/text;charset=utf-8");
			resp.setHeader("pragma", "no-cache");
			resp.setHeader("cache-control", "no-cache");
			PrintWriter out = resp.getWriter();
			out.println();
			out.flush();
		}else if("alertTaskProjectFile6".equals(type)){
			int task_id=(Integer)session.getAttribute("task_id");
			int maxState=file_pathManager.getMaxStateByCondition(1,task_id,6);

			file_pathManager.saveFile(uid, sessionID, 1, task_id, 6, ++maxState, save_time);
			Operation operation=new Operation();
			operation.setContent("工程启动任务单id："+task_id+"上传备注文件");
			operation.setUid(uid);
			operation.setCreate_time(System.currentTimeMillis());
			operationManager.insertOperation(operation);
			flowManager.alertTaskProjectFileSendEmail(task_id);
			resp.setContentType("application/text;charset=utf-8");
			resp.setHeader("pragma", "no-cache");
			resp.setHeader("cache-control", "no-cache");
			PrintWriter out = resp.getWriter();
			out.println();
			out.flush();
		}else if("record".equals(type)){
			/***
			 * 在待办事项中统一考勤备案
			 */
			String travel_ids=req.getParameter("travel_ids");
			String leave_ids=req.getParameter("leave_ids");
			String resumption_ids=req.getParameter("resumption_ids");
			long nowTime=System.currentTimeMillis();
			Flow flow =new Flow();
			flow.setUid(uid);
			flow.setCreate_time(nowTime);
			Operation operation=new Operation();
			operation.setCreate_time(nowTime);
			operation.setUid(uid);
			if(travel_ids!=null&&travel_ids.length()>0){
				String[] ids=travel_ids.split("の");
				flow.setOperation(4);
				flow.setType(7);
				for (String travelid_str : ids) {
					int travel_id=Integer.parseInt(travelid_str);
					Travel travel=travelManager.getTravelByID(travel_id);
					if(travel!=null){
						travel.setAttendance_id(uid);
						travelManager.updateTravel(travel);
						flow.setForeign_id(travel_id);
						operation.setContent("出差单id:" + travel_id + "考勤备案完成");
						flowManager.insertFlow(flow);
						operationManager.insertOperation(operation);
					}
				}
			}
			if(leave_ids!=null&&leave_ids.length()>0){
				String[] ids=leave_ids.split("の");
				flow.setOperation(6);
				flow.setType(8);
				for (String leaveid_str : ids) {
					int leave_id=Integer.parseInt(leaveid_str);
					Leave leave=leaveManager.getLeaveByID(leave_id);
					if(leave!=null){
						leave.setAttendance_id(uid);
						leaveManager.updateLeave(leave);
						flow.setForeign_id(leave_id);
						operation.setContent("请假单id:" + leave_id + "考勤备案完成，审批结束");
						flowManager.insertFlow(flow);
						operationManager.insertOperation(operation);
					}
				}
			}
			if(resumption_ids!=null&&resumption_ids.length()>0){
				String[] ids=resumption_ids.split("の");
				flow.setOperation(3);
				flow.setType(9);
				for (String resumptionid_str : ids) {
					int resumption_id=Integer.parseInt(resumptionid_str);
					Resumption resumption=resumptionManager.getResumptionByID(resumption_id);
					if(resumption!=null){
						resumption.setCheck_id(uid);
						resumptionManager.updateResumption(resumption);
						flow.setForeign_id(resumption_id);
						operation.setContent("销假单id:" + resumption_id+ "考勤备案完成，审批结束");
						flowManager.insertFlow(flow);
						operationManager.insertOperation(operation);
					}
				}
			}
			resp.setContentType("application/text;charset=utf-8");
			resp.setHeader("pragma", "no-cache");
			resp.setHeader("cache-control", "no-cache");
			PrintWriter out = resp.getWriter();
			out.println(1);
			out.flush();
		}else {
			req.getRequestDispatcher("/login.jsp").forward(req, resp);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		req.setCharacterEncoding("utf-8");
		resp.setCharacterEncoding("utf-8");
		HttpSession session = req.getSession();
		String type = req.getParameter("type");
		if(session.getAttribute("uid")==null){
			resp.sendRedirect("login.jsp");
			return;
		}
		int uid=(Integer)session.getAttribute("uid");
		if ("reset".equals(type)) {
			int index = Integer.parseInt(req.getParameter("index"));
			int len = DataUtil.getFlowTypeArray().length;
			String type_flows = "";
			if (0 == index) {
				for (int i = 1; i < len; i++) {
					if(i==5){
						type_flows += 0;
					}else{
						type_flows += 1;
					}
				}
			} else {
				for (int i = 1; i < len; i++) {
					type_flows += (index == i) ? "1" : "0";
				}
			}
			session.setAttribute("type_flows", type_flows);
			session.setAttribute("keywords_flows", "");
			session.setAttribute("nowpage_flows", 1);
			session.setAttribute("newtime_flows", 0);// 不选新建时间
			session.setAttribute("nowtime_flows", 0);// 不选当前时间
			session.setAttribute("isjoin", 1);// 是否参与：全部
			session.setAttribute("process", 1);// 进度过滤：全部
			session.setAttribute("hangUp", 1);// 挂起过滤：全部
			session.setAttribute("stage", 0);//销售阶段：全部
			resp.sendRedirect("flowmanager/flowlist.jsp");
		} else if ("saveBacklogType".equals(type)) {
			int index = Integer.parseInt(req.getParameter("index"));
			session.setAttribute("backlog_type", index);
			resp.sendRedirect("flowmanager/backlog.jsp");
		} else if ("flowdetail".equals(type)) {
			// 跳到详情页面
			try {
				int flowType = Integer.parseInt(req.getParameter("flowtype"));
				int id = Integer.parseInt(req.getParameter("id"));
				switch (flowType) {
				case 1:
					session.setAttribute("task_id", id);
					Task task=taskManager.getTaskByID(id);
					if(task==null){
						resp.sendRedirect("login.jsp");
						return;
					}
					if(task.getType()==0){
						resp.sendRedirect("flowmanager/taskflow_detail.jsp");
					}else{
						resp.sendRedirect("flowmanager/startuptaskflow_detail.jsp");
					}
					break;
				case 2:
					session.setAttribute("product_pid", id);
					resp.sendRedirect("flowmanager/productflow_detail.jsp");
					break;
				case 3:
					session.setAttribute("project_pid", id);
					resp.sendRedirect("flowmanager/projectflow_detail.jsp");
					break;
				case 4:
					session.setAttribute("out_pid", id);
					resp.sendRedirect("flowmanager/outproductflow_detail.jsp");
					break;
				case 5:
					session.setAttribute("m_id", id);
					resp.sendRedirect("flowmanager/manufactureflow_detail.jsp");
					break;
				case 6:
					session.setAttribute("ship_id", id);
					resp.sendRedirect("flowmanager/shipmentsflow_detail.jsp");
					break;
				case 7:
					session.setAttribute("travel_id", id);
					resp.sendRedirect("flowmanager/travelflow_detail.jsp");
					break;
				case 8:
					session.setAttribute("leave_id", id);
					resp.sendRedirect("flowmanager/leaveflow_detail.jsp");
					break;
				case 9:
					session.setAttribute("resumption_id", id);
					resp.sendRedirect("flowmanager/resumptionflow_detail.jsp");
					break;
				case 10:
					session.setAttribute("device_id", id);
					resp.sendRedirect("flowmanager/device_detail.jsp");
					break;
				case 11:
					session.setAttribute("device_id", id);
					resp.sendRedirect("flowmanager/update_device.jsp");
					break;
				case 12:
					session.setAttribute("m_id", id);
					resp.sendRedirect("flowmanager/add_device.jsp");
					break;
				case 13:
					session.setAttribute("task_id", id);
					resp.sendRedirect("flowmanager/update_taskflow.jsp");
					break;
				case 14:
					session.setAttribute("product_pid", id);
					resp.sendRedirect("flowmanager/update_productflow.jsp");
					break;
				case 15:
					session.setAttribute("project_pid", id);
					resp.sendRedirect("flowmanager/update_projectflow.jsp");
					break;
				case 16:
					session.setAttribute("task_id", id);
					resp.sendRedirect("flowmanager/taskflow_conflict.jsp");
					break;
				case 17:
					session.setAttribute("track_id", id);
					resp.sendRedirect("flowmanager/trackflow_detail.jsp");
					break;
				case 18:
					session.setAttribute("track_id", id);
					resp.sendRedirect("flowmanager/update_trackflow.jsp");
					break;
				case 19:
					session.setAttribute("sales_id", id);
					resp.sendRedirect("flowmanager/salesflow_detail.jsp");
					break;
				case 20:
					session.setAttribute("purchase_id", id);
					resp.sendRedirect("flowmanager/purchaseflow_detail.jsp");
					break;
				case 21:
					session.setAttribute("aftersales_tid", id);
					resp.sendRedirect("flowmanager/aftersalestaskflow_detail.jsp");
					break;
				case 22:
					session.setAttribute("seal_id", id);
					resp.sendRedirect("flowmanager/sealflow_detail.jsp");
					break;
				case 23:
					session.setAttribute("vehicle_id", id);
					resp.sendRedirect("flowmanager/vehicleflow_detail.jsp");
					break;
				case 24:
					session.setAttribute("work_id", id);
					resp.sendRedirect("flowmanager/workflow_detail.jsp");
					break;
				case 25:
					session.setAttribute("task_id", id);
					resp.sendRedirect("flowmanager/startuptaskflow_detail.jsp");
					break;
				case 26:
					session.setAttribute("task_id", id);
					resp.sendRedirect("flowmanager/update_startuptaskflow.jsp");
					break;
				case 27:
					session.setAttribute("task_id", id);
					resp.sendRedirect("flowmanager/startuptaskflow_conflict.jsp");
					break;
				case 28:
					session.setAttribute("shipping_id", id);
					resp.sendRedirect("flowmanager/shippingflow_detail.jsp");
					break;
				case 29:
					session.setAttribute("shipping_id", id);
					session.setAttribute("shipping_printType", 0);
					resp.sendRedirect("flowmanager/preview_shipping.jsp");
					break;
				case 30:
					session.setAttribute("shipping_id", id);
					session.setAttribute("shipping_printType", 1);
					resp.sendRedirect("flowmanager/preview_shipping.jsp");
					break;
				case 31:
					session.setAttribute("shipping_id", id);
					resp.sendRedirect("flowmanager/update_shippingflow.jsp");
					break;
				case 32:
					Performance performance=performanceManager.getPerformanceByID(id);
					if(performance==null){
						resp.sendRedirect("flowmanager/login.jsp");
					}else{
						if(uid==performance.getCreate_id()&&performance.getOperation()!=6){
							resp.sendRedirect("flowmanager/create_performanceflow.jsp");
						}else{
							session.setAttribute("performance_month",performance.getPerformance_month());
							session.setAttribute("performance_cid",performance.getCreate_id());
							resp.sendRedirect("flowmanager/performance_detailflow.jsp");
						}
					}
					break;
				case 33:
					session.setAttribute("deliver_id", id);
					resp.sendRedirect("flowmanager/deliver_detailflow.jsp");
					break;
				case 34:
					session.setAttribute("update_did", id);
					session.setAttribute("deliver_id", id);
					resp.sendRedirect("flowmanager/create_deliverflow.jsp");
					break;
				case 35:
					session.setAttribute("deliver_id", id);
					resp.sendRedirect("flowmanager/preview_deliver.jsp");
					break;
				case 36:
					session.setAttribute("departPuchase_id", id);
					resp.sendRedirect("flowmanager/departmentPuchase_detailflow.jsp");
					break;
				case 37:
					session.setAttribute("update_did", id);
					session.setAttribute("departPuchase_id", id);
					resp.sendRedirect("flowmanager/create_departmentPuchaseflow.jsp");
					break;
				case 40:
					session.setAttribute("task_id", id);
					resp.sendRedirect("flowmanager/task_updateflow.jsp");
					break;
				case 41:
					Task_updateflow task_updateflow = task_updateflowManager.getTask_updateflowById(id);
					if(task_updateflow==null){
						resp.sendRedirect("flowmanager/login.jsp");
					}
					session.setAttribute("task_id", task_updateflow.getForeign_id());
					session.setAttribute("task_updateflow_id", id);
					resp.sendRedirect("flowmanager/task_updateflow.jsp");
					break;
				case 42:
					session.setAttribute("task_id", id);
					resp.sendRedirect("flowmanager/task_projectflowlist.jsp");
					break;
				case 43:
//					session.setAttribute("task_id", id);
					resp.sendRedirect("flowmanager/all_vehicleflowlist.jsp");
					break;
				default:
					resp.sendRedirect("login.jsp");
					break;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				resp.sendRedirect("login.jsp");
			}
			return;
		} else {
			req.getRequestDispatcher("/login.jsp").forward(req, resp);
		}
	}

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		task_conflictManager = (Task_conflictManager) WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"task_conflictManager");
		taskManager = (TaskManager) WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"taskManager");
		linkmanManager = (LinkmanManager) WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"linkmanManager");
		file_pathManager = (File_pathManager) WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"file_pathManager");
		flowManager = (FlowManager) WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"flowManager");
		product_procurementManager = (Product_procurementManager) WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"product_procurementManager");
		procurementManager = (ProcurementManager) WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"procurementManager");
		project_procurementManager = (Project_procurementManager) WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"project_procurementManager");
		outsource_productManager = (Outsource_productManager) WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"outsource_productManager");
		manufactureManager = (ManufactureManager) WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"manufactureManager");
		materialManager = (MaterialManager) WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"materialManager");
		deviceManager = (DeviceManager) WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"deviceManager");
		shipmentsManager = (ShipmentsManager) WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"shipmentsManager");
		operationManager = (OperationManager) WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"operationManager");
		userManager = (UserManager) WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"userManager");
		travelManager = (TravelManager) WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"travelManager");
		leaveManager = (LeaveManager) WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"leaveManager");
		resumptionManager = (ResumptionManager) WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"resumptionManager");
		trackManager = (TrackManager) WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"trackManager");
		equipmentManager=(EquipmentManager)WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"equipmentManager");
		performanceManager=(PerformanceManager)WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"performanceManager");
		task_updateflowManager=(Task_updateflowManager)WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"task_updateflowManager");
	}
}