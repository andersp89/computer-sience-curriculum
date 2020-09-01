import webbrowser
import time

count = 1

print("This program started on: " + time.ctime())
while(count <= 3):
    time.sleep(5)
    webbrowser.open("https://www.youtube.com/watch?v=ArNR_QX2SG0")
    count = count + 1
