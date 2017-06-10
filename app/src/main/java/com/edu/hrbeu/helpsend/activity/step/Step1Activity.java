package com.edu.hrbeu.helpsend.activity.step;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.edu.hrbeu.helpsend.R;
import com.edu.hrbeu.helpsend.databinding.ActivityStep1Binding;
import com.edu.hrbeu.helpsend.global.GlobalData;
import com.edu.hrbeu.helpsend.utils.CommonUtil;
import com.edu.hrbeu.helpsend.utils.TopMenuHeader;
import com.xw.repo.XEditText;

import java.io.File;


public class Step1Activity extends Activity{

    private static final int USE_FRONT_CAMERA = 2;
 //   private static String SAVE_PATH=Environment.getExternalStorageDirectory().getPath();
    private static final int REQUEST_CODE_CAPTURE_CAMEIA = 1;
    private final String[] steps={"填写信息","上传身份证","身份认证","完成认证"};
    private ActivityStep1Binding mBinding;
    private String STEP_INDEX;
    private TopMenuHeader top;
    private Context mContext;
    private String currentPic;
    private TextView textFront,textBack;
    private ImageView ivMyPic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
        mBinding= DataBindingUtil.setContentView(this, R.layout.activity_step1);
        Intent intent=getIntent();
        STEP_INDEX=intent.getStringExtra("step");
        initView();
        if (STEP_INDEX==null||STEP_INDEX.equals("0")){
            initStep1Content();
        }else if (STEP_INDEX.equals("1")){
            initStep2Content();
        }else if (STEP_INDEX.equals("2")){
            initStep3Content();
        }
        clickListenner();
    }

    private void initStep3Content() {
        View view=View.inflate(mContext,R.layout.step3,null);
        ivMyPic=(ImageView)view.findViewById(R.id.iv_my_pic);
        Button btnFinish = (Button) view.findViewById(R.id.btn_finish);
        LinearLayout selectPic=(LinearLayout)view.findViewById(R.id.select_pic);
        selectPic.setOnClickListener((View v)->{
            File file=new File(Environment.getExternalStorageDirectory().toString()+System.currentTimeMillis()+".png");
            if (!file.getParentFile().exists()) { // 父文件夹不存在
                file.getParentFile().mkdirs(); // 创建文件夹
            }
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
            Uri uri = Uri.fromFile(file);
            // 获取拍照后未压缩的原图片，并保存在uri路径中
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent,USE_FRONT_CAMERA);
        });
        btnFinish.setOnClickListener((View v)->{
            if (GlobalData.MY_SELFIE!=null){
                //提交
            }
        });

        mBinding.include.addView(view);
    }

    private void initStep2Content() {
        View view=View.inflate(mContext,R.layout.step2,null);
        LinearLayout selectFront=(LinearLayout)view.findViewById(R.id.select_front);
        LinearLayout selectBack=(LinearLayout)view.findViewById(R.id.select_back);
        textFront=(TextView)view.findViewById(R.id.text_front);
        textBack=(TextView)view.findViewById(R.id.text_back);
        Button btnNext=(Button)view.findViewById(R.id.btn_next);

        btnNext.setOnClickListener((View v)->{
            if (GlobalData.MY_CARD_FRONT!=null&&GlobalData.MY_CARD_BACK!=null){
                Intent intent=new Intent(mContext,Step1Activity.class);
                intent.putExtra("step","2");
                startActivity(intent);
                finish();
            }else {
                CommonUtil.showToast(mContext,"请上传图片！");
            }
        });
        selectFront.setOnClickListener((View v)->{
            currentPic="front";
            getImageFromCamera();
        });
        selectBack.setOnClickListener((View v)->{
            currentPic="back";
            getImageFromCamera();
        });
        mBinding.include.addView(view);
    }

    private void getImageFromCamera() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            File file=new File(Environment.getExternalStorageDirectory()+"/mypic/"+System.currentTimeMillis()+"pic.png");
            if (!file.getParentFile().exists()) { // 父文件夹不存在
                file.getParentFile().mkdirs(); // 创建文件夹
            }
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
          //  Uri uri = Uri.fromFile(file);
            // 获取拍照后未压缩的原图片，并保存在uri路径中
            //intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, REQUEST_CODE_CAPTURE_CAMEIA);
        }
        else {
            Toast.makeText(getApplicationContext(), "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
        }
    }

    private void initStep1Content() {
        View view=View.inflate(mContext,R.layout.step1,null);
        XEditText etName= (XEditText) view.findViewById(R.id.et_real_name);
        XEditText etCard= (XEditText) view.findViewById(R.id.et_identity_card);
        Button btnNext=(Button)view.findViewById(R.id.btn_next);

        btnNext.setOnClickListener((View v)->{
                if (!etName.getText().toString().trim().equals("")&&!etCard.getText().toString().trim().equals("")){
                    Intent intent=new Intent(mContext,Step1Activity.class);
                    intent.putExtra("step","1");
                    startActivity(intent);
                    finish();
                }else {
                    CommonUtil.showToast(mContext,"请补充信息！");
                }
        } );
        mBinding.include.addView(view);
    }

    private void clickListenner() {

    }

    private void initView() {
        top=new TopMenuHeader(getWindow().getDecorView());
        top.topMenuTitle.setText("帮带员申请");
        mBinding.stepsView.setLabels(steps)
                .setBarColorIndicator(getResources().getColor(R.color.material_blue_grey_800))
                .setProgressColorIndicator(getResources().getColor(R.color.themeColor))
                .setLabelColorIndicator(getResources().getColor(R.color.themeColor))
                .setCompletedPosition(STEP_INDEX==null?0:Integer.parseInt(STEP_INDEX))
                .drawView();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == REQUEST_CODE_CAPTURE_CAMEIA) {
                Uri uri = data.getData();
                if (uri == null) {
                    //use bundle to get data
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        Bitmap photo = (Bitmap) bundle.get("data"); //get bitmap
                        uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), photo, null,null));
                    } else {
                        Toast.makeText(getApplicationContext(), "未知错误!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(uri,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                File file=new File(picturePath);
                if (currentPic.equals("front")){
                    GlobalData.MY_CARD_FRONT=file;
                    textFront.setText("上传成功✔️");
                    textFront.setTextColor(getResources().getColor(R.color.themeColor));
                }else {
                    GlobalData.MY_CARD_BACK=file;
                    textBack.setText("上传成功✔️");
                    textBack.setTextColor(getResources().getColor(R.color.themeColor));
                }

            }else if (requestCode == USE_FRONT_CAMERA){
                Uri uri = data.getData();
                if (uri == null) {
                    //use bundle to get data
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        Bitmap photo = (Bitmap) bundle.get("data"); //get bitmap
                        uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), photo, null,null));
                    } else {
                        Toast.makeText(getApplicationContext(), "未知错误!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(uri,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                File file=new File(picturePath);
                Glide.with(mContext).load(file)
                        .placeholder(R.drawable.my_pic)
                        .error(R.drawable.my_pic)
                        .crossFade(1000)
                        .into(ivMyPic);
                GlobalData.MY_SELFIE=file;
            }
        }
    }
}
