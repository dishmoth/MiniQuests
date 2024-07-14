/*
 *  Terrain.java
 *  Copyright (c) 2024 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.Arrays;

// utilities for building multiple blocks of terrain
public class Terrain {

  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "Y2" };  // grass green

  // recolour the sides of blocks from grey to brown
  private static void recolourTerrain(BlockArray blocks) {

    final byte grey1  = 7,
               grey2  = 56,
               brown1 = 20,
               brown2 = 48;
    EgaImage image = blocks.getImage();
    byte[] pixels = image.pixels();
    for (int k = 0 ; k < pixels.length ; k++) {
      if      (pixels[k] == grey1) pixels[k] = brown1;
      else if (pixels[k] == grey2) pixels[k] = brown2;
    }

  } // recolourTerrain()

  // utility to construct blocks from height values '0' to '9' (or ' ')
  public static void makeBlocks(SpriteManager spriteManager,
                                int zoneX, int zoneY, int zBase,
                                String heightData[]) {

    assert( heightData.length > 0 && heightData[0].length() > 0 );

    final int numX = heightData[0].length(),
              numY = heightData.length;
    assert( numX % Room.kSize == 0 && numY % Room.kSize == 0 );

    int heightTop[][] = new int[numY][numX];
    for ( int iy = 0 ; iy < numY ; iy++ ) {
      assert( heightData[iy].length() == numX );
      for ( int ix = 0 ; ix < numX ; ix++ ) {
        final char ch = heightData[iy].charAt(ix);
        if ( ch == ' ' ) {
          heightTop[numY-1-iy][ix] = -1;
        } else {
          assert( ch >= '0' && ch <= '9' );
          heightTop[numY-1-iy][ix] = (ch - '0');
        }
      }
    }

    int heightBottom[][] = new int[numY][numX];
    for ( int iy = 0 ; iy < numY ; iy++ ) {
      for ( int ix = 0 ; ix < numX ; ix++ ) {
        if ( heightTop[iy][ix] == -1 ) {
          heightBottom[iy][ix] = -1;
        } else {
          int h = heightTop[iy][ix];
          if ( ix > 0 && heightTop[iy][ix-1] >= 0 ) {
            h = Math.min(h, heightTop[iy][ix-1]);
          }
          if ( ix < numX-1 && heightTop[iy][ix+1] >= 0 ) {
            h = Math.min(h, heightTop[iy][ix+1]);
          }
          if ( iy > 0 && heightTop[iy-1][ix] >= 0 ) {
            h = Math.min(h, heightTop[iy-1][ix]);
          }
          if ( iy < numY-1 && heightTop[iy+1][ix] >= 0 ) {
            h = Math.min(h, heightTop[iy+1][ix]);
          }
          heightBottom[iy][ix] = h;
        }
      }
    }

    for ( int ky = 0 ; ky < numY/Room.kSize ; ky++ ) {
      for ( int kx = 0 ; kx < numX/Room.kSize ; kx++ ) {
        int minHeight = 9,
            maxHeight = 0;
        for ( int jy = 0 ; jy < Room.kSize ; jy++ ) {
          for ( int jx = 0 ; jx < Room.kSize ; jx++ ) {
            final int iy = ky*Room.kSize + jy,
                      ix = kx*Room.kSize + jx;
            if ( heightTop[iy][ix] >= 0 ) {
              minHeight = Math.min(minHeight, heightBottom[iy][ix]);
              maxHeight = Math.max(maxHeight, heightTop[iy][ix]);
            }
          }
        }
        if (minHeight > maxHeight) continue;
        final int numZ = maxHeight-minHeight+1;

        String blockData[][] = new String[numZ][Room.kSize];
        for ( int iz = 0 ; iz < numZ ; iz++ ) {
          final int h = minHeight + iz;
          for ( int jy = 0 ; jy < Room.kSize ; jy++ ) {
            final int iy = ky * Room.kSize + jy;
            StringBuffer str = new StringBuffer(Room.kSize);
            for ( int jx = 0 ; jx < Room.kSize ; jx++ ) {
              final int ix = kx * Room.kSize + jx;
              if ( heightTop[iy][ix] >= h && heightBottom[iy][ix] <= h ) {
                str.append('0');
              } else {
                str.append(' ');
              }
            }
            blockData[iz][Room.kSize-1-jy] = str.toString();
          }
        }

        BlockArray blocks = new BlockArray(blockData, kBlockColours,
                                        (zoneX+kx)*Room.kSize,
                                        (zoneY+ky)*Room.kSize,
                                        zBase+2*minHeight);
        recolourTerrain(blocks);
        spriteManager.addSprite(blocks);
      }
    }

  } // makeBlocks()

  // utility to construct a path across terrain data
  public static TerrainPath makePath(int zoneX, int zoneY, int zBase,
                                     String heightData[],
                                     int offsetX, int offsetY, String path[],
                                     char colour) {

    assert (heightData.length > 0 && heightData[0].length() > 0);
    assert (path.length > 0 && path[0].length() > 0);

    final int numX = path[0].length(),
              numY = path.length;
    String pathData[] = new String[numY];

    for ( int iy = 0 ; iy < numY ; iy++ ) {
      StringBuffer str = new StringBuffer(numX);
      for ( int ix = 0 ; ix < numX ; ix++ ) {
        if ( path[iy].charAt(ix) == ' ' ) {
          str.append(' ');
        } else {
          int jx = ix + offsetX,
              jy = iy - offsetY + (heightData.length - numY);
          if ( jy < 0 || jy >= heightData.length ||
               jx < 0 || jx >= heightData[jy].length() ) {
            str.append(' ');
          } else {
            str.append(heightData[jy].charAt(jx));
          }
        }
      }
      pathData[iy] = str.toString();
    }

    return new TerrainPath(zoneX*Room.kSize+offsetX, zoneY*Room.kSize+offsetY,
                           zBase, pathData, colour);

  } // makePath()

} // Terrain
