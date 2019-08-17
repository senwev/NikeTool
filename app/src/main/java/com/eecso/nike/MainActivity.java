package com.eecso.nike;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;
import com.yalantis.ucrop.model.AspectRatio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URI;

import static android.graphics.Bitmap.Config.ARGB_8888;
import static com.eecso.nike.GlobalVariable.CAMERA_REQUEST_CODE;
import static com.eecso.nike.GlobalVariable.GALLERY_REQUEST_CODE;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button selectImageButton = (Button) findViewById(R.id.selectImage);
        Button selectImageButton2 = (Button) findViewById(R.id.selectImage2);
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED||ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {   //权限还没有授予，需要在这里写申请权限的代码
                    // 第二个参数是一个字符串数组，里面是你需要申请的权限 可以设置申请多个权限
                    // 最后一个参数是标志你这次申请的权限，该常量在onRequestPermissionsResult中使用到
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                            CAMERA_REQUEST_CODE);

                }else { //权限已经被授予，在这里直接写要执行的相应方法即可
                    getPhoto();
                }

            }
        });
        selectImageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED||ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {   //权限还没有授予，需要在这里写申请权限的代码
                    // 第二个参数是一个字符串数组，里面是你需要申请的权限 可以设置申请多个权限
                    // 最后一个参数是标志你这次申请的权限，该常量在onRequestPermissionsResult中使用到
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                            CAMERA_REQUEST_CODE);

                }else { //权限已经被授予，在这里直接写要执行的相应方法即可
                    getPhoto2();
                }

            }
        });
    }

    //从相册获取原始图片
    private void getPhoto()
    {

        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型" 所有类型则写 "image/*"
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intentToPickPic, GALLERY_REQUEST_CODE);

    }

    private File tempFile;
    private void getPhoto2()
    {

        //用于保存调用相机拍照后所生成的文件
        tempFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Nike/cap.jpg");
        //跳转到调用系统相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {   //如果在Android7.0以上,使用FileProvider获取Uri
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(MainActivity.this, "com.eecso.nike.fileprovider", tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        } else {    //否则使用Uri.fromFile(file)方法获取Uri
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        }
        startActivityForResult(intent, CAMERA_REQUEST_CODE);





    }

    private void startCropIntent(Uri uri) {
        Uri mDestinationUri = Uri.fromFile(new File(getCacheDir(),"SampleCropImage.jpg"));
        UCrop uCrop = UCrop.of(uri,mDestinationUri).withAspectRatio(5,5);
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);


        options.setAllowedGestures(UCropActivity.SCALE,UCropActivity.ALL,UCropActivity.NONE);
        options.setMaxScaleMultiplier(20);
        options.setShowCropFrame(true);


        uCrop.withOptions(options);

        try {
            Thread.sleep(200);//休眠3秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        uCrop.start(MainActivity.this);

            }

    // 拍照的照片的存储位置
    private String mTempPhotoPath;
    // 照片所在的Uri地址
    private Uri imageUri;
    private void takePhoto(){
        // 跳转到系统的拍照界面
        Intent intentToTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 指定照片存储位置为sd卡本目录下
        // 这里设置为固定名字 这样就只会只有一张temp图 如果要所有中间图片都保存可以通过时间或者加其他东西设置图片的名称
        // File.separator为系统自带的分隔符 是一个固定的常量
        mTempPhotoPath = Environment.getExternalStorageDirectory() + File.separator + "photo.jpeg";
        // 获取图片所在位置的Uri路径    *****这里为什么这么做参考问题2*****
        /*imageUri = Uri.fromFile(new File(mTempPhotoPath));*/
        imageUri = FileProvider.getUriForFile(MainActivity.this,
                MainActivity.this.getApplicationContext().getPackageName() +".my.provider",
                new File(mTempPhotoPath));
        //下面这句指定调用相机拍照后的照片存储的路径
        intentToTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intentToTakePhoto, CAMERA_REQUEST_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {

            switch (requestCode)
            {
                    //读写权限
                case CAMERA_REQUEST_CODE:
                    getPhoto();
                    //Toast.makeText(MainActivity.this,"相机回调",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        else
        {
            Toast.makeText(MainActivity.this,"权限获取被拒绝，请到设置中授予本应用所需权限！",Toast.LENGTH_SHORT).show();
            finish();

        }
    }

    public static File inputStreamToFile(InputStream ins,String FontName) throws IOException {
        String dir=Environment.getExternalStorageDirectory().getAbsolutePath()+"/Nike/"+FontName+".ttf";
        File f = new File(dir);
        if (!f.exists()) {
            f.getParentFile().mkdirs();
            f.createNewFile();
        }
 OutputStream os = new FileOutputStream(f);
 int bytesRead = 0;
 byte[] buffer = new byte[8192];
 while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
 os.write(buffer, 0, bytesRead);
 }
 os.close();
 ins.close();
 return f;
    }


    private static Uri saveBitmap(Bitmap bm, String picName) {
        try {

            String dir=Environment.getExternalStorageDirectory().getAbsolutePath()+"/Nike/"+picName+".jpg";
            File f = new File(dir);
            if (!f.exists()) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Uri uri = Uri.fromFile(f);
            return uri;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();    }
        return null;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== GALLERY_REQUEST_CODE||requestCode== CAMERA_REQUEST_CODE)
        {
            if(resultCode==RESULT_OK)
            {
                //Toast.makeText(MainActivity.this,data.toString(),Toast.LENGTH_SHORT).show();

                /** * 分享图片 */


                   // startCropIntent(Uri.parse(data.getData().getPath()));
                if(requestCode==CAMERA_REQUEST_CODE)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Uri contentUri = FileProvider.getUriForFile(MainActivity.this, "com.eecso.nike.fileprovider", tempFile);
                        startCropIntent(contentUri);;//裁剪图片
                    } else {
                        startCropIntent(Uri.fromFile(tempFile));
                    }

                }
                    else
                startCropIntent(Uri.parse(data.getData().toString()));



            }else if(resultCode==RESULT_CANCELED)
            {
                Toast.makeText(MainActivity.this,"请求被取消",Toast.LENGTH_SHORT).show();

            }
            else if(resultCode==RESULT_FIRST_USER)
            {
                Toast.makeText(MainActivity.this,"首次使用",Toast.LENGTH_SHORT).show();

            }
            else
            {
                Toast.makeText(MainActivity.this,String.valueOf(resultCode),Toast.LENGTH_SHORT).show();

            }

        }
        else if(requestCode==UCrop.REQUEST_CROP)
        {
            if(resultCode==RESULT_OK)
            {
                Uri croppedFileUri = UCrop.getOutput(data);
                Bitmap selectBitmap = BitmapFactory.decodeFile(croppedFileUri.getPath());
                selectBitmap=resizeBitmap(selectBitmap,2800);
                /**获取资源图*/
                Resources res = MainActivity.this.getResources();
                Bitmap    bgimg0 = BitmapFactory.decodeResource(res, R.drawable.share);
                bgimg0=bgimg0.copy(ARGB_8888,true);

                //切换为小米9校准宽度3248以保证显示效果的一致性
                bgimg0=resizeBitmap(bgimg0,3248);


                Canvas canvas = new Canvas(bgimg0);

                Paint paint =new Paint(Paint.ANTI_ALIAS_FLAG);//消除锯齿
                paint.setDither(true);//获取跟清晰的图像采样
                paint.setFilterBitmap(true);//过滤一些
                paint.setColor(0xff0050f2);
                //paint.setColor(Color.BLUE);

                File ttfFile=null;
                try {
                    ttfFile =  inputStreamToFile(getResources().openRawResource(R.raw.huawenhupo),"huawen");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                EditText editText = (EditText) findViewById(R.id.editText);
                EditText editText1 = (EditText) findViewById(R.id.editText2);
                String tittleText = editText.getText().toString();
                int fontSize = 270;
                paint.setTypeface(Typeface.createFromFile(ttfFile.getPath()));
                paint.setTextSize(fontSize);
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setShadowLayer(1f,0f,1f, Color.DKGRAY);//阴影制作半径，x偏移量，y偏移量，阴影颜色
                canvas.drawText(tittleText,(bgimg0.getWidth())/2,880,paint);
                canvas.drawBitmap(selectBitmap,(bgimg0.getWidth()-selectBitmap.getWidth())/2,1070,paint);


                String priseText = "￥"+editText1.getText().toString();
                paint.setColor(Color.RED);
                int redfontSize = 500;
                paint.setTextSize(redfontSize);
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTypeface(Typeface.DEFAULT_BOLD);
                canvas.drawText(priseText,(bgimg0.getWidth())/2,4300,paint);


                EditText editText2 = (EditText) findViewById(R.id.editText3);
                String huohao = '@'+editText2.getText().toString();
                paint.setTextSize(130);
                paint.setColor(Color.WHITE);
                paint.setTypeface(Typeface.DEFAULT);
                canvas.drawText(huohao,(bgimg0.getWidth())/2,5150,paint);



                // canvas.drawLine(0,0,500,500,paint);
                Intent share_intent = new Intent();
                share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
                share_intent.setType("image/*");  //设置分享内容的类型
                // share_intent.putExtra(Intent.EXTRA_STREAM,data.getData() );

                Uri uri = saveBitmap(bgimg0,"img");
                File file = new File(uri.getPath());
                Uri publicuri = FileProvider.getUriForFile(this,"com.eecso.nike.fileprovider",file);


                share_intent.putExtra(Intent.EXTRA_STREAM, publicuri);
                //创建分享的Dialog
                share_intent = Intent.createChooser(share_intent, "请点击下面微信图标分享到客户群中");
                MainActivity.this.startActivity(share_intent);
            }
        }

    }
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private Bitmap resizeBitmap(Bitmap image, int size) {
        float ratio = (float) image.getHeight()/(float)image.getWidth();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(image, size,(int) (ratio*(float) size), false);
        return drawableToBitmap(new BitmapDrawable(getResources(), bitmapResized));
    }
}
