package com.mercu.bricklink.service;

import com.mercu.http.HttpEntityBuilder;
import com.mercu.http.HttpService;
import org.apache.http.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BrickLinkLoginService {
    private Logger logger = LoggerFactory.getLogger(BrickLinkLoginService.class);

    @Autowired
    private HttpService httpService;

    /**
     *
     */
    public void loginIfNotLoggedin() {
        if (isLoggedIn()) {
            return;
        }

        HttpEntity httpEntity = HttpEntityBuilder.create()
                .addParameter("userid", "mercujjang@gmail.com")
                .addParameter("password", System.getProperty("pass"))
                .addParameter("override", "false")
                .addParameter("keepme_loggedin", "true")
                .addParameter("mid", "166afe6283900000-8299106d32c5b932")
                .addParameter("pageid", "MAIN")
                .build();

        logger.info("loggedin : " + httpService.toStringHttpReponse(httpService.post("https://www.bricklink.com/ajax/renovate/loginandout.ajax", httpEntity)));

    }

    private boolean isLoggedIn() {
        return !httpService.getAsString("http://bricklink.com").contains("Log in or Register");
    }

}
