package be.sanderdebleecker.herinneringsapp;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

import be.sanderdebleecker.herinneringsapp.Core.Adapters.DrawerListAdapter;
import be.sanderdebleecker.herinneringsapp.Core.MainApplication;
import be.sanderdebleecker.herinneringsapp.Helpers.PermissionHelper;
import be.sanderdebleecker.herinneringsapp.Interfaces.IAlbumsFListener;
import be.sanderdebleecker.herinneringsapp.Interfaces.IMemoriesFListener;
import be.sanderdebleecker.herinneringsapp.Interfaces.IQueryableFragment;
import be.sanderdebleecker.herinneringsapp.Models.NavItem;

//TODO feedback when something is created after redirection

public class MainActivity extends AppCompatActivity implements IMemoriesFListener,IAlbumsFListener {
    public static final String EXTRA_ID_MEMORY = "EXTRA_ID_MEMORY";
    public static final String EXTRA_ID_ALBUM = "EXTRA_ID_ALBUM";
    public static final String EXTRA_OVERVIEW = "EXTRA_OVERVIEW";
    private ListView mDrawerList;
    private RelativeLayout mDrawerPane;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;
    private IQueryableFragment mQueryableFragment;
    private ArrayList<NavItem> navItems;
    private Menu actionMenu;
    public enum Overviews {
        Memories,
        Albums,
        Sessions,
        Followers
    }
    private enum NavigationItem {
        Memories("Herinneringen"),
        NewMemory("Nieuw"),
        Map("Kaart"),
        Timeline("Tijdlijn"),
        Albums("Albums"),
        NewAlbum("Nieuw"),
        Sessions("Sessies"),
        NewSession("Nieuw"),
        Followers("Volgers");

        private String displayTitle="";

        NavigationItem(String displayTitle) {
            this.displayTitle = displayTitle;
        }

        private String getTitle() {
            return displayTitle;
        }

    }

    //CTOR
    private void loadMemoriesFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment f = fm.findFragmentById(R.id.main_content);
        if(f!=null){
            if(f instanceof MemoriesFragment) {
                return;
            } else {
                FragmentTransaction trans = fm.beginTransaction();
                MemoriesFragment fragm = MemoriesFragment.newInstance();
                mQueryableFragment = (IQueryableFragment) fragm;
                trans.replace(R.id.main_content,fragm);
                trans.commit();
            }
        }
        FragmentTransaction trans = fm.beginTransaction();
        MemoriesFragment fragm = MemoriesFragment.newInstance();
        mQueryableFragment = (IQueryableFragment) fragm;
        trans.add(R.id.main_content,fragm);
        trans.commit();
    }
    private void loadMapFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment f = fm.findFragmentById(R.id.main_content);
        if(f!=null) {
            if(f instanceof MemoryMapFragment) {
                return;
            }else{
                FragmentTransaction trans = fm.beginTransaction();
                MemoryMapFragment fragm = MemoryMapFragment.newInstance();
                mQueryableFragment = (IQueryableFragment) fragm;
                trans.replace(R.id.main_content, fragm);
                trans.commit();
            }
        }else{
            FragmentTransaction trans = fm.beginTransaction();
            MemoryMapFragment fragm = MemoryMapFragment.newInstance();
            mQueryableFragment = (IQueryableFragment) fragm;
            trans.add(R.id.main_content, fragm);
            trans.commit();
        }
    }
    private void loadTimelineFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment f = fm.findFragmentById(R.id.main_content);
        if(f!=null) {
            if(f instanceof MemoryTimelineFragment) {
                return;
            }else{
                FragmentTransaction trans = fm.beginTransaction();
                MemoryTimelineFragment fragm = MemoryTimelineFragment.newInstance();
                mQueryableFragment = (IQueryableFragment) fragm;
                trans.replace(R.id.main_content, fragm);
                trans.commit();
            }
        }else{
            FragmentTransaction trans = fm.beginTransaction();
            MemoryMapFragment fragm = MemoryMapFragment.newInstance();
            mQueryableFragment = (IQueryableFragment) fragm;
            trans.add(R.id.main_content, fragm);
            trans.commit();
        }
    }
    private void loadAlbumsFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment f = fm.findFragmentById(R.id.main_content);
        if(f!=null) {
            if(f instanceof AlbumsFragment) {
                return;
            }else{
                FragmentTransaction trans = fm.beginTransaction();
                AlbumsFragment fragm = AlbumsFragment.newInstance();
                trans.replace(R.id.main_content, fragm);
                trans.commit();
            }
        }else{
            FragmentTransaction trans = fm.beginTransaction();
            AlbumsFragment fragm = AlbumsFragment.newInstance();
            trans.add(R.id.main_content, fragm);
            trans.commit();
        }
    }
    private void loadNewAlbumFragment() {
        Intent intent = new Intent(this, AlbumActivity.class);
        startActivity(intent);
    }
    private void loadSessionsFragment() {

    }
    private void loadFollowersFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment f = fm.findFragmentById(R.id.main_content);
        if(f!=null) {
            if(f instanceof FollowersFragment) {
                return;
            }else{
                FragmentTransaction trans = fm.beginTransaction();
                FollowersFragment fragm = FollowersFragment.newInstance();
                trans.replace(R.id.main_content, fragm);
                trans.commit();
            }
        }else{
            FragmentTransaction trans = fm.beginTransaction();
            FollowersFragment fragm = FollowersFragment.newInstance();
            trans.add(R.id.main_content, fragm);
            trans.commit();
        }
    }
    private void loadNewSessionFragment() {
        Intent intent = new Intent(this, SessionActivity.class);
        startActivity(intent);
    }
    //LIFECYCLE
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPermissions();
        createToolbar();
        createDrawerMenu();
        createDrawerToggle(toolbar);
        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });
        mDrawerToggle.syncState();
        //Get Data
        Bundle extras = getIntent().getExtras();
        if(extras!=null) {
            Overviews overview = Overviews.valueOf(extras.getString(EXTRA_OVERVIEW));
            switch(overview) {
                case Albums:
                    loadAlbumsFragment();
                    break;
                case Sessions:
                    loadSessionsFragment();
                    break;
                default:
                    loadMemoriesFragment();
                    break;
            }
        }else {
            loadMemoriesFragment();
        }
    }
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        actionMenu = menu;
        getMenuInflater().inflate(R.menu.menu_main,actionMenu);
        MenuItem searchItem = actionMenu.findItem(R.id.action_search);
        final SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mQueryableFragment.queryFragment(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        /*searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                onSearchClose();
                return super.onClose();
            }
        });*/
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mQueryableFragment.cancelQueryFragment();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch(itemId) {
            case R.id.action_logout:
                logout();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onResume() {
        super.onResume();
    }
    public void onBackPressed() {
        moveTaskToBack(true);
    }
    //Permissions
    protected void getPermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET,Manifest.permission.RECORD_AUDIO};
            if(!PermissionHelper.hasPermissions(this,PERMISSIONS)) {
                ActivityCompat.requestPermissions(this,PERMISSIONS,PermissionHelper.ALL);
            }
        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length<1) return;
        if (requestCode == PermissionHelper.ALL && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        }else {
            Toast.makeText(this,"Actie werd niet toegestaan",Toast.LENGTH_SHORT).show();
            // We were not granted permission this time, so don't try to show the contact picker
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    //Methods
    //Toolbar
    private void createToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }
    //Drawer
    private void createDrawerMenu() {
        navItems = new ArrayList<NavItem>();
        navItems.add(new NavItem(NavigationItem.Memories.getTitle(),true,R.drawable.ic_art_track_black_24dp));
        navItems.add(new NavItem(NavigationItem.NewMemory.getTitle(),false,R.drawable.ic_add_box_black_24dp));
        navItems.add(new NavItem(NavigationItem.Map.getTitle(),false,R.drawable.ic_place_black_24dp));
        navItems.add(new NavItem(NavigationItem.Timeline.getTitle(),false,R.drawable.ic_event_note_black_24dp));
        navItems.add(new NavItem(NavigationItem.Albums.getTitle(),true,R.drawable.ic_photo_library_black_24dp));
        navItems.add(new NavItem(NavigationItem.NewAlbum.getTitle(),false,R.drawable.ic_add_box_black_24dp));
        navItems.add(new NavItem(NavigationItem.Sessions.getTitle(),true,R.drawable.ic_assignment_black_24dp));
        navItems.add(new NavItem(NavigationItem.NewSession.getTitle(),false,R.drawable.ic_add_box_black_24dp));
        navItems.add(new NavItem(NavigationItem.Followers.getTitle(),true,R.drawable.ic_group_black_24dp));
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, navItems);
        mDrawerList.setAdapter(adapter);
    }
    private void createDrawerToggle(Toolbar toolbar) {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerToggle =
                new ActionBarDrawerToggle( this,  mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
                ){
                    public void onDrawerOpened(View drawerView) {
                        super.onDrawerOpened(drawerView);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        super.onDrawerClosed(drawerView);
                    }
                };
        mDrawerLayout.addDrawerListener(mDrawerToggle);

    }
    private void selectItemFromDrawer(int position) {
        NavigationItem selectedItem = NavigationItem.values()[position];
        switch(selectedItem) {
            case Memories:
                loadMemoriesFragment();
                break;
            case NewMemory:
                onNewMemory();
                break;
            case Map:
                loadMapFragment();
                break;
            case Timeline:
                loadTimelineFragment();
                break;
            case Albums:
                loadAlbumsFragment();
                break;
            case NewAlbum:
                loadNewAlbumFragment();
                break;
            case Followers:
                loadFollowersFragment();
                break;
            case NewSession:
                loadNewSessionFragment();
            default:
                break;
        }
        if(mDrawerLayout==null) {
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        }
        mDrawerLayout.closeDrawers();
        if (mDrawerLayout.isDrawerOpen(mDrawerPane)) {
            mDrawerLayout.closeDrawer(mDrawerPane);
        }
    }
    //Interface methods
    public void onMemorySelect(int id) {
        Intent intent = new Intent(this, MemoryActivity.class);
        intent.putExtra(EXTRA_ID_MEMORY,id);
        startActivity(intent);
    }
    public void onNewMemory() {
        Intent intent = new Intent(this, MemoryActivity.class);
        startActivity(intent);
    }
    @Override
    public void onAlbumSelect(int id) {
        Intent intent = new Intent(this, AlbumActivity.class);
        intent.putExtra(EXTRA_ID_ALBUM,id);
        startActivity(intent);
        finish();
    }
    @Override
    public void onNewAlbum() {
        Intent intent = new Intent(this, AlbumActivity.class);
        startActivity(intent);
        finish();
    }

    private void logout() {
        MainApplication app = (MainApplication) getApplicationContext();
        app.setCurrSession(null);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
