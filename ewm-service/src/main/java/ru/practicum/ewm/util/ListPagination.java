package ru.practicum.ewm.util;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ListPagination {
    public static <T> List<T> getPage(List<T> sourceList, int page, int pageSize) {
        page++;
        if (pageSize <= 0 || page <= 0) {
            throw new IllegalArgumentException("invalid page size: " + pageSize);
        }

        int fromIndex = (page - 1) * pageSize;
        if (sourceList == null || sourceList.size() < fromIndex) {
            return Collections.emptyList();
        }
        return sourceList.subList(fromIndex, Math.min(fromIndex + pageSize, sourceList.size()));
    }
}
