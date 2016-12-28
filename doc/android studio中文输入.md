# 简介
通过安装fcitx输入法平台，使用Archlinux下的中文输入法，从而实现中文输入。

# 存在问题
目前无法通过快捷键```Ctrl + Space```进行输入法的切换，因此建议使用fcitx自带的拼音输入法，通过```shift```可实现中英文切换
如需使用第三方输入法则需要提前通过```fcitx-configtool```工具将相应的第三方输入法设置为默认输入法，否则因快捷键问题无法进行中文输入

# 操作流程
以下操作步骤都在执行
```
prearch
arch
```
进入Archlinux环境后执行

## 一、安装fcitx、相应输入法模块以及配置工具
通过以下方式安装fcitx输入法平台，相应的输入法模块和fcitx配置工具，其中fcitx-im主要包括 fcitx-gtk2, fcitx-gtk3, fcitx-qt4 和 fcitx-qt5等模块
```
sudo pacman -S fcitx fcitx-im fcitx-configtool
```
如需对fcitx有更多了解，请查看[Arch wiki Fcitx (简体中文)](https://wiki.archlinux.org/index.php/Fcitx_(%E7%AE%80%E4%BD%93%E4%B8%AD%E6%96%87)#.E7.AC.AC.E4.B8.89.E6.96.B9.E6.8B.BC.E9.9F.B3.E8.BE.93.E5.85.A5.E6.B3.95)

## 二、安装字体
使用以下方式进行安装（可以选择合适的字体替换wqy-microhei）
```
sudo pacman -S wqy-microhei
```


可以通过以下方式查看pacman中所有字体，找寻合适的进行替换
```
pacman -Ss font
```
>部分中文字体介绍
+ adobe-source-han-sans-cn-fonts
思源黑体简体中文部分
+ adobe-source-han-sans-tw-fonts
思源黑体繁体中文部分
+ wqy-microhei
文泉驿微米黑，无衬线形式的高质量中日韩越 (CJKV) 轮廓字体。
+ wqy-zenhei
文泉驿正黑，黑体 (无衬线) 的中文轮廓字体，附带文泉驿点阵宋体 (也支持部分日韩字符)。
+ ttf-arphic-ukai
楷书 (带有笔触) Unicode 字体 (推荐启用反锯齿)
+ ttf-arphic-uming
明体 (印刷) Unicode 字体
+ opendesktop-fonts
新宋字体，之前为 ttf-fireflysung
+ wqy-bitmapfont
文泉驿点阵宋体 (衬线) 中文字体
+ ttf-hannom
中文、越南文 TrueType 字体
+ ttf-twAUR
（繁体）台湾教育部发行的标准楷书、宋体字体

如需对字体有更多了解，请查看[Arch wiki Fonts (简体中文)](https://wiki.archlinux.org/index.php/Fonts_(%E7%AE%80%E4%BD%93%E4%B8%AD%E6%96%87)#.E4.B8.AD.E6.96.87.E5.AD.97)

## 三、执行脚本
通过以下方式执行env.sh脚本文件，启动dbus并设置fcitx相应环境变量
```
source env.sh
```

执行后可通过执行以下命令
```
export
```
观察结果中是否存在```DBUS_SESSION_BUS_ADDRESS```、```XMODIFIERS```、```GTK_IM_MODULE```以及```QT_IM_MODULE```这四个环境环境变量从而确认是否执行成功
如不成功，请确保环境变量正确设置再进行下一步，否则fcitx无法启动

## 四、启动fcitx
此步请在执行过```./linuxgui.sh```后执行，否则fcitx无法正常启动

通过以下方式启动fcitx
```
fcitx &
```
执行结果可能出现```(ERROR-4488 /build/fcitx/src/fcitx-4.2.9.1/src/lib/fcitx/ime.c:432) fcitx-keyboard-cm-mmuock already exists```的错误，可以忽略并无影响

执行完成后可以通过以下方式查看是否执行成功
```
ps aux | grep fcitx
```
查看结果中是否存在名为fcitx的进程，如存在则执行成功

## 五、配置fcitx输入法
通过以下方式配置fcitx输入法
```
fcitx-configtool
```
执行后出现以下窗口，点击“+”符号选择输入法进行激活
![image](https://github.com/openthos/linux-android-analysis/tree/master/doc/.pic/1.jpg)

将"Only Show Current Language"项取消
![image](https://github.com/openthos/linux-android-analysis/tree/master/doc/.pic/2.jpg)


找到名为“pinyin”的选项（fcitx自带的中文拼音输入法），选中并点击右下角“OK”进行输入法激活
![image](https://github.com/openthos/linux-android-analysis/tree/master/doc/.pic/3.jpg)
（如想要使用第三方输入法则需要在此步中找到对应的选项进行激活，例如上图中的Google Pinyin）


选中“pinyin”点击下方“^”图标，将已激活的"pinyin" 设置为默认输入法
![image](https://github.com/openthos/linux-android-analysis/tree/master/doc/.pic/4.jpg)

下图为设置完成的结果
![image](https://github.com/openthos/linux-android-analysis/tree/master/doc/.pic/5.jpg)

## 六、启动Android studio
输入法配置完成后，执行一下代码打开Android studio即可输入中文
```
./opt/android-studio/bin/studio.sh
```