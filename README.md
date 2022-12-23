# EquivalenceVerify-Toy

Nanjing University Software Engineering course project, Fall 2022

# 这是什么？

一个图形化的，机器辅助的C++/C程序等价性判断工具。软件首先对程序文件的等价关系作出初步判断，然后将难以确定的程序源文件交给用户人工确认，最后将结果输出到文件中保存。

使用Java语言编写，基于JavaFX图形界面框架。

# 运行环境

项目在`java 17.0.4.1 2022-08-18 LTS`、`macOS 13.0.1`下能够正常编译和运行。请注意，因为未知原因，在`macOS 13.0.1`下需要使用Oracle JDK才能正常编译和运行本软件，如果使用OpenJDK，会出现无法运行本软件的情况。

项目依赖于g++，请确保运行环境中能够正常运行g++。项目依赖于系统的`$SHELL`环境变量所指定的shell程序，请确保系统的`$SHELL`环境变量被正确配置。

# 使用方式

软件是图形化的，因此操控软件本身并不困难，这里不再赘述。

值得注意的是等价判断目标目录的组织方式，软件会查找给定目标目录下的所有子文件夹，并将每一个子文件夹视为进行等价判断的一个单位，即该文件夹内的所有后缀名为c/cpp的文件被视为需要判断相互之间的等价性。此外，每个子文件夹还需要提供一个名为stdin_format.txt的文件，按照下面的规则描述程序的输入，如果不存在该文件则视为没有输入，文件中所有不符合规则的输入格式将被忽略。

规则：请使用空格分割每一个输入，每一个输入可以为以下几个格式中的一种：
- int(a,b): a<=value(int)<=b
- char: 随机大小写字母
- string(a,b): 由char组成，a<=length(string)<=b

例如：`int(1,2) string(2,3)`就是两个合法输入格式，软件将会根据格式信息生成相应输入，例如`1 abc`。

下面是一个目录组织方式的例子，这里的目标目录是input，其中有一个子文件夹dir，dir中存有输入格式描述文件和若干程序源文件：

```text
input
└─dir
    ├─oj1.cpp
    ├─oj2.cpp
    ├─oj3.cpp
    └─stdin_format.txt
```


# 运行演示

<table><tr>
  <td><img src="https://user-images.githubusercontent.com/84324349/209273310-7adb21d1-280a-4e3d-a783-8f09138ab77a.png" width="500px"></td>
  <td><img src="https://user-images.githubusercontent.com/84324349/209273326-15fef274-8d5a-469f-96cc-894aa8d24f2f.png" width="500px"></td>
</tr></table>

<p align="center">初始界面</p>

<table><tr>
  <td><img src="https://user-images.githubusercontent.com/84324349/209273350-952bac38-cca3-42b5-8d23-fcb522dcfa30.png" width="500px"></td>
  <td><img src="https://user-images.githubusercontent.com/84324349/209273374-d9397089-1985-42ce-8148-1febc17dd7c2.png" width="500px"></td>
</tr></table>

<p align="center">人工确认等价性时提供Diff功能</p>


<table><tr>
  <td><img src="https://user-images.githubusercontent.com/84324349/209273385-391627d4-ea64-453e-a4d7-6327a88479e1.png" width="500px"></td>
  <td><img src="https://user-images.githubusercontent.com/84324349/209273399-c692c4b2-a72e-42ca-9381-fe50f8bbafce.png" width="500px"></td>
</tr></table>

<p align="center">完成判断界面（左图）、错误提示（右图）</p>
