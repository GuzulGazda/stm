package dn.stm.shop.model;

/**
 *
 * @author home
 */
public enum ItemUnit {

    SHTUKA("шт."),
    EDINICA("ед."),
    METR("м"),
    KW_METR("кв.м"),
    KUB_METR("куб.м"),
    POG_METR("пог.м"),
    KG("кг"),
    TONN("т"),
    UPAKOVKA("упаковка"),
    RULON("рулон"),
    PARA("пара");

    private final String text;

    private ItemUnit(String text) {
        this.text = text;
    }

    public static ItemUnit fromString(String text) {
        if (text != null) {
            for (ItemUnit itemUnit : ItemUnit.values()) {
                if (text.equalsIgnoreCase(itemUnit.text)) {
                    return itemUnit;
                }
            }
        }
        return null;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }

}
