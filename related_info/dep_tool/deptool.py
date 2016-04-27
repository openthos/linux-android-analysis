#! /usr/bin/python
import sys
import os

def copy(name):
        os.system('cp --parents -d '+name+' .')

def ll(name):
	tmp = os.popen('ls -l '+name).read().strip()
	return tmp 

allcopy = []
cmd = ''
print "Start to analysis", sys.argv[1]

os.system('mkdir dep')
os.chdir('./dep')

for i in range(1, len(sys.argv)):
        cmd += ' '+sys.argv[i]

cmd_search = 'which '+sys.argv[1]
#print cmd_search

result = os.popen(cmd_search).read().strip()
print "cmd path is",result

if result=='':
        print "The input is wrong ,please restart"
        quit()

os.system('strace -e trace=open,execve '+cmd+' 2>log 1>/dev/null')

f = open("log", "r")
while True:
	line = f.readline()
	if line.startswith("open") or line.startswith("exec") :
		#print line
        	if line.find("/proc")<0 and line.find("/dev")<0:
			dep_file = line.split('"')[1]
			#print line
			copy(dep_file)		
			allcopy.append(dep_file)
			if_sym = ll(dep_file).split(' -> ')
			while len(if_sym)>1:
				if if_sym[1].startswith("/"):
					copy(if_sym[1])
					tmp_sym = if_sym[0].split(' ')[-1]
                                        #print tmp_sym
                                        #print if_sym[1]
                                        relpath = os.path.relpath(if_sym[1],tmp_sym)[2:]
                                        #print relpath
					os.system('ln -s '+relpath+' .'+dep_file)
					allcopy.append(if_sym[1])
					if_sym = ll(if_sym[1]).split(' -> ')
				
				else:
					all_path = os.path.dirname(dep_file)+'/'+if_sym[1]
					copy(all_path)
					allcopy.append(all_path)
					if_sym = ll(all_path).split(' -> ')
			
	else:
		break
f.close()

copyname = []

for i in allcopy:
	tmp_name = i.split('/')[-1]
#	print tmp_name
	copyname.append(tmp_name)


for i in allcopy:
	ifelf = os.popen('file '+i).read().strip()
	if 'ELF' in ifelf:
#		print i
		filestrings = os.popen('strings '+i+' | grep "\.so"').read().strip().split('\n')
		for j in filestrings:
			if j not in copyname and ' ' not in j and '%' not in j:
				if j.startswith("/"): 
					name_j = j.split('/')[-1]
					if name_j not in copyname:
						copyname.append(name_j)
						copy(j)
						if_sym = ll(j).split(' -> ')
                        			while len(if_sym)>1:
							if if_sym[1].startswith("/"):
                                        			copy(if_sym[1])
								tmp_sym = if_sym[0].split(' ')[-1]
								print tmp_sym
								print if_sym[1]
								relpath = os.path.relpath(if_sym[1],tmp_sym)[2:]
								print relpath
                                        			os.system('ln -sf '+relpath+' .'+j)
                                        			copyname.append(if_sym[1].split('/')[-1])
                                        			if_sym = ll(if_sym[1]).split(' -> ')

                                			else:
                                        			all_path = os.path.dirname(j)+'/'+if_sym[1]
                                        			copy(all_path)
                                        			copyname.append(if_sym[1])
                                       	 			if_sym = ll(all_path).split(' -> ')

				#		print name_j
				else:
					copyname.append(j)
					nameabspath = os.popen('locate '+j+'| grep -v home').read().strip()
					#print j
					print nameabspath
					copy(nameabspath)
					if_sym = ll(nameabspath).split(' -> ')
                                        while len(if_sym)>1:
                                        	if if_sym[1].startswith("/"):
                                                	copy(if_sym[1])
                                                        tmp_sym = if_sym[0].split(' ')[-1]
                                                        print tmp_sym
                                                        print if_sym[1]
                                                        relpath = os.path.relpath(if_sym[1],tmp_sym)[2:]
                                                        print relpath
                                                        os.system('ln -sf '+relpath+' .'+nameabspath)
                                                        copyname.append(if_sym[1].split('/')[-1])
                                                        if_sym = ll(if_sym[1]).split(' -> ')

                                                else:
                                                        all_path = os.path.dirname(nameabspath)+'/'+if_sym[1]
                                                        copy(all_path)
                                                        copyname.append(if_sym[1])
                                                        if_sym = ll(all_path).split(' -> ')
					
	else:
		continue	

print copyname

print allcopy
