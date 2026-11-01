package com.example.finance.web;

import com.example.finance.entity.Account;
import com.example.finance.service.AccountService;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {

    //AccountMapper是个接口，Spring 在启动时会把@MapperScan生成的那个代理对象自动塞进构造方法里。这就是IoC/DI（控制反转/依赖注入）的实际体现
    //对象的创建权交给了框架。
    private final AccountService accountService;

    // 构造方法注入
    public IndexController(AccountService accountService) {
        this.accountService = accountService;
    }
    @GetMapping("/")
    public String index(@RequestParam(required = false) String keyword,
                        @RequestParam(required = false) String status,
                        @RequestParam(defaultValue = "1")  int pageNum,
                        @RequestParam(defaultValue = "10") int pageSize,
                        Model model) {
        PageInfo<Account> pageInfo = accountService.findPage(pageNum, pageSize, keyword, status);
        model.addAttribute("msg", "银行客户系统");
        //model.addAttribute("accounts", accountService.search(keyword, status));
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        return "index";
    }
}
