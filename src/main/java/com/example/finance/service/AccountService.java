package com.example.finance.service;

import com.example.finance.common.BusinessException;
import com.example.finance.entity.Account;
import com.example.finance.mapper.AccountMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private final AccountMapper accountMapper;

    public AccountService(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    public List<Account> findAll() {
        return accountMapper.findAll();
    }

    public List<Account> search(String keyword, String status) {
        return accountMapper.search(keyword, status);
    }

    /** 开户 */
    /** @Transactional事务，方法内所有SQL要么全部成功，要么全部回滚 */
    @Transactional
    public Long openAccount(Account account) {
        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }
        account.setStatus("NORMAL");
        accountMapper.insert(account);
        return account.getId();   // 自增主键已回填
    }

    /** 转账  */
    @Transactional
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        Account from = accountMapper.findById(fromId);
        Account to   = accountMapper.findById(toId);

        if (from == null || to == null) {
            throw new BusinessException(300,"账户不存在");
        }
        // BigDecimal是对象不是值，比较大小必须用compareTo，不能用> <
        if (from.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("余额不足");
        }

        accountMapper.addBalance(fromId, amount.negate());   // 转出：扣钱
        accountMapper.addBalance(toId, amount);              // 转入：加钱
    }

    public PageInfo<Account> findPage(int pageNum, int pageSize, String keyword, String status) {
        PageHelper.startPage(pageNum, pageSize);          // ① 设置分页参数
        List<Account> list = accountMapper.search(keyword, status);  // ② 紧跟查询
        return new PageInfo<>(list);                       // ③ 包装成分页对象
    }
}