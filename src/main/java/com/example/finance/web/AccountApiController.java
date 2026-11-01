package com.example.finance.web;

import com.example.finance.common.R;
import com.example.finance.dto.TransferRequest;
import com.example.finance.entity.Account;
import com.example.finance.service.AccountService;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

//@RestController这个类的每个方法直接返回JSON数据
@RestController
@RequestMapping("/api/accounts")
public class AccountApiController {

    private final AccountService accountService;

    public AccountApiController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public List<Account> list() {
        return accountService.findAll();
    }

    @GetMapping("/page")
    public R<PageInfo<Account>> page(@RequestParam(defaultValue = "1")  int pageNum,
                                     @RequestParam(defaultValue = "10") int pageSize,
                                     @RequestParam(required = false) String keyword,
                                     @RequestParam(required = false) String status) {
        return R.ok(accountService.findPage(pageNum, pageSize, keyword, status));
    }

    // 开户：POST /api/accounts/open?accountNo=622200000004&holderName=赵六&balance=1000
    @PostMapping("/open")
    public Map<String, Object> open(@RequestParam String accountNo,
                                    @RequestParam String holderName,
                                    @RequestParam(defaultValue = "0") BigDecimal balance) {
        Account a = new Account();
        a.setAccountNo(accountNo);
        a.setHolderName(holderName);
        a.setBalance(balance);
        Long id = accountService.openAccount(a);
        return Map.of("success", true, "id", id);
    }

    // 转账：POST /api/accounts/transfer?fromId=1&toId=2&amount=5000
    @PostMapping("/transfer")
    //只加@RequestBody不加@Valid不会触发校验
    //非法参数在进入Service前就被拦下了
    public R<Void> transfer(@RequestBody @Valid TransferRequest req) {
        accountService.transfer(req.getFromId(), req.getToId(), req.getAmount());
        return R.ok();
    }
}