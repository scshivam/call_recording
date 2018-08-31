package call_recording.jbglass.in.callrecording.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import call_recording.jbglass.in.callrecording.R;

/**
 * Created by satyam on 20/4/18.
 */

public class dialog_feedback extends DialogFragment {

    ViewPager viewPager;
    LinearLayout dotsLayout;
    private TextView[] dots;
    ViewPagerAdapter mSectionsPagerAdapter;
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View parentView=inflater.inflate(R.layout.dialog_feedback,null);
        builder.setView(parentView);
        viewPager = (ViewPager) parentView.findViewById(R.id.viewPager);
        dotsLayout = (LinearLayout) parentView.findViewById(R.id.layoutDots);

        mSectionsPagerAdapter = new ViewPagerAdapter(getActivity());
        viewPager.setAdapter(mSectionsPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        addBottomDots(0);
        builder.setTitle("How do you feel?");
        setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });
        Dialog dialog=builder.create();
        return dialog;
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[10];

        int colorsActive = R.color.selected;
        int colorsInactive = R.color.not_selected;

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            try {
                dots[i] = new TextView(getContext());
                dots[i].setText(Html.fromHtml("&#8226;"));
                dots[i].setTextSize(35);
                dots[i].setTextColor(getResources().getColor(colorsInactive));
                dotsLayout.addView(dots[i]);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        try {
            if (dots.length > 0)
                dots[currentPage].setTextColor(getResources().getColor(colorsActive));

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    public class ViewPagerAdapter extends PagerAdapter {

        private Context context;
        private LayoutInflater layoutInflater;

        public ViewPagerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.frag_layout, null);
           // ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
//            Picasso
//                    .with(getContext())
//                    .load(images[position])
//                    .fit()
//                    .centerCrop()
//                    .into(imageView);


            ViewPager vp = (ViewPager) container;
            vp.addView(view, 0);
            return view;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            ViewPager vp = (ViewPager) container;
            View view = (View) object;
            vp.removeView(view);

        }
    }


}
