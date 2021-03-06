package com.edu.hrbeu.helpsend.activity.step;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.edu.hrbeu.helpsend.R;
import com.edu.hrbeu.helpsend.cache.ACache;
import com.edu.hrbeu.helpsend.databinding.ActivityStep1Binding;
import com.edu.hrbeu.helpsend.global.GlobalData;
import com.edu.hrbeu.helpsend.pojo.IdentifyInfo;
import com.edu.hrbeu.helpsend.pojo.ResponsePojo;
import com.edu.hrbeu.helpsend.seivice.IdentifyService;
import com.edu.hrbeu.helpsend.utils.CommonUtil;
import com.edu.hrbeu.helpsend.utils.ImgLoadUtil;
import com.edu.hrbeu.helpsend.utils.TopMenuHeader;
import com.google.gson.Gson;
import com.xw.repo.XEditText;
import com.youth.banner.loader.ImageLoader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.edu.hrbeu.helpsend.seivice.IdentifyService.retrofit;


public class Step1Activity extends Activity implements View.OnClickListener {

    private static final int USE_FRONT_CAMERA = 2;
    //   private static String SAVE_PATH=Environment.getExternalStorageDirectory().getPath();
    private static final int REQUEST_CODE_CAPTURE_CAMEIA = 1;
    private final String[] steps = {"填写信息", "上传身份证", "身份认证", "完成认证"};
    private ActivityStep1Binding mBinding;
    private String STEP_INDEX;
    private TopMenuHeader top;
    private Context mContext;
    private String currentPic;
    private TextView textFront, textBack;
    private ImageView ivMyPic;
    private String frontUrl;
    private String backUrl;
    private String mypicUrl;
    private String IDENTIFY_STATE;
    private ACache mCache;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mCache = ACache.get(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_step1);
        Intent intent = getIntent();
        STEP_INDEX = intent.getStringExtra("step");
        IDENTIFY_STATE = intent.getStringExtra("identifyState");
        initView();
        if (IDENTIFY_STATE == null || IDENTIFY_STATE.equals("-1")) {
            if (STEP_INDEX == null || STEP_INDEX.equals("0")) {
                initStep1Content();
            } else if (STEP_INDEX.equals("1")) {
                initStep2Content();
            } else if (STEP_INDEX.equals("2")) {
                initStep3Content();
            } else if (STEP_INDEX.equals("3")) {
                initStrp4Content();
            }
        } else {
            STEP_INDEX = "3";
            initStrp4Content();
        }
        clickListenner();

        handlerUriException();
    }


    private void initView() {
        top = new TopMenuHeader(getWindow().getDecorView());
        top.topMenuTitle.setText("帮带员申请");
        mBinding.stepsView.setLabels(steps)
                .setBarColorIndicator(getResources().getColor(R.color.material_blue_grey_800))
                .setProgressColorIndicator(getResources().getColor(R.color.themeColor))
                .setLabelColorIndicator(getResources().getColor(R.color.themeColor))
                .setCompletedPosition(STEP_INDEX == null ? 0 : Integer.parseInt(STEP_INDEX))
                .drawView();
    }

    private void initStrp4Content() {
        View view = View.inflate(mContext, R.layout.step4, null);
        ImageView ivAvatar = (ImageView) view.findViewById(R.id.iv_avatar);
        TextView tvName = (TextView) view.findViewById(R.id.tv_name);
        ImgLoadUtil.displayCircle(ivAvatar, mCache.getAsString("mAvatar"));
        tvName.setText(mCache.getAsString("mNickName"));
        mBinding.include.addView(view);
    }


    private void initStep3Content() {
        View view = View.inflate(mContext, R.layout.step3, null);
        ivMyPic = (ImageView) view.findViewById(R.id.iv_my_pic);
        Button btnFinish = (Button) view.findViewById(R.id.btn_finish);
        LinearLayout selectPic = (LinearLayout) view.findViewById(R.id.select_pic);
        selectPic.setOnClickListener((View v) -> {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
            mypicUrl = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + String.valueOf(System.currentTimeMillis()) + ".png";
            File file = new File(mypicUrl);
            Uri imageUri = Uri.fromFile(file);
            GlobalData.MY_SELFIE = file;
            // 获取拍照后未压缩的原图片，并保存在uri路径中
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, USE_FRONT_CAMERA);
        });
        btnFinish.setOnClickListener((View v) -> {
            if (GlobalData.MY_SELFIE != null) {
                //提交
                IdentifyService service = retrofit.create(IdentifyService.class);
                RequestBody photoFront = RequestBody.create(MediaType.parse("image/png"), GlobalData.MY_CARD_FRONT);
                RequestBody photoBack = RequestBody.create(MediaType.parse("image/png"), GlobalData.MY_CARD_BACK);
                RequestBody myPhoto = RequestBody.create(MediaType.parse("image/png"), GlobalData.MY_SELFIE);
                Map<String, RequestBody> map = new HashMap<String, RequestBody>();
                map.put("idcardFace\"; filename="+System.currentTimeMillis()+".png", photoFront);
                map.put("idcardBack\"; filename="+System.currentTimeMillis()+".png", photoBack);
                map.put("realHead\"; filename ="+System.currentTimeMillis()+".png", myPhoto);
                Call<ResponsePojo> call = service.submitIdentifyInfo(map, RequestBody.create(null,GlobalData.IDENTIFY_INFO));
                call.enqueue(new Callback<ResponsePojo>() {
                    @Override
                    public void onResponse(Call<ResponsePojo> call, Response<ResponsePojo> response) {
                        ResponsePojo pojo = response.body();
                        CommonUtil.showToast(mContext, pojo.getMessage());
                        Intent intent = new Intent(mContext, Step1Activity.class);
                        intent.putExtra("step", "3");
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<ResponsePojo> call, Throwable t) {
                        CommonUtil.showToast(mContext,"上传失败！");
                    }
                });

            }
        });

        mBinding.include.addView(view);
    }

    private void initStep2Content() {
        View view = View.inflate(mContext, R.layout.step2, null);
        LinearLayout selectFront = (LinearLayout) view.findViewById(R.id.select_front);
        LinearLayout selectBack = (LinearLayout) view.findViewById(R.id.select_back);
        textFront = (TextView) view.findViewById(R.id.text_front);
        textBack = (TextView) view.findViewById(R.id.text_back);
        Button btnNext = (Button) view.findViewById(R.id.btn_next);

        btnNext.setOnClickListener((View v) -> {
            if (GlobalData.MY_CARD_FRONT != null && GlobalData.MY_CARD_BACK != null) {
                Intent intent = new Intent(mContext, Step1Activity.class);
                intent.putExtra("step", "2");
                startActivity(intent);
                finish();
            } else {
                CommonUtil.showToast(mContext, "请上传图片！");
            }
        });
        selectFront.setOnClickListener((View v) -> {
            currentPic = "front";
            getImageFromCamera();
        });
        selectBack.setOnClickListener((View v) -> {
            currentPic = "back";
            getImageFromCamera();
        });
        mBinding.include.addView(view);
    }

    private void getImageFromCamera() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            Uri imageUri = null;
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //如果没有授权，则请求授权
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAPTURE_CAMEIA);
            } else {
                //有授权，直接开启摄像头

            }
            File file = null;
            if (currentPic.equals("front")) {
                frontUrl = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + String.valueOf(System.currentTimeMillis()) + ".png";
                file = new File(frontUrl);
                imageUri = Uri.fromFile(file);
                GlobalData.MY_CARD_FRONT = file;
            } else {
                backUrl = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + String.valueOf(System.currentTimeMillis()) + ".png";
                file = new File(backUrl);
                imageUri = Uri.fromFile(file);
                GlobalData.MY_CARD_BACK = file;
            }
            //Uri   imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), String.valueOf(System.currentTimeMillis()) + ".jpg"));
            // 获取拍照后未压缩的原图片，并保存在uri路径中
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, REQUEST_CODE_CAPTURE_CAMEIA);
        } else {
            Toast.makeText(getApplicationContext(), "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
        }
    }

    private void initStep1Content() {
        View view = View.inflate(mContext, R.layout.step1, null);
        XEditText etName = (XEditText) view.findViewById(R.id.et_real_name);
        XEditText etCard = (XEditText) view.findViewById(R.id.et_identity_card);
        Button btnNext = (Button) view.findViewById(R.id.btn_next);

        btnNext.setOnClickListener((View v) -> {
            if (!etName.getText().toString().trim().equals("") && !etCard.getText().toString().trim().equals("")) {

                Gson gson = new Gson();
                IdentifyInfo info = new IdentifyInfo();
                info.setUserId(mCache.getAsString("mId"));
                info.setName(etName.getText().toString().trim());
                info.setIdCard(etCard.getText().toString().trim());
                GlobalData.IDENTIFY_INFO = gson.toJson(info);
                Intent intent = new Intent(mContext, Step1Activity.class);
                intent.putExtra("step", "1");
                startActivity(intent);
                finish();
                CommonUtil.showToast(mContext, "请补充信息！");
            }
        });
        mBinding.include.addView(view);
    }

    private void clickListenner() {
        top.topMenuLeft.setOnClickListener(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAPTURE_CAMEIA) {

                if (currentPic.equals("front")) {
                    File file = new File(frontUrl);
                    GlobalData.MY_CARD_FRONT = file;
                    textFront.setText("上传成功✔️");
                    textFront.setTextColor(getResources().getColor(R.color.themeColor));
                } else {
                    File file = new File(backUrl);
                    GlobalData.MY_CARD_BACK = file;
                    textBack.setText("上传成功✔️");
                    textBack.setTextColor(getResources().getColor(R.color.themeColor));
                }

            } else if (requestCode == USE_FRONT_CAMERA) {
                File file = new File(mypicUrl);
                Glide.with(mContext).load(file)
                        .placeholder(R.drawable.my_pic)
                        .error(R.drawable.my_pic)
                        .crossFade(1000)
                        .into(ivMyPic);
                GlobalData.MY_SELFIE = file;
            }
        }
    }

    private void handlerUriException() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.top_menu_left:
                finish();
                break;
        }
    }
}
