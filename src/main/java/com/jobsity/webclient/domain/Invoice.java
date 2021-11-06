package com.jobsity.webclient.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Invoice implements Serializable {

    private Long id;

    private LocalDate issued;

    private BigDecimal total;

    public Invoice(Long id, LocalDate issued, BigDecimal total) {
        this.id = id;
        this.issued = issued;
        this.total = total;
    }

    public Invoice() {
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Invoice invoice = (Invoice) o;
        return Objects.equals(id, invoice.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", issued=" + issued +
                ", total=" + total +
                '}';
    }

}
