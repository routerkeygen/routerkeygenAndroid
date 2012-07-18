package org.exobel.routerkeygen.ui;

import org.exobel.routerkeygen.R;
import org.exobel.routerkeygen.algorithms.Keygen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class NetworkActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            final Keygen keygen =
                    (Keygen) getIntent().getParcelableExtra(NetworkFragment.NETWORK_ID);
            arguments.putParcelable(NetworkFragment.NETWORK_ID,keygen);
            setTitle(keygen.getSsidName());
            NetworkFragment fragment = new NetworkFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, NetworksListActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
