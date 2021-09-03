package com.MemeGenerator.meme.creator.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.MemeGenerator.meme.creator.R;
import com.MemeGenerator.meme.creator.ui.activities.MainActivity;
import com.MemeGenerator.meme.creator.ui.activities.OnBoardingActivity;
import com.MemeGenerator.meme.creator.ui.activities.SlideActivity;

public class SlideViewPagerAdapter extends PagerAdapter {

    Context ctx;

    public SlideViewPagerAdapter(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_screen, container, false);


        ImageView logo = view.findViewById(R.id.logo);

        ImageView ind1 = view.findViewById(R.id.ind1);
        ImageView ind2 = view.findViewById(R.id.ind2);
        ImageView ind3 = view.findViewById(R.id.ind3);
        ImageView ind4 = view.findViewById(R.id.ind4);


//        TextView title=view.findViewById(R.id.title);
//        TextView desc=view.findViewById(R.id.desc);

        ImageView next = view.findViewById(R.id.next);
        ImageView back = view.findViewById(R.id.back);

        Button btnGetStarted = view.findViewById(R.id.btnGetStarted);
        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, OnBoardingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SlideActivity.viewPager.setCurrentItem(position + 1);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SlideActivity.viewPager.setCurrentItem(position - 1);
            }
        });

        switch (position) {
            case 0:
                logo.setBackgroundResource(R.drawable.tutorial1);
                ind1.setImageResource(R.drawable.seleted);
                ind2.setImageResource(R.drawable.unselected);
                ind3.setImageResource(R.drawable.unselected);
                ind4.setImageResource(R.drawable.unselected);

//                title.setText("Meme Generator app");
//                desc.setText("This is random text taking for tesing puspose");
                back.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                break;
            case 1:
                logo.setBackgroundResource(R.drawable.tutorial2);
                ind1.setImageResource(R.drawable.unselected);
                ind2.setImageResource(R.drawable.seleted);
                ind3.setImageResource(R.drawable.unselected);
                ind4.setImageResource(R.drawable.unselected);
//                title.setText("Meme Generator app");
//                desc.setText("This is random text taking for tesing puspose");
                back.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
                break;
            case 2:
                logo.setBackgroundResource(R.drawable.tutorial3);
                ind1.setImageResource(R.drawable.unselected);
                ind2.setImageResource(R.drawable.unselected);
                ind3.setImageResource(R.drawable.seleted);
                ind4.setImageResource(R.drawable.unselected);
//                title.setText("Meme Generator app");
//                desc.setText("This is random text taking for tesing puspose");
                back.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
                break;
            case 3:
                logo.setBackgroundResource(R.drawable.tutorial4);
                ind1.setImageResource(R.drawable.unselected);
                ind2.setImageResource(R.drawable.unselected);
                ind3.setImageResource(R.drawable.unselected);
                ind4.setImageResource(R.drawable.seleted);
//                title.setText("Meme Generator app");
//                desc.setText("This is random text taking for tesing puspose");
                back.setVisibility(View.VISIBLE);
                next.setVisibility(View.GONE);
                break;

        }


        container.addView(view);
        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

}
