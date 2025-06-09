package com.vworks.wms.common_lib.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasePagination<T> implements Serializable {
    private Integer currentPage;
    private Integer lastPage;
    private Long total;
    private List<T> body;

    public BasePagination(Page<T> page) {
        this.currentPage = page.getPageable().getPageNumber() + 1;
        this.lastPage = page.getTotalPages();
        this.total = page.getTotalElements();
        this.body = page.getContent();
    }
}
