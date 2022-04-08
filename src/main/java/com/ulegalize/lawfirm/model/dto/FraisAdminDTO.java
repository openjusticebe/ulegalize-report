package com.ulegalize.lawfirm.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FraisAdminDTO {
    private String vcKey;

    private String type;
    private String mesure;
    private Integer unit;
    private BigDecimal pricePerUnit;
}
