package com.example.book.Adapter.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.book.Fragment.home.AllFragment;
import com.example.book.Fragment.home.DownloadsFragment;
import com.example.book.Fragment.home.GoiYFragment;
import com.example.book.Fragment.home.ViewsFragment;

public class HomeViewPagerAdapter extends FragmentStatePagerAdapter {

    public HomeViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new GoiYFragment();
            case 1:
                return new AllFragment();
            case 2:
                return new DownloadsFragment();
            case 3:
                return new ViewsFragment();
            default:
                return new GoiYFragment();
        }

    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Gợi ý";
            case 1:
                return "Tất cả sách";
            case 2:
                return "Sách tải nhiều nhất";
            case 3:
                return "Sách xem nhiều nhất";
            default:
                return "Gợi ý";
        }

    }
}
