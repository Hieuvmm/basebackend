package com.vworks.wms.common_lib.base;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Filter {
    private QueryOperator operator;
    private String field;
    private String value;
    private List<String> values;
}
