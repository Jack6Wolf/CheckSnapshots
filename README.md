# Release 构建依赖检查

在平常的开发迭代中，如果 Code Review 不够仔细，就容易出现将 SNAPSHOT 版本带到线上。其实
这是一种比较危险的做法。

> 某 APP 在上一个版本 v1.2.0 中依赖了 libsecurity 库，版本号为 1.0.0-SNAPSHOT，接下来，APP 要发布 v1.3.0，在这个版本中，
> 依赖了 libsecurity 库的 1.0.1-SNAPSHOT 版本，经过灰度发布后，未见明显异常，于是开始全量发布，结果，在放量的过程中，
> 发现有个新增的崩溃陡增，经排查发现，是由于 libsecurity 库中的动态库导致，因此不得不将 libsecurity 库的版本回滚到
> 上一个版本 —— 1.0.0-SNAPSHOT，回滚后灰度依然未发现问题，接着开始全量，在全量的过程，又发现了跟之前一样的崩溃。

故事到这儿，可能有人就会问了，上一个版本不是没问题么？为什么回滚版本了，在新版本中出现的崩溃为什么会出现在旧版本中？

经过排查发现，原来是 libsecurity 库的维护者将 1.0.1-SNAPSHOT 中的 feature 以 1.0.0-SNAPSHOT 的版本号发布到了 Maven
仓库中，导致原来的 1.0.0-SNAPSHOT 混入了新的代码，所以，即使回滚到了原来的版本，问题依然还存在。

## 解决方案

为了方便的解决这一问题，用于对 *Release* 构建的依赖库版本进行检查，避免依赖 *SNAPSHOT* 版本的库；当然我们也可以随意配置！

这样我们也可以在自动化打包中也能无缝使用！

1. root build.gradle

```groovy
buildscript {
   
    dependencies {
        classpath 'com.jack.plugin:checksnapshots:1.0.0'
    }
}
```
2. app build.gradle

```groovy
apply plugin: 'checksnapshot'
snapshotCheck {
    dump = true // 是否将依赖写到build/dependency.txt
    abortBuild = true // 如果有snapshot 是否中断打包
    checkBuildTypes = ['release', 'prod'] // 在指定的build type会做snapshot检查
//    needCheck = true //和checkBuildTypes互相冲突 是否需要做snapshot检查
}
```

如上面代码注释一般，它会在打release包的mergeReleaseAssets Task阶段中检测是否依赖 SNAPSHOT 版本。如果依赖则中断打包！提醒用户替换 RELEASE 版本！