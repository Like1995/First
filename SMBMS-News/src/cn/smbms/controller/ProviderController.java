package cn.smbms.controller;


import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.smbms.pojo.Provider;
import cn.smbms.pojo.User;
import cn.smbms.service.provider.ProviderService;
import cn.smbms.tools.Constants;
import cn.smbms.tools.PageSupport;

@Controller
@RequestMapping("/provider")
public class ProviderController {
	@Resource
	private ProviderService providerService;
	/*
	 * 模糊查询显示provider供应商数据
	 */
	@RequestMapping(value="/providerlist")
	public String getProviderList(
			Model model,
			@RequestParam(value = "queryProName", required = false) String queryProName,
			@RequestParam(value = "queryProCode", required = false) String queryProCode,
			@RequestParam(value = "pageIndex", required = false) String pageIndex) {
		List<Provider> providerList = null;
		//设置页面容量
    	int pageSize = Constants.pageSize;
    	//当前页码
    	int currentPageNo = 1;
		if(queryProName == null){
			queryProName = "";
		}
		if(queryProCode == null){
			queryProCode = "";
		}
		
    	if(pageIndex != null){
    		try{
    			currentPageNo = Integer.valueOf(pageIndex);
    		}catch(NumberFormatException e){
    			return "redirect:/provider/syserror";
    		}
    	}	
    	//总数量（表）	
    	int totalCount	= providerService.getProviderCount(queryProCode, queryProName);
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
    	providerList = providerService.getProviderList(queryProName, queryProCode, currentPageNo, pageSize);
		model.addAttribute("providerList", providerList);
		model.addAttribute("queryProName", queryProName);
		model.addAttribute("queryProCode", queryProCode);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("currentPageNo", currentPageNo);
		return "providerlist";
	}
	/**
	 * 查看单条信息
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/view/{id}",method=RequestMethod.GET)
	public String view(@PathVariable String id,Model model){
		Provider provider=new Provider();
		provider=providerService.getProviderById(id);
		model.addAttribute("provider", provider);
		return "providerview";
	}
	/**
	 * 进入修改页面
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/providermodify.html",method=RequestMethod.GET)
	public String getprovidermodify(@RequestParam String id,Model model){
		Provider provider=new Provider();
		provider=providerService.getProviderById(id);
		model.addAttribute("provider", provider);
		return "providermodify";
	}
	/**
	 * 保存修改
	 * @param provider
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/providerModifySave",method=RequestMethod.POST)
	public String providerModifySave(Provider provider,HttpSession session){
		provider.setModifyBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
		provider.setModifyDate(new Date());
		if(providerService.modify(provider)){
			return "redirect:/provider/providerlist";
		}
		return "providermodify";
	}
	/**
	 * 进入添加页面
	 * @return
	 */
	@RequestMapping(value="provideradd",method=RequestMethod.GET)
	public String getProviderAdd(){
		return "provideradd";
	}
	/**
	 * 添加保存信息
	 * @param user
	 * @param model
	 * @return
	 */
	@RequestMapping(value="provideraddsave",method=RequestMethod.POST)
	public String provideradd(Provider provider,HttpSession session,Model model){
		provider.setCreatedBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
		provider.setCreationDate(new Date());
		if(providerService.add(provider)){
			return "redirect:/provider/providerlist";
		}
		return "provideradd";
	}
	/**
	 * 根据id删除供应商
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/delprovider")
	@ResponseBody
	public HashMap<String,String> delProvider(@RequestParam String proid){
		HashMap<String,String> hasmap=new HashMap<String, String>();
		int flag=providerService.deleteProviderById(proid);
		if(proid==null||"".equals(proid)){
			hasmap.put("delResult", "notexist");
		}else{
			if(flag==0){
				hasmap.put("delResult","true");
			}else if(flag==-1){
				hasmap.put("delResult","false");
			}else if(flag>0){
				hasmap.put("delResult",String.valueOf(flag));
			}
			
		}
		return hasmap;
	}
	
}
