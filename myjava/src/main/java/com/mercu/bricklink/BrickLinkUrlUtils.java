package com.mercu.bricklink;

import com.mercu.bricklink.model.CategoryType;

public class BrickLinkUrlUtils {
    /**
     * @param itemType
     * @param itemNo
     * @param colorId
     * @return
     */
    public static String itemImageUrl(String itemType, String itemNo, String colorId) {
        if (CategoryType.P.getCode().equals(itemType)) {
            return partImageUrl(itemNo, colorId);
        } else if (CategoryType.M.getCode().equals(itemType)) {
            return minifigImageUrl(itemNo, colorId);
        }
        return null;
    }

    /**
     * @param itemNo
     * @param colorId
     * @return
     */
    public static String partImageUrl(String itemNo, String colorId) {
        return String.format(BrickLinkUrlConstants.PART_IMG_URL, colorId, itemNo);
    }

    public  static String minifigImageUrl(String itemNo, String colorId) {
        return String.format(BrickLinkUrlConstants.MINIFIG_IMG_URL, colorId, itemNo);
    }

}
