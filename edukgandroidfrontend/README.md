# Edukg

### Init repo

This module use the following libraries:

- [Androidx](https://developer.android.com/jetpack/androidx)
- [ViewPager2](https://developer.android.com/jetpack/androidx/releases/viewpager2)
- [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
- [Navigation](https://developer.android.com/guide/navigation)
- [ViewBinding](https://developer.android.com/topic/libraries/view-binding)
- [Google Material Design](https://material.io/develop/android/docs/getting-started)

### Update 0813 UTC +0800 22:37

DONE: Init login, fragment transfering

TODO: searchView, login logic and register logic

### Update 0814 UTC +0800 15:16

问题：

1.如果编译的时候信息乱码怎么办？

答：到安装包bin文件夹修改studio.exe.vmoptions和studio64.exe.vmoptions,加入-Dfile.encoding=UTF-8

2.样式问题？

答：上下margin和padding确定好，图片的一些比较重要的性质包括：
	android:layout_width="match_parent"
	android:layout_height="wrap_content"
	android:adjustViewBounds="true"
	android:clickable="true"
	android:cropToPadding="false"
	android:maxHeight="100dp"
	android:padding="30dp"
	android:layout_marginTop="@dimen/normalPadding"
	android:layout_marginBottom="@dimen/normalPadding"
	android:scaleType="fitCenter"     

3.前端暂存（非cookie）

答：暂时使用SharedPreference内置类，具体用法如下：

	修改时：
	final EditText usernameEditText = findViewById(R.id.username);
	final EditText passwordEditText = findViewById(R.id.password);
	Editable usernameText = usernameEditText.getText();
	Editable passwordText = usernameEditText.getText();
	SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
	SharedPreferences.Editor editor = sharedPreferences.edit(); //获取编辑器
	editor.putString("username", usernameText.toString());
	editor.putString("password", passwordText.toString());
	editor.putInt("state", 1);
	editor.commit(); //提交修改
	
	查询时：
	TextView personalInfo = thisView.findViewById(R.id.nickname_personal);
	SharedPreferences preferences = getActivity().getSharedPreferences ("userInfo",
	        getActivity().MODE_PRIVATE);
	int state = preferences.getInt("state", 0);
	if(state == 0){
	    personalInfo.setVisibility(View.INVISIBLE);
	}

4. 下一步规划

答：看登入登出，个人信息修改，信息暂存，可见和不可见等逻辑，这部分可以补充单元测试。
	页面角度下一步做详情页面的逻辑和结构设计，涉及到数据结构。
        

个人问题：

1. 用户登录后点击学情信息（含详情信息）跳转到哪里？点击快速跳转跳转到哪里？点击知识网络（含详情信息）跳转到哪里？

2. 用户端的页面可见性何不可见性可能需要更具体一点，如果遇到问题会写在文档里面。

DONE:

1. 个人页面，主页页面，部分跳转和登录逻辑。

TODO:

1. searchView，前端样式美化

2. 详情页面的设计和跳转逻辑。

### Update 0814 UTC +0800 22:29

1. 一个特别香的东西：https://www.jianshu.com/p/d3027acf475a

2. 一个可以真机DEBUG的东西： Toast.makeText(getActivity(), "你的选择是：" + query,
                        Toast.LENGTH_SHORT).show();

3. 请注意不同组件的大小，这可能和能否点击识别有很大关系，也请注意组件的重叠顺序。

DONE:

1. 修复了searchView的bug，一些显示问题。

2. 加入了详情页面部分跳转逻辑。

3. 修复了点击图片不能跳转的bug。

4. 加入了searchView的模糊匹配功能（有bug）。

TODO：

1. 修复模糊匹配的BUG。

2. 设计知识、习题、类别标签等不同的数据结构，重构代码。

3. 补充前半部分单元测试。

### Update 0815 UTC +0800 11:43

1. Database 请参考仓库里新加代码关于database的描述，基础方法是通过文件流将assets静态文件写入SD卡，然后从SD卡读数据库操作，这个cache会比SharedPreference大一点。

DONE:

1. Add question and answer page (with bug)

2. Import picture loading, voice loading modules

3. Import sqlite3 database to cache some pictures and assets

4. Add a little unit test

TODO:

1. Fix these bugs

2. Add more unit test

### Update 0815 UTC +0800 13:28

1. 这个应用在现在需要给应用开照相权限，文件权限，应用存储权限，挂载权限（如果有，一般是deprecated了），具体可以参考androidmanifests.xml

DONE:

1. 修复了数据库 cannot find line EMOJI 的bug（如果再次出现这个bug，请修改com.appsnipp.education.dmoji.emojidao文件中!file.exists()为file.exists()重新生成一次数据库文件。
通常如果克隆到自己目录不会出现无法适配的问题，但如果是复制粘贴请把目录改成自己项目的目录。

2. 基本完成了对话系统的前端界面，需要补充所有和后端的交互逻辑。

3. 修复了在有提示情况下不能直接登录的bug。

TODO:

1. 完成前后端通信逻辑。
2. 完善单元测试。
3. 完成搜索和详情界面。

### UPDATE 0815 UTC +0800 17:27

1. TAGGROUP: https://github.com/2dxgujun/AndroidTagGroup

DONE:

1. 用viewpager实现了tag的真正切换。

2. 完成了详情界面架构，但有bug。

### UPDATE 0816 UTC +0800 12:00

1. CHARTS https://github.com/lecho/hellocharts-android

DONE: 

1. 加入charts dependency

2. refactor fragment

### UPDATE 0817 UTC +0800 16:56

1. 加入tag view，输入框（有bug）。

2. 加入model框架。

### UPDATE 0817 UTC +0800 19:41

1. 增加tagView增加，删除效果。

2. 增加tagView样式。

### UPDATE 0817 UTC +0800 20:27

1. 修改扫描页面，主页格式

2. 增加chartsview依赖，增加chartview界面（有BUG）

### UPDATE 0818 UTC +0800 13:41
 
1. 增加Question类，Knowledge类，增加关联。

2. 修改searchView，优化体验。

### UPDATE 0818 UTC +0800 14:35

1. 修改homeFragment内容，增加外观优化。

2. 修复菜单栏的小bug。

### UPDATE 0818 UTC +0800 18:55

1. 更新了一些页面的外观样式和组件样式。

2. 增加scan扫描逻辑。

### UPDATE 0818 UTC +0800 19:23

1. 增加做题页面搜索筛选逻辑。

2. 优化部分界面外观。

### UPDATE 0818 UTC +0800 19:46

1. 增加搜索框跳转逻辑。

2. 增加fragment层叠关系。

### UPDATE 0818 UTC +0800 20:22

1. Add jump back hint

2. Fix some bugs

3. Beautify some UI components in knowledge detail page.

TODO:

1. Optimize the fragment dependency and jumping relations.

2. Optimize the structure of the code.

### UPDATE 0818 UTC +0800 20:40

1. 增加了卡片的跳转逻辑。

TODO:

1. 优化传参逻辑，特别是页面间的传参逻辑。

2. 和后端通信。

3. 主页subFragment的跳转逻辑和显示逻辑还需要修正。

### UPDATE 0819 UTC +0800 14:41

1. 增加前后端通信逻辑。

### UPDATE 0819 UTC +0800 15:39

1. 优化主页UI

2. 优化跳转逻辑

3. 优化知识详情页UI

## LSM 部分

### 关于网络使用

1. 请确保您的手机可以通过浏览器访问tomcat服务器。
2. 将AppSingleton类下的静态常量URL_PREFIX替换为访问服务器的ip及其端口。
3. 现在已经可以正常与服务器进行交互。

### future TODOs 

TODO:
1. 实现较好的重定向功能，目前仅仅是使用start intent的方式进行重定向，会压很多栈
2. 实现返回刷新的功能，当修改用户资料候，能自动跳到上一级，并且刷新上一级的页面显示新的用户资料。目前采用的是start intent的方式进行转移。
3. 整理页面栈，进行能做到返回保护：例如退出登录后按返回不会弹到登录前的页面
4. 很多UI设计用的默认名，例如ImageView7，如有必须需要改名。
5. 增加扫描的新功能：focus时候能自动上拉文本框，类似网易有道
6. 输入搜索框或文本框后显示叉号，点击叉号能取消文本 **
7. 进入用户信息页后每次都会重新请求昵称，会导致加载慢。可以考虑本地缓存 **
8. 搜索框往下拉的时候可以保持搜索框在上，参考百度的效果，此外下方也需要一点margin才能不被navigation遮挡 * （如果没时间，录DEMO的时候可以不拉）

### UPDATE 0820 UTC +0800 19:09

1. 和后端通信逻辑完成一部分。

2. 和EDUKG通信逻辑完成一部分。

TODO：

1. 前端有大量的UI和逻辑上的BUG需要修复。