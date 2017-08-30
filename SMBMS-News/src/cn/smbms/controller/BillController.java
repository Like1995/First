package cn.smbms.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.smbms.pojo.Bill;
import cn.smbms.pojo.Provider;
import cn.smbms.pojo.User;
import cn.smbms.service.bill.BillService;
import cn.smbms.service.provider.ProviderService;
import cn.smbms.tools.Constants;
import cn.smbms.tools.PageSupport;
@Controller
@RequestMapping("/bill")
public class BillController {
	@Resource
	private BillService billService;
	@Resource
	private ProviderService providerService;
	/**
	 * 模糊查询
	 * @param model
	 * @param queryProductName
	 * @param queryProviderId
	 * @param queryIsPayment
	 * @param pageIndex
	 * @return
	 */

	/**
	 * 模糊查询显示界面
	 * @param model
	 * @param queryProductName
	 * @param queryProviderId
	 * @param queryIsPayment
	 * @param pageIndex
	 * @return
	 */
	@RequestMapping(value="/billlist1")
	public String getBillList(Model model,
			@RequestParam(value = "queryProductName", required = false) String queryProductName,
			@RequestParam(value = "queryProviderId", required = false) String queryProviderId,
			@RequestParam(value = "queryIsPayment", required = false) String queryIsPayment,
			@RequestParam(value = "pageIndex", required = false) String pageIndex){
		Bill bill=new Bill();
		int queryIsPayment1 = 0;
		int queryProviderId1=0;
		List<Bill> billList = null;
		//设置页面容量
    	int pageSize = Constants.pageSize;
    	//当前页码
    	int currentPageNo = 1;
		if(queryProductName == null){
			queryProductName = "";
		}
		if(queryProviderId != null){
			queryProviderId1 =Integer.parseInt(queryProviderId);
		}
		if(queryIsPayment != null){
			queryIsPayment1 = Integer.parseInt(queryIsPayment);
		}
    	if(pageIndex != null){
    		try{
    			currentPageNo = Integer.valueOf(pageIndex);
    		}catch(NumberFormatException e){
    			return "redirect:/bill/syserror";
    		}
    	}
    	bill.setProductName(queryProductName);
    	bill.setProviderId(queryProviderId1);
    	bill.setIsPayment(queryIsPayment1);
    	//总数量（表）	
    	int totalCount	= billService.getBillCount(queryProductName, queryProviderId1, queryIsPayment1);
    	//总页数
    	PageSupport pages=new PageSupport();
    	pages.setCurrentPageNo(currentPageNo);
    	pages.setPageSize(pageSize);
    	pages.setTotalCount(totalCount);
    	int totalPageCount = pages.getTotalPageCount();
    	//控制首页和尾页
    	if(currentPageNo < 1){
    		currentPageNo = 1;
    	}else if(currentPageNo > totalPageCount){
    		currentPageNo = totalPageCount;
    	}
    	List<Provider> providerlist=new ArrayList<Provider>();
    	providerlist=providerService.getNameList();
    	billList = billService.getBillList(bill,currentPageNo, pageSize);
    	model.addAttribute("providerList", providerlist);
		model.addAttribute("billList", billList);
		model.addAttribute("queryProductName", queryProductName);
		model.addAttribute("queryProviderId", queryProviderId1);
		model.addAttribute("queryIsPayment", queryIsPayment1);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("currentPageNo", currentPageNo);
		return "billlist";
	}
	/**
	 * 查看单条数据信息
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/view{id}",method=RequestMethod.GET)
	public String view(@PathVariable String id,Model model){
		Bill bill=new Bill();
		bill=billService.getBillById(id);
		model.addAttribute("bill", bill);
		return "billview";
	}
	/**
	 * 跳转进入修改页面
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/billModify",method=RequestMethod.GET)
	public String getBillModify(String id,Model model){
		Bill bill=new Bill();
		List<Provider> providerList=new ArrayList<Provider>();
		providerList=providerService.getNameList();
		bill=billService.getBillById(id);
		model.addAttribute("bill", bill);
		model.addAttribute("providerList", providerList);
		return "billmodify";
	}
	/**
	 * 修改订单信息
	 * @param bill
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/billModifySave",method=RequestMethod.POST)
	public String billModifySave(Bill bill,HttpSession session){
		bill.setModifyBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
		bill.setModifyDate(new Date());
		
		if(billService.modify(bill)){
			return "redirect:/bill/billlist1";
		}
		return "billmodify";
	}
	/**
	 * 跳转到添加页面
	 * @return
	 */
	@RequestMapping(value="add",method=RequestMethod.GET)
	public String getBillAdd(Model model){
		List<Provider> providerList=new ArrayList<Provider>();
		providerList=providerService.getNameList();
		model.addAttribute("providerList", providerList);
		return "billadd";
	}
	/**
	 * 添加信息保存
	 * @param bill
	 * @param session
	 * @return
	 */
	@RequestMapping(value="billAddSave",method=RequestMethod.POST)
	public String billAddSave(Bill bill,HttpSession session){
		bill.setCreatedBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
		bill.setCreationDate(new Date());
		if(billService.add(bill)){
			return "redirect:/bill/billlist1";
		}
		return "billadd";
	}
	/**
	 * 根据id删除bill
	 * @param billid
	 * @return
	 */
	@RequestMapping(value="/delbill")
	@ResponseBody
	public HashMap<String, String>delBill(@RequestParam String billid){
		HashMap<String,String> hasmap=new HashMap<String, String>();
		if(billid==null||"".equals(billid)){
			hasmap.put("delResult", "notexist");
		}else{
			if(billService.deleteBillById(billid)){
				hasmap.put("delResult","true");
			}else{
				hasmap.put("delResult", "false");
			}
		}
		return hasmap;
	}
	
}
