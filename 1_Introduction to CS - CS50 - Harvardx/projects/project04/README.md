# Questions

## What's `stdint.h`?

stdint.h is a library giving the possibility to define integer types. This is beneficial, to ensure that the program work universally in any system environment.

## What's the point of using `uint8_t`, `uint32_t`, `int32_t`, and `uint16_t` in a program?

To exactly specify the bits for an int in any system environment, e.g. uint8_t is a 8-bit int. The reason being, that you can not always be sure, that an int is 32 bits in all environments.

## How many bytes is a `BYTE`, a `DWORD`, a `LONG`, and a `WORD`, respectively?

Please find below the bytes for each datatype:
BYTE = unsigned 8 bit value
WORD = unsigned 16 bit value
DWORD = unsigned 32 bit value
LONG = unsigned 64 bit value

## What (in ASCII, decimal, or hexadecimal) must the first two bytes of any BMP file be? Leading bytes used to identify file formats (with high probability) are generally called "magic numbers."

Th first two bytes of any BMP file is: 255, 216

## What's the difference between `bfSize` and `biSize`?

biSize is the size of the BITMAPINFOHEADER. Whereas, bfSize inlcudes the the whole BMP as such.

## What does it mean if `biHeight` is negative?

If biHeight is positive, the bitmap is a bottom-up DIB and its origin is the lower-left corner. If biHeight is negative, the bitmap is a top-down DIB and its origin is the upper-left corner. 

## What field in `BITMAPINFOHEADER` specifies the BMP's color depth (i.e., bits per pixel)?

biBitCount

Specifies the number of bits per pixel (bpp). For uncompressed formats, this value is the average number of bits per pixel. For compressed formats, this value is the implied bit depth of the uncompressed image, after the image has been decoded.

## Why might `fopen` return `NULL` in lines 24 and 32 of `copy.c`?

If there is no available memory in computer, it will return 0.

## Why is the third argument to `fread` always `1` in our code?

Because, we want to read one element only. 

## What value does line 65 of `copy.c` assign to `padding` if `bi.biWidth` is `3`?

3, because:
int padding = (4 - (3 * 3)) % 4) % 4;

## What does `fseek` do?

The C library function int fseek(FILE *stream, long int offset, int whence) sets the file position of the stream to the given offset.

## What is `SEEK_CUR`?

SEEK_CUR: Current position of the file pointer

## Whodunit?

Professor Plum with the candlestick in the library