# OTA实现

**一，组成部分**

整个ota分成三个部分:

1.客户端，负责下载iso文件，检测更新，iso文件校验，文件下载完成之后，对iso文件进行校验，保持文件的完整性。

2.服务端，响应客户端的响应请求，iso文件的下载，检测更新等。

3.ota部分，根据下载的iso，完成系统的更新，并保存用户资料。



**二，运行流程**


首先，客户端点击检测更新按钮带着当前系统版本请求服务端，服务端对当前系统版本进行对比，如果有更新则返回有更新的提示，否则无提示。请求完服务端之后，如果用户点击进行更新按钮，那么请求服务端去下载最新的iso文件，下载完成之后，请求服务端最新iso文件的md5值，在客户端对下载的iso文件进行md5校验，然后对比两个md5值，目的检测iso文件的完整性。同时在sd卡的system_os目录下创建update文件，写入iso文件名和一个用于更新的flag。一切工作完成之后，弹出是否重启按钮，当点击重启按钮之后，在内核成功启动之后，更新脚本会检测update文件中的flag进入升级模式，如果没有该文件则正常启动系统。进入升级模式之后且存在更新，将对话框提示用户选择更新；之后执行系统的更新动作，并保存下用户资料；最后将增加更新后系统启动项“new Android-x86”，且保留原系统启动项。



**三，OTA脚本分析**

```
#
# By xly
# Last updated 2016/06/28
#
# License: GNU Public License
#

# for update

rebooting()
{
	dialog --title " Rebooting... " --nocancel --pause "" 8 41 1
		sync
			umount -a
				reboot -f
				}

				progress_bar()
				{
					which dialog > /dev/null 2>&1
						if [ $? -ne 0 ]; then
							echo "$1  $2"

								else
									dialog --clear --title " $1 " --gauge "\n $2" 8 70
										fi
										}

										check_update_root()
										{
										#	echo "check_update_root() $PWD"

											iso=$1

												#try_mount ro $dev /mnt || return 1
														
																#把iso文件挂载到/update/iso目录下
																		mkdir /update/iso
																				mount -o loop $iso /update/iso
																						#SRC=/update/iso
																								SRC=iso
																									#检测是否存在ramdisk.img镜像
																										if [ ! -e /update/$SRC/ramdisk.img ]; then
																												return 1
																													fi
																														
																															#解压ramdisk.img到update目录
																																zcat /update/$SRC/ramdisk.img | cpio -id > /dev/null

																																	#检测是否存在system.sfs文件并对其进行挂载
																																		if [ -e /update/$SRC/system.sfs ]; then
																																				[[ -d sfs ]] || mkdir -p sfs
																																						mount -o loop /update/$SRC/system.sfs sfs
																																								mount -o loop sfs/system.img system

																																									#检测是否存在system.img文件并对其进行挂载
																																										elif [ -e /update/$SRC/system.img ]; then
																																												mount -o remount,rw foo /update
																																														mount -o loop /update/$SRC/system.img system

																																															#检测是否存在system目录并对其进行挂载
																																																elif [ -d /update/$SRC/system ]; then
																																																		mount -o remount,rw foo /update
																																																				mount --bind /update/$SRC/system system
																																																					else
																																																							rm -rf *
																																																									return 1
																																																										fi
																																																											mkdir mnt
																																																												echo " found at $1"
																																																													#rm /sbin/mke2fs
																																																														#hash -r
																																																														}

																																																														update_to()
																																																														{
																																																															echo "update_to() $PWD"

																																																																cd /

																																																																	#检测是否存在android分区，在前面的一些操作中，可能会损坏了android分区，有必要第二次检测
																																																																		if [ !\( mountpoint -q /mnt -a -a /mnt/android*/kernel \) ];then
																																																																				dialog --clear --title " Error " --defaultno --yesno \
																																																																							"\n Cannot find Android-x86 partition \n" 8 37

																																																																									return 255
																																																																										fi

																																																																											#adir=`dirname /mnt/android*/kernel`

																																																																												fs=`cat /proc/mounts | grep '/android/system' | awk '{ print $3 }'`

																																																																												###
																																																																													cmdline=`sed "s|\(initrd.*img\s*\)||; s|quiet\s*||; s|\(vga=\w\+\?\s*\)||; s|\(DPI=\w\+\?\s*\)||; s|\(INSTALL=\w\+\?\s*\)||; s|\(SRC=\S\+\?\s*\)||; s|\(DEBUG=\w\+\?\s*\)||; s|\(BOOT_IMAGE=\S\+\?\s*\)||" /proc/cmdline`
																																																																														asrc=android-$VER
																																																																															#asrc=$(echo $SRC | cut -b 2- )
																																																																															#add GRUB
																																																																																
																																																																																	#在grub菜单中添加新的启动项
																																																																																		if [ -d /mnt/grub ]; then
																																																																																				cp -af /mnt/grub/menu.lst /mnt/grub/menu.lst.old
																																																																																						menulst=/mnt/grub/menu.lst
																																																																																								#echo -e "title new $asrc\n\tkernel /$asrc/kernel.new quiet $cmdline SRC=/$asrc\n\tinitrd /$asrc/initrd.img.new\n" >> $menulst
																																																																																										echo -e "title new Android-x86\n\tkernel /$asrc/upgrade/kernel quiet $cmdline SRC=/$asrc/upgrade\n\tinitrd /$asrc/upgrade/initrd.img\n" >> $menulst

																																																																																											fi

																																																																																											#add EFI GRUB2
																																																																																												#adev=$( awk '/android\/system/{print $1}' /proc/mounts )
																																																																																													#for efi_dev in $( blkid | awk '/EFI/{print $1}') ; do
																																																																																														
																																																																																															#如果有efi分区，在efi分区的grub菜单中添加新的启动项
																																																																																																if [ -d /mnt/efi ]; then
																																																																																																		cp -af /mnt/efi/boot/grub.cfg /mnt/efi/boot/grub.cfg.old
																																																																																																				grubcfg=/mnt/efi/boot/grub.cfg
																																																																																																						#echo -e "menuentry \"new $asrc \" {\n\tsearch --set=root --file /$asrc/kernel.new\n\tlinuxefi /$asrc/kernel.new quiet $cmdline \n\tinitrdefi /$asrc/initrd.img.new\n}" >> $grubcfg
																																																																																																								echo -e "menuentry \"new Android-x86 \" {\n\tsearch --set=root --file /$asrc/upgrade/kernel\n\tlinuxefi /$asrc/upgrade/kernel quiet $cmdline \n\tinitrdefi /$asrc/upgrade/initrd.img\n}" >> $grubcfg

																																																																																																									elif [ ! -d /mnt/grub ]; then

																																																																																																										adev=$( awk '/android\/system/{print $1}' /proc/mounts )
																																																																																																											for fat_dev in $( blkid | awk '/fat/{print $1}') ;do

																																																																																																													if [ "${fat_dev%%[0-9]*}"x = "${adev%%[0-9]*}"x ];then
																																																																																																															efi_dir="/update/efi"
																																																																																																																	[ -d $efi_dir ] || mkdir $efi_dir
																																																																																																																			mountpoint -q $efi_dir && umount $efi_dir
																																																																																																																					mount -o rw ${fat_dev%:*} $efi_dir

																																																																																																																								if [ -e $efi_dir/efi/boot/grub.cfg ]; then
																																																																																																																												grubcfg=$efi_dir/efi/boot/grub.cfg
																																																																																																																																cp -af $grubcfg ${grubcfg}.old
																																																																																																																																				echo -e "menuentry \"new Android-x86 \" {\n\tsearch --set=root --file /$asrc/upgrade/kernel\n\tlinuxefi /$asrc/upgrade/kernel quiet $cmdline \n\tinitrdefi /$asrc/upgrade/initrd.img\n}" >> $grubcfg

																																																																																																																																								efi_dev=${fat_dev%:*}
																																																																																																																																												break
																																																																																																																																															fi
																																																																																																																																																	fi
																																																																																																																																																		done
																																																																																																																																																			fi

																																																																																																																																																			#system ...

																																																																																																																																																				files="update/$SRC/kernel update/$SRC/initrd.img update/$SRC/ramdisk.img"
																																																																																																																																																					sysimg="update/system"
																																																																																																																																																						files="$files $sysimg"

																																																																																																																																																							size=0
																																																																																																																																																								#计算需要的空间大小
																																																																																																																																																									for s in `du -sk $files | awk '{print $1}'`; do
																																																																																																																																																											size=$(($size+$s))
																																																																																																																																																												done

																																																																																																																																																													#删除以往更新可能存在的更新目录
																																																																																																																																																														rm -rf $adir/upgrade
																																																																																																																																																															#在当前android系统的根目录下重新创建upgrade目录
																																																																																																																																																																mkdir -p $adir/upgrade
																																																																																																																																																																	cd $adir/upgrade

																																																																																																																																																																		#更新的进度条显示
																																																																																																																																																																			( ( cd /; find $files | cpio -H newc -o ) | pv -ns ${size}k | ( cpio -iud > /dev/null; echo $? > /tmp/result )) 2>&1 \
																																																																																																																																																																					| progress_bar "Upgrade Android-x86" "Expect to write $size KB..."
																																																																																																																																																																						result=$((`cat /tmp/result`*255))
																																																																																																																																																																							
																																																																																																																																																																								#把新系统的系统文件mv到upgrade目录下
																																																																																																																																																																									if [ $result -eq 0 ]; then
																																																																																																																																																																											for d in update sfs ./$SRC; do
																																																																																																																																																																														[ -d $d ] && mv $d/* . && rmdir $d
																																																																																																																																																																																done
																																																																																																																																																																																		chown 0.0 *
																																																																																																																																																																																				for f in *; do
																																																																																																																																																																																							[ -d $f ] || chmod 644 $f
																																																																																																																																																																																									done

																																																																																																																																																																																										fi

																																																																																																																																																																																											#ln -s ../data ./data
																																																																																																																																																																																												#cp data目录到upgrade目录下，保存用户资料
																																																																																																																																																																																													cp -af ../data ./

																																																																																																																																																																																														#flush数据
																																																																																																																																																																																															dialog --infobox "\n Syncing to disk..." 5 27
																																																																																																																																																																																																sync

																																																																																																																																																																																																	return $result
																																																																																																																																																																																																	}

																																																																																																																																																																																																	do_update()
																																																																																																																																																																																																	{
																																																																																																																																																																																																	#adir=`dirname /mnt/android*/kernel`

																																																																																																																																																																																																	#debug模式下，在根目录创建一个update目录，存放iso解压文件
																																																																																																																																																																																																	mkdir /update
																																																																																																																																																																																																	#把update目录挂载成临时文件系统，防止空间不够，加速解压。
																																																																																																																																																																																																	mount -t tmpfs tmpfs /update
																																																																																																																																																																																																	cd /update

																																																																																																																																																																																																	#对iso文件进行挂载函数
																																																																																																																																																																																																	check_update_root $iso_file

																																																																																																																																																																																																	#检测是否存在install.img文件并对其解压到根目录
																																																																																																																																																																																																	if [ -e /update/iso/install.img ];then

																																																																																																																																																																																																	zcat /update/iso/install.img | ( cd /; cpio -iud > /dev/null )

																																																																																																																																																																																																	#载入键盘输入模块
																																																																																																																																																																																																	busybox modprobe atkbd

																																																																																																																																																																																																	else
																																																																																																																																																																																																		echo "Cannot find install.img "
																																																																																																																																																																																																			return 1
																																																																																																																																																																																																			fi
																																																																																																																																																																																																				
																																																																																																																																																																																																					#提示是否进行更新对话框
																																																																																																																																																																																																						dialog --title " Confirm " --no-label Skip --defaultno --yesno \
																																																																																																																																																																																																								"\n Do you want to upgrade Android-x86 ?" 7 47
																																																																																																																																																																																																									if [ $? -ne 0 ]; then
																																																																																																																																																																																																											return 1
																																																																																																																																																																																																												fi

																																																																																																																																																																																																												#update_hd

																																																																																																																																																																																																												#命令是否执行成功标记
																																																																																																																																																																																																												retval=1

																																																																																																																																																																																																												choice=""

																																																																																																																																																																																																												#真正进行更新的函数
																																																																																																																																																																																																												update_to $choice
																																																																																																																																																																																																												retval=$?

																																																																																																																																																																																																												#更新失败时的提示
																																																																																																																																																																																																												if [ $retval -eq 255 ]; then
																																																																																																																																																																																																												dialog --title ' Error! ' --yes-label Retry --no-label Reboot \
																																																																																																																																																																																																												--yesno '\n Upgrade failed! Please check if you have enough free disk space to upgrade Android-x86.' 8 51
																																																																																																																																																																																																												[ $? -eq 1 ] && rebooting
																																																																																																																																																																																																												fi

																																																																																																																																																																																																												#检测新系统的kernel是否存在
																																																																																																																																																																																																												if [ -e /mnt/android*/upgrade/kernel ];then

																																																																																																																																																																																																												#把update文件保存为更新过的update文件
																																																																																																																																																																																																												mv $detect_file ${detect_file}.old

																																																																																																																																																																																																												#重启
																																																																																																																																																																																																												rebooting
																																																																																																																																																																																																												fi

																																																																																																																																																																																																												return 1;

																																																																																																																																																																																																												}

																																																																																																																																																																																																												#升级脚本的入口函数，当系统初始化的时候会调用这个入口函数
																																																																																																																																																																																																												update_detect()
																																																																																																																																																																																																												{		
																																																																																																																																																																																																													
																																																																																																																																																																																																														#检测是否存在android分区，有必要的检测。因为如果不存在android分区，进行升级是无意义的。
																																																																																																																																																																																																															if [ !\( mountpoint -q /mnt -a -e /mnt/android*/kernel \) ];then
																																																																																																																																																																																																																	echo "Cannot find Android-x86 partition "

																																																																																																																																																																																																																			return 255
																																																																																																																																																																																																																				fi
																																																																																																																																																																																																																					#获取android系统的根目录
																																																																																																																																																																																																																						adir=`dirname /mnt/android*/kernel`

																																																																																																																																																																																																																							#detect_file="$adir/data/local/update"
																																																																																																																																																																																																																								#定义升级flag文件，用于检测是否存在升级
																																																																																																																																																																																																																									detect_file="$adir/data/media/0/System_OS/update"

																																																																																																																																																																																																																										iso_name=$(tac $detect_file | sed -n 2p )
																																																																																																																																																																																																																											#定义升级包文件
																																																																																																																																																																																																																												iso_file=$(dirname $detect_file)/$iso_name
																																																																																																																																																																																																																													#iso_file="$adir/data/local/android_x86_64_4.4.iso"
																																																																																																																																																																																																																														#检查升级文件update中的flag标记，flag标记为1表示可以进行升级。
																																																																																																																																																																																																																															value=`tail -n 1 $detect_file`
																																																																																																																																																																																																																																if [ "$value"x = "1"x -a -e $iso_file ]; then
																																																																																																																																																																																																																																	echo "Update Android-x86 "
																																																																																																																																																																																																																																		#有更新的时候进行更新的入口函数
																																																																																																																																																																																																																																			do_update
																																																																																																																																																																																																																																				fi
																																																																																																																																																																																																																																					#flag不为1提示无更新
																																																																																																																																																																																																																																						echo "No Update "

																																																																																																																																																																																																																																						}

																																																																																																																																																																																																																																						```

