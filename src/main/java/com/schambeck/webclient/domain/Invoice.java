package com.schambeck.webclient.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Invoice implements Serializable {

    private Long id;

    @PastOrPresent(message = "Issued must be in the past")
    @NotNull(message = "Issued is mandatory")
    private LocalDate issued;

    @Positive(message = "Total must be positive")
    @NotNull(message = "Total is mandatory")
    private BigDecimal total;

}
