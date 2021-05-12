package com.example.graduationproject.dawn;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.R;

import java.util.ArrayList;

public class ViewActivity extends AppCompatActivity {
    private ArrayList<com.example.graduationproject.dawn.ImageInfo> imgList;
    private int pos;
    private ImageView img_view;

    //定义手势管理器
    private GestureDetector gesture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        gesture = new GestureDetector(com.example.graduationproject.dawn.ViewActivity.this,
                new GestureDetector.OnGestureListener() {
                    @Override
                    public boolean onDown(MotionEvent e) {
                        return false;
                    }

                    @Override
                    public void onShowPress(MotionEvent e) {

                    }

                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return false;
                    }

                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                        return false;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {

                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                        //从右往左滑动
                        if ((e1.getX() - e2.getX()) > 100) {
                            if (pos > 0) {
                                pos--;
                            }
                            if (pos <= 0) {
                                pos = 0;
                                Toast.makeText(com.example.graduationproject.dawn.ViewActivity.this, "已经是第一张",
                                        Toast.LENGTH_LONG).show();
                            }
                            String currentImgSrc = imgList.get(pos).getFilePath();
                            img_view.setImageBitmap(BitmapFactory.decodeFile(currentImgSrc));
                        }
                        //从左往右滑动
                        else if ((e1.getX() - e2.getX()) < -100) {
                            //当滑到第一张时，回到最后一张
                            if (pos < imgList.size()) {
                                pos++;
                            }
                            if (pos >= imgList.size()){
                                pos = imgList.size() - 1;
                                Toast.makeText(com.example.graduationproject.dawn.ViewActivity.this, "已经是最后一张",
                                        Toast.LENGTH_LONG).show();
                            }
                            String currentImgSrc = imgList.get(pos).getFilePath();
                            img_view.setImageBitmap(BitmapFactory.decodeFile(currentImgSrc));
                        }
                        return true;
                    }
                });

        img_view = (ImageView) findViewById(R.id.img_view);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        pos = bundle.getInt("pos");
        imgList = (ArrayList<com.example.graduationproject.dawn.ImageInfo>)bundle.getSerializable("image_list");
        String currentImgSrc = imgList.get(pos).getFilePath();
        img_view.setImageBitmap(BitmapFactory.decodeFile(currentImgSrc));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gesture.onTouchEvent(event);
    }
}
