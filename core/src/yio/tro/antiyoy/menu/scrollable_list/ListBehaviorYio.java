package yio.tro.antiyoy.menu.scrollable_list;

public interface ListBehaviorYio {


    void applyItem(ListItemYio item);


    void onItemRenamed(ListItemYio item);


    void onItemDeleteRequested(ListItemYio item);
}
