package com.widescope.sqlThunder.config;

import com.widescope.logging.AppLogger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Component
public class LifeCycle implements DisposableBean {

    @Override
    public void destroy() {
        AppLogger.logInfo("LifeCycle", "destroy", AppLogger.obj, "Application is shutting down");
    }
}
