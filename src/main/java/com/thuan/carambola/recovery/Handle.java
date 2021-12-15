package com.thuan.carambola.recovery;

import lombok.Data;

@Data
public class Handle<T> {
    private T entity;
    private String action; // ghi xoa sua
}
