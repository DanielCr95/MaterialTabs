package com.example.tiger.materialtabs.Loader;

import android.content.Context;
import android.widget.ImageView;

import com.example.tiger.materialtabs.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Tiger on 3/18/2017.
 */

public class PicassoClient {

    public static void downloadImage(Context c, String imageUrl, ImageView img)
    {
        if(imageUrl!=null && imageUrl.length()>0)

        {Picasso.with(c).load(imageUrl).placeholder(R.drawable.place_background).into(img);

        }else
        {
            Picasso.with(c).load(R.drawable.place_background).into(img);
        }

    }
}
