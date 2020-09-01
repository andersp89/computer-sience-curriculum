// Resizes a bmp-file n-times
#include <stdio.h>
#include <stdlib.h>
#include "bmp.h"

int main(int argc, char *argv[])
{
    // ensure proper usage
    if (argc != 4)
    {
        fprintf(stderr, "Usage: ./resize n infile outfile\n");
        return 1;
    }

    // remember filenames
    char *ntimes = argv[1]; //for n-resizing. REMEMBER TO CONVERT TO INT AND CHECK IT
    char *infile = argv[2]; //CHANGED TO POSITION 2
    char *outfile = argv[3]; //CHANGED TO POSITION 3

    // convert n to int
    int ntimesint = atoi(ntimes);
    // atoicheck returns 0 if conversion to int failed
    if (ntimesint == 0 || ntimesint < 0 || ntimesint > 100)
    {
        fprintf(stderr, "The first argument provided must be a positive integer!\n");
        return 1;
    }
    
    // open input file
    FILE *inptr = fopen(infile, "r");
    if (inptr == NULL)
    {
        fprintf(stderr, "Could not open %s.\n", infile);
        return 1;
    }

    // open output file
    FILE *outptr = fopen(outfile, "w");
    if (outptr == NULL)
    {
        fclose(inptr);
        fprintf(stderr, "Could not create %s.\n", outfile);
        return 1;
    }

    // read infile's BITMAPFILEHEADER
    BITMAPFILEHEADER bf;
    fread(&bf, sizeof(BITMAPFILEHEADER), 1, inptr);

    // read infile's BITMAPINFOHEADER
    BITMAPINFOHEADER bi;
    fread(&bi, sizeof(BITMAPINFOHEADER), 1, inptr);

    // ensure infile is (likely) a 24-bit uncompressed BMP 4.0
    if (bf.bfType != 0x4d42 || bf.bfOffBits != 54 || bi.biSize != 40 ||
        bi.biBitCount != 24 || bi.biCompression != 0)
    {
        fclose(outptr);
        fclose(inptr);
        fprintf(stderr, "Unsupported file format.\n");
        return 1;
    }
    
    // save original width and height of infile
    int orgWidth = bi.biWidth;
    int orgHeight = bi.biHeight;

    // determine padding for original scanlines
    int padding = (4 - (orgWidth * sizeof(RGBTRIPLE)) % 4) % 4; 

    // Update infile's header information
    bi.biWidth *= ntimesint;
    bi.biHeight *= ntimesint;
    
    // Total size of image in bytes (including padding)
    int newPadding = (4 - (bi.biWidth * sizeof(RGBTRIPLE)) % 4) % 4;
    bi.biSizeImage = ((sizeof(RGBTRIPLE) * bi.biWidth) + newPadding) * abs(bi.biHeight);
    
    // Total size of file in bytes
    bf.bfSize = bi.biSizeImage + sizeof(BITMAPFILEHEADER) + sizeof(BITMAPINFOHEADER);
    
    // write outfile's BITMAPFILEHEADER
    fwrite(&bf, sizeof(BITMAPFILEHEADER), 1, outptr);
    
    // write outfile's BITMAPINFOHEADER
    fwrite(&bi, sizeof(BITMAPINFOHEADER), 1, outptr);

    // iterate over infile's scanlines
    for (int i = 0, biHeight = abs(orgHeight); i < biHeight; i++)
    {
        // iterate n-times
        for (int x = 0; x < ntimesint-1; x++)
        {
            // iterate over pixels in scanline
            for (int j = 0; j < orgWidth; j++)
            {
                // temporary storage
                RGBTRIPLE triple;
    
                // read RGB triple from infile
                fread(&triple, sizeof(RGBTRIPLE), 1, inptr);
            
                // write RGB triple to outfile n-times
                for (int k = 0; k < ntimesint; k++)
                {
                    fwrite(&triple, sizeof(RGBTRIPLE), 1, outptr);    
                }
            }
            
            // add the new padding after n-times resizing
            for (int l = 0; l < newPadding; l++)
            {
                fputc(0x00, outptr);
            }
            
            // send cursor back in infile, to repeat
            fseek(inptr, -(sizeof(RGBTRIPLE)*orgWidth), SEEK_CUR);
        }
        
        // write pixels the last time, w/o sending cursor back
        for (int j = 0; j < orgWidth; j++)
        {
            // temporary storage
            RGBTRIPLE triple;
            // read RGB triple from infile
            fread(&triple, sizeof(RGBTRIPLE), 1, inptr);
            
            // write RGB triple to outfile n-times
            for (int k = 0; k < ntimesint; k++)
            {
                fwrite(&triple, sizeof(RGBTRIPLE), 1, outptr);    
            }
        }
            
        // add padding
        for (int l = 0; l < newPadding; l++)
        {
            fputc(0x00, outptr);
        }
        
        // skip over padding, if any
        fseek(inptr, padding, SEEK_CUR);
    }

    // close infile
    fclose(inptr);

    // close outfile
    fclose(outptr);

    // success
    return 0;
}
