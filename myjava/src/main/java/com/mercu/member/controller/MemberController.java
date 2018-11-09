package com.mercu.member.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mercu.bricklink.model.info.PartInfo;
import com.mercu.utils.JsonUtils;
import sun.plugin.liveconnect.SecurityContextHelper;

/**
 * @author 고종봉 (jongbong.ko@navercorp.com)
 */
@RestController
public class MemberController {

    @RequestMapping("/loginCheck")
    @ResponseBody
    public String loginCheck() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return String.valueOf(!"anonymousUser".equals(authentication.getPrincipal()));
    }
}
