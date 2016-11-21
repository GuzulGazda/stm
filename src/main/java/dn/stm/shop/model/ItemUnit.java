package dn.stm.shop.model;

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

    private final String name;

    /**
     * private Constructor 
     * 
     * creates ItemUnit by name
     * @param name - name of ItemUnit
     */
    private ItemUnit(String name) {
        this.name = name;
    }

    /**
     *
     * @param name - ItemUnit name
     * @return ItemUnit object
     */
    public static ItemUnit fromString(String name) {
        if (name != null) {
            for (ItemUnit itemUnit : ItemUnit.values()) {
                if (name.equalsIgnoreCase(itemUnit.name)) {
                    return itemUnit;
                }
            }
        }
        return null;
    }

    /**
     * TODO
     * @return name of ItemUnit
     */
    public String getName() {
        return name;
    }
}
