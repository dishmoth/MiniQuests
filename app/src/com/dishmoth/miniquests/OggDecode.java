/* 
 *  OggDecode.java
 *  Based on "DecodeExample.java" from JOrbis (http://www.jcraft.com/jorbis/)
 */

package com.dishmoth.miniquests;

import java.io.*;
import javax.sound.sampled.*;

// package JOrbis, by JCraft,Inc 
//import com.jcraft.jogg.*;
//import com.jcraft.jorbis.*;  

// read an Ogg Vorbis input stream and return a Clip (Java Sound) object
public class OggDecode {

  // running commentary (off by default)
  private static boolean kVerbose = false;

  // set whether the decode displays comments when running
  public static void setVerbose(boolean v) { kVerbose = v; }
  
  // decoded data from an Ogg Vorbis stream
  private static class DecodedData {
    public int rate;
    public int channels;
    public byte[] bytes;
  } // OggDecode.DecodedData

  // read the input stream as Ogg Vorbis data, and return a Clip object
  public static Clip toClip(InputStream input) throws IOException {
  
    DecodedData data = decodeOggStream(input);
    return newClip(data.rate, data.channels, data.bytes);
    
  } // toClip()
    
  // read the input stream as Ogg Vorbis data, and return a SoundEffect object
  public static SoundEffect toSoundEffect(InputStream input) throws IOException {
  
    DecodedData data = decodeOggStream(input);
    return newSoundEffect(data.rate, data.channels, data.bytes);
    
  } // toSoundEffect()

  // create a Clip from an array of bytes in 16-bit signed PCM format
  private static Clip newClip(int    sampleRate, 
                              int    numChannels,
                              byte[] pcmBytes) 
                             throws IOException {

    int     bitsPerSample = 16;
    boolean isSigned      = true,
            isBigEndian   = false;
    AudioFormat format = new AudioFormat((float)sampleRate, bitsPerSample,
                                         numChannels, isSigned, isBigEndian);

    Clip clip = null;
    try {
      clip = AudioSystem.getClip();
      clip.open(format, pcmBytes, 0, pcmBytes.length);
    } catch ( Exception ex ) {
      throw new IOException("failure during creation of Clip (" + ex + ")");
    }
  
    if ( kVerbose ) {
      System.out.println("Constructing clip, " + pcmBytes.length
                         + " bytes of PCM data");
    }

    assert( clip != null );
    return clip;
    
  } // newClip()
  
  // create a Sound Effect from an array of bytes in 16-bit signed PCM format
  private static SoundEffect newSoundEffect(int    sampleRate, 
                                            int    numChannels,
                                            byte[] pcmBytes) 
                                           throws IOException {

    int     bitsPerSample = 16;
    boolean isSigned      = true,
            isBigEndian   = false;
    AudioFormat format = new AudioFormat((float)sampleRate, bitsPerSample,
                                         numChannels, isSigned, isBigEndian);

    SoundEffect se = new SoundEffect(format, pcmBytes);
  
    if ( kVerbose ) {
      System.out.println("Constructing sound effect, " + pcmBytes.length
                         + " bytes of PCM data");
    }

    return se;
    
  } // newSoundEffect()
  
  // read the input stream as Ogg Vorbis data
  private static DecodedData decodeOggStream(InputStream input) throws IOException {
    
    throw new IOException("JOrbis library needed to decode Ogg Vorbis sound files");
    
  } // decodeOggStream()
  
/*
  // read the input stream as Ogg Vorbis data
  private static DecodedData decodeOggStream(InputStream input) throws IOException {
    
    // struct that stores all the static vorbis bitstream settings
    Info info = new Info();
    
    // buffer for holding PCM data as it is generated
    ByteArrayOutputStream outStream = new ByteArrayOutputStream(1024);

    // convert the OGG input into a PCM byte array
    decodeStream(input, outStream, info);

    DecodedData data = new DecodedData();
    data.rate = info.rate;
    data.channels = info.channels;
    data.bytes = outStream.toByteArray();
    return data;

  } // decodeOggStream()
  
  // size of a chunk of data
  private static final int kNumBytes = 4096;
  
  // read the input stream as Ogg Vorbis data
  private static void decodeStream(InputStream           input,
                                   ByteArrayOutputStream outStream,
                                   Info                  info) 
                                  throws IOException {
  
    // sync and verify incoming physical bitstream
    SyncState syncState = new SyncState();
    syncState.init();
    
    // take physical pages, weld into a logical stream of packets
    StreamState streamState = new StreamState();
    
    // one Ogg bitstream page, Vorbis packets are inside
    Page page = new Page();
    
    // one raw packet of data for decode
    Packet packet = new Packet();
    
    // struct that stores all the bitstream user comments
    Comment comment = new Comment();
    
    // central working state for the packet->PCM decoder
    DspState dspState = new DspState();
    
    // local working space for packet->PCM decode
    com.jcraft.jorbis.Block block = new com.jcraft.jorbis.Block(dspState);

    if ( kVerbose ) System.out.println("Decoding Ogg Vorbis bitstream");
    
    // decode loop (keep repeating in case the bitstream is chained)
    while ( true ) {
    
      // read the first page and check whether it contains data
      boolean okay = readFirstPage(syncState, page, input);
      if ( !okay ) break; // EXIT DECODE LOOP!
      
      // initialize decoder
      streamState.init(page.serialno());
      info.init();
      comment.init();
      
      // verify bitstream and get the initial header
      readFirstHeader(streamState, page, packet, info, comment);
      
      // get the comment and codebook headers
      readSecondaryHeaders(syncState, streamState, page, 
                           packet, info, comment, input);
      if ( kVerbose ) displayComments(comment, info);
      
      // initialize the Vorbis packet->PCM decoder
      dspState.synthesis_init(info);
      block.init(dspState);
      
      // just a straight decode loop until end of stream
      while ( true ) {
        int result = syncState.pageout(page);
        if ( result == 0 ) {
          int numBytes = readAndSubmitBytes(syncState, input, kNumBytes);
          if ( numBytes == 0 ) break;
        } else if ( result == -1 ) { 
          if ( kVerbose ) System.out.println("Corrupt or missing data in "
                                             + "bitstream; continuing...");
        } else {
          streamState.pagein(page); // can safely ignore errors at this point
          processPcm(streamState, dspState, packet, 
                     block, info.channels, outStream);
          if ( page.eos() != 0 ) break;
        }
      }
      
      // clean up (note: page and packet are never freed directly)
      streamState.clear();
      block.clear();
      dspState.clear();
      info.clear();  // must be called last

    } // while(true) - decode loop
    
    syncState.clear();
        
  } // decodeStream()
    
  // submit a block to the Ogg layer, return the number of bytes submitted
  private static int readAndSubmitBytes(SyncState   syncState, 
                                        InputStream inStream, 
                                        int         numBytes) 
                                       throws IOException {
  
    int index = syncState.buffer(numBytes);

    try {
      numBytes = inStream.read(syncState.data, index, numBytes);
      if ( numBytes < 0 ) numBytes = 0;
    } catch(Exception ex) {
      throw new IOException("error reading input stream (" + ex + ")");
    }
    syncState.wrote(numBytes);

    return numBytes;
    
  } // readAndSubmitBytes()
  
  // grab some data and extract the first page (which is guaranteed to
  // be small and only contain the Vorbis stream initial header)
  // return false if the stream has run dry
  private static boolean readFirstPage(SyncState   syncState,
                                       Page        page,
                                       InputStream inStream) 
                                      throws IOException  {

      int numBytes = readAndSubmitBytes(syncState, inStream, kNumBytes);
      
      if ( syncState.pageout(page) != 1 ) {
        if ( numBytes < kNumBytes ) {
          return false;
        } else {
          throw new IOException("input does not appear to be "
                                + "an Ogg bitstream");
        }
      }

      return true;
      
  } // readFirstPage()

  // verify that the bitstream is Vorbis, and read the initial header
  private static void readFirstHeader(StreamState streamState, 
                                      Page        page, 
                                      Packet      packet, 
                                      Info        info, 
                                      Comment     comment) 
                                     throws IOException {
                                      
      if ( streamState.pagein(page) < 0 ) { 
        throw new IOException("error reading first page of Ogg bitstream "
                              + "data (version mismatch perhaps)");
      }
      
      if ( streamState.packetout(packet) != 1 ) { 
        throw new IOException("error reading initial header packet "
                              + "(not Vorbis data)");
      }
      
      if ( info.synthesis_headerin(comment,packet) < 0 ) { 
        throw new IOException("error reading initial header packet "
                              + "(Ogg bitstream does not contain Vorbis "
                              + "audio data?)");
      }

  } // readFirstHeader()
      
  // read the stream's next two packets (comment and codebook headers)
  // (the headers may be large and may span multiple pages)
  private static void readSecondaryHeaders(SyncState   syncState, 
                                           StreamState streamState,
                                           Page        page,
                                           Packet      packet,
                                           Info        info,
                                           Comment     comment,
                                           InputStream inStream)
                                          throws IOException {
  
    int numPackets = 0;
    while ( numPackets < 2 ) {

      while ( numPackets < 2 ) {
        int result = syncState.pageout(page);
        if ( result == 0 ) break; // need more data
        if ( result == -1 ) continue; // corrupt data
          
        streamState.pagein(page);
        while ( numPackets < 2 ) {
          result = streamState.packetout(packet);
          if ( result == 0 ) break;
          if ( result == -1 ) {
            throw new IOException("corrupt secondary Vorbis header");
          }
          
          info.synthesis_headerin(comment, packet);
          numPackets += 1;
        }
      }

      // read more data
      int numBytes = readAndSubmitBytes(syncState, inStream, kNumBytes);
      if ( numBytes == 0 && numPackets < 2 ) {
        throw new IOException("end of file before finding all Vorbis headers");
      }
      
    }
  
  } // readSecondaryHeaders()
  
  // show the user comments plus a few lines about the bitstream
  private static void displayComments(Comment comments, Info info) {

    byte[][] lines = comments.user_comments;
    for( byte[] line : lines ) {
      if ( line == null) break;
      System.out.println(new String(line, 0, line.length-1));
    } 

    System.out.println("Bitstream is " + info.channels
                       + " channel, " + info.rate + "Hz");
    System.out.println("Encoded by: " 
                       + new String(comments.vendor, 0, 
                                    comments.vendor.length-1));
    
  } // displayComments()
    
  // append decoded PCM data to the output stream until we run out of data
  private static void processPcm(StreamState             streamState, 
                                 DspState                dspState, 
                                 Packet                  packet, 
                                 com.jcraft.jorbis.Block block, 
                                 int                     numChannels,
                                 OutputStream            outStream) 
                                throws IOException {
  
    float[][][] pcm = new float[1][][];
    int[] indices = new int[numChannels];

    while (true) {
        
      int result = streamState.packetout(packet);
      if ( result == 0 ) return; // need more data
      if ( result == -1 ) continue; // missing or corrupt data

      if ( block.synthesis(packet) == 0 ) dspState.synthesis_blockin(block);
      
      int numSamples;
      while( (numSamples=dspState.synthesis_pcmout(pcm, indices)) > 0 ) {
        writeData(numChannels, numSamples, indices, pcm[0], outStream);
        dspState.synthesis_read(numSamples);
      }

    }
  
  } // processPcm()

  // write the array of PCM data as bytes to the output stream
  private static void writeData(int          numChannels, 
                                int          numSamples, 
                                int[]        indices, 
                                float[][]    pcm, 
                                OutputStream outStream) 
                               throws IOException {

    byte[] outputBuffer = new byte[2*numChannels*numSamples];    
    
    for ( int i = 0 ; i < numChannels ; i++ ) {
      int ptr = i*2;
      int mono = indices[i];
      for ( int j = 0 ; j < numSamples ; j++ ) {
        float valFloat = pcm[i][mono+j];
        int val = (int)(32767.0 * valFloat);
        if      ( val > +32767 ) val = +32767;
        else if ( val < -32768 ) val = -32768;
        outputBuffer[ptr]   = (byte)(val & 0xFF);
        outputBuffer[ptr+1] = (byte)((val>>>8) & 0xFF);
        ptr += 2*numChannels;
      }
    }
    
    try {
      outStream.write(outputBuffer, 0, outputBuffer.length);
    } catch ( Exception ex ) {
      throw new IOException("error writing to result buffer (" + ex + ")");
    }

  } // writeData()
*/
  
} // class OggDecode
