import os

def rename_files():
    #(1) Get file names from a folder
    file_list = os.listdir("/Users/anderspedersen/Desktop/Udacity/Project 1/Secret/prank")
    print(file_list)
    os.chdir("/Users/anderspedersen/Desktop/Udacity/Project 1/Secret/prank")

    #(2) for eah file, rename filename
    for file_name in file_list:
        os.rename(file_name, file_name.translate(None, "0123456789"))

rename_files()
