#!/usr/bin/python3
import sys
import time
import signal

QUEUED = 1
DELAY = 3

filepath1 = "contest.csv"

if len(sys.argv) > 1:
    filepath1 = sys.argv[1]

f1 = open(filepath1, 'r')

filepath2 = "submits.csv"

if len(sys.argv) > 2:
    filepath2 = sys.argv[2]

lines = f1.readlines()

def que(s):
    r = s.split(',')
    # print(r)
    r[4] = "QUE\n"
    return  ','.join(r)

def signal_handler(sig, frame):
    open(filepath2, 'w').close()
    sys.exit(0)
    
signal.signal(signal.SIGINT, signal_handler)

for i in range(QUEUED, len(lines) + QUEUED+1):
    with open(filepath2, 'w') as f2:
        for j in range(0, i - QUEUED):
            print(lines[j], file=f2, end='')
        for j in range(i - QUEUED, min(i, len(lines))):
            print(que(lines[j]), file=f2, end='')
    time.sleep(DELAY)
