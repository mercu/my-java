package com.mercu.member.controller;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mercu.member.model.SecurityMember;

/**
 * @author 고종봉 (jongbong.ko@navercorp.com)
 */
@RestController
public class MemberController {

    @RequestMapping("/loginUser")
    @ResponseBody
    public String loginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ("anonymousUser".equals(authentication.getPrincipal())) {
            return "anonymousUser";
        } else {
            return ((SecurityMember)authentication.getPrincipal()).getMember().getNick();
        }
    }

    @RequestMapping("/accessDenied")
    @ResponseBody
    public String accessDenied(HttpServletResponse httpServletResponse) {
        httpServletResponse.setStatus(HttpStatus.SC_FORBIDDEN);
        return "access denied!";
    }

}
