package com.example.finance.mapper;

import com.example.finance.entity.Account;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface AccountMapper {
    //findAll() 对应 <select id="findAll">
    List<Account> findAll();
    Account findById(Long id);

    /** 两个以上参数必须加 @Param，否则 XML 里认不出名字 */
    List<Account> search(@Param("keyword") String keyword,
                         @Param("status") String status);

    int insert(Account account);

    int update(Account account);

    int addBalance(@Param("id") Long id,
                   @Param("delta") BigDecimal delta);

    int deleteById(Long id);
}
