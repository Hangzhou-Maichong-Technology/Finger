# Finger
杭州迈冲科技指纹模块实例

## 一、使用说明
指纹模块采用串口与迈冲开发板进行连接，要使用指纹模块，即使用[迈冲串口库](https://github.com/Hangzhou-Maichong-Technology/SerialPort)与开发板进行通讯。

指纹模块协议可分为以下步骤：
1. 迈冲串口库与指纹模块建立连接
``` java
btnHandshake.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        serialPortManager.sendPacket(FingerUtil.handshake());
    }
});
```
2. 发送注册指纹协议进行指纹注册
``` java
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
```
3. 发送校验指纹协议检测指纹
``` java
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
```


## 二、下载体验
[Finger 实例 apk 下载](https://github.com/Hangzhou-Maichong-Technology/Finger/raw/master/apk/Finger.apk)