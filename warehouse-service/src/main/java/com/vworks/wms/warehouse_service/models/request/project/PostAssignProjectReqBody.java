package com.vworks.wms.warehouse_service.models.request.project;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostAssignProjectReqBody {
    private List<String> projectCodes;
    private List<String> approves;
}
