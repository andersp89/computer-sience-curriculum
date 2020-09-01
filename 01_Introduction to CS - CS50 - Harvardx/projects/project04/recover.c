// Program to recover jpegs from raw data
#include <stdio.h>
#include <stdlib.h>

int main (int argc, char *argv[])
{
    
    // ensure proper usage
    if (argc != 2)
    {
        fprintf(stderr, "Usage: ./recover image\n");
        return 1;
    }
    
    // open memory file
    FILE *input = fopen(argv[1], "r");
    
    // tjeck if file is OK
    if (input == NULL)
    {
        fprintf(stderr, "Could not open %s.\n", argv[1]);
        return 1;
    }
    
    // img for jpegs
    FILE *img = NULL;
    
    // temporary storage for read stream
    unsigned char buffer[512];
    
    // Check whether we have found jpeg or not
    int foundJpeg = 0;
    
    // filename holder
    char filename[8];
    
    // counter for fie name
    int fileCount = 0;
    
    // read until end of file (because fread returns 1, when reading blocks OK)
    while (fread(&buffer, 512, 1, input) == 1) 
    {
        // tjeck whether jpeg
        if (buffer[0] == 0xff && buffer[1] == 0xd8 && buffer[2] == 0xff && (buffer[3] & 0xf0) == 0xe0)
        {
            // Close image, if opened already
            if (foundJpeg == 1)
            {
                // We have found picture, so close current
                fclose(img);
            } 
            else
            {
                // jpeg found
                foundJpeg = 1;
            }
            
            // create image file name
            sprintf(filename, "%03i.jpg", fileCount);
            img = fopen(filename, "w"); //måske skal denne flyttes op!?
            fileCount++;
        }   
        // write to img, until if above activates and another jpeg is found
        if (foundJpeg == 1)
        {
            fwrite(&buffer, 512, 1, img);
        }
    }
    
    // close files
    fclose(input);
    fclose(img);
    
    // success
    return 0;
}