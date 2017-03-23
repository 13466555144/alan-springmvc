package springData.controller;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import com.alan.springmvc.service.UserService;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;

@Controller
public class UserAction {
    
    protected final static Logger log = LoggerFactory.getLogger(UserAction.class);
    
    @Autowired
    UserService userService;
    @Autowired
    RestTemplate restTemplate;
    @RequestMapping("/")
    @ResponseBody
    public String index(){
        return "index";
    }
    
    @RequestMapping("/info")
    public String info(){
        return "info";
    }
    @RequestMapping("/findall")
    @ResponseBody
    public Map<String, Object> getUser(){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("state", "success");
        map.put("data", userService.findAll());
        return map;
    }
    @RequestMapping("/findbyid")
    @ResponseBody
    public Map<String, Object> findById(Integer id){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("state", "success");
        map.put("data", userService.findById(id));
        return map;
    }
    @RequestMapping("/add")
    @ResponseBody
    public Map<String, Object> save(String name){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("state", "success");
        map.put("data", userService.save(name));
        return map;
    }
    
    @RequestMapping("/findbyss")
    @ResponseBody
    public Hentity findByss(Integer id){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("state", "success");
        String url = "http://taccount.haier.com/oauth/token?client_id=haokongqi&client_secret=h1otB90c6Am_e9&grant_type=client_credentials";
        Hentity hentity = restTemplate.postForObject(url, null,Hentity.class);
        log.info(hentity.getAccess_token()+"====="+hentity.getExpires_in());
        return hentity;
    }
    
    
    @RequestMapping("/findbyhttps")
    @ResponseBody
    public String findbyhttps(Integer id){
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("state", "success");
//        String url = "http://taccount.haier.com/oauth/token?client_id=haokongqi&client_secret=h1otB90c6Am_e9&grant_type=client_credentials";
//        Hentity hentity = restTemplate.postForObject(url, null,Hentity.class);
//        log.info(hentity.getAccess_token()+"====="+hentity.getExpires_in());
        
        
        Map<String, String> params = new HashMap<String, String>(); 
        params.put("TransName", "IQSR"); 
        params.put("Plain", "transId=IQSR~|~originalorderId=2012~|~originalTransAmt= ~|~merURL= "); 
        params.put("Signature", "9b759887e6ca9d4c24509d22ee4d22494d0dd2dfbdbeaab3545c1acee62eec7"); 
//        sendSSLPostRequest("https://www.cebbank.com/per/QueryMerchantEpay.do", params); 
        
        String hentity = restTemplate.postForObject("https://www.cebbank.com/per/QueryMerchantEpay.do" ,  null , String.class , params);
        
        return hentity;
    }
}
