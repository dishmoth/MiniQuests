/*
 *  StorageMuffins.java
 *  Copyright Simon Hern 2008
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests;

import com.dishmoth.miniquests.game.Env;

import java.io.*;
import java.net.URL;
import java.util.*;
import javax.jnlp.*;

// wrapper around the WebStart persistent storage functionality
// (muffins are cached in memory to avoid stutters when accessing the disk)
public class StorageMuffins {
  
  // status flag (false => not running under WebStart, so storage unavailable)
  static private boolean mValid;
  
  // the application's code base
  static private URL mCodeBase = null;

  // cached muffins
  static private HashMap<String, byte[]> mCache = null;
  
  // static constructor
  static {
    
    mValid = true;
    
    try {
      Class.forName("javax.jnlp.ServiceManager");
    } catch ( Exception ex ) {
      Env.debug("StorageMuffins: not available (WebStart not used))");
      mValid = false;
    }

    if ( mValid ) {
      try {
        BasicService bs = (BasicService)ServiceManager.lookup(
                                                   "javax.jnlp.BasicService");
        mCodeBase = bs.getCodeBase();
        Env.debug("StorageMuffins: code base " + mCodeBase);
      } catch ( Exception ex ) {
        Env.debug("StorageMuffins: not available");
        mValid = false;
      }
    }

    if ( mValid ) mCache = new HashMap<String,byte[]>();
    
  } // static constructor
  
  // create a new save file, overwrite if it already exists
  // (writes to cache initially)
  static public void saveToMuffin(String name, byte[] data) {

    if ( !mValid ) return;

    mCache.put(name, data);
    
  } // saveToMuffin()
  
  // read data from a saved file (returns null if no data)
  // (reads from cached muffin, if one exists)
  static public byte[] loadFromMuffin(String name) {
    
    if ( !mValid ) return null;

    byte[] data = mCache.get(name);
    if ( data == null ) {
      data = load(name);
      if ( data != null ) mCache.put(name, data);
    }
    
    return data;
        
  } // loadFromMuffin()

  // remove a saved file (only affects the cache initially)
  static public void deleteMuffin(String name) {

    if ( !mValid ) return;

    mCache.put(name, null);

  } // deleteMuffin()

  // update the true muffins based on our cached data
  static public void flushCache() {
    
    if ( !mValid ) return;
    
    for ( Map.Entry<String,byte[]> muffin : mCache.entrySet() ) {
      String name = muffin.getKey();
      byte[] data = muffin.getValue();
      if ( data == null ) {
        delete(name);
      } else {
        save(name, data);
      }
    }

    mCache.clear();
    
  } // flushCache()
  
  // utility function for debugging, list the available saved files
  static public void listMuffins() {

    if ( !mValid ) return;

    if ( !mCache.isEmpty() ) {
      System.out.println("StorageMuffins.listMuffins(): "
                         + "NOTE cache is not empty ("
                         + mCache.size() + " entries)");
    }
    
    PersistenceService ps = getPersistenceService();
    if ( ps == null ) return;
    
    String names[] = null;
    try {
      names = ps.getNames(mCodeBase);
    } catch ( Exception ex ) {
      Env.debug("StorageMuffins.listMuffins(): could not get name list");
      Env.debug("-> " + ex.toString());
    }
      
    if ( names == null || names.length == 0 ) {
      System.out.println("StorageMuffins: no muffins found");
    } else {
      for ( int k = 0 ; k < names.length ; k++ ) {
        FileContents fc = getFileContents(ps, names[k]);
        assert( fc != null );
        System.out.println("StorageMuffins: muffin " + (k+1) + ": " + names[k]
                           + " (" + fileLength(fc) + " bytes)");
      }
    }

  } // listMuffins()

  // create a new save file, overwrite if it already exists
  // (returns false if save failed)
  static private boolean save(String name, byte[] data) {

    assert( mValid );
    
    PersistenceService ps = getPersistenceService();
    if ( ps == null ) return false;

    assert( data != null );
    if ( data.length == 0 ) {
      Env.debug("StorageMuffins: muffin " + name + " cleared");
      delete(ps, name);
      return true;
    }
    
    boolean okay = create(ps, name, data.length);
    if ( !okay ) return false;
  
    FileContents fc = getFileContents(ps, name);
    if ( fc == null ) return false;

    okay = write(fc, data);
    if ( !okay ) return false;
    setDirty(ps, name);
    
    Env.debug("StorageMuffins: wrote " + data.length 
              + " bytes to muffin " + name);
    return true;
    
  } // save()
          
  // read data from a saved file 
  // (returns null if no data, for whatever reason)
  static private byte[] load(String name) {
    
    assert( mValid );

    PersistenceService ps = getPersistenceService();
    if ( ps == null ) return null;

    FileContents fc = getFileContents(ps, name);
    if ( fc == null ) {
      Env.debug("StorageMuffins: muffin " + name + " does not exist");
      return null;
    }

    if ( fileLength(fc) == 0 ) {
      Env.debug("StorageMuffins: muffin " + name + " holds no data");
      return null;
    }
    
    byte data[] = read(fc);
    if ( data == null | data.length == 0 ) {
      Env.debug("StorageMuffins: read zero bytes from muffin " + name);
      return null;
    }
    
    Env.debug("StorageMuffins: read " + (data.length) 
              + " bytes from muffin " + name);
    return data;
        
  } // load()

  // remove a saved file
  static private boolean delete(String name) {

    assert( mValid );

    PersistenceService ps = getPersistenceService();
    if ( ps == null ) return false;

    boolean okay = delete(ps, name); 

    if ( okay ) {
      Env.debug("StorageMuffins: muffin " + name + " deleted");
    } else {
      Env.debug("StorageMuffins: muffin " + name + " not found");
    }
    
    return okay;
    
  } // delete()

  // returns null if service not available
  static private PersistenceService getPersistenceService() {
    
    PersistenceService ps = null;
    try {
      ps = (PersistenceService)ServiceManager.lookup(
                                             "javax.jnlp.PersistenceService");
    } catch ( Exception ex ) {
      Env.debug("StorageMuffins: no persistence service");
      Env.debug("-> " + ex.toString());
      return null;
    }
    assert( ps != null );
    return ps;
    
  } // getPersistenceService()

  // returns null if file contents not found
  static private FileContents getFileContents(PersistenceService ps, 
                                              String             name) {

    assert( ps != null );
    
    FileContents fc = null;
    try {
      fc = ps.get(new URL(mCodeBase.toString() + name));
    } catch ( FileNotFoundException ex ) {
      return null;
    } catch ( Exception ex ) {
      Env.debug("StorageMuffins: no file contents for muffin " + name);
      Env.debug("-> " + ex.toString());
      return null;
    }
    assert( fc != null );

    return fc;
    
  } // getFileContents()

  // returns false if file does not exist
  static private boolean delete(PersistenceService ps, String name) {

    assert( ps != null );
    
    try {
      ps.delete(new URL(mCodeBase.toString() + name)); 
    } catch ( FileNotFoundException ex ) {
      return false;
    } catch ( Exception ex ) {
      Env.debug("StorageMuffins.delete(" + name + "): exception");
      Env.debug("-> " + ex.toString());
      return false;
    }

    return true;
    
  } // delete()

  // returns false if the creation failed
  static private boolean create(PersistenceService ps, String name, int len) {
    
    assert( ps != null );

    delete(ps, name);
    
    long got = 0;
    try {
      got = ps.create(new URL(mCodeBase.toString() + name), len); 
    } catch ( Exception ex ) {
      Env.debug("StorageMuffins.create(" + name + "): exception");
      Env.debug("-> " + ex.toString());
      return false;
    }
    
    if ( got < len ) {
      Env.debug("StorageMuffins.create(" + name + "): "
                + "insufficient space allocated (" + got + " < " + len + ")");
      return false;
    }

    return true;
    
  } // create()

  // returns false if the write failed
  static private boolean write(FileContents fc, byte[] data) {

    assert( fc != null );
    assert( data != null && data.length > 0 );
    
    try {
      OutputStream os = fc.getOutputStream(false); 
      os.write(data); 
      os.close(); 
    } catch ( Exception ex ) {
      Env.debug("StorageMuffins.write: output failure");
      Env.debug("-> " + ex.toString());
      return false;
    }

    return true;
    
  } // write()

  // returns null if read failed
  static private byte[] read(FileContents fc) {

    assert( fc != null );
    
    byte data[] = null;
    try {
      data = new byte[(int)fc.getLength()]; 
      
      InputStream is = fc.getInputStream(); 
      int pos = 0; 
      while( (pos = is.read(data, pos, (int)data.length - pos)) > 0 ) {} 
      is.close(); 
      
    } catch ( Exception ex ) {
      Env.debug("StorageMuffins.read: input failure");
      Env.debug("-> " + ex.toString());
      return null;
    }
    
    return data;
    
  } // read()

  // returns file length (in bytes)
  static private long fileLength(FileContents fc) {

    assert( fc != null );
    
    long len = 0;
    try {
      len = fc.getLength();
    } catch ( Exception ex ) {
      Env.debug("StorageMuffins.fileLength: exception");
      Env.debug("-> " + ex.toString());
      return 0;
    }
    return len;
    
  } // fileLength()
  
  // set the file tag to dirty (so WebStart will be reluctant to delete it)
  static private void setDirty(PersistenceService ps, String name) {
    
    try {
      ps.setTag(new URL(mCodeBase.toString() + name),
                PersistenceService.DIRTY);
    } catch ( Exception ex ) {
      Env.debug("StorageMuffins.setDirty(" + name + "): exception");
      Env.debug("-> " + ex.toString());
    }
    
  } // setDirty()
  
} // class StorageMuffins
