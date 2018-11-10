package com.mercu.member.controller;

import com.mercu.member.model.Member;
import com.mercu.member.model.SecurityMember;
import com.mercu.utils.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @author 고종봉 (jongbong.ko@navercorp.com)
 */
@RestController
public class MemberController {

    @RequestMapping("/loginUser")
    @ResponseBody
    public String loginUser() {
        LoginResponse loginResponse = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ("anonymousUser".equals(authentication.getPrincipal())) {
            loginResponse = new LoginResponse("anonymousUser", "anonymousUser", false, false);
        } else {
            Member loginMember = ((SecurityMember)authentication.getPrincipal()).getMember();
            loginResponse = new LoginResponse(
                    loginMember.getId(),
                    loginMember.getNick(),
                    true,
                    ("mercu".equals(loginMember.getId()))
            );
        }
        return JsonUtils.toJson(loginResponse);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public class LoginResponse {
        private String userId;
        private String nick;
        private boolean isUser;
        private boolean isAdmin;
    }

    @RequestMapping("/accessDenied")
    @ResponseBody
    public String accessDenied(HttpServletResponse httpServletResponse) {
        httpServletResponse.setStatus(HttpStatus.SC_FORBIDDEN);
        return "access denied!";
    }

}
