package com.hzmct.finger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.concurrent.atomic.AtomicBoolean;

import android_serialport_api.SerialPortManager;

public class MainActivity extends AppCompatActivity {
    private ImageView ivFinger;
    private Button btnHandshake;
    private Button btnRegister;
    private Button btnVerity;
    private KProgressHUD kProgressHUD;

    private SerialPortManager serialPortManager;
    private int registerStep = 0;
    private int queryStep = 0;
    private int resultType = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivFinger = findViewById(R.id.iv_finger);
        btnHandshake = findViewById(R.id.btn_handshake);
        btnRegister = findViewById(R.id.btn_register);
        btnVerity = findViewById(R.id.btn_verity);

        serialPortManager = new SerialPortManager("/dev/ttyS3",  57600);

        kProgressHUD = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请按指纹")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        initListener();
    }

    private void initListener() {
        btnHandshake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serialPortManager.sendPacket(FingerUtil.handshake());
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (registerStep < 4) {
                    serialPortManager.sendPacket(FingerUtil.collect());
                } else {
                    serialPortManager.sendPacket(FingerUtil.compound());
                }

                kProgressHUD.show();
                resultType = 0;
            }
        });

        btnVerity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serialPortManager.sendPacket(FingerUtil.collect());

                kProgressHUD.show();
                queryStep = 0;
                resultType = 1;
            }
        });

        serialPortManager.setOnDataReceiveListener(new SerialPortManager.OnDataReceiveListener() {
            @Override
            public void onDataReceive(byte[] bytes, int i) {
                LogUtils.i("recvBytes === " + ConvertUtils.bytes2HexString(bytes) + ", size == " + i);
                FingerPacket resultPacket = FingerUtil.result(bytes);

                if (resultPacket.confirmCode == 0x02) {
                    LogUtils.i("没有手指触摸");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showShort("请按指纹后重试");
                            resultType = -1;
                            if (kProgressHUD.isShowing()) {
                                kProgressHUD.dismiss();
                            }
                        }
                    });
                    return;
                }

                if (resultType == 0 && resultPacket.confirmCode == 0x00) {
                    updateRegisterState();
                } else if (resultType == 1) {
                    updateQueryState(resultPacket.confirmCode);
                } else {
                    resultType = -1;
                }
            }
        });
    }

    private void updateRegisterState() {
        switch (registerStep) {
            case 0:
                serialPortManager.sendPacket(FingerUtil.saveId(1));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        kProgressHUD.dismiss();
                        btnRegister.setText("第二次注册");
                    }
                });
                break;
            case 1:
                serialPortManager.sendPacket(FingerUtil.saveId(2));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        kProgressHUD.dismiss();
                        btnRegister.setText("第三次注册");
                    }
                });
                break;
            case 2:
                serialPortManager.sendPacket(FingerUtil.saveId(3));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        kProgressHUD.dismiss();
                        btnRegister.setText("第四次注册");
                    }
                });
                break;
            case 3:
                serialPortManager.sendPacket(FingerUtil.saveId(4));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        kProgressHUD.dismiss();
                        btnRegister.setText("第五次注册");
                    }
                });
                break;
            case 4:
                serialPortManager.sendPacket(FingerUtil.saveCompound());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        kProgressHUD.dismiss();
                        btnRegister.setText("注册完成");
                    }
                });
                break;
            default:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        kProgressHUD.dismiss();
                    }
                });
                break;
        }

        registerStep++;
        resultType = -1;
    }

    private void updateQueryState(byte confirmCode) {
        if (confirmCode == 0x00) {
            if (queryStep == 0) {
                serialPortManager.sendPacket(FingerUtil.saveId(1));
            } else if (queryStep == 1) {
                serialPortManager.sendPacket(FingerUtil.query());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        kProgressHUD.dismiss();
                    }
                });
            } else if (queryStep == 2) {
                ToastUtils.showShort("指纹验证成功");
            }
        } else if (confirmCode == 0x09) {
            ToastUtils.showShort("指纹未注册，验证失败");
        }

        queryStep++;
    }
}
