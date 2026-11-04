package com.example.finance.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@NotNull @NotBlank @NotEmpty @Size(min=,max=) @Min @Max @DecimalMin @Pattern（正则）
public class TransferRequest {

    private static final Logger log = LoggerFactory.getLogger(TransferRequest.class);

    @NotNull(message = "转出账户不能为空")
    private Long fromId;

    @NotNull(message = "转入账户不能为空")
    private Long toId;

    @NotNull(message = "转账金额不能为空")
    @DecimalMin(value = "0.01", message = "转账金额必须大于 0")
    private BigDecimal amount;

    public Long getFromId() {
        return fromId;
    }

    public void setFromId(Long fromId) {
        this.fromId = fromId;
    }

    public Long getToId() {
        return toId;
    }

    public void setToId(Long toId) {
        this.toId = toId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
