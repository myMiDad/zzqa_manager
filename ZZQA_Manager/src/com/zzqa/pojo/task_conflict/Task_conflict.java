package com.zzqa.pojo.task_conflict;

import java.io.Serializable;

public class Task_conflict implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8017100768869429044L;
	private int id;//唯一序号
	private int task_id;//任务单id 唯一 修改时判断是否存在，不存在才新建
	private int project_category;//项目分类 0：发电；1：石化
	private int product_type;//产品类型 0：CS2000；1：CS2200；2:DS9000；3：DS9100有线版；4：DS9100无线版
	private String project_id;//项目编号
	private String project_name;//项目名称
	private String project_life;//项目质保期
	private String project_report_peried;//项目诊断报告周期
	private String address;//发货地址
	private int project_case;//项目情况 0：普项；1：急项
	private int stage;//销售阶段 1：试用；2：已签订技术协议；3：已签订商务合同
	private int project_type;//项目类型：0：新建项目；1：技改项目
	private String customer;//用户名称
	private long delivery_time;//要求发货时间
	private long contract_time;//合同生效时间
	private String delivery_timestr;//要求发货时间
	private String contract_timestr;//合同生效时间
	private String description;//项目说明及特殊要求
	private int inspection;//0：要求施工前现场开箱验货；1：不要求
	private int verify;//0：发货前需和销售经理确认；1：不需要
	private int protocol;//0：有技术协议；1没有
	private String other;//其他说明
	private String other2;//其他说明
	private String other3;//其他说明
	private String other4;//其他说明
	private String other5;//其他说明
	private String other6;//其他说明
	private String remarks;//备注
	private int create_id;//创建者id
	private int type;//0:老任务单；1：项目启动任务单

	//新加字段
	private String fan_num;//风机数量
	private String factory;//主机厂家
	private String submit_date;//项目交期（周）
	private String contract_type;//合同类型
	private String equipment_type;//设备类型
	private String consignee;//收货人
	private String fan_product_type;//产品类型（风电项目）
	private String is_new_data;//是否是新数据

	public String getFan_num() {
		return fan_num;
	}

	public void setFan_num(String fan_num) {
		this.fan_num = fan_num;
	}

	public String getFactory() {
		return factory;
	}

	public void setFactory(String factory) {
		this.factory = factory;
	}

	public String getSubmit_date() {
		return submit_date;
	}

	public void setSubmit_date(String submit_date) {
		this.submit_date = submit_date;
	}

	public String getContract_type() {
		return contract_type;
	}

	public void setContract_type(String contract_type) {
		this.contract_type = contract_type;
	}

	public String getEquipment_type() {
		return equipment_type;
	}

	public void setEquipment_type(String equipment_type) {
		this.equipment_type = equipment_type;
	}

	public String getConsignee() {
		return consignee;
	}

	public void setConsignee(String consignee) {
		this.consignee = consignee;
	}

	public String getFan_product_type() {
		return fan_product_type;
	}

	public void setFan_product_type(String fan_product_type) {
		this.fan_product_type = fan_product_type;
	}

	public String getIs_new_data() {
		return is_new_data;
	}

	public void setIs_new_data(String is_new_data) {
		this.is_new_data = is_new_data;
	}

	public int getType() {
		return type;
	}
	public String getOther2() {
		return other2;
	}
	public void setOther2(String other2) {
		this.other2 = other2;
	}
	public String getOther3() {
		return other3;
	}
	public void setOther3(String other3) {
		this.other3 = other3;
	}
	public String getOther4() {
		return other4;
	}
	public void setOther4(String other4) {
		this.other4 = other4;
	}
	public String getOther5() {
		return other5;
	}
	public void setOther5(String other5) {
		this.other5 = other5;
	}
	public String getOther6() {
		return other6;
	}
	public void setOther6(String other6) {
		this.other6 = other6;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getProject_category() {
		return project_category;
	}
	public void setProject_category(int project_category) {
		this.project_category = project_category;
	}
	public long getContract_time() {
		return contract_time;
	}
	public void setContract_time(long contract_time) {
		this.contract_time = contract_time;
	}
	public String getContract_timestr() {
		return contract_timestr;
	}
	public void setContract_timestr(String contract_timestr) {
		this.contract_timestr = contract_timestr;
	}
	public int getProduct_type() {
		return product_type;
	}
	public void setProduct_type(int product_type) {
		this.product_type = product_type;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getTask_id() {
		return task_id;
	}
	public void setTask_id(int task_id) {
		this.task_id = task_id;
	}
	public String getProject_id() {
		return project_id;
	}
	public void setProject_id(String project_id) {
		this.project_id = project_id;
	}
	public String getProject_name() {
		return project_name;
	}
	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}
	public int getProject_case() {
		return project_case;
	}
	public void setProject_case(int project_case) {
		this.project_case = project_case;
	}
	public int getStage() {
		return stage;
	}
	public void setStage(int stage) {
		this.stage = stage;
	}
	public int getProject_type() {
		return project_type;
	}
	public void setProject_type(int project_type) {
		this.project_type = project_type;
	}
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	public long getDelivery_time() {
		return delivery_time;
	}
	public void setDelivery_time(long delivery_time) {
		this.delivery_time = delivery_time;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getInspection() {
		return inspection;
	}
	public void setInspection(int inspection) {
		this.inspection = inspection;
	}
	public int getVerify() {
		return verify;
	}
	public void setVerify(int verify) {
		this.verify = verify;
	}
	public int getProtocol() {
		return protocol;
	}
	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}
	public String getOther() {
		return other;
	}
	public void setOther(String other) {
		this.other = other;
	}
	public int getCreate_id() {
		return create_id;
	}
	public void setCreate_id(int create_id) {
		this.create_id = create_id;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getDelivery_timestr() {
		return delivery_timestr;
	}
	public void setDelivery_timestr(String delivery_timestr) {
		this.delivery_timestr = delivery_timestr;
	}
	public String getProject_life() {
		return project_life;
	}
	public void setProject_life(String project_life) {
		this.project_life = project_life;
	}
	public String getProject_report_peried() {
		return project_report_peried;
	}
	public void setProject_report_peried(String project_report_peried) {
		this.project_report_peried = project_report_peried;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
}
