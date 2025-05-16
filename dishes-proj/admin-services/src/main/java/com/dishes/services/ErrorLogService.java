package com.dishes.services;

import java.util.List;

import com.dishes.entities.Log;

public interface ErrorLogService {
    void saveError(Log event);
    List<Log> getAllErrors();
}