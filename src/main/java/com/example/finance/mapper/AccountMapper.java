package com.example.finance.mapper;

import com.example.finance.entity.Account;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

public interface AccountMapper {

    public static final Logger log = LoggerFactory.getLogger(AccountMapper.class);

    //findAll() 对应 <select id="findAll">
    List<Account> findAll();
    Account findById(Long id);

    /** 两个以上参数必须加 @Param，否则 XML 里认不出名字 */
    List<Account> search(@Param("keyword") String keyword,
                         @Param("status") String status);

    int insert(Account account);

    int update(Account account);

    int updateWithVersion(Account account);

    int addBalance(@Param("id") Long id,
                   @Param("delta") BigDecimal delta);

    int deleteById(Long id);
}
