package com.mercu.lego.controller;

import com.mercu.bricklink.model.CategoryType;
import com.mercu.bricklink.service.BrickLinkMyService;
import com.mercu.lego.model.my.MyItem;
import com.mercu.lego.model.my.MyItemGroup;
import com.mercu.lego.service.MatchMyItemService;
import com.mercu.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 고종봉 (jongbong.ko@navercorp.com)
 */
@RestController
public class MyPartController {
    @Autowired
    private BrickLinkMyService brickLinkMyService;
    @Autowired
    private MatchMyItemService matchMyItemService;

    @RequestMapping("/admin/myPartsByGroup")
    @ResponseBody
    public String myPartsByGroup() {
        List<MyItemGroup> myItemGroupList = brickLinkMyService.findMyItemsGroup();
        return JsonUtils.toJson(myItemGroupList);
    }

    @RequestMapping("/admin/myPartWheresSimilar")
    @ResponseBody
    public String myPartWheresSimilar(@RequestParam String partNo, @RequestParam String colorId, @RequestParam(required = false) String setNo) {
        List<MyItem> myPartWhereInfos = brickLinkMyService.findMyItemWheresSimilar(CategoryType.P.getCode(), partNo, colorId, setNo);
        return JsonUtils.toJson(myPartWhereInfos);
    }

    @RequestMapping("/admin/myPartWhereInfos")
    @ResponseBody
    public String myPartWhereInfos(@RequestParam String partNo, @RequestParam String colorId) {
        List<MyItem> myPartWhereInfos = brickLinkMyService.findMyItemWheresWithInfos(CategoryType.P.getCode(), partNo, colorId);
        return JsonUtils.toJson(myPartWhereInfos);
    }

    @RequestMapping(value = "/admin/myPartQty", method = RequestMethod.POST)
    @ResponseBody
    public String myPartQty(@RequestParam String partNo, @RequestParam String colorId, @RequestParam String whereCode, @RequestParam String whereMore, @RequestParam Integer qty) {
        MyItem saved = brickLinkMyService.addMyItem(CategoryType.P.getCode(), partNo, colorId, whereCode, whereMore, qty);
        return JsonUtils.toJson(saved);
    }

    @RequestMapping(value = "/admin/myPartWhereIncrease", method = RequestMethod.POST)
    @ResponseBody
    public String myPartWhereIncrease(@RequestParam String partNo, @RequestParam String colorId, @RequestParam String whereCode, @RequestParam String whereMore, @RequestParam Integer val, @RequestParam(required = false) String setNo, @RequestParam(required = false) String matchId) {
        // 부품-단건 보유 수량(양수/음수) 변경 후, 갱신된 목록 리스트 반환
        List<MyItem> myPartWhereInfos = brickLinkMyService.increaseMyPartWhere(CategoryType.P.getCode(), partNo, colorId, whereCode, whereMore, val, setNo);
        // 매칭 정보도 갱신 (매칭 부품, 매칭율)
        if (StringUtils.isNotBlank(setNo) & StringUtils.isNotBlank(matchId)) {
            matchMyItemService.updateMatchSetPart(partNo, colorId, setNo, matchId);
        }
        return JsonUtils.toJson(myPartWhereInfos);
    }

    @RequestMapping(value = "/admin/removeAllSetMyParts", method = RequestMethod.POST)
    @ResponseBody
    public String removeAllSetMyParts(@RequestParam String setNo) {
        // 해당 wanted-setNo의 부품 목록을 모두 제거
        brickLinkMyService.removeAllSetMyPartsWhereWanted(setNo);
        return "{\"message\":\"삭제 완료\"}";
    }

}
