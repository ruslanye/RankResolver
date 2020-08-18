#!/usr/bin/python3
import sys
import time
import signal


filepath1 = "contest.csv"

if len(sys.argv) > 1:
    filepath1 = sys.argv[1]

f1 = open(filepath1, 'r')

filepath2 = "submits.csv"

if len(sys.argv) > 2:
    filepath2 = sys.argv[2]

lines = f1.readlines()

def signal_handler(sig, frame):
    open(filepath2, 'w').close()
    sys.exit(0)
    
signal.signal(signal.SIGINT, signal_handler)

for i in range(len(lines)):
    with open(filepath2, 'w') as f2:
        for j in range(i):
            print(lines[j], file=f2, end='')
        row = lines[i].split(',')
        row[4] = "QUE"
        print(','.join(row), file=f2, end='')
    time.sleep(0.5)
with open(filepath2, 'w') as f2:
    for line in lines:
        print(line, file=f2, end='')
        
