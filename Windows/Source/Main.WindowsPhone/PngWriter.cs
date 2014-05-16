/*
 * This file contains the ToolStack.com C# PNG Writer and CRC libraries by Greg Ross.
 * Code that is unused by PocketCampus has been removed, some code has been slightly cleaned up,
 * and PngWriter has been slightly modified to make it non-static (to avoid concurrency issues).
 * 
 * Original PNG Writer library header:
 * ------------------------------------------------------------------------------------------------------------
 * ToolStack.com C# PNG Writer library by Greg Ross
 * 
 * Homepage: http://ToolStack.com/PNGWriter
 * 
 * This library is inspired by the examples hosted at the forums on WriteableBitmapEx
 * project at the codeplex site (http://writeablebitmapex.codeplex.com/discussions/274445), however
 * there's not really any of that code left, just some constants.
 * 
 * Compression is currently not supported but I am looking at adding it in.
 * 
 * This is public domain software, use and abuse as you see fit.
 * 
 * Version 1.0 - Released Feburary 22, 2012
 *         2.0 - Rewrote WriteDataChunksUncompressed() pretty much from the ground up to reduce the 3
 *               copies of the image in memory down to just a single copy.  This also reduced the 
 *               number of loops performed to manipulate the data from 2 to 1.
 *         2.1 - De-multiplied alpha channel, thanks to Jan for supplying the fix!
 * ------------------------------------------------------------------------------------------------------------
 * 
 * 
 * Original CRC library header:
 * ------------------------------------------------------------------------------------------------------------
 * ToolStack.com C# CRC library by Greg Ross
 * 
 * Homepage: http://ToolStack.com/CRCLib
 * 
 * This library is based upon the examples hosted at the PNG and zlib
 * home pages (www.libpng.org/pub/png/ & zlib.net), ported to C#.
 * 
 * This is public domain software, use and abuse as you see fit.
 * 
 * Version 1.0 - Released Feburary 22, 2012
 *         2.0 - Fixed adler() and addToAdler() to increment the index after the loop instead of before
 *             - Fixed addToAdler() to work with offsets
 *             - Fixed resetAdler() to reset the A & B values correctly
 * ------------------------------------------------------------------------------------------------------------
 */

using System;
using System.IO;
using System.Windows.Media.Imaging;

namespace PocketCampus.Main
{
    /// <summary>
    /// WriteableBitmap Extensions for PNG Writing
    /// </summary>
    internal sealed class PngWriter
    {
        private const int MaxBlockSize = 0xFFFF;

        private const double DefaultDensityX = 75;
        private const double DefaultDensityY = 75;

        private static double Gamma = 2.2f;

        /* Data in a PNG is in RGBA format but source from a writeablebitmap is in BGRA format
                BGRA=2,1,0,3

                Red = 0;
                Green = 1;
                Blue = 2;
                Alpha = 3;
        */
        private static int[] WBByteOrder = { 2, 1, 0, 3 };

        private readonly Stream _stream;
        private readonly WriteableBitmap _bitmap;

        public PngWriter( Stream stream, WriteableBitmap bitmap )
        {
            _stream = stream;
            _bitmap = bitmap;
        }

        /// <summary>
        /// Write and PNG file out to a file stream.  Currently compression is not supported.
        /// </summary>
        /// <param name="image">The WriteableBitmap to work on.</param>
        /// <param name="stream">The destination file stream.</param>
        /// <param name="compression">Level of compression to use (-1=auto, 0=none, 1-100 is percentage).</param>
        public void Write()
        {
            // Write the png header.
            _stream.Write( new byte[] 
                { 
                    0x89, 0x50, 0x4E, 0x47, 
                    0x0D, 0x0A, 0x1A, 0x0A 
                }, 0, 8 );

            // Set the PNG header values for this image.
            PngHeader header = new PngHeader
            {
                Width = _bitmap.PixelWidth,
                Height = _bitmap.PixelHeight,
                ColorType = 6,
                BitDepth = 8,
                FilterMethod = 0,
                CompressionMethod = 0,
                InterlaceMethod = 0
            };

            // Write out the header.
            WriteHeaderChunk( header );
            // Write out the rest of the mandatory fields to the PNG.
            WritePhysicsChunk();
            WriteGammaChunk();
            // Write PNG without any compression
            WriteDataChunksUncompressed();
            // Write out the end of the PNG.
            WriteEndChunk();
            // Flush the stream to make sure it's all written.
            _stream.Flush();
        }


        private void WritePhysicsChunk()
        {
            int dpmX = (int) Math.Round( DefaultDensityX * 39.3700787d );
            int dpmY = (int) Math.Round( DefaultDensityY * 39.3700787d );

            byte[] chunkData = new byte[9];

            WriteInteger( chunkData, 0, dpmX );
            WriteInteger( chunkData, 4, dpmY );

            chunkData[8] = 1;

            WriteChunk( PngChunkTypes.Physical, chunkData );
        }

        private void WriteGammaChunk()
        {
            int gammeValue = (int) ( Gamma * 100000f );

            byte[] fourByteData = new byte[4];

            byte[] size = BitConverter.GetBytes( gammeValue );
            fourByteData[0] = size[3]; fourByteData[1] = size[2]; fourByteData[2] = size[1]; fourByteData[3] = size[0];

            WriteChunk( PngChunkTypes.Gamma, fourByteData );
        }

        private void WriteDataChunksUncompressed()
        {
            // First setup some variables we're going to use later on so we can calculate how big of byte[] we need 
            // to store the entire PNG file in so we only keep a single copy of the data in memory.

            // Figure out how much image data we're going to have:
            //      H * W * (number of bytes in an ARGB value) + H to account for the filter byte in PNG files
            int dataLength = _bitmap.PixelWidth * _bitmap.PixelHeight * 4 + _bitmap.PixelHeight;

            // Variables for the number of PNG blocks and how big the last block is going to be.
            int blockCount;
            int lastBlockSize;

            // We could have an exactly even count of blocks (ie MaxBlockSize * x), but that seems unlikely.
            // If we don't, then add one for the remainder of the data and figure out how much data will be
            // left.
            if ( ( dataLength % MaxBlockSize ) == 0 )
            {
                blockCount = dataLength / MaxBlockSize;
                lastBlockSize = MaxBlockSize;
            }
            else
            {
                blockCount = ( dataLength / MaxBlockSize ) + 1;
                lastBlockSize = dataLength - ( MaxBlockSize * ( blockCount - 1 ) );
            }

            // The size of the PNG file will be:
            //      2 header bytes +
            //      ( blockCount - 1 ) * 
            //      (
            //          1 last block byte +
            //          2 block size bytes +
            //          2 block size one's complement bytes +
            //          maxBlockSize ) +
            //      (
            //          1 last block byte +
            //          2 block size bytes +
            //          2 block size one's complement bytes +
            //          lastBlockSize ) +
            //      4 Adler32 bytes +
            //      
            //  = 2 + ((blockCount-1)*(5+MaxBlockSize)) + (5+lastBlockSize) + 4
            //  = 11 + ((blockCount-1)*(5+MaxBlockSize)) + lastBlockSize
            //
            int pngLength;
            pngLength = 11 + ( ( blockCount - 1 ) * ( 5 + MaxBlockSize ) ) + lastBlockSize;

            // Make a buffer to store the PNG in.
            byte[] data = new byte[pngLength];

            // Write zlib headers.
            data[0] = 0x78;
            data[1] = 0xDA;

            //  zlib compression uses Adler32 CRCs instead of CRC32s, so setup on up to calculate.
            Adler32 crcCode = new Adler32();
            crcCode.Reset();

            // Setup some variables to use in the loop.
            var blockRemainder = 0;                         // How much of the current block we have left, 0 to start so we write the block header out on the first block.
            var currentBlock = 0;                           // The current block we're working on, start with 0 as we increment in the first pass thorugh.
            var dataPointer = 2;                            // A pointer to where we are in the data array, start at 2 as we 'wrote' two bytes a few lines ago.
            var pixelSource = 0;                            // The current pixel we're working on from the image.
            byte[] pixel = new byte[4];                     // Temporary storage to store the current pixel in as a byte array.

            // This is the main logic loop, we're going to be doing a lot of work so stick with me...
            //      The loop has three parts to it:
            //          1. looping through each row (y)
            //          2. looping through each pixel in the row (x)
            //          3. looping through each byte of the pixel (z)

            // Loop thorough each row in the image.
            for ( int y = 0; y < _bitmap.PixelHeight; y++ )
            {
                // This code appears twice, once here and once in the pixel byte loop (loop 3).
                // It checks to see if we're at the boundry for the PNG block and if so writes
                // out a new block header.  It get executed on the first time through to setup
                // the first block but is unlikly to get executed again as it would mean the 
                // block boundry is at a row boundry, which seems unlikly.
                if ( blockRemainder == 0 )
                {
                    // Setup a temporary byte array to store the block size in.
                    byte[] tempBytes = new byte[2];

                    // Increment the current block count.
                    currentBlock++;

                    // Figure out the current block size and if we're at the last block, write
                    // out and 1 to let the zlib decompressor know.  By default, use the MaxBlockSize.
                    int length = MaxBlockSize;

                    if ( currentBlock == blockCount )
                    {
                        length = lastBlockSize;
                        data[dataPointer] = 0x01;
                    }
                    else
                    {
                        data[dataPointer] = 0x00;
                    }

                    // Each and every time we write something to the data array, increment the pointer.
                    dataPointer++;

                    // Write the block length out.
                    tempBytes = BitConverter.GetBytes( length );
                    data[dataPointer + 0] = tempBytes[0];
                    data[dataPointer + 1] = tempBytes[1];
                    dataPointer += 2;

                    // Write one's compliment of length for error checking.
                    tempBytes = BitConverter.GetBytes( (ushort) ~length );
                    data[dataPointer + 0] = tempBytes[0];
                    data[dataPointer + 1] = tempBytes[1];
                    dataPointer += 2;

                    // Reset the remaining block size to the next block's length.
                    blockRemainder = length;
                }

                // Set the filter byte to 0, not really required as C# initalizes the byte array to 0 by default, but here for clarity.
                data[dataPointer] = 0;

                // Add the current byte to the running Adler32 value, note we ONLY add the filter byte and the pixel bytes to the
                // Adler32 CRC, all other header and block header bytes are execluded from the CRC.
                crcCode.Add( data, 1, (uint) dataPointer );

                // Increment the data pointer and decrement the remain block value.
                dataPointer++;
                blockRemainder--;

                // Loop thorough each pixel in the row, you have to do this as the source format and destination format may be different.
                for ( int x = 0; x < _bitmap.PixelWidth; x++ )
                {
                    // Data is in RGBA format but source may not be
                    pixel = BitConverter.GetBytes( _bitmap.Pixels[pixelSource] );

                    // Loop through the 4 bytes of the pixel and 'write' them to the data array.
                    for ( int z = 0; z < 4; z++ )
                    {
                        // This is the second appearance of this code code.
                        // It checks to see if we're at the boundry for the PNG block and if so writes
                        // out a new block header.  
                        if ( blockRemainder == 0 )
                        {
                            // Setup a temporary byte array to store the block size in.
                            byte[] tempBytes = new byte[2];

                            // Increment the current block count.
                            currentBlock++;

                            // Figure out the current block size and if we're at the last block, write
                            // out and 1 to let the zlib decompressor know.  By default, use the MaxBlockSize.
                            int length = MaxBlockSize;

                            if ( currentBlock == blockCount )
                            {
                                length = lastBlockSize;
                                data[dataPointer] = 0x01;
                            }
                            else
                            {
                                data[dataPointer] = 0x00;
                            }

                            // Each and every time we write something to the data array, increment the pointer.
                            dataPointer++;

                            // Write the block length out.
                            tempBytes = BitConverter.GetBytes( length );
                            data[dataPointer + 0] = tempBytes[0];
                            data[dataPointer + 1] = tempBytes[1];
                            dataPointer += 2;

                            // Write one's compliment of length for error checking.
                            tempBytes = BitConverter.GetBytes( (ushort) ~length );
                            data[dataPointer + 0] = tempBytes[0];
                            data[dataPointer + 1] = tempBytes[1];
                            dataPointer += 2;

                            // Reset the remaining block size to the next block's length.
                            blockRemainder = length;
                        }

                        // Store the pixel's byte in to the data array. We use the WBByteOrder array to ensure 
                        // we have the write order of bytes to store in the PNG file.
                        if ( z != 3 && pixel[WBByteOrder[3]] != 0 && pixel[WBByteOrder[3]] != 255 )
                        {
                            // Calculate unmultiplied pixel value from premultiplied value (Windows Phone always uses premultiplied ARGB32)  
                            data[dataPointer] = (byte) ( ( 255 * pixel[WBByteOrder[z]] ) / pixel[WBByteOrder[3]] );
                        }
                        else
                        {
                            // Alpha channel or no need to unpremultiply  
                            data[dataPointer] = pixel[WBByteOrder[z]];
                        }

                        // Add the current byte to the running Adler32 value, note we ONLY add the filter byte and the pixel bytes to the
                        // Adler32 CRC, all other header and block header bytes are execluded from the CRC.
                        crcCode.Add( data, 1, (uint) dataPointer );

                        // Increment the data pointer and decrement the remain block value.
                        dataPointer++;
                        blockRemainder--;
                    }

                    // Increment where we start writting the next pixel and where we get the next pixel from.
                    pixelSource++;
                }
            }

            // Whew, wipe that brow, we're done all the complex bits now!

            // Write the Adler32 CRC out, but reverse the order of the bytes to match the zlib spec.
            pixel = BitConverter.GetBytes( crcCode.CurrentValue );
            data[dataPointer + 0] = pixel[3];
            data[dataPointer + 1] = pixel[2];
            data[dataPointer + 2] = pixel[1];
            data[dataPointer + 3] = pixel[0];

            // Yes, yes, I know I said "Each and every time we write something to the data array, increment the pointer."
            // but we're done with it now so I'm not going to bother ;)

            // Write the entire PNG data chunk out to the file stream.
            WriteChunk( PngChunkTypes.Data, data, 0, pngLength );
        }

        private void WriteEndChunk()
        {
            WriteChunk( PngChunkTypes.End, null );
        }

        private void WriteHeaderChunk( PngHeader header )
        {
            byte[] chunkData = new byte[13];

            WriteInteger( chunkData, 0, header.Width );
            WriteInteger( chunkData, 4, header.Height );

            chunkData[8] = header.BitDepth;
            chunkData[9] = header.ColorType;
            chunkData[10] = header.CompressionMethod;
            chunkData[11] = header.FilterMethod;
            chunkData[12] = header.InterlaceMethod;

            WriteChunk( PngChunkTypes.Header, chunkData );
        }

        private void WriteChunk( string type, byte[] data )
        {
            WriteChunk( type, data, 0, data != null ? data.Length : 0 );
        }

        private void WriteChunk( string type, byte[] data, int offset, int length )
        {
            // Write out the length to the PNG.
            WriteInteger( _stream, length );

            // Write the chunck type out to the PNG.
            byte[] typeArray = new byte[4];
            typeArray[0] = (byte) type[0];
            typeArray[1] = (byte) type[1];
            typeArray[2] = (byte) type[2];
            typeArray[3] = (byte) type[3];

            _stream.Write( typeArray, 0, 4 );

            // If we have some data to write out (some chunk types don't), the do so.
            if ( data != null )
            {
                _stream.Write( data, offset, length );
            }

            // All chunk types need to have a CRC32 value at their end to make sure they haven't been currupted.
            CRC32 crcCode = new CRC32();
            crcCode.Add( typeArray, 4 );

            if ( data != null )
            {
                crcCode.Add( data, length, (uint) offset );
            }

            WriteInteger( _stream, crcCode.CurrentValue );
        }

        private static void WriteInteger( byte[] data, int offset, int value )
        {
            byte[] buffer = BitConverter.GetBytes( value );

            Array.Reverse( buffer );
            Array.Copy( buffer, 0, data, offset, 4 );
        }

        private static void WriteInteger( Stream stream, int value )
        {
            byte[] buffer = BitConverter.GetBytes( value );

            Array.Reverse( buffer );

            stream.Write( buffer, 0, 4 );
        }

        private static void WriteInteger( Stream stream, uint value )
        {
            byte[] buffer = BitConverter.GetBytes( value );

            Array.Reverse( buffer );

            stream.Write( buffer, 0, 4 );
        }


        private static class PngChunkTypes
        {
            /// <summary>
            /// The first chunk in a png file. Can only exists once. Contains 
            /// common information like the width and the height of the image or
            /// the used compression method.
            /// </summary>
            public const string Header = "IHDR";
            /// <summary>
            /// The PLTE chunk contains from 1 to 256 palette entries, each a three byte
            /// series in the RGB format.
            /// </summary>
            public const string Palette = "PLTE";
            /// <summary>
            /// The IDAT chunk contains the actual image data. The image can contains more
            /// than one chunk of this type. All chunks together are the whole image.
            /// </summary>
            public const string Data = "IDAT";
            /// <summary>
            /// This chunk must appear last. It marks the end of the PNG datastream. 
            /// The chunk's data field is empty. 
            /// </summary>
            public const string End = "IEND";
            /// <summary>
            /// This chunk specifies that the image uses simple transparency: 
            /// either alpha values associated with palette entries (for indexed-color images) 
            /// or a single transparent color (for grayscale and truecolor images). 
            /// </summary>
            public const string PaletteAlpha = "tRNS";
            /// <summary>
            /// Textual information that the encoder wishes to record with the image can be stored in 
            /// tEXt chunks. Each tEXt chunk contains a keyword and a text string.
            /// </summary>
            public const string Text = "tEXt";
            /// <summary>
            /// This chunk specifies the relationship between the image samples and the desired 
            /// display output intensity.
            /// </summary>
            public const string Gamma = "gAMA";
            /// <summary>
            /// The pHYs chunk specifies the intended pixel size or aspect ratio for display of the image. 
            /// </summary>
            public const string Physical = "pHYs";
        }

        private sealed class PngHeader
        {
            /// <summary>
            /// The dimension in x-direction of the image in pixels.
            /// </summary>
            public int Width;
            /// <summary>
            /// The dimension in y-direction of the image in pixels.
            /// </summary>
            public int Height;
            /// <summary>
            /// Bit depth is a single-byte integer giving the number of bits per sample 
            /// or per palette index (not per pixel). Valid values are 1, 2, 4, 8, and 16, 
            /// although not all values are allowed for all color types. 
            /// </summary>
            public byte BitDepth;
            /// <summary>
            /// Color type is a integer that describes the interpretation of the 
            /// image data. Color type codes represent sums of the following values: 
            /// 1 (palette used), 2 (color used), and 4 (alpha channel used).
            /// </summary>
            public byte ColorType;
            /// <summary>
            /// Indicates the method  used to compress the image data. At present, 
            /// only compression method 0 (deflate/inflate compression with a sliding 
            /// window of at most 32768 bytes) is defined.
            /// </summary>
            public byte CompressionMethod;
            /// <summary>
            /// Indicates the preprocessing method applied to the image 
            /// data before compression. At present, only filter method 0 
            /// (adaptive filtering with five basic filter types) is defined.
            /// </summary>
            public byte FilterMethod;
            /// <summary>
            /// Indicates the transmission order of the image data. 
            /// Two values are currently defined: 0 (no interlace) or 1 (Adam7 interlace).
            /// </summary>
            public byte InterlaceMethod;
        }

        /// <summary>
        /// Computes the CRC32 value for a given data set
        /// </summary>
        private sealed class CRC32
        {
            private static readonly uint[] CrcTable = {
                    0, 1996959894, 3993919788, 2567524794, 124634137,
                    1886057615, 3915621685, 2657392035, 249268274, 2044508324,
                    3772115230, 2547177864, 162941995, 2125561021, 3887607047,
                    2428444049, 498536548, 1789927666, 4089016648, 2227061214,
                    450548861, 1843258603, 4107580753, 2211677639, 325883990,
                    1684777152, 4251122042, 2321926636, 335633487, 1661365465,
                    4195302755, 2366115317, 997073096, 1281953886, 3579855332,
                    2724688242, 1006888145, 1258607687, 3524101629, 2768942443,
                    901097722, 1119000684, 3686517206, 2898065728, 853044451,
                    1172266101, 3705015759, 2882616665, 651767980, 1373503546,
                    3369554304, 3218104598, 565507253, 1454621731, 3485111705,
                    3099436303, 671266974, 1594198024, 3322730930, 2970347812,
                    795835527, 1483230225, 3244367275, 3060149565, 1994146192,
                    31158534, 2563907772, 4023717930, 1907459465, 112637215,
                    2680153253, 3904427059, 2013776290, 251722036, 2517215374,
                    3775830040, 2137656763, 141376813, 2439277719, 3865271297,
                    1802195444, 476864866, 2238001368, 4066508878, 1812370925,
                    453092731, 2181625025, 4111451223, 1706088902, 314042704,
                    2344532202, 4240017532, 1658658271, 366619977, 2362670323,
                    4224994405, 1303535960, 984961486, 2747007092, 3569037538,
                    1256170817, 1037604311, 2765210733, 3554079995, 1131014506,
                    879679996, 2909243462, 3663771856, 1141124467, 855842277,
                    2852801631, 3708648649, 1342533948, 654459306, 3188396048,
                    3373015174, 1466479909, 544179635, 3110523913, 3462522015,
                    1591671054, 702138776, 2966460450, 3352799412, 1504918807,
                    783551873, 3082640443, 3233442989, 3988292384, 2596254646,
                    62317068, 1957810842, 3939845945, 2647816111, 81470997,
                    1943803523, 3814918930, 2489596804, 225274430, 2053790376,
                    3826175755, 2466906013, 167816743, 2097651377, 4027552580,
                    2265490386, 503444072, 1762050814, 4150417245, 2154129355,
                    426522225, 1852507879, 4275313526, 2312317920, 282753626,
                    1742555852, 4189708143, 2394877945, 397917763, 1622183637,
                    3604390888, 2714866558, 953729732, 1340076626, 3518719985,
                    2797360999, 1068828381, 1219638859, 3624741850, 2936675148,
                    906185462, 1090812512, 3747672003, 2825379669, 829329135,
                    1181335161, 3412177804, 3160834842, 628085408, 1382605366,
                    3423369109, 3138078467, 570562233, 1426400815, 3317316542,
                    2998733608, 733239954, 1555261956, 3268935591, 3050360625,
                    752459403, 1541320221, 2607071920, 3965973030, 1969922972,
                    40735498, 2617837225, 3943577151, 1913087877, 83908371,
                    2512341634, 3803740692, 2075208622, 213261112, 2463272603,
                    3855990285, 2094854071, 198958881, 2262029012, 4057260610,
                    1759359992, 534414190, 2176718541, 4139329115, 1873836001,
                    414664567, 2282248934, 4279200368, 1711684554, 285281116,
                    2405801727, 4167216745, 1634467795, 376229701, 2685067896,
                    3608007406, 1308918612, 956543938, 2808555105, 3495958263,
                    1231636301, 1047427035, 2932959818, 3654703836, 1088359270,
                    936918000, 2847714899, 3736837829, 1202900863, 817233897,
                    3183342108, 3401237130, 1404277552, 615818150, 3134207493,
                    3453421203, 1423857449, 601450431, 3009837614, 3294710456,
                    1567103746, 711928724, 3020668471, 3272380065, 1510334235,
                    755167117
                };

            private uint crcValue = 0xffffffffU;

            /// <summary>
            /// Returns the current running CRC32 result for a PNG file.
            /// </summary>
            /// <returns>An unsigned 32bit integer representing the current CRC32.</returns>
            public uint CurrentValue
            {
                get { return crcValue ^ 0xffffffffU; }
            }

            /// <summary>
            /// Adds to the current running CRC32 the bytes from buf[].
            /// </summary>
            /// <param name="buf">A byte[] to process.</param>
            /// <param name="len">The length of the byte[].</param>
            public void Add( byte[] buf, int len )
            {
                Add( buf, len, 0 );
            }

            /// <summary>
            /// Adds to the current running CRC32 the bytes from buf[].
            /// </summary>
            /// <param name="buf">A byte[] to process.</param>
            /// <param name="len">The length of the byte[].</param>
            /// <param name="offset">The offset to start processing byte[] at.</param>
            public void Add( byte[] buf, int len, uint offset )
            {
                uint c = crcValue;

                for ( uint n = offset; n < offset + len; n++ )
                {
                    c = CrcTable[( c ^ buf[n] ) & 0xffU] ^ ( c >> 8 );
                }

                crcValue = c;
            }
        }

        /// <summary>
        /// Computes the Adler32 CRC value for a given data set
        /// </summary>
        private sealed class Adler32
        {
            private const int AdlerModulus = 65521;
            private uint AdlerA = 1;
            private uint AdlerB = 0;

            /// <summary>
            /// Returns the current running Adler32 result.
            /// </summary>
            /// <returns>An unsigned 32bit integer representing the current Adler32.</returns>
            public uint CurrentValue
            {
                get { return ( AdlerB << 16 ) | AdlerA; }
            }

            /// <summary>
            /// Adds to the current running Adler32 the bytes from buf[].
            /// </summary>
            /// <param name="data">A byte[] to process.</param>
            /// <param name="len">The length of the byte[].</param>
            /// <param name="offset">The offset to start processing byte[] at.</param>
            public void Add( byte[] data, int len, uint offset )
            {
                for ( uint index = offset; index < offset + len; index++ )
                {
                    AdlerA = ( AdlerA + data[index] ) % AdlerModulus;
                    AdlerB = ( AdlerB + AdlerA ) % AdlerModulus;
                }
            }

            /// <summary>
            /// Resets the running Adler32.
            /// </summary>
            public void Reset()
            {
                AdlerA = 1;
                AdlerB = 0;
            }
        }
    }
}