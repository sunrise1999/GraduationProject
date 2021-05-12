package com.example.graduationproject.dawn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.graduationproject.R;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class albumActivity extends AppCompatActivity {
    private List<ImageInfo> imgList = new ArrayList<ImageInfo>();
    private ImageListAdapter imgAdapter;
    private ListView mListView;
    private String ImageName;
    private int currentSel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        //如果在SDK23以上，如果没有设置权限弹出允许权限的对话框
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }

        //如果在SDK23以上，如果没有设置权限弹出允许权限的对话框
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SET_WALLPAPER) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.SET_WALLPAPER}, 1);
            }
        }

        loadImageList();

        imgAdapter = new ImageListAdapter(albumActivity.this, imgList);
        mListView = (ListView) findViewById(R.id.image_list);
        mListView.setAdapter(imgAdapter);

        registerForContextMenu(mListView);

        //长按呼出上下文菜单
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ImageName = (String) imgAdapter.getItem(position);
                currentSel = position;
                mListView.showContextMenu();
                return true;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentSel = position;
                String imgPath = imgList.get(position).getFilePath();
                if (imgPath.length() > 0) {
                    Intent intent = new Intent();
                    Bundle bl = new Bundle();
                    bl.putInt("pos", position);
                    bl.putSerializable("image_list", (Serializable) imgList);
                    intent.putExtras(bl);
                    intent.setClass(albumActivity.this, ViewActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    //loadImageList函数从Android默认照片目录DCIM中扫描图像文件
    //然后通过MimeTypeMap类匹配文件图像类型的文件
    //再将过滤后的图像文件的基本信息如文件名，路径，修改时间信息读取出来
    public void loadImageList() {
        //获得内置sd卡路径
        String internalSdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Navi";
        //获得外部sd卡路径
        //String internalSdcard = System.getenv("SECONDARY_STORAGE");
        new AlertDialog.Builder(albumActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.str_warning)
                .setMessage("路径：" + internalSdcard)
                .setPositiveButton(R.string.btn_confirm,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
        File directory = new File(internalSdcard);
        File[] files = directory.listFiles();
        if (files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].exists()) {
                    Uri selectedUri = Uri.fromFile(files[i]);
                    String fileExtension = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
                    //如果文件没有扩展名，容易产生null，所以添加fileExtension是否为null的判断
                    if (fileExtension != null) {
                        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
                        if (mimeType.equals("image/jpeg") || mimeType.equals("image/png") || mimeType.equals("image/bmp")) {
                            ImageInfo info = new ImageInfo();
                            info.setFileName(files[i].getName());
                            info.setFilePath(files[i].getPath());
                            Date lastModified = new Date(files[i].lastModified());
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh.mm.ss");
                            info.setFileDate(formatter.format(lastModified));
                            imgList.add(info);
                        }
                        else {
                            continue;
                        }
                    }
                    else {
                        continue;
                    }
                }
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, 0, 0, R.string.menu_share);
        menu.add(0, 1, 1, R.string.menu_detail);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                Bitmap bm =
                        BitmapFactory.decodeFile(imgList.get(currentSel).getFilePath());

                /*try {
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(albumActivity.this);
                    *//*wallpaperManager.setBitmap(bm);
                    Toast.makeText(MainActivity.this, "壁纸设置成功!",
                            Toast.LENGTH_LONG).show();*//*
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(albumActivity.this, "启动分享失败!",
                            Toast.LENGTH_LONG).show();
                }*/
                break;
            case 1:
                StringBuilder msgBuilder = new StringBuilder();
                msgBuilder.append("文 件 名：\n" + imgList.get(currentSel).getFileName() + "\n");
                msgBuilder.append("修改时间：\n" + imgList.get(currentSel).getFileDate() + "\n");
                msgBuilder.append("文件路径：\n" + imgList.get(currentSel).getFilePath() + "\n");
                DetailDialog detailDialog = new DetailDialog(this, msgBuilder.toString());
                detailDialog.show();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean retValue = super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_album, menu);
        return retValue;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_about) {
            StringBuilder msgBuilder = new StringBuilder();
            msgBuilder.append("骑行导航 v1.0\n");
            msgBuilder.append("作者：朱锦涛\n");
            msgBuilder.append("2021 南昌大学软件学院\n");
            DetailDialog detailDialog = new DetailDialog(this, msgBuilder.toString());
            detailDialog.show();
        }
        if (item.getItemId() == R.id.item_exit) {
            String title = "提示";
            new AlertDialog.Builder(albumActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.str_warning)
                    .setMessage("确定要退出吗？")
                    .setPositiveButton(R.string.btn_confirm,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                    .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create().show();
        }
        return super.onOptionsItemSelected(item);
    }
}