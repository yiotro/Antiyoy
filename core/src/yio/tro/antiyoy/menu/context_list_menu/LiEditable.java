package yio.tro.antiyoy.menu.context_list_menu;

public interface LiEditable {


    void rename(String name);


    void onDeleteRequested();


    void onContextMenuDestroy();


    String getEditableName();

}
