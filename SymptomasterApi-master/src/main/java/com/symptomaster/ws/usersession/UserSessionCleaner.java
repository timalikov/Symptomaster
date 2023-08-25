package com.symptomaster.ws.usersession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by nikolay on 02/02/2017.
 */
@Component
public class UserSessionCleaner implements Runnable {
    public static volatile boolean running = true;

    @Autowired
    private UserSessionManager userSessionManager;

    public UserSessionCleaner() {
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(1_860_000); // 31 min
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            userSessionManager.clearExpiredSessions();
        }
    }
}
