package com.mercu.bricklink;

public class BrickLinkUrlUtils {
    public static String partImageUrl(String itemNo, String colorId) {
        return String.format(BrickLinkUrlConstants.PART_IMG_URL, colorId, itemNo);
    }
}
