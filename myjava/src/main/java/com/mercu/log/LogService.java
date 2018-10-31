package com.mercu.log;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LogService {
    Logger logger = LoggerFactory.getLogger(LogService.class);

    @Autowired
    private LogInfoRepository logInfoRepository;

    /**
     * @param type
     * @param description
     */
    public void log(String type, String description) {
        log(type, description, null);
    }

    public void log(String type, String description, String brief) {
        LogInfo logInfo = new LogInfo();
        logInfo.setDateTime(new Date());
        logInfo.setType(type);
        logInfo.setDescription(StringUtils.substring(description,0, 1000));
        logInfo.setBrief(brief);
        logger.info(logInfo.toString());

        logInfoRepository.save(logInfo);
    }

}
