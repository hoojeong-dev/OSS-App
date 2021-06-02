package com.example.oss_app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class CategoryAdapter extends PagerAdapter {

    private List<CategoryModel> models;
    private LayoutInflater layoutInflater;
    private Context context;

    public CategoryAdapter(List<CategoryModel> models, Context context) {
        this.models = models;
        this.context = context;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.activity_category_item, container, false);

        TextView title;
        String str;

        title = view.findViewById(R.id.title);

        title.setBackground(ContextCompat.getDrawable(context, models.get(position).getImage()));
        title.setText(models.get(position).getTitle());
        str = models.get(position).getTitle();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), NewsList.class);
                intent.putExtra("value", str);
                v.getContext().startActivity(intent);
            }
        });

        container.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
