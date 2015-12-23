#wimlib 文档

##1. 基本 WIM 处理概念

wimlib生成一个WIM文件（使用WIMSstruct结构体）。有两种方式去创建这样一个结构体：

（1）wimlib_open_wim()  

这是打开一个WIM文件并且创建一个WIMStruct代替它

（2）wimlib_create_new_wim()  

这是创建一个新的WIMStruct，它初始没有包含任何镜像并且还没有一个备份磁盘文件

See Creating and Opening WIMs for more details.

一个WIM文件，被一个WIMStruct文件表示，它包含0个或多个镜像。

镜像被提取（或者applied）： wimlib_extract_image()

镜像被增加（"captured" or "appended"）：wimlib_add_image()

删除镜像： wimlib_delete_image()

镜像导出： wimlib_export_image()

镜像更新或修改：wimlib_update_image()

实际写到磁盘文件中： wimlib_write()

！！如果WIM初始打开使用的是 wimlib_open_wim()，则需要使用 wimlib_overwrite()替代wimlib_write()来实现写入

See Extracting WIMs, Modifying WIMs, and Writing and Overwriting WIMs for more details.

注意在 WIMStruct 这个抽象的情况下，执行许多任务在WIM文件上是一个多步骤的过程。举例如下：为了增加一个镜像到一个已经存在的WIM文件中（wimlib-imagex append），必须依次使用以下函数：

    wimlib_open_wim()
    wimlib_add_image()
    wimlib_overwrite()

这个设计的目的是使这个库更加有用（通过允许不同方式的函数组合）。比如你可以多次改变WIM并且提交这些改变去磁盘文件中只使用一次 overwrite操作，从而提高效率

注意：在调用任何在 wimlib.h中声明的函数之前，wimlib_global_init() 可以被调用（一些时候是必需的），See its documentation for more details.

##清理

在你完成了任何 WIMStruct结构体相关操作之后，你可以调用wimlib_free()去释放与它相联的资源。当然，当你使用wimlib在你的程序中完成所有任务以后，你可以调用wimlib_global_cleanup()去释放任何其他为库分配的资源。

##错误处理

wimlib大部分函数成功返回0，并且在失败时会返回一个正数的wimlib_error_code值。使用wimlib_get_error_string()去获得一个描述错误代码的字符串。wimlib当然也会在发生一个错误时，在标准错误中输出错误信息。这个可能比错误代码会有更多的信息；为了使用它，需要调用wimlib_set_print_errors()。请注意这个只是为了便利，并且一些错误可能发生却并没有信息输出。当前，错误信息和字符串只能使用英语。

##语言环境和字符编码

o support Windows as well as UNIX-like systems, wimlib's API typically takes and returns strings of wimlib_tchar, which are in a platform-dependent encoding.

On Windows, each wimlib_tchar is 2 bytes and is the same as a "wchar_t", and the encoding is UTF-16LE.

On UNIX-like systems, each wimlib_tchar is 1 byte and is simply a "char", and the encoding is the locale-dependent multibyte encoding. I recommend you set your locale to a UTF-8 capable locale to avoid any issues. Also, by default, wimlib on UNIX will assume the locale is UTF-8 capable unless you call wimlib_global_init() after having set your desired locale.



###详细描述

Creating and Opening WIMs


