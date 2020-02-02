package com.cloudglasses.weixin.service;

import com.cloudglasses.model.SystemConfig;
import com.cloudglasses.repository.SystemConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemConfigService {
    @Autowired
    private SystemConfigRepository systemConfigRepository;

    public String getValue(String key) {
        String result = systemConfigRepository.findById(key).orElse(new SystemConfig()).getCfgValue();
        return result == null ? null : result.replace("\\n", "\n");
    }
}
