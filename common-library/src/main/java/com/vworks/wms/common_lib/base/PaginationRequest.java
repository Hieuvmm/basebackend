package com.vworks.wms.common_lib.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaginationRequest implements Serializable {
    private Integer limit;
    private Integer page;
    private String searchText;
    private String orderBy;

    public Map<String, String> getOrders() {
        Map<String, String> fields = new HashMap<>();
        if (orderBy != null && !orderBy.isEmpty()) {
            String[] splits = orderBy.split(",");
            for (String s : splits) {
                String[] f = s.split(":");
                String key = f[0];
                String order = (f.length > 1 && "DESC".equalsIgnoreCase(f[1])) ? "DESC" : "ASC";
                fields.put(key, order);
            }
        }
        return fields;
    }
}
