package com.mercu.lego.controller;

import com.mercu.lego.service.MatchMyItemService;
import com.mercu.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 고종봉 (jongbong.ko@navercorp.com)
 */
@Slf4j
@RestController
public class CandidateController {
    @Autowired
    private MatchMyItemService matchMyItemService;

    @RequestMapping("/admin/matchIdList")
    @ResponseBody
    public String matchIdList() {
        return JsonUtils.toJson(matchMyItemService.findMatchIds());
    }

    @RequestMapping("/admin/matchSetList")
    @ResponseBody
    public String matchSetList(@RequestParam(value = "matchId", required = false) String matchId) {
        return JsonUtils.toJson(matchMyItemService.findMatchSetList(matchId));
    }

    @RequestMapping("/admin/matchSetParts")
    @ResponseBody
    public String matchSetParts(@RequestParam(value = "matchId") String matchId, @RequestParam(value = "setId") String setId, @RequestParam(value = "whereValue", required = false) String whereValue) {
        return JsonUtils.toJson(matchMyItemService.findMatchSetParts(matchId, setId, whereValue));
    }

    @RequestMapping(value = "/admin/hideMatchSet", method = RequestMethod.POST)
    @ResponseBody
    public String hideMatchSet(@RequestParam String matchId, @RequestParam String setId) {
        // 해당 setNo의 match 목록을 모두 제거
        matchMyItemService.removeMatchSetParts(matchId, setId);
        return "{\"message\":\"삭제 완료\"}";
    }

}
