package usc.cs578.trojannow.drawer;

import android.view.View;
import android.widget.TextView;

import usc.cs578.com.trojannow.R;

/*
 * Created by Ekasit_Ja on 17-Apr-15.
 */
public class DrawerMenuItemHolder {
    protected TextView menuItem;

    public DrawerMenuItemHolder(View v) {
        menuItem = (TextView) v.findViewById(R.id.menu_item);
    }
}
