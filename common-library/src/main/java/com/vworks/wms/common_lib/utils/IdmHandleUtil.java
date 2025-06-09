package com.vworks.wms.common_lib.utils;

import com.vworks.wms.common_lib.model.idm.IdmAppPermission;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

public class IdmHandleUtil {
    public static Comparator<IdmAppPermission> buildAppPermissionComparatorSort(Map<String, String> orders) {
        Comparator<IdmAppPermission> comparator = null;
        for (Map.Entry<String, String> entry : orders.entrySet()) {
            String field = entry.getKey();
            String direction = entry.getValue();

            Comparator<IdmAppPermission> fieldComparator = switch (field) {
                case "type" -> Comparator.comparing(IdmAppPermission::getType, Comparator.nullsLast(String::compareToIgnoreCase));
                case "id" -> Comparator.comparing(IdmAppPermission::getPermissionId);
                default -> Comparator.comparing(IdmAppPermission::getName, Comparator.nullsLast(String::compareToIgnoreCase));
            };

            if ("DESC".equalsIgnoreCase(direction)) {
                fieldComparator = fieldComparator.reversed();
            }

            comparator = (comparator == null) ? fieldComparator : comparator.thenComparing(fieldComparator);
        }
        return Objects.nonNull(comparator) ? comparator : Comparator.comparing(IdmAppPermission::getName);
    }
}
