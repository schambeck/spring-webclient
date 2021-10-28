package com.jobsity.webclient.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Invoice implements Serializable {

    private Long id;

    private LocalDate issued;

    private BigDecimal total;

    public Long getId() {
        return id;
    }

    public LocalDate getIssued() {
        return issued;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setIssued(LocalDate issued) {
        this.issued = issued;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

}
