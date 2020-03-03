通讯录字母滑动指示器，高仿微信通讯录

<img src="https://github.com/lgdcoder/LetterIndex/blob/master/images/1.gif" style="zoom:20%;max-width:40%;" />

attrs for LetterIndexView

```
<declare-styleable name="LetterIndex">
    <attr name="text_size" format="integer" />
    <attr name="text_color" format="color" />
    <attr name="text_selected_color" format="color" />
    <attr name="circle_padding" format="dimension" />
    <attr name="circle_color" format="color" />
    <attr name="item_space" format="dimension" />
    <attr name="draw_circle_action_up" format="boolean" />
    <attr name="show_pop" format="boolean" />
</declare-styleable>
```

布局文件示例：

```
<com.xm.letterindex.LetterIndexView
    android:id="@+id/vLetterIndex"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_alignParentRight="true"
    android:layout_centerVertical="true"
    android:layout_gravity="right"
    android:paddingLeft="10dp"
    android:paddingTop="10dp"
    android:paddingRight="10dp"
    android:paddingBottom="10dp"
    app:circle_color="#07C160"
    app:circle_padding="2dp"
    app:draw_circle_action_up="false"
    app:item_space="10dp"
    app:show_pop="true"
    app:text_color="#202020"
    app:text_selected_color="#FFFFFF"
    app:text_size="10" />
```

