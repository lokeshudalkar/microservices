package com.jobportal.jobservice.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * The type Page wrapper.
 *
 * @param <T> the type parameter
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageWrapper<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private List<T> content;
    private int number;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private int numberOfElements;
    private boolean empty;
    private String sortDirection;
    private String sortProperty;

    /**
     * Instantiates a new Page wrapper.
     *
     * @param page the page
     */
    public PageWrapper(Page<T> page) {
        this.content = page.getContent();
        this.number = page.getNumber();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.numberOfElements = page.getNumberOfElements();
        this.empty = page.isEmpty();

        // Extract sort information
        if (page.getSort().isSorted()) {
            Sort.Order order = page.getSort().iterator().next();
            this.sortDirection = order.getDirection().name();
            this.sortProperty = order.getProperty();
        }
    }

    /**
     * To page.
     *
     * @return the page
     */
    public Page<T> toPage() {
        Sort sort = Sort.unsorted();
        if (sortProperty != null && sortDirection != null) {
            sort = Sort.by(Sort.Direction.valueOf(sortDirection), sortProperty);
        }
        return new PageImpl<>(content, PageRequest.of(number, size, sort), totalElements);
    }
}
