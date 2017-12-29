package com.example.android.project2.ui;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.example.android.project2.R;

/**
 * Created by diego on 28/12/17.
 */

public abstract class BaseFragment extends Fragment {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_settings){
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
