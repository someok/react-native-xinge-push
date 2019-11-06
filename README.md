# react-native-xinge-push

fork 自 [PandaQQ/react-native-xinge-push](https://github.com/PandaQQ/react-native-xinge-push) `0.8.0`

## 简介 - 20190809 正式更新

经过一段时间的项目测试和完善，虽然中间遇到很多推送的坑。但是还是一直坚持使用信鸽推送，第一免费，第二支持内地厂商通道以及海外 FCM 同时推送。

## TODO

-   [ ] 如何在 React Native 中优雅的使用这个推送 （Redux+Saga 实战）

## New Features

-   [x] Android 适配 FCM，小米，华为(魅族没申请到无法测试)文档
-   [x] 支持厂商通道的 intent 来点击回调获取参数或者跳转自定义页面
-   [x] 修改注册机制，信鸽推送在一开启 App 时就开始注册而非在 RN 触发注册
-   [x] Android 配置无需在 RN 配置，直接在 app.gradle 文件配置即可
-   [x] Andrroid getInititalNotficaiton 功能完成并在 FCM，小米，华为机均测试通过
-   [x] iOS 在 getInititalNotficaiton 之后清除通知

## Done

-   [x] 支持 Fcm 集成
-   [x] 计划更新 iOS SDK 到 v3.3.5
-   [x] 计划更新 Android SDK v4.3.2 厂商通道
-   [x] 升级信鸽 Android SDK 到 v3.2.2
-   [x] 适配华为官方推送通道
-   [x] 适配小米官方推送通道
-   [x] 适配魅族官方推送通道
-   [x] 升级信鸽 iOS SDK 到 v3.1.0

## 简介 - 20190227 更新

因为项目开发决定采用信鸽推送，鉴于信鸽官方并不支持 React Native，所幸有 Jeepeng 推出了 https://github.com/Jeepeng/react-native-xinge-push, 是已经好久没有更新了。同时这个库遇到 https://github.com/Jeepeng/react-native-xinge-push/issues/22 这个问题，所幸https://github.com/wanxsb/react-native-xinge-push 在这个版本已经解决。接下去因为项目会涉及 xinge 推送的开发以及维护，所以会有些时间帮忙维护下一个库。本人比较熟悉 iOS&Android 原生开发，同时负责的团队也正在往 React Native 过渡，希望可以给社区贡献一点力量。

## 版本对照表

| react-native-xinge-push | 信鸽 SDK（Android） | 信鸽 SDK（iOS） |
| ----------------------- | ------------------- | --------------- |
| 1.0                     | 4.3.2               | v3.3.5          |
| 0.6                     | 3.2.2               | 3.1.1           |
| 0.4 ～ 0.5              | 3.2.2               | 2.5.0           |
| 0.3                     | 3.1.0               | 2.5.0           |

## install

```
npm install --save https://github.com/someok/react-native-xinge-push.git
```

## link

```
react-native link react-native-xinge-push
```

## usage

### Android

Android 的推送简直就是个无敌大坑啊！！！！！但是终于我还是搞定啦哈哈哈哈～～

#### Android 集成攻略

众所周知，当 Application 启动完成之后才开始启动 React Native。所以在 React Native 中初始化并且注册信鸽推送服务其实并不是特别理想。因为当 App 已经被杀死的时候，就无法获取推送的消息内容并作下一步的业务。所以在 Android 平台下，我们必须要注册信鸽服务于 React Native 之前。

-   1. 在 build.gradle (Module:app) 下配置 manifestPlaceholders， （注意 my*xm_xg_id 和 my_xm_xg_key 需要在 XM-后添加 id 和 key, 因为我发现 Android 读取全是数字的 id 和 key 会自动转成 Float - *神一样的操作，写这个的人出来看我不打死他！\_）

```javascript
        manifestPlaceholders = [
                XG_ACCESS_ID : "",
                XG_ACCESS_KEY: "",
                HW_APPID     : "",
                PACKAGE_NAME : "",
                my_xm_xg_id : "XM-",
                my_xm_xg_key: "XM-",
        ]
```

-   2. 在项目的 AndroidManifest.xml

```xml
        <meta-data
            android:name="MY_XM_XG_ID"
            android:value="${my_xm_xg_id}"/>
        <meta-data
            android:name="MY_XM_XG_KEY"
            android:value="${my_xm_xg_key}"/>
```

-   3. 在项目的 AndroidManifest.xml 中的 MainActivity 下添加 intent-filter

```xml
<activity
                android:screenOrientation="portrait"
                android:name=".MainActivity"
                android:configChanges="keyboard|keyboardHidden|orientation|screenSize"
                android:label="@string/app_name"
                android:launchMode="singleTask"
                android:windowSoftInputMode="adjustPan">

            <intent-filter >
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT"/>
                <data android:scheme="xgscheme"
                    android:host="com.xg.push"
                    android:path="/notify_detail" />
            </intent-filter>
</activity>
```

-   4. 在 MainActivity 中添加 intent 接受方法

```java
  @Override
    public void onCreate(Bundle instance) {

        super.onCreate(instance);
        //// 集成厂商通道之后，点击推送通知使用 intent 推送内容
        Uri uri = getIntent().getData();
        PushMessage.getInstance().setAllValueIntentUrl(uri);
    }
```

#### Android intent 测试

https://xg.qq.com/docs/android_access/android_faq.html#消息点击事件以及跳转页面方法

-   测试过上述连接给出的方法再做总结,下面这段代码不行，获取不到参数，但是和使用无关：

```java

	 Uri uri = getIntent().getData();
 	 if (uri != null) {
		String url = uri.toString();
		UrlQuerySanitizer sanitizer = new UrlQuerySanitizer();
		sanitizer.setUnregisteredParameterValueSanitizer(UrlQuerySanitizer.getAllButNulLegal());
 		sanitizer.parseUrl(url);
 		String value1 = sanitizer.getValue("key1");
 		String value2 = sanitizer.getValue("key2");
 		Log.i("XG" , "value1 = " + value1 + " value2 = " + value2);
 	}

```

-   intent 调用 sample,注意 key 要符合定义，否则库收不到参数。(title, content 和 custom_content)

```
xgscheme://com.xg.push/notify_detail?title=aa&content=bb&custom_content=<json string>
```

#### FCM 通道集成指南

https://xg.qq.com/docs/android_access/FCM.html

#### 小米通道集成指南

https://xg.qq.com/docs/android_access/mi_push.html

#### 华为推送通道集成指南

http://xg.qq.com/docs/android_access/huawei_push.html

1. 确认已在信鸽管理台中「应用配置-厂商&海外通道」中填写相关的应用信息。通常，相关配置将在 1 个小时后生效，请您耐心等待，在生效后再进行下一个步骤
2. 将集成好的 App（测试版本）安装在测试机上，并且运行 App
3. 保持 App 在前台运行，尝试对设备进行单推/全推
4. 如果应用收到消息，将 App 退到后台，并且杀掉所有 App 进程
5. 再次进行单推/全推，如果能够收到推送，则表明厂商通道集成成功

###### 注意事项

消息目前将其理解为两类：静默消息和普通通知
静默消息不会弹窗，通知会弹窗

如果在 EMUI 8.0（Android 8）上，出现发通知成功但通知栏不显示的情况，并在 Logcat 看到以下错误：

```
E/NotificationService: No Channel found for pkg=com.jeepeng.push, channelId=null, id=995033369, tag=null, opPkg=com.huawei.android.pushagent, callingUid=10060, userId=0, incomingUserId=0, notificationUid=10261, notification=Notification(channel=null pri=0 contentView=null vibrate=null sound=default tick defaults=0x1 flags=0x10 color=0x00000000 vis=PRIVATE)
```

需要将`targetSdkVersion`[降到 25](https://stackoverflow.com/questions/45668079/notificationchannel-issue-in-android-o)

####

### iOS

AppDelegate.m:

```oc
/**
 * Copyright (c) 2015-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

#import "AppDelegate.h"

#import <XGPush/XGPushManager.h>
#import <XGPush.h>
#import <React/RCTBundleURLProvider.h>
#import <React/RCTRootView.h>

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
  NSURL *jsCodeLocation;

  jsCodeLocation = [[RCTBundleURLProvider sharedSettings] jsBundleURLForBundleRoot:@"index" fallbackResource:nil];

  RCTRootView *rootView = [[RCTRootView alloc] initWithBundleURL:jsCodeLocation
                                                      moduleName:@"example"
                                               initialProperties:nil
                                                   launchOptions:launchOptions];
  rootView.backgroundColor = [[UIColor alloc] initWithRed:1.0f green:1.0f blue:1.0f alpha:1];

  self.window = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
  UIViewController *rootViewController = [UIViewController new];
  rootViewController.view = rootView;
  self.window.rootViewController = rootViewController;
  [self.window makeKeyAndVisible];
  // 统计消息推送的抵达情况
  [[XGPush defaultManager] reportXGNotificationInfo:launchOptions];
  return YES;
}

// Required to register for notifications
- (void)application:(UIApplication *)application didRegisterUserNotificationSettings:(UIUserNotificationSettings *)notificationSettings
{
  [XGPushManager didRegisterUserNotificationSettings:notificationSettings];
}

- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
    [XGPushManager didRegisterForRemoteNotificationsWithDeviceToken:deviceToken];
}

// Required for the registrationError event.
- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error {
	NSLog(@"[XGPush] register APNS fail.\n[XGPush] reason : %@", error);
  [XGPushManager didFailToRegisterForRemoteNotificationsWithError:error];
}

// Required for the localNotification event.
- (void)application:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)notification
{
  [XGPushManager didReceiveLocalNotification:notification];
}

/**
 收到通知消息的回调，通常此消息意味着有新数据可以读取（iOS 7.0+）

 @param application  UIApplication 实例
 @param userInfo 推送时指定的参数
 @param completionHandler 完成回调
 */
- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler {
  NSLog(@"[XGPush] receive slient Notification");
  NSLog(@"[XGPush] userinfo %@", userInfo);
  UIApplicationState state = [application applicationState];
  BOOL isClicked = (state != UIApplicationStateActive);
  NSMutableDictionary *remoteNotification = [NSMutableDictionary dictionaryWithDictionary:userInfo];
  if(isClicked) {
    remoteNotification[@"clicked"] = @YES;
    remoteNotification[@"background"] = @YES;
  }
  [[XGPush defaultManager] reportXGNotificationInfo:remoteNotification];
  [XGPushManager didReceiveRemoteNotification:userInfo fetchCompletionHandler:completionHandler];
}

@end

```

## Example

see `example` folder for more details
