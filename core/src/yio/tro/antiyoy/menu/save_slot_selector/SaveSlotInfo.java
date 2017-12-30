package yio.tro.antiyoy.menu.save_slot_selector;

public class SaveSlotInfo {


    public String name;
    public String description;
    public String key;


    public String getDescription() {
        if (description == null) {
            return "";
        }

        return description;
    }


    @Override
    public String toString() {
        return "[Slot: " +
                "key='" + key + "' " +
                "name='" + name + "' " +
                "description='" + description + "' " +
                "]";
    }
}
