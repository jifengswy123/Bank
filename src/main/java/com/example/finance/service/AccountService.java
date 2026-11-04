package com.example.finance.service;

import com.example.finance.common.BusinessException;
import com.example.finance.entity.Account;
import com.example.finance.mapper.AccountMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

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
        log.info("FromId:"+fromId+"toId:"+toId+"amount:"+amount);

        if (from == null || to == null) {
            throw new BusinessException(300,"账户不存在");
        }

        // BigDecimal是对象不是值，比较大小必须用compareTo，不能用> <
        if (from.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("余额不足");
        }

        //关于没有锁的解决方法1，为这张数据表添加version字段
        //SELECT语句执行时会记录这时的version信息，并参与UPDATE语句的组装。
        //当第一条UPDATE语句开始执行，会把version+1,此时第二条UPDATE语句中旧的version就不成立了，SQL执行失败，r1，r2为SQL影响的条数，若为0表示SQL没有更新任何一行，即这行正在被使用。
//        int r1 = accountMapper.updateWithVersion(from);
//        if (r1 == 0) {
//            throw new BusinessException("账户变动过于频繁，请重试");
//        }

        //用于测试死锁，同时发起1to2和2to1的转账，一笔返回200成功，另一笔返回500且异常为DeadlockLoserDataAccessException
        //Error updating database.  Cause: com.mysql.cj.jdbc.exceptions.MySQLTransactionRollbackException: Deadlock found when trying to get lock; try restarting transaction
        //由于innoDB的特性，死锁会立刻被发现并且innoDB会选择代价小的SQL进行回退，让另一条SQL跑完，所以钱不会少

        //try { Thread.sleep(3000); } catch (InterruptedException e) { }

//        int r2 = accountMapper.updateWithVersion(to);
//        if (r2 == 0) {
//            throw new BusinessException("账户变动过于频繁，请重试");
//        }


        //这行为了方便模拟乐观锁，将流程延迟3秒，同时发送两条转账交易，都是从账户1到账户2，金额为8万。虽然账户1只有10万，但是两笔交易都能成功，账户1余额变为-6万。
        //交易a进行中还没有改账户1的余额前，交易b就已经来读账户1余额了，这时候b读到的余额还是10万而不是2万，因为这行数据没有乐观锁
//        try{
//            Thread.sleep(3000);
//        }catch(InterruptedException e){}

        //先在内存中把交易后的余额算好
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        //决定更新顺序
        boolean fromFirst = from.getId().compareTo(to.getId()) < 0;
        Account first  = fromFirst ? from : to;
        Account second = fromFirst ? to : from;

        //先更新id小的，后更新id大的
        if(accountMapper.updateWithVersion(first) == 0) {
            throw new BusinessException("账户变动过于频繁，请重试");
        }

        //这段是为了方便模拟出死锁状态，验证后删除
//        try{
//            Thread.sleep(3000);
//        }catch(InterruptedException e) {
//        }

        if (accountMapper.updateWithVersion(second) == 0) {
            throw new BusinessException("账户变动过于频繁，请重试");
        }

        //accountMapper.addBalance(fromId, amount.negate());   // 转出：扣钱
        //accountMapper.addBalance(toId, amount);              // 转入：加钱
    }

    public PageInfo<Account> findPage(int pageNum, int pageSize, String keyword, String status) {
        PageHelper.startPage(pageNum, pageSize);
        List<Account> list = accountMapper.search(keyword, status);
        return new PageInfo<>(list);
    }
}